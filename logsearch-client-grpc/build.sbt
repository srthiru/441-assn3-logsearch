name := "client_grpc"

version := "0.1"

scalaVersion := "2.13.6"

lazy val sfl4sVersion = "2.0.0-alpha5"
lazy val typesafeConfigVersion = "1.4.1"
lazy val logBackVersion = "1.2.6"
lazy val scalaTestVersion = "3.2.9"

Compile / PB.targets := Seq(
  scalapb.gen() -> (Compile / sourceManaged).value / "scalapb"
)

mainClass / run := Some("SearchClient")

libraryDependencies ++= Seq(
  "ch.qos.logback" % "logback-classic" % logBackVersion,
  "org.slf4j" % "slf4j-api" % sfl4sVersion,
  "com.typesafe" % "config" % typesafeConfigVersion,
  "org.scalatest" %% "scalatest" % scalaTestVersion % Test,

  "io.grpc" % "grpc-netty" % scalapb.compiler.Version.grpcJavaVersion,
  "com.thesamet.scalapb" %% "scalapb-runtime-grpc" % scalapb.compiler.Version.scalapbVersion,
  "com.thesamet.scalapb" %% "scalapb-runtime" % scalapb.compiler.Version.scalapbVersion % "protobuf"
)