name := "ingredients"

version := "1.0"

resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

libraryDependencies ++= Seq(
  "org.specs2" %% "specs2" % "2.3.12" % "test",
  "com.typesafe.akka" %% "akka-actor" % "2.3.4",
  "org.apache.commons" % "commons-math" % "2.2")
