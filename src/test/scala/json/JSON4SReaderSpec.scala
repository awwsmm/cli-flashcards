package org.clif
package json

class JSON4SReaderSpec extends JSONReaderBaseSpec:

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
			|    "choices": [
			|      { "text": "green",  "correct": true },
			|      { "text": "red",    "correct": false, "feedback": "some feedback" },
			|      { "text": "blue",   "correct": true },
			|      { "text": "orange", "correct": false, "feedback": "some more feedback" }
			|    ]
			|  },
			|  {
			|    "prompt": "This sentence is true",
			|    "isTrue": true
			|  }
			|]
			|""".stripMargin

	it should "read the example JSON" in {
		reader.read(json) should contain theSameElementsAs expected
	}

	it should "count the number of questions" in {
		reader.count(json) shouldBe 3
	}