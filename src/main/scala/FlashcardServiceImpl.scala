package org.clif

import json.JSONReader
import repository.Repository

import akka.NotUsed
import akka.grpc.GrpcServiceException
import akka.stream.scaladsl.Source
import com.google.protobuf.empty.Empty
import io.grpc.Status

import java.io.File
import java.net.{JarURLConnection, URL, URLDecoder}
import java.nio.file.{FileSystems, Files}
import java.util.jar.JarFile
import scala.jdk.CollectionConverters.*
import scala.util.{Failure, Success, Try}

class FlashcardServiceImpl(repository: Repository) extends FlashcardService:

	override def categories(in: Empty): Source[Category, NotUsed] =
		repository.categories match

			case Failure(exception) =>
				val message = s"Unable to list categories due to: ${exception.getMessage}"
				val status = Status.INTERNAL.withDescription(message)
				throw new GrpcServiceException(status)

			case Success(names) =>
				Source(names).map(Category(_))

	override def flashcards(in: Category): Source[Flashcard, NotUsed] =
		repository.flashcards(in.name) match

			case Failure(_) =>
				val message = s"""Unknown category "${in.name}". See /Categories"""
				val status = Status.NOT_FOUND.withDescription(message)
				throw new GrpcServiceException(status)

			case Success(flashcards) =>
				Source(flashcards).map { _ match
					case org.clif.model.MultipleChoice(prompt, choices) =>
						Flashcard(in.name, prompt)
							.withMultipleChoice(
								MultipleChoice(
									choices.map {
										case org.clif.model.MultipleChoice.Choice(text, correct, feedback) =>
											MultipleChoice.Choice(text, correct, feedback)
									}.toSeq
								)
							)

					case org.clif.model.FillInTheBlank(prompt, regex, feedback) =>
						Flashcard(in.name, prompt)
							.withFillInTheBlank(
								FillInTheBlank(regex, feedback)
							)
				}
