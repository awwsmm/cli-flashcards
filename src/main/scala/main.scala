package org.clif

import json.JSON4SReader
import org.slf4j.{Logger, LoggerFactory}
import repository.InJarRepository

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import com.typesafe.config.ConfigFactory

import scala.concurrent.ExecutionContext
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
			val grpcService = new FlashcardServiceImpl(new InJarRepository(new JSON4SReader))
			log.info(s"Starting gRPC server at $host:$port...")
			FlashcardServer.start(host, port, system, grpcService)

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
