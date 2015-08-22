lazy val root = (project in file(".")).
  settings(
    name := "ludum-dare-33",
    libraryDependencies += "io.dylemma" %% "scala-frp" % "1.1",
    libraryDependencies += "org.streum" %% "configrity-core" % "1.0.0"
  )
