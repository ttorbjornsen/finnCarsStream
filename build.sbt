name := "finnCarsStream"

version := "1.0"

scalaVersion := "2.10.5"

resolvers += "Typesafe Repo" at "http://repo.typesafe.com/typesafe/releases/"

libraryDependencies += "org.jsoup" % "jsoup" % "1.7.3"
libraryDependencies += "org.apache.kafka" %% "kafka" % "0.8.2.0"
libraryDependencies += "com.typesafe.play" %% "play-json" % "2.2.1" % "provided"
libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.6" % "provided"

