name := "groceryList"

scalaVersion := "2.11.4"

version := "1.0"

val akkaVersion = "2.3.4"
val sprayVersion = "1.3.2"

resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

libraryDependencies ++= Seq(
  "org.specs2" %% "specs2" % "2.3.12" % "test",
  "com.typesafe.akka" %% "akka-actor" % akkaVersion
      exclude ("org.scala-lang" , "scala-library"),
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion % "test",
  "com.typesafe.akka" %% "akka-contrib" % akkaVersion,
  "io.spray" %% "spray-testkit" % sprayVersion % "test",
  "org.mockito" % "mockito-core" % "1.8.5" % "test",
  "io.spray" %% "spray-can" % sprayVersion,
  "io.spray" %% "spray-routing" % sprayVersion,
  "org.json4s"  %% "json4s-native" % "3.2.9",
  "io.spray" %% "spray-json" % "1.3.1" exclude ("org.scala-lang" , "scala-library"),
  "org.apache.commons" % "commons-math" % "2.2")
