package org.clif
package repository

import json.JSONReader
import model.Flashcard

import java.io.File
import scala.io.Source
import scala.util.{Failure, Success, Try, Using}

class InMemoryRepository(dir: File)(reader: JSONReader[Flashcard[?]]) extends Repository:
	require(dir.exists && dir.isDirectory)

	override def categories: Try[Seq[String]] =
		Success(
			dir.listFiles(_.isFile)
				.filter(_.getName.endsWith(".json"))
				.map(_.getName.replace(".json", ""))
				.toSeq
		)

	override def flashcards(category: String): Try[Seq[Flashcard[_]]] =
		Using(Source.fromFile(new File(dir, category)))(_.getLines.mkString) match
			case Failure(exception) => Failure(exception)
			case Success(json) => Success(reader.read(json))