import sbt._
import sbt.Keys._
import com.typesafe.sbt.SbtNativePackager.autoImport._
import com.typesafe.sbt.packager.archetypes.JavaAppPackaging
import com.typesafe.sbt.packager.docker.DockerPlugin

lazy val common = Seq(
  organization := "com.zenaptix",
  version := "0.1.8",
  scalaVersion := "2.11.7",
  ivyScala := ivyScala.value map {
    _.copy(overrideScalaVersion = true)
  },
  bintrayOrganization := Some("zenaptix"),
  bintrayRepository := "bright",
  licenses += ("Apache-2.0", url("http://www.apache.org/licenses/LICENSE-2.0")),
  resolvers ++= Seq(
    "Local Maven Repository" at "file://"+Path.userHome.absolutePath+"/.m2/repository",
    "scalaz at bintray" at "http://dl.bintray.com/scalaz/releases",
    "krasserm at bintray" at "http://dl.bintray.com/krasserm/maven",
    "zenaptix at bintray" at "http://dl.bintray.com/zenaptix/rx_zmq_streams"
   ),
  libraryDependencies ++= Seq(
    "org.scalacheck" %% "scalacheck" % "1.11.6" % "test",
    "org.scalatest" %% "scalatest" % "2.2.4" % "test",
    "com.typesafe.akka" %% "akka-testkit" % "2.3.11" % "test",
    "com.typesafe.scala-logging" %% "scala-logging" % "3.1.0",
    "com.typesafe.akka" %% "akka-actor" % "2.3.11",
    "com.typesafe.akka" %% "akka-slf4j" % "2.3.11",
    "com.typesafe.akka" %% "akka-agent" % "2.3.11",
    "com.zenaptix" %% "rx_zmq_streams" % "0.1.8",
    "org.json4s" %% "json4s-native" % "3.2.10",
    "ch.qos.logback" % "logback-classic" % "1.0.13",
    "com.github.krasserm" %% "streamz-akka-camel" % "0.3",
    "com.github.krasserm" %% "streamz-akka-persistence" % "0.3",
    "com.github.krasserm" %% "streamz-akka-stream" % "0.3",
    "com.typesafe.akka" %% "akka-stream-experimental" % "1.0-RC4"
  ),
  version := version.value,
  dockerBaseImage := "zenphu/jzmq:latest",
  dockerExposedPorts := Seq(5556, 5557)
)
lazy val root = (project in file(".")).aggregate(core, bright_consumer, bright_producer)

lazy val core = (project in file("core")).settings(common: _*).enablePlugins(JavaAppPackaging, DockerPlugin).
  settings(
    name := "bright"
  )
lazy val bright_consumer = (project in file("consumer")).dependsOn(core).settings(common: _*).enablePlugins(JavaAppPackaging, DockerPlugin).
  settings(
    name := "bright_consumer",
    mainClass in Compile := Some("com.zenaptix.bright.consumer.Consumer"),
    dockerEntrypoint in Docker := Seq("./bin/bright_consumer")
  )
lazy val bright_producer = (project in file("producer")).dependsOn(core).settings(common: _*).enablePlugins(JavaAppPackaging, DockerPlugin).
  settings(
    name := "bright_producer",
    mainClass in Compile := Some("com.zenaptix.bright.producer.Producer"),
    dockerExposedVolumes += "/data",
    dockerEntrypoint in Docker := Seq("./bin/bright_producer")
  )