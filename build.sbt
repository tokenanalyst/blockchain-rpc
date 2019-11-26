lazy val commonSettings = Seq(
  organization := "io.tokenanalyst",
  version := "2.3.0",
  scalaVersion := "2.12.10",
  description := "bitcoin-rpc")

lazy val bitcoinrpc = (project in file(".")).
  settings(commonSettings: _*).
  settings(
    assemblyJarName in assembly := "bitcoin-rpc.jar",
    publishMavenStyle := false,
    publishTo := Some(Resolver.url("TA-S3", url("s3://ivy-jar-repository-ta/"))(Resolver.ivyStylePatterns))
  ).
  settings(
    libraryDependencies ++= http4s ++ json ++ zmq ++ cats 
  )

val circeVersion = "0.12.3"
val http4sVersion = "0.21.0-M5"

lazy val http4s = Seq(
  "org.http4s" %% "http4s-dsl" % http4sVersion,
  "org.http4s" %% "http4s-blaze-server" % http4sVersion,
  "org.http4s" %% "http4s-blaze-client" % http4sVersion,
  "org.http4s" %% "http4s-prometheus-metrics" % http4sVersion,
  "org.http4s" %% "http4s-circe" % http4sVersion,
)

lazy val json = Seq(
  "io.circe" %% "circe-generic" % circeVersion,
  "io.circe" %% "circe-literal" % circeVersion,
  "io.circe" %% "circe-parser" % circeVersion,
)

lazy val zmq = Seq (
  "org.zeromq" % "jeromq" % "0.5.1"
)

lazy val cats = Seq (
  "org.typelevel" %% "cats-effect" % "2.0.0"
)

