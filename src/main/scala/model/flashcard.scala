package org.clif
package model

import scala.util.matching.Regex

sealed trait Flashcard[T]:
	def prompt: String
	def validate(t: T): Boolean

case class MultipleChoice(prompt: String, choices: Map[String, Boolean]) extends Flashcard[Seq[String]]:
	override def validate(responses: Seq[String]): Boolean = choices.forall { case (choice, correct) => correct == responses.contains(choice) }

case class FillInTheBlank(prompt: String, regex: String) extends Flashcard[String]:
	private[this] val matcher = Regex(regex)
	override def validate(response: String): Boolean = matcher.matches(response)
