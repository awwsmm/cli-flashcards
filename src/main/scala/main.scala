package org.clif

import json.JSON4SReader
import org.slf4j.{Logger, LoggerFactory}
import repository.InJarRepository

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.grpc.scaladsl.ServiceHandler
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import com.typesafe.config.ConfigFactory

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

@main def entrypoint(args: String*): Unit =

	val opts = parseArgs(args)

	val log: Logger = LoggerFactory.getLogger(getClass)

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



      // TODO new below

			val grpcService = new FlashcardServiceImpl(new InJarRepository(new JSON4SReader))

			log.info(s"Starting server at $host:$port...")
			FlashcardServer.start(host, port, system, grpcService)

//			val f = for {
//				bindingFuture <- Http().newServerAt(host, port).bind(service)
//				waitOnFuture  <- Future.never
//			} yield waitOnFuture
//
//			sys.addShutdownHook {
//				println("GOODBYE!")
//			}
//
//			Await.ready(f, Duration.Inf)

			// TODO remove new test (below)

//			val binding = Http().newServerAt(host, port).bind(service)
//
//			binding.onComplete {
//				case Success(_) => println("Success!")
//				case Failure(error) => println(s"Failed: ${error.getMessage}")
//			}
//
//			import scala.concurrent.duration._
//			Await.result(binding, 3.seconds)

			// TODO remove old below (new above)

//			// bind it to the host and port
//			val binding = Http().newServerAt(host, port).bind(service)
//
//			// report successful binding
//			binding.foreach { binding => println(s"gRPC server bound to: ${binding.localAddress}") }
//			println(s"Server now online. Press RETURN to stop...")
//			StdIn.readLine() // let it run until user presses return
//
//			binding
//				.flatMap(_.unbind()) // trigger unbinding from the port
//				.onComplete(_ => system.terminate()) // and shutdown when done

// simple command-line argument parser
def parseArgs(args: Seq[String]): Map[String, String] =

	def hostParser(str: String): Option[String] =
		val HostPattern = "(--host|-h)=(.+)".r
		str match
			case HostPattern(_, host) => Some(host)
			case _ => None

	def portParser(str: String): Option[Int] =
		val PortPattern = "(--port|-p)=([1-9]\\d{1,4})".r
		str match
			case PortPattern(_, portStr) => Some(portStr.toInt)
			case _ => None

	def parse(str: String): Option[(String, String)] =
		hostParser(str).map("host" -> _).orElse(
			portParser(str).map("port" -> _.toString))

	args.flatMap(parse).toMap
