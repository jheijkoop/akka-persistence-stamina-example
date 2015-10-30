name := "Akka Persistence Stamina Demo"
scalaVersion := "2.11.7"

resolvers += "Sonatype Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-persistence-experimental" % "2.3.11",
  "com.github.fommil" %% "spray-json-shapeless" % "1.1.0",
  "com.scalapenos" %% "stamina-json" % "0.1.1-SNAPSHOT"
)
