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
import uk.gov.hmrc.customsservicestatusfrontend.helpers.TestData.{now, serviceStatuses, validUnplannedOutageData}
import uk.gov.hmrc.customsservicestatusfrontend.models.DetailType.{InternalReference, Preview}
import uk.gov.hmrc.customsservicestatusfrontend.models.State.{UNAVAILABLE, UNKNOWN}
import uk.gov.hmrc.customsservicestatusfrontend.models.{CustomsServiceStatus, ServiceStatuses}
import uk.gov.hmrc.customsservicestatusfrontend.services.{StatusService, UnplannedOutageService}
import uk.gov.hmrc.customsservicestatusfrontend.utils.Formatters
import uk.gov.hmrc.customsservicestatusfrontend.views.html.DashboardPage
import uk.gov.hmrc.http.HeaderCarrier

import java.time.Instant
import scala.concurrent.Future

class DashboardControllerSpec extends ControllerBaseSpec {

  private val fakeRequest = FakeRequest("GET", "/service-availability")
  private val dashboardPage: DashboardPage = app.injector.instanceOf[DashboardPage]
  private val mockService       = mock[StatusService]
  private val mockOutageService = mock[UnplannedOutageService]

  private val controller = new DashboardController(
    stubMessagesControllerComponents(),
    dashboardPage,
    mockService,
    mockOutageService
  )

  "GET /service-availability" should {
    "show dashboard content as expected when there are no issues or planned work and no CLS updates posted" in {
      (mockService
        .getStatus()(_: HeaderCarrier))
        .expects(*)
        .returns(Future.successful(serviceStatuses))

      (mockOutageService
        .getLatest()(_: HeaderCarrier))
        .expects(*)
        .returns(
          Future.successful(
            validUnplannedOutageData
              .copy(internalReference = InternalReference(""), preview = Preview(""), lastUpdated = Instant.now, notesForClsUsers = None)
          )
        )

      val result = controller.show(fakeRequest)
      status(result) shouldBe Status.OK

      val doc = Jsoup.parse(contentAsString(result))
      doc.getElementsByClass("govuk-heading-l").text() shouldBe "Service availability for GVMS"
      doc.getElementsByClass("govuk-heading-m").text()   should include("Live service status")

      doc.getElementsByClass("govuk-tag--green").text() shouldBe "No issues detected"
      doc.getElementById("refresh-link").attr("href").contains("/customs-service-status/service-availability/status")
      doc.getElementsByClass("govuk-body").text() should include("Refresh this page to check for changes.")

      doc.getElementsByClass("govuk-heading-m").text() should include("Planned work")
      doc.getElementsByClass("govuk-body").text()      should include("Find out when future outages are happening")

      doc.getElementsByClass("govuk-heading-m").text() should include("Other HMRC services")
      doc.getElementsByClass("govuk-body").text()      should include("Track availability for other HMRC services")

    }
    "show dashboard content as expected when there are no issues or planned work and there is a CLS update" in {
      (mockService
        .getStatus()(_: HeaderCarrier))
        .expects(*)
        .returns(Future.successful(serviceStatuses))

      (mockOutageService
        .getLatest()(_: HeaderCarrier))
        .expects(*)
        .returns(Future.successful(validUnplannedOutageData))

      val result = controller.show(fakeRequest)
      status(result) shouldBe Status.OK

      val doc = Jsoup.parse(contentAsString(result))
      doc.getElementsByClass("govuk-heading-l").text() shouldBe "Service availability for GVMS"
      doc.getElementsByClass("govuk-heading-m").text()   should include("Live service status")

      doc.getElementsByClass("govuk-tag--green").text() shouldBe "No issues detected"
      doc.getElementById("refresh-link").attr("href").contains("/customs-service-status/service-availability/status")
      doc.getElementsByClass("govuk-body").text() should include("Refresh this page to check for changes.")

      doc.getElementsByClass("govuk-heading-m").text() should include("Planned work")
      doc.getElementsByClass("govuk-body").text()      should include("Find out when future outages are happening")

      doc.getElementsByClass("govuk-heading-m").text() should include("Other HMRC services")
      doc.getElementsByClass("govuk-body").text()      should include("Track availability for other HMRC services")

    }

    "show dashboard content as expected when there are issues" in {
      val serviceStatus:   CustomsServiceStatus = CustomsServiceStatus("haulier", "Haulier", "description", Some(UNAVAILABLE), Some(now), Some(now))
      val serviceStatuses: ServiceStatuses      = ServiceStatuses(List(serviceStatus))

      (mockService
        .getStatus()(_: HeaderCarrier))
        .expects(*)
        .returns(Future.successful(serviceStatuses))

      (mockOutageService
        .getLatest()(_: HeaderCarrier))
        .expects(*)
        .returns(Future.successful(validUnplannedOutageData))

      val result = controller.show(fakeRequest)
      status(result) shouldBe Status.OK

      val doc = Jsoup.parse(contentAsString(result))
      doc.getElementsByClass("govuk-heading-l").text()   shouldBe "Service availability for GVMS"
      doc.getElementsByClass("govuk-tag--orange").text() shouldBe "Known issues"

      val body = doc.getElementsByClass("govuk-body").text()
      // body should include(s"Last updated: ${Formatters.instantFormatHours(now)}")
      body should include(s"Known issues since: ${Formatters.instantFormatHours(now)} on ${Formatters.instantFormatDate(now)}")
      body should include("We are currently investigating.")

      doc.getElementsByClass("govuk-heading-m").text      should include("Issues with the service")
      body                                                should include("You may not be able to:")
      doc.getElementsByClass("govuk-list--bullet").text() should include("create a Goods Movement Reference (GMR) manage your GMRs")
      doc.getElementsByClass("govuk-list--bullet").text() should include("manage your GMRs")

      doc.getElementsByClass("govuk-heading-m").text() should include("What you can do next")
      body                                             should include("Do not travel to the border if you do not have a valid GMR.")
      body should include("This page does not currently show information about action plans. You can find out:")

      doc.getElementsByClass("govuk-list--bullet").text() should include(
        "if HMRC has published an action plan to help keep your goods moving if there is any planned downtime for GVMS"
      )
      doc.getElementsByClass("govuk-list--bullet").text() should include("if there is any planned downtime for GVMS")
      body                                                should include("By going to the GVMS service information page")
    }

    "show dashboard content as expected when status is unknown" in {
      val serviceStatus:   CustomsServiceStatus = CustomsServiceStatus("haulier", "Haulier", "description", Some(UNKNOWN), Some(now), Some(now))
      val serviceStatuses: ServiceStatuses      = ServiceStatuses(List(serviceStatus))
      (mockService
        .getStatus()(_: HeaderCarrier))
        .expects(*)
        .returns(Future.successful(serviceStatuses))

      (mockOutageService
        .getLatest()(_: HeaderCarrier))
        .expects(*)
        .returns(Future.successful(validUnplannedOutageData))

      val result = controller.show(fakeRequest)
      status(result) shouldBe Status.OK

      val doc = Jsoup.parse(contentAsString(result))
      doc.getElementsByClass("govuk-heading-l").text() shouldBe "GVMS availability unknown"
      doc.getElementsByClass("govuk-body").text()        should include("The Check GVMS availability service is not working right now.")
      doc.getElementsByClass("govuk-body").text() should include(
        "You can check if the service is working by logging in to the Goods Vehicle Movement Service."
      )
      doc
        .getElementById("service_url")
        .attr("href") shouldBe "https://www.gov.uk/guidance/get-a-goods-movement-reference#get-a-goods-movement-reference"
    }
  }
}
