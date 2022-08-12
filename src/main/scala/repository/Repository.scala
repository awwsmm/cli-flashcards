package org.clif
package repository

import scala.util.Try

trait Repository:
	def categories: Try[Seq[String]]
	def flashcards(category: String): Try[Seq[org.clif.model.Flashcard[_]]]