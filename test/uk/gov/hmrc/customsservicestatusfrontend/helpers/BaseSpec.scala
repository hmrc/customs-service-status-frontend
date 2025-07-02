/*
 * Copyright 2023 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.customsservicestatusfrontend.helpers

import org.apache.pekko.actor.ActorSystem
import org.apache.pekko.stream.Materializer
import com.typesafe.config.{Config, ConfigFactory}
import org.scalamock.scalatest.MockFactory
import org.scalatest.*
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.{Application, Configuration}
import play.api.http.{HeaderNames, Status}
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.{DefaultAwaitTimeout, FutureAwaits, ResultExtractors}
import uk.gov.hmrc.customsservicestatusfrontend.config.AppConfig
import uk.gov.hmrc.customsservicestatusfrontend.views.html.{Layout, govukLayoutFullWidth}
import uk.gov.hmrc.govukfrontend.views.html.components.{FixedWidthPageLayout, GovukBackLink, GovukButton, GovukExitThisPage, GovukFooter, GovukHeader, GovukLayout, GovukPhaseBanner, GovukSkipLink, GovukTag, GovukTemplate, TwoThirdsMainContent}
import uk.gov.hmrc.govukfrontend.views.html.helpers.GovukLogo
import uk.gov.hmrc.hmrcfrontend.config.{AccessibilityStatementConfig, AssetsConfig, ContactFrontendConfig, LanguageConfig, RebrandConfig, TrackingConsentConfig, TudorCrownConfig}
import uk.gov.hmrc.hmrcfrontend.views.config.{HmrcFooterItems, StandardBetaBanner}
import uk.gov.hmrc.hmrcfrontend.views.html.components.{HmrcBanner, HmrcFooter, HmrcHeader, HmrcLanguageSelect, HmrcReportTechnicalIssue, HmrcUserResearchBanner}
import uk.gov.hmrc.hmrcfrontend.views.html.helpers.{HmrcHead, HmrcLanguageSelectHelper, HmrcReportTechnicalIssueHelper, HmrcScripts, HmrcStandardFooter, HmrcStandardHeader, HmrcStandardPage, HmrcTrackingConsentSnippet}
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.ExecutionContext

trait BaseSpec
    extends AnyWordSpec
    with OptionValues
    with Inside
    with Matchers
    with EitherValues
    with BeforeAndAfterEach
    with DefaultAwaitTimeout
    with FutureAwaits
    with ScalaFutures
    with Status
    with HeaderNames
    with ResultExtractors
    with GuiceOneAppPerSuite
    with StubMessageControllerComponents
    with MockFactory {

  implicit lazy val ec:           ExecutionContext      = scala.concurrent.ExecutionContext.Implicits.global
  implicit lazy val hc:           HeaderCarrier         = HeaderCarrier()
  implicit lazy val system:       ActorSystem           = ActorSystem()
  implicit lazy val materializer: Materializer          = Materializer(system)
  implicit lazy val cfConfig:     ContactFrontendConfig = app.injector.instanceOf[ContactFrontendConfig]

  val assetsConfig = new AssetsConfig()

  def configuration: Configuration = Configuration(ConfigFactory.parseResources("application.conf"))

  implicit def applicationConfig: AppConfig = new AppConfig(configuration)

  override def fakeApplication(): Application =
    new GuiceApplicationBuilder()
      .configure(
        "metrics.jvm"     -> false,
        "metrics.enabled" -> false
      )
      .build()

  val govukLogo = new GovukLogo

  val govukLayoutFullWidth = new govukLayoutFullWidth()

  val govukLayout = new GovukLayout(
    govukTemplate = new GovukTemplate(
      govukHeader = new GovukHeader(TudorCrownConfig(configuration), RebrandConfig(configuration), govukLogo),
      govukFooter = new GovukFooter(RebrandConfig(configuration), govukLogo),
      govukSkipLink = new GovukSkipLink,
      fixedWidthPageLayout = new FixedWidthPageLayout,
      rebrandConfig = RebrandConfig(configuration)
    ),
    govukHeader = new GovukHeader(TudorCrownConfig(configuration), RebrandConfig(configuration), govukLogo),
    govukFooter = new GovukFooter(RebrandConfig(configuration), govukLogo),
    govukBackLink = new GovukBackLink,
    defaultMainContentLayout = new TwoThirdsMainContent,
    fixedWidthPageLayout = new FixedWidthPageLayout
  )

  val govukTag = new GovukTag
  val tudorCrownConfig: TudorCrownConfig = TudorCrownConfig(applicationConfig.config)
  val hmrcBanner             = new HmrcBanner(tudorCrownConfig)
  val hmrcUserResearchBanner = new HmrcUserResearchBanner
  val govukPhaseBanner       = new GovukPhaseBanner(govukTag)
  val hmrcHeader = new HmrcHeader(hmrcBanner, hmrcUserResearchBanner, govukPhaseBanner, tudorCrownConfig, RebrandConfig(configuration), govukLogo)
  val hmrcStandardHeader           = new HmrcStandardHeader(hmrcHeader)
  val govukFooter                  = new GovukFooter(RebrandConfig(configuration), govukLogo)
  val hmrcFooter                   = new HmrcFooter(govukFooter)
  val accessibilityStatementConfig = new AccessibilityStatementConfig(applicationConfig.config)
  val hmrcFooterItems              = new HmrcFooterItems(accessibilityStatementConfig)
  val hmrcStandardFooter           = new HmrcStandardFooter(hmrcFooter, hmrcFooterItems)
  val trackingConsentConfig        = new TrackingConsentConfig(applicationConfig.config)
  val hmrcTrackingConsentSnippet   = new HmrcTrackingConsentSnippet(trackingConsentConfig)
  val hmrcHead                     = new HmrcHead(hmrcTrackingConsentSnippet, assetsConfig)
  val hmrcLanguageSelectHelper =
    new HmrcLanguageSelectHelper(hmrcLanguageSelect = new HmrcLanguageSelect, languageConfig = new LanguageConfig(applicationConfig.config))
  val hmrcScripts          = new HmrcScripts(assetsConfig)
  val govukBackLink        = new GovukBackLink
  val govukExitThisPage    = new GovukExitThisPage(govukButton = new GovukButton)
  val defaultMainContent   = new TwoThirdsMainContent
  val fixedWidthPageLayout = new FixedWidthPageLayout

  val hmrcStandardPage = new HmrcStandardPage(
    govukLayout = govukLayout,
    hmrcStandardHeader = hmrcStandardHeader,
    hmrcStandardFooter = hmrcStandardFooter,
    hmrcHead = hmrcHead,
    hmrcLanguageSelectHelper = hmrcLanguageSelectHelper,
    hmrcScripts = hmrcScripts,
    govukBackLink = govukBackLink,
    govukExitThisPage = govukExitThisPage,
    defaultMainContent = defaultMainContent,
    fixedWidthPageLayout = fixedWidthPageLayout
  )

  val standardBetaBanner             = new StandardBetaBanner
  val hmrcReportTechnicalIssueHelper = new HmrcReportTechnicalIssueHelper(HmrcReportTechnicalIssue(), cfConfig)

  val layout = new Layout(
    appConfig = applicationConfig,
    govukLayout = govukLayoutFullWidth,
    hmrcStandardPage = hmrcStandardPage,
    standardBetaBanner = standardBetaBanner,
    hmrcReportTechnicalIssueHelper = hmrcReportTechnicalIssueHelper
  )(cfConfig)

}
