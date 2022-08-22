package org.clif
package repository

import json.JSON4SReader
import org.scalatest.TryValues

import java.io.File

class InMemoryRepositoryIntegrationSpec extends json.JSONReaderBaseSpec with TryValues:

	behavior of "InMemoryRepository with JSON4SReader"

	val reader = new JSON4SReader
	val dir = new File("./src/main/resources/categories")
	val repo = new InMemoryRepository(dir)(reader)

	it should "get the list of categories from the list of JSON files in the directory" in {
		repo.categories.success.value should contain theSameElementsAs Seq("example")
	}

	it should "correctly read the example JSON file" in {
		repo.flashcards("example.json").success.value should contain theSameElementsAs expected
	}

	it should "correctly count the number of flashcards in a category" in {
		repo.count("example.json").success.value shouldBe 5
	}