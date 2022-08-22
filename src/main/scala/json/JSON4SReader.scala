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

					extension (jobj: JObject)
						def tryToExtractAs[T](field: String)(using m: Manifest[T]): Option[T] =
							if jobj \ field == JNothing then None else Some((jobj \ field).extract[T])

					def tryToExtractFillInTheBlank: Option[FillInTheBlank] =
						jobj.tryToExtractAs[String]("regex")
							.map(FillInTheBlank(prompt, _, jobj.tryToExtractAs[String]("feedback")))

					def tryToExtractMultipleChoice: Option[MultipleChoice] =
						jobj.tryToExtractAs[Set[MultipleChoice.Choice]]("choices")
							.map(MultipleChoice(prompt, _))

					def tryToExtractTrueOrFalse: Option[TrueOrFalse] =
						jobj.tryToExtractAs[Boolean]("isTrue")
							.map(TrueOrFalse(prompt, _, jobj.tryToExtractAs[String]("feedback")))

					tryToExtractFillInTheBlank orElse
						tryToExtractMultipleChoice orElse
							tryToExtractTrueOrFalse getOrElse
								(throw new Exception("missing fields to discriminate"))
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