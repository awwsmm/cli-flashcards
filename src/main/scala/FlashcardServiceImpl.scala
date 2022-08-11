package org.clif

import akka.NotUsed
import akka.grpc.GrpcServiceException
import akka.stream.scaladsl.Source
import io.grpc.Status
import upickle.default.read

import scala.util.{Failure, Success, Try}

class FlashcardServiceImpl extends FlashcardService:

	override def flashcards(in: Category): Source[Flashcard, NotUsed] =

		Try(scala.io.Source.fromResource(f"${in.name}.json")) match

			case Failure(exception) =>
				val message = s"Cannot find category file ${in.name}.json"
				val status = Status.NOT_FOUND.withDescription(message)
				throw new GrpcServiceException(status)

			case Success(fileSource) =>
				println(s"flashcards for ${in.name} category")

				val json = fileSource.getLines().mkString("\n")

				val questions = read[Array[MFlashcard[?]]](json).toSeq

				Source(questions).map {
					_ match
						case MMultipleChoice(prompt, choices) =>
							Flashcard(in.name, prompt)
								.withMultipleChoice(
									MultipleChoice(
										choices.map {
											case (text, correct) =>
												MultipleChoice.Choice(text, correct)
										}.toSeq
									)
								)

						case MFillInTheBlank(prompt, matcher) =>
							Flashcard(in.name, prompt)
								.withFillInTheBlank(
									FillInTheBlank(matcher.pattern.toString)
								)
				}
