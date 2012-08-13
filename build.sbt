name := "ParallelAkkaTest"

scalaVersion := "2.9.2"

resolvers ++= Seq(
  "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases"
)

libraryDependencies ++= Seq(
  "com.typesafe.akka" % "akka-actor" % "2.0.2" % "compile",
  "com.typesafe.akka" % "akka-slf4j" % "2.0.2" % "compile",
  "ch.qos.logback" % "logback-classic" % "1.0.0"
)
