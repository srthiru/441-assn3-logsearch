name := "logsearch-service"

version := "0.1"

scalaVersion := "2.13.6"

mainClass := Some("LogSearcherService")

lazy val akkaVersion = "2.6.17"
lazy val akkaHttpVersion = "10.2.6"
lazy val akkaGrpcVersion = "2.1.0"

lazy val sfl4sVersion = "2.0.0-alpha5"
lazy val typesafeConfigVersion = "1.4.1"
lazy val logBackVersion = "1.2.6"
lazy val scalaTestVersion = "3.2.9"

val json4sNative = "org.json4s" %% "json4s-native" % "4.0.2"

lazy val scalaPBCompilerVersion = "0.11.6"

lazy val awsCoreVersion = "1.2.1"
lazy val awsEventsVersion = "3.10.0"

assembly / assemblyMergeStrategy := {
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case x => MergeStrategy.first
}

Compile / PB.targets := Seq(
  scalapb.gen() -> (Compile / sourceManaged).value
)

libraryDependencies ++= Seq(
  "ch.qos.logback" % "logback-classic" % logBackVersion,
  "org.slf4j" % "slf4j-api" % sfl4sVersion,
  "com.typesafe" % "config" % typesafeConfigVersion,
  "org.scalatest" %% "scalatest" % scalaTestVersion % Test,

  "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-http2-support" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-actor-typed" % akkaVersion,
  "com.typesafe.akka" %% "akka-stream" % akkaVersion,
  "com.typesafe.akka" %% "akka-discovery" % akkaVersion,
  "com.typesafe.akka" %% "akka-pki" % akkaVersion,

  "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-http2-support" % akkaHttpVersion,

  "com.typesafe.akka" %% "akka-actor-testkit-typed" % akkaVersion % Test,
  "com.typesafe.akka" %% "akka-stream-testkit" % akkaVersion % Test,

  "com.thesamet.scalapb" %% "compilerplugin" % scalaPBCompilerVersion,
  "io.grpc" % "grpc-netty" % scalapb.compiler.Version.grpcJavaVersion,
  "com.thesamet.scalapb" %% "scalapb-runtime-grpc" % scalapb.compiler.Version.scalapbVersion,
  "com.thesamet.scalapb" %% "scalapb-runtime" % scalapb.compiler.Version.scalapbVersion % "protobuf",

  "com.amazonaws" % "aws-lambda-java-core" % awsCoreVersion,
  "com.amazonaws" % "aws-lambda-java-events" % awsEventsVersion,
  json4sNative

)