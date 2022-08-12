package org.clif
package readers

import model.*
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should

class UPickleReaderSpec extends AnyFlatSpec with should.Matchers:

	behavior of "UPickleReader"

	val reader = new UPickleReader

	val json: String =
		"""
			|[
			|  {
			|    "$type": "org.clif.model.FillInTheBlank",
			|    "prompt": "Type America or america",
			|    "regex": "[Aa]merica"
			|  },
			|  {
			|    "$type": "org.clif.model.MultipleChoice",
			|    "prompt": "Choose green and blue",
			|    "choices": [
			|      ["green", true],
			|      ["red", false],
			|      ["blue", true],
			|      ["orange", false]
			|    ]
			|  }
			|]
			|""".stripMargin

	it should "read the example JSON" in {

		val expected = Seq(
			FillInTheBlank("Type America or america", "[Aa]merica"),
			MultipleChoice("Choose green and blue", Map(
				"green" -> true,
				"red" -> false,
				"blue" -> true,
				"orange" -> false
			))
		)

		reader.read(json) should contain theSameElementsAs expected
	}
