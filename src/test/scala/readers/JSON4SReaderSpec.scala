package org.clif
package readers

import model.*
import org.json4s.*
import org.json4s.JsonDSL.*
import org.json4s.native.Serialization
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should

import scala.util.{Failure, Success, Try}

class JSON4SReaderSpec extends AnyFlatSpec with should.Matchers:

	behavior of "JSON4SReader"

	val reader = new JSON4SReader

	val json: String =
		"""
			|[
			|  {
			|    "prompt": "Type America or america",
			|    "regex": "[Aa]merica"
			|  },
			|  {
			|    "prompt": "Choose green and blue",
			|    "choices": {
			|      "green": true,
			|      "red": false,
			|      "blue": true,
			|      "orange": false
			|    }
			|  }
			|]
			|""".stripMargin

	it should "read the example JSON" in {

		val fitb = FillInTheBlank("Type America or america", "[Aa]merica")
		val mc = MultipleChoice("Choose green and blue", Map(
			"green" -> true,
			"red" -> false,
			"blue" -> true,
			"orange" -> false
		))

		val expected = Seq(
			fitb,
			mc
		)

		reader.read(json) should contain theSameElementsAs expected

	}