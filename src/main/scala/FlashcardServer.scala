package org.clif

import akka.actor.typed.ActorSystem
import akka.grpc.scaladsl.{ServerReflection, ServiceHandler}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}

import scala.concurrent.duration.DurationInt
import scala.concurrent.{ExecutionContext, Future}
import scala.io.StdIn
import scala.util.{Failure, Success}

object FlashcardServer:

	def start(
	         host: String,
	         port: Int,
	         system: ActorSystem[_],
	         service: proto.FlashcardService
	         ): Unit =
		given actorSystem: ActorSystem[_] = system
		given ec: ExecutionContext = actorSystem.executionContext

		val bindingFuture = Http()
			.newServerAt(host, port)
			.bind(proto.FlashcardServiceHandler(service))
			.map(_.addToCoordinatedShutdown(3.seconds))

		StdIn.readLine() // let it run until user presses return
		bindingFuture
			.flatMap(_.unbind()) // trigger unbinding from the port
			.onComplete(_ => system.terminate()) // and shutdown when done