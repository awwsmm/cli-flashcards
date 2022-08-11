ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.1.3"

lazy val root = (project in file("."))
  .settings(
    name := "cli-flashcards",
    idePackagePrefix := Some("org.clif")
  )

val AkkaVersion = "2.6.19"
val AkkaHttpVersion = "10.2.9"

libraryDependencies ++= Seq(
  ("com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion).cross(CrossVersion.for3Use2_13),
  ("com.typesafe.akka" %% "akka-stream" % AkkaVersion).cross(CrossVersion.for3Use2_13),
  ("com.typesafe.akka" %% "akka-http" % AkkaHttpVersion).cross(CrossVersion.for3Use2_13)
)

enablePlugins(AkkaGrpcPlugin)

excludeDependencies ++= Seq(
  "com.thesamet.scalapb"   % "scalapb-runtime_2.13",
  "org.scala-lang.modules" % "scala-collection-compat_2.13"
)