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

	// do some horrible classpath hacking to get list of files in /resources/categories directory
	private val jarPath =
		Try(getClass.getClassLoader.getResource("categories/example.json")) match
			case Success(url) if isJar(url.getPath) =>
				url.getPath.substring(5, url.getPath.indexOf('!'))

			case Success(_) =>
				throw new IllegalAccessException("InJarRepository must only be used inside a running jar. Consider using an InMemoryRepository instead.")

			case Failure(exception) => throw exception

	override def categories: Try[Seq[String]] =
		val jar = new JarFile(URLDecoder.decode(jarPath, "UTF-8"))
		val categories = jar.entries().asScala.filter(_.getName.endsWith(".json"))
			.map(each => proto.Category(each.getName
				.replace(".json", "")
				.replace("categories/", "")
			))
		Success(categories.map(_.name).toSeq)

	override def count(category: String): Try[Int] =
		whenCategoryExists(category)(reader.count)

	override def flashcards(category: String): Try[Seq[Flashcard[_]]] =
		whenCategoryExists(category)(reader.read)

	private def whenCategoryExists[T](category: String)(f: String => T): Try[T] =
		Try(scala.io.Source.fromResource(s"categories/$category.json")) match
			case Failure(exception) => Failure(exception)
			case Success(fileSource) => Success(f(fileSource.getLines().mkString("\n")))
