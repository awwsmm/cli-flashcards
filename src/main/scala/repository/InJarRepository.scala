package org.clif
package repository

import json.JSONReader
import model.Flashcard

import java.net.URLDecoder
import java.util.jar.JarFile
import scala.jdk.CollectionConverters.*
import scala.util.{Failure, Success, Try}

class InJarRepository(reader: JSONReader[Flashcard[?]]) extends Repository:

	private def isJar(path: String): Boolean =
		path.startsWith("file:") && path.contains(".jar!")

	// do some horrible classpath hacking to get list of files in /resources directory
	private val jarPath =
		Try(getClass.getClassLoader.getResource("application.conf")) match
			case Success(url) if isJar(url.getPath) =>
				url.getPath.substring(5, url.getPath.indexOf('!'))

			case Success(_) =>
				throw new IllegalAccessException("InJarRepository must only be used inside a running jar. Consider using an InMemoryRepository instead.")

			case Failure(exception) => throw exception

	override def categories: Try[Seq[String]] =
		val jar = new JarFile(URLDecoder.decode(jarPath, "UTF-8"))
		val categories = jar.entries().asScala.filter(_.getName.endsWith(".json"))
			.map(each => Category(each.getName.replace(".json", "")))
		Success(categories.map(_.name).toSeq)

	override def flashcards(category: String): Try[Seq[org.clif.model.Flashcard[_]]] =
		Try(scala.io.Source.fromResource(s"$category.json")) match

			case Success(fileSource) =>
				val json = fileSource.getLines().mkString("\n")
				Success(reader.read(json))

			case Failure(exception) => Failure(exception)