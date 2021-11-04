name := "logsearch-lambda"

version := "0.1"

scalaVersion := "2.13.7"

mainClass in (Compile, run) := Some("LogSearcher")

lazy val sfl4sVersion = "2.0.0-alpha5"
lazy val typesafeConfigVersion = "1.4.1"
lazy val logBackVersion = "1.2.6"
lazy val scalaTestVersion = "3.2.9"

lazy val awsCoreVersion = "1.2.1"
lazy val awsEventsVersion = "3.10.0"

val json4sNativeVersion = "4.0.2"

assembly / assemblyMergeStrategy := {
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case x => MergeStrategy.first
}

libraryDependencies ++= Seq(
  "ch.qos.logback" % "logback-classic" % logBackVersion,
  "org.slf4j" % "slf4j-api" % sfl4sVersion,
  "com.typesafe" % "config" % typesafeConfigVersion,
  "org.scalatest" %% "scalatest" % scalaTestVersion % Test,

  "org.json4s" %% "json4s-native" % json4sNativeVersion,

  "com.amazonaws" % "aws-lambda-java-core" % awsCoreVersion,
  "com.amazonaws" % "aws-lambda-java-events" % awsEventsVersion,
  "com.macasaet.fernet" % "fernet-java" % "1.5.0"
)