package org.clif
package readers

trait JSONReader[T]:
	def read(json: String): Seq[T]