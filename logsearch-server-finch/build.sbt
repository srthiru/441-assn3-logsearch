name := "finch-trial"

version := "0.1"

scalaVersion := "2.12.9"


lazy val typesafeConfigVersion = "1.4.1"
lazy val scalaTestVersion = "3.2.9"

libraryDependencies ++= Seq(

  "com.github.finagle" %% "finchx-core" % "0.31.0",
  "com.github.finagle" %% "finchx-circe" % "0.31.0",
  "org.scalaj" %% "scalaj-http" % "2.4.2",
  "com.typesafe" % "config" % typesafeConfigVersion,
  "org.scalatest" %% "scalatest" % scalaTestVersion % Test,

  "io.circe" %% "circe-generic" % "0.9.0",
  "org.typelevel" %% "cats-effect" % "2.1.3",
  "org.typelevel" %% "cats-core" % "2.1.1",
  "org.xerial" % "sqlite-jdbc" % "3.31.1",
  "org.tpolecat" %% "doobie-core" % "0.8.8",

)