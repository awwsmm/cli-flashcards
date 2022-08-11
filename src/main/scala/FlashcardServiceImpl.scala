package org.clif
import akka.NotUsed
import akka.stream.scaladsl.Source

class FlashcardServiceImpl extends FlashcardService:

	override def flashcards(in: Category): Source[Flashcard, NotUsed] =
		println(s"flashcards for ${in.name} category")

		val fc = Flashcard("cet", "prampt")

		Source.single(fc)

//	override def flashcards(in: Any): Source[Any, NotUsed] = ???
//
//	/**
//	 * Return flashcards in a category
//	 */
//	override def flashcards(in: Category): Source[Flashcard, NotUsed] =
//		println(s"flashcards for ${in.name} category")
//
////		val fileSource = scala.io.Source.fromResource(f"${in.name}.json")
//
//		val fc =
//			new Flashcard()
//				.withPrompt("quoi?")
//				.withCategory("stuff")
//
//		Source.single(fc)
//
////		Source.fromIterator(new Iterator[Flashcard] {
////			override def hasNext: Boolean = true
////
////			override def next(): Flashcard =
//////					.withMultipleChoice(
//////						new MultipleChoice().withChoices(Seq(
//////							MultipleChoice.Choice("this is false", false),
//////							MultipleChoice.Choice("this is true", true)
//////						))
//////					)
////		})