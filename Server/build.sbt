name := "RFID"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  javaJdbc,
  javaEbean,
  cache
)    

libraryDependencies += "mysql" % "mysql-connector-java" % "5.1.29"

libraryDependencies += "org.mindrot" % "jbcrypt" % "0.3m"

play.Project.playJavaSettings
