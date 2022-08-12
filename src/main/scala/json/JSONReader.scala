package org.clif
package json

trait JSONReader[T]:
	def read(json: String): Seq[T]