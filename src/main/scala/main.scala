package org.clif

import readers.UPickleReader

import akka.actor.typed.scaladsl.{AbstractBehavior, ActorContext, Behaviors}
import akka.actor.typed.{ActorSystem, Behavior, PostStop, Signal}
import akka.grpc.GrpcServiceException
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import akka.http.scaladsl.{Http, HttpsConnectionContext}
import com.typesafe.config.ConfigFactory
import io.grpc.Status

import scala.concurrent.{ExecutionContext, Future}
import scala.io.StdIn
import scala.util.{Failure, Success, Try}

/*

grpcurl -vv -d '{"name": "example"}' -plaintext -import-path ./src/main/protobuf -proto flashcards.proto localhost:8083 cli_flashcards.FlashcardService/Flashcards

*/

@main def entrypoint(): Unit =

	given system: ActorSystem[Nothing] = ActorSystem[Nothing](
		Behaviors.empty[Nothing], "actor-system")

	given ec: ExecutionContext = system.executionContext

	// Create service handlers
	val service: HttpRequest => Future[HttpResponse] =
		FlashcardServiceHandler(new FlashcardServiceImpl(new UPickleReader))

	// Bind service handler servers to localhost:8080/8081
	val binding = Http().newServerAt("127.0.0.1", 8083).bind(service)

	// report successful binding
	binding.foreach { binding => println(s"gRPC server bound to: ${binding.localAddress}") }
	println(s"Server now online. Press RETURN to stop...")
	StdIn.readLine() // let it run until user presses return

	binding
		.flatMap(_.unbind()) // trigger unbinding from the port
		.onComplete(_ => system.terminate()) // and shutdown when done