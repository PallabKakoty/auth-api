name := """auth-api"""
organization := "personal"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.11"

//libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "2.0.0" % Test

val jacksonVersion = "2.9.4"

libraryDependencies ++= Seq(
  ws,
  "mysql" % "mysql-connector-java" % "5.1.24",
  "com.typesafe.slick" %% "slick" % "3.2.0",
  "com.typesafe.play" %% "play-slick" % "2.1.0",
  "com.github.tototoshi" %% "slick-joda-mapper" % "2.3.0",
  "org.scalatestplus.play" %% "scalatestplus-play" % "2.0.0" % Test,
  "com.typesafe.akka" % "akka-actor_2.11" % "2.4.18",
  "com.enragedginger" %% "akka-quartz-scheduler" % "1.6.0-akka-2.4.x",
  "com.typesafe.play" %% "play-mailer" % "6.0.1",
  "com.typesafe.play" %% "play-mailer-guice" % "6.0.1"
)

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "personal.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "personal.binders._"
