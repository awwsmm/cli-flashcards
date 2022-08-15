package org.clif
package repository

import json.JSON4SReader
import org.scalatest.TryValues

import java.io.File

class InJarRepositorySpec extends json.JSONReaderBaseSpec with TryValues:

	behavior of "InJarRepository"

	val reader = new JSON4SReader

	// TODO: should it actually just fall back to being an InMemoryRepository?
	it should "fail to instantiate when used outside of a jar" in {
		an[IllegalAccessException] shouldBe thrownBy (new InJarRepository(reader))
	}