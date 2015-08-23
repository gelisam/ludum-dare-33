lazy val root = (project in file(".")).
  settings(
    name := "ludum-dare-33",
    libraryDependencies += "org.streum" %% "configrity-core" % "1.0.0"
  )
