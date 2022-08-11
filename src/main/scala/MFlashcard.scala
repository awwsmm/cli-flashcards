package org.clif

import upickle.default.{macroRW, readwriter, ReadWriter as RW}

import scala.util.matching.Regex

sealed trait MFlashcard[T]:
	def prompt: String
	def validate(t: T): Boolean

object MFlashcard:
	given rw: RW[MFlashcard[?]] = RW.merge(MMultipleChoice.rw, MFillInTheBlank.rw)

case class MMultipleChoice(prompt: String, choices: Map[String, Boolean]) extends MFlashcard[Seq[String]]:
	override def validate(responses: Seq[String]): Boolean = choices.forall { case (choice, correct) => correct == responses.contains(choice) }

object MMultipleChoice:
	given rw: RW[MMultipleChoice] = macroRW

case class MFillInTheBlank(prompt: String, matcher: Regex) extends MFlashcard[String]:
	override def validate(response: String): Boolean = matcher.matches(response)

object MFillInTheBlank:
	given rwRegex: RW[Regex] = readwriter[String].bimap[Regex](_.pattern.toString, new Regex(_))
	given rw: RW[MFillInTheBlank] = macroRW