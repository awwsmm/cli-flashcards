package org.clif

import json.JSON4SReader
import repository.InJarRepository

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

@main def entrypoint(args: String*): Unit =

	// simple command-line argument parser

	def hostParser(str: String): Option[String] =
		val HostPattern = "(--host|-h)=(.+)".r
		str match
			case HostPattern(flag, host) => Some(host)
			case _ => None

	def portParser(str: String): Option[Int] =
		val PortPattern = "(--port|-p)=([1-9]\\d{1,4})".r
		str match
			case PortPattern(flag, portStr) => Some(portStr.toInt)
			case _ => None

	def parse(str: String): Option[(String, String)] =
		hostParser(str).map("host" -> _).orElse(
			portParser(str).map("port" -> _.toString))

	val opts = args.flatMap(parse).toMap

	given system: ActorSystem[Nothing] =
		ActorSystem[Nothing](Behaviors.empty[Nothing], "actor-system")

	given ec: ExecutionContext = system.executionContext

	// CL args override application.conf / env vars

	Try {
		val config = ConfigFactory.load().getConfig("clif")
		val host = opts.getOrElse("host", config.getString("host"))
		val port = opts.get("port").map(_.toInt).getOrElse(config.getInt("port"))
		(host, port)
	} match

		case Failure(exception) =>
			throw new IllegalStateException("Cannot parse application.conf", exception)

		case Success((host, port)) =>

			// create service handler
			val service: HttpRequest => Future[HttpResponse] =
				FlashcardServiceHandler(new FlashcardServiceImpl(new InJarRepository(new JSON4SReader)))

			// bind it to the host and port
			val binding = Http().newServerAt(host, port).bind(service)

			// report successful binding
			binding.foreach { binding => println(s"gRPC server bound to: ${binding.localAddress}") }
			println(s"Server now online. Press RETURN to stop...")
			StdIn.readLine() // let it run until user presses return

			binding
				.flatMap(_.unbind()) // trigger unbinding from the port
				.onComplete(_ => system.terminate()) // and shutdown when done