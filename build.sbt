name := "actorsys"

version := "1.0"

scalaVersion := "2.12.1"
lazy val akkaVersion = "2.4.17"
libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-agent" % akkaVersion,
  "com.typesafe.akka" %% "akka-camel" % akkaVersion,
  "com.typesafe.akka" %% "akka-cluster" % akkaVersion,
  "com.typesafe.akka" %% "akka-cluster-metrics" % akkaVersion,
  "com.typesafe.akka" %% "akka-cluster-sharding" % akkaVersion,
  "com.typesafe.akka" %% "akka-remote" % akkaVersion,
  "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
  "com.typesafe.akka" %% "akka-stream" % akkaVersion,
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion,
  "com.typesafe.akka" %% "akka-http-core" % "10.0.3",
  "com.typesafe.akka" %% "akka-http" % "10.0.3",
  "com.typesafe.akka" %% "akka-http-spray-json" % "10.0.3",
  "com.typesafe.akka" %% "akka-protobuf" % "2.4.17",
  "com.typesafe.slick" % "slick_2.11" % "3.1.1",
  "com.oracle" % "ojdbc6" % "11.2.0.3",
  "org.apache.logging.log4j" % "log4j-api" % "2.7",
  "org.apache.logging.log4j" % "log4j-core" % "2.7",
  "org.apache.logging.log4j" % "log4j-slf4j-impl" % "2.7",
  "com.novocode" % "junit-interface" % "0.11" % "test"
)

testOptions += Tests.Argument(TestFrameworks.JUnit, "-v")
    