package org.clif

import readers.JSONReader

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

class FlashcardServiceImpl(reader: JSONReader[org.clif.model.Flashcard[?]]) extends FlashcardService:

	// do some horrible classpath hacking to get list of files in /resources directory
	override def categories(in: Empty): Source[Category, NotUsed] =

		Try(getClass.getClassLoader.getResource("example.json")) match

			case Failure(exception) =>
				val message = s"Unable to list categories."
				val status = Status.INTERNAL.withDescription(message)
				throw new GrpcServiceException(status)

			case Success(value) =>

				val jarPath = value.getPath.substring(5, value.getPath.indexOf('!'))
				val jar = new JarFile(URLDecoder.decode(jarPath, "UTF-8"))
				val categories = jar.entries().asScala.filter(_.getName.endsWith(".json")).map(each => Category(each.getName.replace(".json", "")))
				Source.fromIterator(() => categories)

	override def flashcards(in: Category): Source[Flashcard, NotUsed] =

		Try(scala.io.Source.fromResource(f"${in.name}.json")) match

			case Failure(exception) =>
				val message = s"Cannot find category file ${in.name}.json. See /Categories"
				val status = Status.NOT_FOUND.withDescription(message)
				throw new GrpcServiceException(status)

			case Success(fileSource) =>
				println(s"flashcards for ${in.name} category")

				val json = fileSource.getLines().mkString("\n")

				val questions = reader.read(json)

				Source(questions).map {
					_ match
						case org.clif.model.MultipleChoice(prompt, choices) =>
							Flashcard(in.name, prompt)
								.withMultipleChoice(
									MultipleChoice(
										choices.map {
											case (text, correct) =>
												MultipleChoice.Choice(text, correct)
										}.toSeq
									)
								)

						case org.clif.model.FillInTheBlank(prompt, regex) =>
							Flashcard(in.name, prompt)
								.withFillInTheBlank(
									FillInTheBlank(regex)
								)
				}
