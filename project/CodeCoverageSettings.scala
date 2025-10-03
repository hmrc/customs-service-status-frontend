import sbt.Setting
import scoverage.ScoverageKeys

object CodeCoverageSettings {

  val settings: Seq[Setting[_]] = Seq(
    ScoverageKeys.coverageExcludedFiles := "<empty>;com.kenshoo.play.metrics.*;.*definition.*;prod.*;testOnlyDoNotUseInAppConf.*;" +
      "app.*;.*BuildInfo.*;.*Routes.*;.*repositories.*;.*controllers.test.*;.*connectors.test.*;.*connectors.analytics.*;.*services.test.*;.*LanguageSwitchController;.*metrics.*;" +
      "Reverse.*;.*models.api.GoodsMovementRecordData;.*models.api.metadata.*;.*utils.EqualUtils.*;.*models.*;",
    ScoverageKeys.coverageMinimumStmtTotal := 85,
    ScoverageKeys.coverageFailOnMinimum := false,
    ScoverageKeys.coverageHighlighting := true
  )
}
