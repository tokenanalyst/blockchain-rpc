lazy val commonSettings = Seq(
  organization := "io.tokenanalyst",
  version := "1.21.0",
  scalaVersion := "2.12.10",
  description := "bitcoin-rpc")

lazy val bitcoinrpc = (project in file(".")).
  settings(commonSettings: _*).
  settings(
    libraryDependencies ++= http4s ++ json ++ zmq ++ cats
  )

val workaround = {
  sys.props += "packaging.type" -> "jar"
  ()
}

publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value)
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases" at nexus + "service/local/staging/deploy/maven2")
}

publishMavenStyle := true

publishArtifact in Test := false

pomIncludeRepository := { _ => false }

pomExtra :=
  <url>https://github.com/tokenanalyst/bitcoin-rpc</url>
    <licenses>
      <license>
        <name>Apache License Version 2.0</name>
        <url>http://www.apache.org/licenses/LICENSE-2.0</url>
        <distribution>repo</distribution>
      </license>
    </licenses>
    <developers>
      <developer>
        <id>jpzk</id>
        <name>Jendrik Poloczek</name>
        <url>https://www.madewithtea.com</url>
      </developer>
      <developer>
        <id>CesarPantoja</id>
        <name>Cesar Pantoja</name>
      </developer>
    </developers>


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

lazy val zmq = Seq (
  "org.zeromq" % "jeromq" % "0.5.1"
)

lazy val cats = Seq (
  "org.typelevel" %% "cats-effect" % "2.0.0"
)

/*
micrositeName := "bitcoin-rpc"
micrositeDescription := "The functional Bitcoin RPC client"
micrositeUrl := "https://github.com/tokenanalyst/bitcoin-rpc"
micrositeDocumentationUrl := "/docs"
micrositeGitterChannel := false
micrositeDocumentationLabelDescription := "Documentation"
micrositeDataDirectory := (resourceDirectory in Compile).value / "docs" / "data"
micrositeGithubOwner := "tokenanalyst"
micrositeGithubRepo := "bitcoin-rpc"
micrositeAuthor := "Jendrik Poloczek"
micrositeTwitter := "@thetokenanalyst"
micrositeTwitterCreator := "@thetokenanalyst"
micrositeCompilingDocsTool := WithMdoc
micrositeShareOnSocial := true

lazy val docs = project       // new documentation project
  .in(file("ms-docs")) // important: it must not be docs/
  .dependsOn(bitcoinrpc)
  .enablePlugins(MdocPlugin)

enablePlugins(MicrositesPlugin)
*/