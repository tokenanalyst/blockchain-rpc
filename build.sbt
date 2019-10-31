lazy val commonSettings = Seq(
  organization := "io.tokenanalyst",
  version := "1.9.1",
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
    libraryDependencies ++= http4s ++ json ++ zmq ++ log ++ cats
  )

val workaround = {
  sys.props += "packaging.type" -> "jar"
  ()
}

val http4sVersion = "0.20.11"

lazy val http4s = Seq(
  "org.http4s" %% "http4s-dsl" % http4sVersion,
  "org.http4s" %% "http4s-blaze-server" % http4sVersion,
  "org.http4s" %% "http4s-blaze-client" % http4sVersion
)

lazy val json = Seq(
  "org.http4s" %% "http4s-circe" % http4sVersion,
  "io.circe" %% "circe-generic" % "0.11.1",
  "io.circe" %% "circe-literal" % "0.11.1",
  "io.circe" %% "circe-parser" % "0.11.1"
)

lazy val log = Seq(
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "net.logstash.logback" % "logstash-logback-encoder" % "4.11",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.0"
)

lazy val zmq = Seq (
  "org.zeromq" % "jeromq" % "0.5.1"
)

lazy val cats = Seq (
  "org.typelevel" %% "cats-effect" % "2.0.0"
)
