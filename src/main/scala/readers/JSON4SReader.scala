package org.clif
package readers

import model.*
import org.json4s.*
import org.json4s.JsonDSL.*
import org.json4s.native.Serialization

class JSON4SReader extends JSONReader[Flashcard[_]]:
	given formats: Formats = DefaultFormats ++ Seq(JSON4SReader.FlashcardDeserializer)

	override def read(json: String): Seq[Flashcard[?]] =
		Serialization.read[Seq[Flashcard[?]]](json)

object JSON4SReader:

	given defaultFormats: Formats = DefaultFormats

	object FlashcardDeserializer extends CustomSerializer[Flashcard[?]](_ => (
		{ // custom deserialization below
			case jobj: JObject => jobj \ "prompt" match
				case JNothing => throw new Exception("missing 'prompt' field in definition")
				case promptJson =>
					val prompt = promptJson.extract[String]

					if (jobj \ "regex") != JNothing then
						FillInTheBlank(prompt, (jobj \ "regex").extract[String])

					else if (jobj \ "choices") != JNothing then
						MultipleChoice(prompt, (jobj \ "choices").extract[Map[String, Boolean]])

					else throw new Exception("missing fields to discriminate")
		},
		{ // custom serialization below
			case FillInTheBlank(prompt, regex) => ("prompt" -> prompt) ~ ("regex" -> regex)
			case MultipleChoice(prompt, map) => ("prompt" -> prompt) ~ ("choices" -> map)
			case _: Flashcard[?] => throw new Exception("Cannot write generic trait instance")
		}
	))