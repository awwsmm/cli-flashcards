package org.clif
package json

import model.*

import upickle.default.{macroRW, readwriter, ReadWriter as RW}

class UPickleReader extends JSONReader[Flashcard[?]]:

	given rwChoice: RW[MultipleChoice.Choice] = macroRW
	given rwMultipleChoice: RW[MultipleChoice] = macroRW
	given rwFillInTheBlank: RW[FillInTheBlank] = macroRW
	given rwTrueOrFalse: RW[TrueOrFalse] = macroRW
	given rwFlashcard: RW[Flashcard[?]] = RW.merge(rwMultipleChoice, rwFillInTheBlank, rwTrueOrFalse)

	override def read(json: String): Seq[Flashcard[?]] =
		upickle.default.read[Array[Flashcard[?]]](json).toSeq

	override def count(json: String): Int =
		given counter: RW[Int] =
			upickle.default.readwriter[ujson.Value].bimap[Int](
				_ => throw new Exception("purposefully unimplemented"),
				each => 1
			)

		upickle.default.read[Array[Int]](json).sum