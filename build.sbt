libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.1.4",
  "com.typesafe.akka" %% "akka-remote" % "2.1.4"
)

scalaVersion := "2.10.2"

fork in run := true
