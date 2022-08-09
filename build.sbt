ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.1.3"

lazy val root = (project in file("."))
  .settings(
    name := "cli-flashcards",
    idePackagePrefix := Some("org.clif")
  )

libraryDependencies += "org.scalactic" %% "scalactic" % "3.2.13"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.13" % "test"
libraryDependencies += "org.jline" % "jline" % "3.21.0"
libraryDependencies += "com.lihaoyi" %% "upickle" % "2.0.0"