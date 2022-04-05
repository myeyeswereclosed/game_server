name := "game_server"

version := "0.1"

scalaVersion := "2.13.8"

val Http4sVersion  = "0.23.11"
val LogbackVersion = "1.2.3"

val circeVersion = "0.14.1"

val enumeratumCirceVersion = "1.7.0"

val scalaTestVersion = "3.2.11"

val pureConfigVersion = "0.17.1"

libraryDependencies ++= Seq(
  "org.http4s"     %% "http4s-blaze-server" % Http4sVersion,
  "org.http4s"     %% "http4s-dsl"          % Http4sVersion,
  "ch.qos.logback" % "logback-classic"      % LogbackVersion
)

libraryDependencies ++= Seq(
    "io.circe" %% "circe-core",
    "io.circe" %% "circe-generic",
    "io.circe" %% "circe-parser"
  ).map(_ % circeVersion)

libraryDependencies += "com.beachape" %% "enumeratum-circe" % enumeratumCirceVersion

libraryDependencies += "org.scalatest" %% "scalatest" % scalaTestVersion % Test

libraryDependencies += "com.github.pureconfig" %% "pureconfig" % pureConfigVersion

scalacOptions ++= Seq(
    "-deprecation",
    "-language:higherKinds",
    "-language:postfixOps",
    "-feature",
    "-Xfatal-warnings"
)

