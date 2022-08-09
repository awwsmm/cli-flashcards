package org.clif

import upickle.default.*

import scala.io.StdIn

@main
def main(): Unit =

  val file = scala.io.Source.fromResource("example-questions.json").getLines().mkString("\n")

  val questions = read[Array[Flashcard[?]]](file).toSeq

  val rand = scala.util.Random

  rand.shuffle(questions).zipWithIndex.foreach { _ match
    case (q @ MultipleChoice(prompt, choices), index) =>
      println(f"\n$index: $prompt")

      val selections = ('A' to 'Z').zip(rand.shuffle(choices)).toMap
      val letters = selections.keys

      selections.foreach {
        case (char, (choice, correct)) =>
          println(f"  $char) $choice")
      }

      val input = StdIn.readLine("> ")
      val lettersSelected = input.replaceAll(f"[^$letters]", "").toCharArray
      val answersSelected = lettersSelected.flatMap(selections.get).map(_._1).toSeq

      if q.validate(answersSelected) then println("CORRECT") else println("INCORRECT")

    case (q @ FillInTheBlank(prompt, matcher), index) =>
      println(f"\n$index: $prompt")

      val input = StdIn.readLine("> ")
      if q.validate(input) then println("CORRECT") else println("INCORRECT")
  }