import sbt.{Build, Project, ProjectRef, uri}

object StaminaDemoBuild extends Build {
  lazy val root = Project("root", sbt.file(".")).dependsOn(staminaCore, staminaJson)

  lazy val staminaCore = ProjectRef(uri("git://github.com/scalapenos/stamina.git#master"), "stamina-core")
  lazy val staminaJson = ProjectRef(uri("git://github.com/scalapenos/stamina.git#master"), "stamina-json")
}