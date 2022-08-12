package org.clif
package json

import model.*

import upickle.default.{macroRW, readwriter, ReadWriter as RW}

class UPickleReader extends JSONReader[Flashcard[?]]:

	given rwMultipleChoice: RW[MultipleChoice] = macroRW
	given rwFillInTheBlank: RW[FillInTheBlank] = macroRW
	given rwFlashcard: RW[Flashcard[?]] = RW.merge(rwMultipleChoice, rwFillInTheBlank)

	override def read(json: String): Seq[Flashcard[?]] =
		upickle.default.read[Array[Flashcard[?]]](json).toSeq