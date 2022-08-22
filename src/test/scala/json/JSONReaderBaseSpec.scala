package org.clif
package json

import model.*
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should

trait JSONReaderBaseSpec extends AnyFlatSpec with should.Matchers {

	import MultipleChoice.Choice

	val expected: Seq[Flashcard[_]] = Seq(
		FillInTheBlank("Type America or america", "[Aa]merica", None),
		FillInTheBlank("Type any three-letter word that starts with a capital A", "A.{2}", Some("Try: Ace")),
		MultipleChoice("Choose green and blue", Set(
			Choice("green", true, None),
			Choice("red", false, Some("some feedback")),
			Choice("blue", true, None),
			Choice("orange", false, Some("some more feedback"))
		)),
		TrueOrFalse("This sentence is true", true, None),
		TrueOrFalse("This prompt has some feedback", true, Some("It has feedback, so the answer is true"))
	)

}
