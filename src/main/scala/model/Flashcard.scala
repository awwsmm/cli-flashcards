package org.clif
package model

import upickle.default.{macroRW, readwriter, ReadWriter as RW}

import scala.util.matching.Regex

sealed trait Flashcard[T]:
	def prompt: String
	def validate(t: T): Boolean

object Flashcard:
	given rw: RW[Flashcard[?]] = RW.merge(MultipleChoice.rw, FillInTheBlank.rw)

case class MultipleChoice(prompt: String, choices: Map[String, Boolean]) extends Flashcard[Seq[String]]:
	override def validate(responses: Seq[String]): Boolean = choices.forall { case (choice, correct) => correct == responses.contains(choice) }

object MultipleChoice:
	given rw: RW[MultipleChoice] = macroRW

case class FillInTheBlank(prompt: String, matcher: Regex) extends Flashcard[String]:
	override def validate(response: String): Boolean = matcher.matches(response)

object FillInTheBlank:
	given rwRegex: RW[Regex] = readwriter[String].bimap[Regex](_.pattern.toString, new Regex(_))
	given rw: RW[FillInTheBlank] = macroRW