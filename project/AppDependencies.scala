import sbt._

object AppDependencies {

  private val bootstrapVersion = "9.19.0"
  private val hmrcMongoVersion = "2.11.0"
  private val playVersion = "play-30"

  val compile = Seq(
    "uk.gov.hmrc"             %% s"bootstrap-frontend-$playVersion"         % bootstrapVersion,
    "uk.gov.hmrc"             %% s"play-frontend-hmrc-$playVersion"         % "12.25.0",
    "uk.gov.hmrc.mongo"       %% "hmrc-mongo-play-30"                       % hmrcMongoVersion
  )

  val test = Seq(
    "uk.gov.hmrc"             %% s"bootstrap-test-$playVersion"     % bootstrapVersion    % "test, it",
    "org.jsoup"               %  "jsoup"                            % "1.18.1"            % Test
  )
}
