package org.clif

import json.JSONReader
import repository.Repository

import akka.NotUsed
import akka.grpc.GrpcServiceException
import akka.stream.scaladsl.Source
import io.grpc.Status

import java.io.File
import java.net.{JarURLConnection, URL, URLDecoder}
import java.nio.file.{FileSystems, Files}
import java.util.jar.JarFile
import scala.jdk.CollectionConverters.*
import scala.util.{Failure, Success, Try}
import model.*

import scala.concurrent.Future

class FlashcardServiceImpl(repository: Repository) extends proto.FlashcardService:

	override def categories(in: proto.Empty): Source[proto.Category, NotUsed] =
		println("/Categories")

		repository.categories match

			case Failure(exception) =>
				val message = s"Unable to list categories due to: ${exception.getMessage}"
				val status = Status.INTERNAL.withDescription(message)
				throw new GrpcServiceException(status)

			case Success(names) =>
				Source(names).map(proto.Category(_))

	override def count(in: proto.Category): Future[proto.CategoryCount] =
		println(s"""/CategoryCount for {"name": "${in.name}"}""")

		repository.count(in.name) match
			case Failure(exception) =>
				val status = Status.INTERNAL.withDescription(exception.getMessage)
				throw new GrpcServiceException(status)

			case Success(count) => Future.successful(
				proto.CategoryCount()
					.withCategory(proto.Category(in.name))
					.withCount(count)
			)

	override def flashcards(in: proto.Category): Source[proto.Flashcard, NotUsed] =
		println(s"""/Flashcards with {"name": "${in.name}"}""")

		repository.flashcards(in.name) match

			case Failure(_) =>
				val message = s"""Unknown category "${in.name}". See /Categories"""
				val status = Status.NOT_FOUND.withDescription(message)
				throw new GrpcServiceException(status)

			case Success(flashcards) =>
				Source(flashcards).map { _ match
					case MultipleChoice(prompt, choices) =>
						proto.Flashcard(in.name, prompt)
							.withMultipleChoice(
								proto.MultipleChoice(
									choices.map {
										case MultipleChoice.Choice(text, correct, feedback) =>
											proto.MultipleChoice.Choice(text, correct, feedback)
									}.toSeq
								)
							)

					case FillInTheBlank(prompt, regex, feedback) =>
						proto.Flashcard(in.name, prompt)
							.withFillInTheBlank(
								proto.FillInTheBlank(regex, feedback)
							)

					case TrueOrFalse(prompt, isTrue, feedback) =>
						proto.Flashcard(in.name, prompt)
							.withTrueOrFalse(
								proto.TrueOrFalse(isTrue, feedback)
							)
				}
