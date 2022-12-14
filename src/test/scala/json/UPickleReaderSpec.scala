package org.clif
package json

class UPickleReaderSpec extends JSONReaderBaseSpec:

	behavior of "UPickleReader"

	val reader = new UPickleReader

	val json: String =
		"""
			|[
			|  {
			|    "$type": "org.clif.model.FillInTheBlank",
			|    "prompt": "Type America or america",
			|    "regex": "[Aa]merica",
			|    "feedback": []
			|  },
			|  {
			|    "$type": "org.clif.model.FillInTheBlank",
			|    "prompt": "Type any three-letter word that starts with a capital A",
			|    "regex": "A.{2}",
			|    "feedback": ["Try: Ace"]
			|  },
			|  {
			|    "$type": "org.clif.model.MultipleChoice",
			|    "prompt": "Choose green and blue",
			|    "choices": [
			|      { "text": "green",  "correct": true,  "feedback": [] },
			|      { "text": "red",    "correct": false, "feedback": ["some feedback"] },
			|      { "text": "blue",   "correct": true,  "feedback": [] },
			|      { "text": "orange", "correct": false, "feedback": ["some more feedback"] }
			|    ]
			|  },
			|  {
			|    "$type": "org.clif.model.TrueOrFalse",
			|    "prompt": "This sentence is true",
			|    "isTrue": true,
			|    "feedback": []
			|  },
			|  {
			|    "$type": "org.clif.model.TrueOrFalse",
			|    "prompt": "This prompt has some feedback",
			|    "isTrue": true,
			|    "feedback": ["It has feedback, so the answer is true"]
			|  }
			|]
			|""".stripMargin

	it should "read the example JSON" in {
		reader.read(json) should contain theSameElementsAs expected
	}

	it should "count the number of questions" in {
		reader.count(json) shouldBe 5
	}