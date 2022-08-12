ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.1.3"

lazy val root = (project in file("."))
  .settings(
    name := "cli-flashcards",
    idePackagePrefix := Some("org.clif")
  )

scalacOptions := Seq("-unchecked", "-deprecation")

val AkkaVersion = "2.6.19"
val ScalaTestVersion = "3.2.13"

libraryDependencies ++= Seq(
  "com.lihaoyi" %% "upickle" % "2.0.0",
  ("com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion).cross(CrossVersion.for3Use2_13),
  ("com.typesafe.akka" %% "akka-http" % "10.2.9").cross(CrossVersion.for3Use2_13),
  ("com.typesafe.akka" %% "akka-stream" % AkkaVersion).cross(CrossVersion.for3Use2_13),
  "org.json4s" %% "json4s-native" % "4.0.5",
  "org.scalactic" %% "scalactic" % ScalaTestVersion,
  "org.scalatest" %% "scalatest" % ScalaTestVersion % "test"
)

enablePlugins(AkkaGrpcPlugin)

excludeDependencies ++= Seq(
  "com.thesamet.scalapb"   % "scalapb-runtime_2.13",
  "org.scala-lang.modules" % "scala-collection-compat_2.13"
)