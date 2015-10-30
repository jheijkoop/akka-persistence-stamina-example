name := "Akka Persistence Stamina Demo"
scalaVersion := "2.11.7"

resolvers += "Sonatype Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"

libraryDependencies ++= Seq(
  "org.iq80.leveldb" % "leveldb" % "0.7",
  "org.fusesource.leveldbjni" % "leveldbjni-all" % "1.8",
  "com.typesafe.akka" %% "akka-persistence" % "2.4.0",
  "com.github.fommil" %% "spray-json-shapeless" % "1.1.0",
  "com.scalapenos" %% "stamina-json" % "0.1.1-SNAPSHOT"
)
