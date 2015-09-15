name := """finatra"""

organization := "com.example"

version := "1.0.0-SNAPSHOT"

scalaVersion := "2.11.7"

fork in run := true

javaOptions ++= Seq(
  "-Dlog.service.output=/dev/stderr",
  "-Dlog.access.output=/dev/stderr")

resolvers ++= Seq(
  Resolver.sonatypeRepo("releases"),
  "Twitter Maven" at "http://maven.twttr.com",
  "Finatra Repo" at "http://twitter.github.com/finatra"
)

lazy val versions = new {
  val finagle = "6.28.0"
  val finatra = "2.0.0.RC1"
  val logback = "1.0.13"
  val mockito = "1.9.5"
  val scalatest = "2.2.3"
  val specs2 = "2.3.12"
  val jackson = "2.6.1"
  val slick = "3.0.3"
}

//aopalliance was being included from somewhere else, but wasn't found in IntelliJ without this
//(though it worked from command line for some reason)
libraryDependencies ++= Seq(
  "aopalliance" % "aopalliance" % "1.0",

  "ch.qos.logback" % "logback-classic" % versions.logback,
  "ch.qos.logback" % "logback-classic" % versions.logback % "test",

  "com.twitter.finatra" %% "finatra-http" % versions.finatra,
  "com.twitter.finatra" %% "finatra-slf4j" % versions.finatra,

  "com.twitter.finatra" %% "finatra-http" % versions.finatra % "test",
  "com.twitter.inject" %% "inject-server" % versions.finatra % "test",
  "com.twitter.inject" %% "inject-app" % versions.finatra % "test",
  "com.twitter.inject" %% "inject-core" % versions.finatra % "test",
  "com.twitter.inject" %% "inject-modules" % versions.finatra % "test",
  "com.twitter.finatra" %% "finatra-http" % versions.finatra % "test" classifier "tests",
  "com.twitter.inject" %% "inject-server" % versions.finatra % "test" classifier "tests",
  "com.twitter.inject" %% "inject-app" % versions.finatra % "test" classifier "tests",
  "com.twitter.inject" %% "inject-core" % versions.finatra % "test" classifier "tests",
  "com.twitter.inject" %% "inject-modules" % versions.finatra % "test" classifier "tests",

  "org.mockito" % "mockito-core" % versions.mockito % "test",
  "org.scalatest" %% "scalatest" % versions.scalatest % "test",
  "org.specs2" %% "specs2" % versions.specs2 % "test",

  "com.typesafe.slick" %% "slick" % versions.slick,
  "com.typesafe.slick" %% "slick-codegen" % versions.slick,
  "org.slf4j" % "slf4j-nop" % "1.6.4",
  "com.h2database" % "h2" % "1.3.175",

//  "com.fasterxml.jackson.core" % "jackson-core" % "2.5.3",
//  "com.fasterxml.jackson.core" % "jackson-databind" % "2.5.3",
//  "com.fasterxml.jackson.module" % "jackson-module-scala_2.11" % "2.5.3"

  "org.scalaj" % "scalaj-http_2.11" % "1.1.5",

  "com.fasterxml.jackson.core" % "jackson-core" % versions.jackson,
  "com.fasterxml.jackson.core" % "jackson-databind" % versions.jackson,
  "com.fasterxml.jackson.core" % "jackson-annotations" % versions.jackson,
  "com.fasterxml.jackson.module" % "jackson-module-scala_2.11" % versions.jackson

/*
  "com.fasterxml.jackson.core" % "jackson-core" % "2.6.1",
  "com.fasterxml.jackson.core" % "jackson-databind" % "2.6.1",
  //"com.fasterxml.jackson.datatype" % "jackson-datatype-joda" % "2.6.1",
  //"com.fasterxml.jackson.core" % "jackson-annotations" % "2.6.1",
  "com.fasterxml.jackson.module" % "jackson-module-scala_2.11" % "2.6.1"
  */
)
