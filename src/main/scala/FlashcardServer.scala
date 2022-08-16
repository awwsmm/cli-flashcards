package org.clif

import akka.actor.typed.ActorSystem
import akka.grpc.scaladsl.{ServerReflection, ServiceHandler}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}

import scala.concurrent.duration.DurationInt
import scala.concurrent.{ExecutionContext, Future}
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

		val bound = Http()
			.newServerAt(host, port)
			.bind(proto.FlashcardServiceHandler(service))
			.map(_.addToCoordinatedShutdown(3.seconds))

		bound.onComplete {
			case Success(binding) =>
				val address = binding.localAddress
				system.log.info(
					"FlashcardServer running at {}:{}",
					address.getHostString,
					address.getPort
				)
			case Failure(ex) =>
				system.log.error("Failed to bind gRPC endpoint, terminating system", ex)
				system.terminate()
		}

		sys.addShutdownHook {
			println(""" FlashcardServer says: "au revoir!"""")
		}