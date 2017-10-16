val NAME: String = "nowfloats-channel-api"
val VERSION: String = "5.0.1"
val ORGANIZATION: String = "com.insuranceinbox"
val PORT: Int = 10080
val ECR_REPOSITORY: String = s"registry.insuranceinbox.com"

name := NAME
version := VERSION
organization := ORGANIZATION
scalaVersion := "2.11.11"

val prepareProductionTask = TaskKey[Unit]("iprepare", "prepare for production deploy")

lazy val root = (project in file(".")).enablePlugins(PlayScala)
  .settings(prepareProductionTask := {
    val s: TaskStreams = streams.value
    s.log.info(s"executing application.ini generation script")
    val envFile = baseDirectory.value / "conf" / "application.ini"
    IO.write(envFile,
      s"""
         |-Dhttp.port=$PORT
         |-Dconfig.resource=env/application.env.conf
         |""".stripMargin)
  })
libraryDependencies += guice
libraryDependencies += ws
libraryDependencies += filters
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.0" % Test
libraryDependencies ++= Seq("org.reactivemongo" %% "play2-reactivemongo" % "0.12.6-play26")
libraryDependencies += "org.reactivemongo" %% "reactivemongo-play-json" % "0.11.14"

PlayKeys.devSettings := Seq("play.server.http.port" -> s"$PORT")
PlayKeys.playDefaultPort := PORT

//Dockerfile Details
maintainer in Docker := "Chandan Pasunoori <chandan@insuranceinbox.com>"
packageName in Docker := NAME
version in Docker := scala.util.Properties.envOrElse("CI_PIPELINE_ID", "latest")
dockerExposedPorts := Seq(PORT)
dockerExposedVolumes := Seq("conf/", "logs/", "share/")
dockerRepository := Some(s"$ECR_REPOSITORY")

resolvers += "Artifactory-Sbt" at "https://artifactory.insuranceinbox.com/artifactory/inbox-all-sbt/"

libraryDependencies += "com.typesafe.akka" %% "akka-stream-kafka" % "0.13"
libraryDependencies += "mysql" % "mysql-connector-java" % "5.1.40"
libraryDependencies += "com.typesafe.play" %% "play-slick" % "3.0.1"
libraryDependencies +="com.typesafe.play" %% "play-slick-evolutions" % "3.0.1"

