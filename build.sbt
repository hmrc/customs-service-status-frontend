import play.sbt.PlayImport.PlayKeys.playDefaultPort
import uk.gov.hmrc.DefaultBuildSettings.integrationTestSettings
import sbt.Keys.evictionErrorLevel

val appName = "customs-service-status-frontend"

lazy val microservice = Project(appName, file("."))
  .enablePlugins(play.sbt.PlayScala, SbtDistributablesPlugin)
  .disablePlugins(JUnitXmlReportPlugin)
  .settings(
    majorVersion := 0,
    playDefaultPort := 8993,
    scalaVersion := "3.3.3",
    libraryDependencies ++= AppDependencies.compile ++ AppDependencies.test,
    scalafmtOnCompile := true,
    pipelineStages := Seq(gzip)
  )
  .configs(IntegrationTest)
  .settings(integrationTestSettings(): _*)
  .settings(CodeCoverageSettings.settings: _*)
  .settings( //fix scaladoc generation in jenkins
    scalacOptions += "-language:postfixOps",
    scalacOptions += "-no-indent"
  )
  .settings(
    addCommandAlias("runTestOnly", "run -Dplay.http.router=testOnlyDoNotUseInAppConf.Routes")
  )

evictionErrorLevel := Level.Warn
