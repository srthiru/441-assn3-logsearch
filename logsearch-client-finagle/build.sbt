name := "logsearch-rest-client"

version := "0.1"

scalaVersion := "2.13.6"

mainClass := Some("RestClient")

lazy val sfl4sVersion = "2.0.0-alpha5"
lazy val typesafeConfigVersion = "1.4.1"
lazy val logBackVersion = "1.2.6"
lazy val scalaTestVersion = "3.2.9"

lazy val akkaHttpVersion = "10.2.6"
lazy val akkaVersion = "2.6.17"

val scalaJHTTPVersion = "2.4.2"

val finagleVersion = "21.10.0"

libraryDependencies ++= Seq(
  "ch.qos.logback" % "logback-classic" % logBackVersion,
  "org.slf4j" % "slf4j-api" % sfl4sVersion,
  "com.typesafe" % "config" % typesafeConfigVersion,
  "org.scalatest" %% "scalatest" % scalaTestVersion % Test,

  "org.scalaj" %% "scalaj-http" % scalaJHTTPVersion,
  "com.thesamet.scalapb" %% "scalapb-json4s" % "0.11.1",
  "com.google.code.gson" % "gson" % "2.8.8",

  "com.twitter" %% "finagle-http" % finagleVersion,
)