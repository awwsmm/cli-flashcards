package org.clif
package model

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should

class flashcardSpec extends AnyFlatSpec with should.Matchers:

	behavior of "Multiple Choice Flashcard (with a single correct choice)"

	it should "evaluate to true when the user selects the only correct choice" in {
		val mc = multipleChoice("?", Map("true" -> true, "false" -> false))
		mc.validate(Seq("true")) shouldBe true
	}

	it should "evaluate to false when the user selects an incorrect choice" in {
		val mc = multipleChoice("?", Map("true" -> true, "false" -> false))
		mc.validate(Seq("false")) shouldBe false
	}

	behavior of "Multiple Choice Flashcard (with multiple correct choices)"

	it should "evaluate to false when the user doesn't select all correct choices" in {
		val mc = multipleChoice("?", Map("true" -> true, "also true" -> true, "false" -> false))
		mc.validate(Seq("true")) shouldBe false
	}

	it should "evaluate to true when the user selects all correct choices and no incorrect choices" in {
		val mc = multipleChoice("?", Map("true" -> true, "also true" -> true, "false" -> false))
		mc.validate(Seq("true", "also true")) shouldBe true
	}

	it should "evaluate to false when the user selects any incorrect choices" in {
		val mc = multipleChoice("?", Map("true" -> true, "also true" -> true, "false" -> false))
		mc.validate(Seq("true", "also true", "false")) shouldBe false
	}

	it should "evaluate to false if no choices were selected" in {
		val mc = multipleChoice("?", Map("true" -> true, "also true" -> true, "false" -> false))
		mc.validate(Seq.empty) shouldBe false
	}

	behavior of "Fill-In-The-Blank Flashcard"

	it should "evaluate to true if the user's answer matches the correct answer" in {
		val fitb = FillInTheBlank("?", "[Aa]merica", None)
		fitb.validate("America") shouldBe true
		fitb.validate("america") shouldBe true
	}

	it should "evaluate to false if the user's answer does not match the correct answer" in {
		val fitb = FillInTheBlank("?", "[Aa]merica", None)
		fitb.validate("me") shouldBe false
		fitb.validate(" America ") shouldBe false
	}

	def multipleChoice(prompt: String, choices: Map[String, Boolean]): MultipleChoice =
		MultipleChoice(prompt, choices.map {
			case (text, correct) => MultipleChoice.Choice(text, correct, None)
		}.toSet)