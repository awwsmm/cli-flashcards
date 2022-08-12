package org.clif
package repository

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

class InJarRepository(reader: JSONReader[org.clif.model.Flashcard[?]]) extends Repository:

	override def categories: Try[Seq[String]] =
		Try(getClass.getClassLoader.getResource("application.conf")) match

			case Success(value) =>

				// do some horrible classpath hacking to get list of files in /resources directory
				val jarPath = value.getPath.substring(5, value.getPath.indexOf('!'))
				val jar = new JarFile(URLDecoder.decode(jarPath, "UTF-8"))
				val categories = jar.entries().asScala.filter(_.getName.endsWith(".json"))
					.map(each => Category(each.getName.replace(".json", "")))

				Success(categories.map(_.name).toSeq)

			case Failure(exception) => Failure(exception)

	override def flashcards(category: String): Try[Seq[org.clif.model.Flashcard[_]]] =
		Try(scala.io.Source.fromResource(s"$category.json")) match

			case Success(fileSource) =>
				val json = fileSource.getLines().mkString("\n")
				Success(reader.read(json))

			case Failure(exception) => Failure(exception)