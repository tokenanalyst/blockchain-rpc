lazy val commonSettings = Seq(
  organization := "io.tokenanalyst",
  version := "2.5.2",
  scalaVersion := "2.13.1",
  crossScalaVersions := Seq("2.13.1", "2.12.10"),
  organizationHomepage := Some(
    url("https://github.com/tokenanalyst/blockchain-rpc")
  ),
  description := "JSON RPC client for Bitcoin, Bitcoin-based, and Ethereum nodes"
)

lazy val `blockchain-rpc` = (project in file("."))
  .settings(commonSettings: _*)
  .settings(
    assemblyJarName in assembly := "blockchain-rpc.jar",
    publishMavenStyle := false,
    publishTo := {
      val nexus = "https://oss.sonatype.org/"
      if (isSnapshot.value)
        Some("snapshots" at nexus + "content/repositories/snapshots")
      else
        Some("releases" at nexus + "service/local/staging/deploy/maven2")
    }
  )
  .settings(
    libraryDependencies ++= Seq(
      "commons-codec" % "commons-codec" % "1.13",
      "com.typesafe.akka" %% "akka-actor" % "2.6.1"
    ) ++ http4s ++ json ++ zmq ++ cats ++ scalaTest
  )

publishMavenStyle := true

publishArtifact in Test := false

pomIncludeRepository := { _ =>
  false
}

scmInfo := Some(
  ScmInfo(
    url("https://github.com/tokenanalyst/blockchain-rpc"),
    "scm:git@github.com:tokenanalyst/blockchain-rpc.git"
  )
)

pomExtra :=
  <url>https://github.com/tokenanalyst/blockchain-rpc</url>
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
        <url>https://twitter.com/chpanto</url>
      </developer>
    </developers>

val http4sVersion = "0.21.0-M5"
val circeVersion = "0.12.0-M4"
val scalaTestVersion = "3.1.0"

lazy val http4s = Seq(
  "org.http4s" %% "http4s-dsl" % http4sVersion,
  "org.http4s" %% "http4s-blaze-server" % http4sVersion,
  "org.http4s" %% "http4s-blaze-client" % http4sVersion
)

lazy val json = Seq(
  "org.http4s" %% "http4s-circe" % http4sVersion,
  "io.circe" %% "circe-generic" % circeVersion,
  "io.circe" %% "circe-literal" % circeVersion,
  "io.circe" %% "circe-parser" % circeVersion
)
lazy val scalaTest = Seq(
  "org.scalatest" %% "scalatest" % scalaTestVersion % "test",
)

lazy val zmq = Seq(
  "org.zeromq" % "jeromq" % "0.5.1"
)

lazy val cats = Seq(
  "org.typelevel" %% "cats-effect" % "2.0.0"
)
