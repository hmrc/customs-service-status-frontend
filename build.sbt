import play.sbt.PlayImport.PlayKeys.playDefaultPort
import scoverage.ScoverageKeys
import uk.gov.hmrc.DefaultBuildSettings
import uk.gov.hmrc.DefaultBuildSettings.{integrationTestSettings, scalaSettings}
import sbt.Keys.evictionErrorLevel

val appName         = "customs-service-status-frontend"

lazy val microservice = Project("customs-service-status-frontend", file("."))
  .enablePlugins(play.sbt.PlayScala, SbtDistributablesPlugin)
  .settings(
    majorVersion        := 0,
    ScoverageKeys.coverageExcludedFiles :=
      "<empty>;com.kenshoo.play.metrics.*;.*definition.*;prod.*;testOnlyDoNotUseInAppConf.*;" +
        "app.*;.*BuildInfo.*;.*Routes.*;.*repositories.*;.*controllers.test.*;.*connectors.test.*;.*connectors.analytics.*;.*services.test.*;.*LanguageSwitchController;.*metrics.*;" +
        ".*views.html.*;Reverse.*;.*models.api.GoodsMovementRecordData;.*models.api.metadata.*",
    ScoverageKeys.coverageMinimumStmtTotal := 80,
    ScoverageKeys.coverageFailOnMinimum := true,
    ScoverageKeys.coverageHighlighting := true,
    playDefaultPort := 8993,
    scalaVersion        := "2.13.10",
    libraryDependencies ++= AppDependencies.compile ++ AppDependencies.test,
    // https://www.scala-lang.org/2021/01/12/configuring-and-suppressing-warnings.html
    // suppress warnings in generated routes files
    scalacOptions += "-Wconf:src=routes/.*:s",
    scalacOptions += "-Wconf:cat=unused-imports&src=html/.*:s",
    pipelineStages := Seq(gzip),
  )
  .configs(IntegrationTest)
  .settings(integrationTestSettings(): _*)
  .settings(resolvers += Resolver.jcenterRepo)
  .settings(CodeCoverageSettings.settings: _*)
  .settings( //fix scaladoc generation in jenkins
      Compile / scalacOptions -= "utf8",
      scalacOptions += "-language:postfixOps"
  )

evictionErrorLevel := Level.Warn
