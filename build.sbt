lazy val commonSettings = Seq(
  organization := "tokenanalyst",
  version := "1.0.0",
  scalaVersion := "2.12.10",
  description := "json-rpc")

lazy val core = (project in file(".")).
  settings(commonSettings: _*).
  settings(
    libraryDependencies ++= http4s ++ json 
  )

val workaround = {
  sys.props += "packaging.type" -> "jar"
  ()
}

lazy val json = Seq(
  "org.http4s" %% "http4s-circe" % http4sVersion,
  "io.circe" %% "circe-generic" % "0.11.1",
  "io.circe" %% "circe-literal" % "0.11.1",
)

val http4sVersion = "0.20.11"

lazy val http4s = Seq(
  "org.http4s" %% "http4s-dsl" % http4sVersion,
  "org.http4s" %% "http4s-blaze-server" % http4sVersion,
  "org.http4s" %% "http4s-blaze-client" % http4sVersion
)
