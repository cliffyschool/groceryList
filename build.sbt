name := "groceryList"

version := "1.0"

val akkaVersion = "2.3.4"
val sprayVersion = "1.3.1"

resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

libraryDependencies ++= Seq(
  "org.specs2" %% "specs2" % "2.3.12" % "test",
  "com.typesafe.akka" %% "akka-actor" % akkaVersion
      exclude ("org.scala-lang" , "scala-library"),
 "com.typesafe.akka" %% "akka-testkit" % akkaVersion % "test",
   "io.spray" % "spray-testkit" % sprayVersion % "test",
  "io.spray" % "spray-can" % sprayVersion,
  "io.spray" % "spray-routing" % sprayVersion,
  "io.spray" %% "spray-json" % "1.2.5" exclude ("org.scala-lang" , "scala-library"),
  "org.apache.commons" % "commons-math" % "2.2")
