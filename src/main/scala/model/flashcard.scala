package org.clif
package model

import scala.util.matching.Regex

sealed trait Flashcard[T]:
	def prompt: String
	def validate(t: T): Boolean

case class MultipleChoice(prompt: String, choices: Set[MultipleChoice.Choice]) extends Flashcard[Seq[String]]:
	override def validate(responses: Seq[String]): Boolean = choices.forall {
		case MultipleChoice.Choice(text, correct, _) => correct == responses.contains(text)
	}

object MultipleChoice:
	case class Choice(text: String, correct: Boolean, feedback: Option[String])

case class FillInTheBlank(prompt: String, regex: String, feedback: Option[String]) extends Flashcard[String]:
	private[this] val matcher = Regex(regex)
	override def validate(response: String): Boolean = matcher.matches(response)
