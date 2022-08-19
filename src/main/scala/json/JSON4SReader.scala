package org.clif
package json

import model.*
import org.json4s.*
import org.json4s.JsonDSL.*
import org.json4s.native.Serialization

class JSON4SReader extends JSONReader[Flashcard[_]]:
	given formats: Formats = DefaultFormats ++ Seq(
		JSON4SReader.FlashcardSerializer,
		JSON4SReader.FlashcardCounter
	)

	override def read(json: String): Seq[Flashcard[?]] =
		Serialization.read[Seq[Flashcard[?]]](json)

	override def count(json: String): Int =
		Serialization.read[Seq[Int]](json).sum

object JSON4SReader:

	given defaultFormats: Formats = DefaultFormats

	object FlashcardSerializer extends CustomSerializer[Flashcard[?]](_ => (
		{
			case jobj: JObject => jobj \ "prompt" match
				case JNothing => throw new Exception("missing 'prompt' field in definition")
				case promptJson =>
					summon[scala.reflect.ClassTag[String]]
					val prompt = promptJson.extract[String]

					if (jobj \ "regex") != JNothing then
						FillInTheBlank(prompt, (jobj \ "regex").extract[String], None)

					else if (jobj \ "choices") != JNothing then
						MultipleChoice(prompt, (jobj \ "choices").extract[Set[MultipleChoice.Choice]])

					else if (jobj \ "isTrue") != JNothing then
						TrueOrFalse(prompt, (jobj \ "isTrue").extract[Boolean])

					else throw new Exception("missing fields to discriminate")
		},
		serializationPurposefullyUnimplemented
	))

	object FlashcardCounter extends CustomSerializer[Int](_ => (
		{
			case _: JObject => 1
		},
		serializationPurposefullyUnimplemented
	))

	private val serializationPurposefullyUnimplemented: PartialFunction[Any, JValue] = {
		case _ => throw new IllegalStateException("should never get here")
	}