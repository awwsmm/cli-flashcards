package org.clif
package repository

import scala.util.Try
import model.Flashcard

trait Repository:
	def categories: Try[Seq[String]]
	def flashcards(category: String): Try[Seq[Flashcard[_]]]