name := "ParallelAkkaTest"

scalaVersion := "2.10.0-M6"

resolvers ++= Seq(
  "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases",
  "Typesafe Repository snaphosts" at "http://repo.typesafe.com/typesafe/snapshots"
)

libraryDependencies ++= Seq(
  "com.typesafe.akka" % "akka-actor" % "2.1-M1" % "compile",
  "com.typesafe.akka" % "akka-slf4j" % "2.1-M1" % "compile",
  "ch.qos.logback" % "logback-classic" % "1.0.0"
)
