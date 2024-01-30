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

package uk.gov.hmrc.customsservicestatusfrontend.controllers

import org.jsoup.Jsoup
import play.api.http.Status
import play.api.test.FakeRequest
import uk.gov.hmrc.customsservicestatusfrontend.helpers.ControllerBaseSpec
import uk.gov.hmrc.customsservicestatusfrontend.helpers.TestData.{now, serviceStatuses}
import uk.gov.hmrc.customsservicestatusfrontend.models.State.{UNAVAILABLE, UNKNOWN}
import uk.gov.hmrc.customsservicestatusfrontend.models.{CustomsServiceStatus, ServiceStatuses}
import uk.gov.hmrc.customsservicestatusfrontend.services.StatusService
import uk.gov.hmrc.customsservicestatusfrontend.utils.Formatters
import uk.gov.hmrc.customsservicestatusfrontend.views.html.DashboardPage
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.Future

class DashboardControllerSpec extends ControllerBaseSpec {

  private val fakeRequest = FakeRequest("GET", "/service-availability")
  private val dashboardPage: DashboardPage = app.injector.instanceOf[DashboardPage]
  private val mockService = mock[StatusService]

  private val controller = new DashboardController(
    stubMessagesControllerComponents(),
    dashboardPage,
    mockService
  )

  "GET /service-availability" should {
    "show dashboard content as expected when there are no issues" in {
      (mockService
        .getStatus()(_: HeaderCarrier))
        .expects(*)
        .returns(Future.successful(serviceStatuses))

      val result = controller.show(fakeRequest)
      status(result) shouldBe Status.OK

      val doc = Jsoup.parse(contentAsString(result))
      doc.getElementsByClass("govuk-heading-l").text()  shouldBe "Current GVMS availability"
      doc.getElementsByClass("govuk-body").text()         should include(s"Last updated: ${Formatters.instantFormatHours(now)}")
      doc.getElementsByClass("govuk-tag--green").text() shouldBe "AVAILABLE"
      doc.getElementsByClass("govuk-body").text() should include(
        "There are currently no issues with creating and updating a goods movement reference."
      )
    }

    "show dashboard content as expected when there are issues" in {
      val serviceStatus:   CustomsServiceStatus = CustomsServiceStatus("Haulier", "description", Some(UNAVAILABLE), Some(now), Some(now))
      val serviceStatuses: ServiceStatuses      = ServiceStatuses(List(serviceStatus))

      (mockService
        .getStatus()(_: HeaderCarrier))
        .expects(*)
        .returns(Future.successful(serviceStatuses))

      val result = controller.show(fakeRequest)
      status(result) shouldBe Status.OK

      val doc = Jsoup.parse(contentAsString(result))
      doc.getElementsByClass("govuk-heading-l").text()   shouldBe "Current GVMS availability"
      doc.getElementsByClass("govuk-tag--orange").text() shouldBe "KNOWN ISSUES"

      val body = doc.getElementsByClass("govuk-body").text()
      body                                                should include(s"Last updated: ${Formatters.instantFormatHours(now)}")
      body                                                should include(s"Known issues since: ${Formatters.instantFormatHours(now)} on ${Formatters.instantFormatDate(now)}")
      body                                                should include("We are currently investigating this issue.")
      body                                                should include("You may not be able to:")
      doc.getElementsByClass("govuk-list--bullet").text() should include("create a Goods Movement Reference (GMR) manage your GMRs")
      body                                                should include(s"Last updated: ${Formatters.instantFormatHours(now)}")

      doc.getElementsByClass("govuk-heading-m").text() shouldBe "What you can do next"
      body                                               should include("Do not travel to the border if you do not have a valid GMR.")

      doc.getElementsByClass("govuk-list--bullet").text() should include(
        "if HMRC has published an action plan to help keep your goods moving if there is any planned downtime for GVMS"
      )
    }

    "show dashboard content as expected when status is unknown" in {
      val serviceStatus:   CustomsServiceStatus = CustomsServiceStatus("Haulier", "description", Some(UNKNOWN), Some(now), Some(now))
      val serviceStatuses: ServiceStatuses      = ServiceStatuses(List(serviceStatus))
      (mockService
        .getStatus()(_: HeaderCarrier))
        .expects(*)
        .returns(Future.successful(serviceStatuses))

      val result = controller.show(fakeRequest)
      status(result) shouldBe Status.OK

      val doc = Jsoup.parse(contentAsString(result))
      doc.getElementsByClass("govuk-heading-l").text() shouldBe "GVMS availability unknown"
      doc.getElementsByClass("govuk-body").text()        should include("The Check GVMS availability service is not working right now.")
      doc.getElementsByClass("govuk-body").text()        should include("You can log into the Goods Vehicle Movement Service (GVMS) to check this yourself.")
      doc.getElementById("gvms_url").attr("href")      shouldBe "https://www.gov.uk/guidance/get-a-goods-movement-reference#get-a-goods-movement-reference"
    }
  }
}
