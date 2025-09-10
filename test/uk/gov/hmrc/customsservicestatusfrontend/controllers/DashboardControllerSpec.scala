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
import org.mockito.ArgumentMatchers
import org.mockito.ArgumentMatchers.*
import org.mockito.Mockito.*
import play.api.http.Status
import play.api.test.FakeRequest
import uk.gov.hmrc.customsservicestatusfrontend.helpers.ControllerBaseSpec
import uk.gov.hmrc.customsservicestatusfrontend.TestData.{fakePlannedWork, now, serviceStatuses, validPlannedOutageData, validUnplannedOutageData}
import uk.gov.hmrc.customsservicestatusfrontend.models.State.{UNAVAILABLE, UNKNOWN}
import uk.gov.hmrc.customsservicestatusfrontend.models.{CustomsServiceStatus, OutageType, ServiceStatuses}
import uk.gov.hmrc.customsservicestatusfrontend.services.{OutageService, PlannedWorkService, StatusService}
import uk.gov.hmrc.customsservicestatusfrontend.utils.Formatters
import uk.gov.hmrc.customsservicestatusfrontend.views.html.DashboardPage

import scala.concurrent.Future

class DashboardControllerSpec extends ControllerBaseSpec {

  private val fakeRequest = FakeRequest("GET", "/service-availability")
  private val dashboardPage: DashboardPage = new DashboardPage(layout, govukInsetText)
  private val mockService            = mock[StatusService]
  private val mockOutageService      = mock[OutageService]
  private val mockPlannedWorkService = mock[PlannedWorkService]

  private val controller = new DashboardController(
    stubMessagesControllerComponents(),
    dashboardPage,
    mockService,
    mockOutageService,
    mockPlannedWorkService
  )

  "GET /service-availability" should {
    "show dashboard content as expected when there are no issues, no planned work and no CLS updates posted" in {
      when(mockService.getStatus()(any())).thenReturn(Future.successful(serviceStatuses))

      when(mockOutageService.getLatest(any())(any())).thenReturn(Future.successful(None))
      when(mockPlannedWorkService.getAllPlannedWorks()(any())).thenReturn(Future.successful(List(fakePlannedWork)))

      val result = controller.show(fakeRequest)
      status(result) shouldBe Status.OK

      val doc = Jsoup.parse(contentAsString(result))
      doc.getElementsByClass("govuk-heading-l").text()  shouldBe "Service availability for GVMS"
      doc.getElementsByClass("govuk-heading-m").text()    should include("Live service status")
      doc.getElementsByClass("govuk-tag--green").text() shouldBe "No issues detected"

      doc.getElementById("refresh-link").attr("href").contains("/customs-service-status/service-availability/status")
      doc.getElementsByClass("govuk-body").text() should include("Refresh this page to check for changes.")

      doc.getElementsByClass("govuk-heading-m").text() should include("Planned work")
      doc.getElementsByClass("govuk-body").text()      should include("Find out when future outages are happening")

      doc.getElementsByClass("govuk-heading-m").text() should include("Other HMRC services")
      doc.getElementsByClass("govuk-body").text()      should include("Track availability for other HMRC services")

    }

    "show dashboard content as expected when there are no issues, no planned work and there is a CLS update" in {
      when(mockService.getStatus()(any())).thenReturn(Future.successful(serviceStatuses))

      when(mockOutageService.getLatest(ArgumentMatchers.eq(OutageType.Unplanned))(any()))
        .thenReturn(Future.successful(Some(validUnplannedOutageData)))
      when(mockPlannedWorkService.getAllPlannedWorks()(any())).thenReturn(Future.successful(List(fakePlannedWork)))

      val result = controller.show(fakeRequest)
      status(result) shouldBe Status.OK

      val doc = Jsoup.parse(contentAsString(result))
      doc.getElementsByClass("govuk-heading-l").text() shouldBe "Service availability for GVMS"
      doc.getElementsByClass("govuk-heading-m").text()   should include("Live service status")

      doc.getElementsByClass("govuk-tag--green").text() shouldBe "No issues detected"
      doc.getElementById("refresh-link").attr("href").contains("/customs-service-status/service-availability/status")
      doc.getElementsByClass("govuk-body").text() should include("Refresh this page to check for changes.")

      doc.getElementsByClass("hmrc-timeline__event-title govuk-table__caption--s").text() should include(
        s"Update at ${Formatters.instantFormatHours(validUnplannedOutageData.publishedDateTime)} on ${Formatters.instantFormatDate(validUnplannedOutageData.publishedDateTime)}"
      )

      doc.getElementsByClass("govuk-heading-m").text() should include("Planned work")
      doc.getElementById("plannedwork-link").attr("href").contains("/customs-service-status/service-availability/planned-work")
      doc.getElementsByClass("govuk-body").text() should include("Find out when future outages are happening")

      doc.getElementsByClass("govuk-heading-m").text() should include("Other HMRC services")
      doc
        .getElementById("available_p3_link")
        .attr("href")
        .contains("https://www.gov.uk/government/collections/hm-revenue-and-customs-service-availability-and-issues")
      doc.getElementsByClass("govuk-body").text() should include("Track availability for other HMRC services")

    }

    "show dashboard content as expected when there are no issues, there is planned work and there is a CLS update" in {
      when(mockService.getStatus()(any())).thenReturn(Future.successful(serviceStatuses))

      when(mockOutageService.getLatest(ArgumentMatchers.eq(OutageType.Unplanned))(any()))
        .thenReturn(Future.successful(Some(validUnplannedOutageData)))
      when(mockPlannedWorkService.getAllPlannedWorks()(any())).thenReturn(Future.successful(List(fakePlannedWork)))

      val result = controller.show(fakeRequest)
      status(result) shouldBe Status.OK

      val doc = Jsoup.parse(contentAsString(result))
      doc.getElementsByClass("govuk-heading-l").text() shouldBe "Service availability for GVMS"
      doc.getElementsByClass("govuk-heading-m").text()   should include("Live service status")

      doc.getElementsByClass("govuk-tag--green").text() shouldBe "No issues detected"
      doc.getElementById("refresh-link").attr("href").contains("/customs-service-status/service-availability/status")
      doc.getElementsByClass("govuk-body").text() should include("Refresh this page to check for changes.")

      doc.getElementsByClass("hmrc-timeline__event-title govuk-table__caption--s").text() should include(
        s"Update at ${Formatters.instantFormatHours(validUnplannedOutageData.publishedDateTime)} on ${Formatters.instantFormatDate(validUnplannedOutageData.publishedDateTime)}"
      )

      val expectedDateFrom: String =
        s"From: ${Formatters.instantFormatHours(fakePlannedWork.startDateTime)} on ${Formatters.instantFormatDate(fakePlannedWork.startDateTime)}"
      val expectedDateTo: String =
        s"To: ${Formatters.instantFormatHours(fakePlannedWork.endDateTime.get)} on ${Formatters.instantFormatDate(fakePlannedWork.endDateTime.get)}"
      val expectedDetails: String = fakePlannedWork.commsText.html

      doc.getElementsByClass("govuk-heading-m").text() should include("Planned work happening today")
      doc.getElementsByClass("govuk-body").text()      should include(s"$expectedDateFrom")
      doc.getElementsByClass("govuk-body").text()      should include(s"$expectedDateTo")
      doc.getElementsByClass("govuk-body").text()      should include("Details:")
      doc.getElementsByClass("govuk-body").text()      should include(expectedDetails)
      doc.getElementsByClass("govuk-heading-m").text() should include("Planned work happening later")

      doc.getElementById("plannedwork-link").attr("href").contains("/customs-service-status/service-availability/planned-work")
      doc.getElementsByClass("govuk-body").text() should include("Find out when future outages are happening")

      doc.getElementsByClass("govuk-heading-m").text() should include("Other HMRC services")
      doc
        .getElementById("available_p3_link")
        .attr("href")
        .contains("https://www.gov.uk/government/collections/hm-revenue-and-customs-service-availability-and-issues")
      doc.getElementsByClass("govuk-body").text() should include("Track availability for other HMRC services")

    }

    "show dashboard content as expected when there are no issues, there is planned work and no CLS update" in {
      when(mockService.getStatus()(any())).thenReturn(Future.successful(serviceStatuses))

      when(mockOutageService.getLatest(any())(any())).thenReturn(Future.successful(Some(validPlannedOutageData)))
      when(mockPlannedWorkService.getAllPlannedWorks()(any())).thenReturn(Future.successful(List(fakePlannedWork)))

      val result = controller.show(fakeRequest)
      status(result) shouldBe Status.OK

      val doc = Jsoup.parse(contentAsString(result))
      doc.getElementsByClass("govuk-heading-l").text() shouldBe "Service availability for GVMS"
      doc.getElementsByClass("govuk-heading-m").text()   should include("Live service status")

      doc.getElementsByClass("govuk-tag--green").text() shouldBe "No issues detected"
      doc.getElementById("refresh-link").attr("href").contains("/customs-service-status/service-availability/status")
      doc.getElementsByClass("govuk-body").text() should include("Refresh this page to check for changes.")

      val expectedDateFrom: String =
        s"From: ${Formatters.instantFormatHours(fakePlannedWork.startDateTime)} on ${Formatters.instantFormatDate(fakePlannedWork.startDateTime)}"
      val expectedDateTo: String =
        s"To: ${Formatters.instantFormatHours(fakePlannedWork.endDateTime.get)} on ${Formatters.instantFormatDate(fakePlannedWork.endDateTime.get)}"
      val expectedDetails: String = fakePlannedWork.commsText.html

      doc.getElementsByClass("govuk-heading-m").text() should include("Planned work happening today")
      doc.getElementsByClass("govuk-body").text()      should include(s"$expectedDateFrom")
      doc.getElementsByClass("govuk-body").text()      should include(s"$expectedDateTo")
      doc.getElementsByClass("govuk-body").text()      should include("Details:")
      doc.getElementsByClass("govuk-body").text()      should include(expectedDetails)
      doc.getElementsByClass("govuk-heading-m").text() should include("Planned work happening later")

      doc.getElementsByClass("govuk-heading-m").text() should include("Planned work happening later")
      doc.getElementById("refresh-link").attr("href").contains("/customs-service-status/service-availability/planned-work")
      doc.getElementsByClass("govuk-body").text() should include("Find out when future outages are happening")

      doc.getElementsByClass("govuk-heading-m").text() should include("Other HMRC services")
      doc
        .getElementById("available_p3_link")
        .attr("href")
        .contains("https://www.gov.uk/government/collections/hm-revenue-and-customs-service-availability-and-issues")
      doc.getElementsByClass("govuk-body").text() should include("Track availability for other HMRC services")

    }

    "show dashboard content as expected when there are issues, but no planned work and no CLS updates posted" in {
      val serviceStatus:   CustomsServiceStatus = CustomsServiceStatus("haulier", "Haulier", "description", Some(UNAVAILABLE), Some(now), Some(now))
      val serviceStatuses: ServiceStatuses      = ServiceStatuses(List(serviceStatus))

      when(mockService.getStatus()(any())).thenReturn(Future.successful(serviceStatuses))

      when(mockOutageService.getLatest(any())(any())).thenReturn(Future.successful(None))
      when(mockPlannedWorkService.getAllPlannedWorks()(any())).thenReturn(Future.successful(List(fakePlannedWork)))

      val result = controller.show(fakeRequest)
      status(result) shouldBe Status.OK

      val doc = Jsoup.parse(contentAsString(result))
      doc.getElementsByClass("govuk-heading-l").text()   shouldBe "Service availability for GVMS"
      doc.getElementsByClass("govuk-heading-m").text()     should include("Live service status")
      doc.getElementsByClass("govuk-tag--orange").text() shouldBe "Issue detected"

      doc.getElementById("refresh-link").attr("href").contains("/customs-service-status/service-availability/status")
      doc.getElementsByClass("govuk-body").text() should include("Refresh this page to check for changes.")

      doc.getElementsByClass("hmrc-timeline__event-title govuk-table__caption--s").text() should include(
        s"Issue detected at ${Formatters.instantFormatHours(now)} on ${Formatters.instantFormatDate(now)}"
      )

      val timelineText = doc.getElementsByClass("hmrc-timeline__event-content")
      timelineText.size shouldBe 2

      timelineText.get(0).text() should include(
        "You may not be able to create a Goods Movement Reference (GMR) or manage your GMRs."
      )
      timelineText.get(1).text() should include(
        "Do not travel to the border without a valid GMR. We’ll post any further updates here."
      )

      doc.getElementsByClass("govuk-heading-m").text() should include("Planned work")
      doc.getElementsByClass("govuk-body").text()      should include("Find out when future outages are happening")

      doc.getElementsByClass("govuk-heading-m").text() should include("Other HMRC services")
      doc.getElementsByClass("govuk-body").text()      should include("Track availability for other HMRC services")

    }

    "show dashboard content as expected when there are issues, no planned work and there is a CLS update" in {
      val serviceStatus:   CustomsServiceStatus = CustomsServiceStatus("haulier", "Haulier", "description", Some(UNAVAILABLE), Some(now), Some(now))
      val serviceStatuses: ServiceStatuses      = ServiceStatuses(List(serviceStatus))

      when(mockService.getStatus()(any())).thenReturn(Future.successful(serviceStatuses))

      when(mockOutageService.getLatest(ArgumentMatchers.eq(OutageType.Unplanned))(any()))
        .thenReturn(Future.successful(Some(validUnplannedOutageData)))
      when(mockPlannedWorkService.getAllPlannedWorks()(any())).thenReturn(Future.successful(List(fakePlannedWork)))

      val result = controller.show(fakeRequest)
      status(result) shouldBe Status.OK

      val doc = Jsoup.parse(contentAsString(result))
      doc.getElementsByClass("govuk-heading-l").text()   shouldBe "Service availability for GVMS"
      doc.getElementsByClass("govuk-heading-m").text()     should include("Live service status")
      doc.getElementsByClass("govuk-tag--orange").text() shouldBe "Issue detected"

      doc.getElementById("refresh-link").attr("href").contains("/customs-service-status/service-availability/status")
      doc.getElementsByClass("govuk-body").text() should include("Refresh this page to check for changes.")

      val timelineHeaders = doc.getElementsByClass("hmrc-timeline__event-title govuk-table__caption--s")
      timelineHeaders.size shouldBe 2

      timelineHeaders.get(0).text() should include(
        s"Update at ${Formatters.instantFormatHours(validUnplannedOutageData.publishedDateTime)} on ${Formatters.instantFormatDate(validUnplannedOutageData.publishedDateTime)}"
      )

      timelineHeaders.get(1).text() should include(
        s"Issue detected at ${Formatters.instantFormatHours(validUnplannedOutageData.publishedDateTime)} on ${Formatters.instantFormatDate(validPlannedOutageData.publishedDateTime)}"
      )

      doc.getElementsByClass("govuk-heading-m").text() should include("Planned work")
      doc.getElementsByClass("govuk-body").text()      should include("Find out when future outages are happening")

      doc.getElementsByClass("govuk-heading-m").text() should include("Other HMRC services")
      doc.getElementsByClass("govuk-body").text()      should include("Track availability for other HMRC services")

    }

    "show dashboard content as expected when there are issues, there is planned work and there is a CLS update" in {
      val serviceStatus:   CustomsServiceStatus = CustomsServiceStatus("haulier", "Haulier", "description", Some(UNAVAILABLE), Some(now), Some(now))
      val serviceStatuses: ServiceStatuses      = ServiceStatuses(List(serviceStatus))

      when(mockService.getStatus()(any())).thenReturn(Future.successful(serviceStatuses))

      when(mockOutageService.getLatest(ArgumentMatchers.eq(OutageType.Unplanned))(any()))
        .thenReturn(Future.successful(Some(validUnplannedOutageData)))
      when(mockPlannedWorkService.getAllPlannedWorks()(any())).thenReturn(Future.successful(List(fakePlannedWork)))

      val result = controller.show(fakeRequest)
      status(result) shouldBe Status.OK

      val doc = Jsoup.parse(contentAsString(result))
      doc.getElementsByClass("govuk-heading-l").text()   shouldBe "Service availability for GVMS"
      doc.getElementsByClass("govuk-heading-m").text()     should include("Live service status")
      doc.getElementsByClass("govuk-tag--orange").text() shouldBe "Issue detected"

      doc.getElementById("refresh-link").attr("href").contains("/customs-service-status/service-availability/status")
      doc.getElementsByClass("govuk-body").text() should include("Refresh this page to check for changes.")

      val timelineHeaders = doc.getElementsByClass("hmrc-timeline__event-title govuk-table__caption--s")
      timelineHeaders.size shouldBe 2

      timelineHeaders.get(0).text() should include(
        s"Update at ${Formatters.instantFormatHours(validUnplannedOutageData.publishedDateTime)} on ${Formatters.instantFormatDate(validUnplannedOutageData.publishedDateTime)}"
      )

      timelineHeaders.get(1).text() should include(
        s"Issue detected at ${Formatters.instantFormatHours(validUnplannedOutageData.publishedDateTime)} on ${Formatters.instantFormatDate(validPlannedOutageData.publishedDateTime)}"
      )

      val expectedDateFrom: String =
        s"From: ${Formatters.instantFormatHours(fakePlannedWork.startDateTime)} on ${Formatters.instantFormatDate(fakePlannedWork.startDateTime)}"
      val expectedDateTo: String =
        s"To: ${Formatters.instantFormatHours(fakePlannedWork.endDateTime.get)} on ${Formatters.instantFormatDate(fakePlannedWork.endDateTime.get)}"
      val expectedDetails: String = fakePlannedWork.commsText.html

      doc.getElementsByClass("govuk-heading-m").text() should include("Planned work happening today")
      doc.getElementsByClass("govuk-body").text()      should include(s"$expectedDateFrom")
      doc.getElementsByClass("govuk-body").text()      should include(s"$expectedDateTo")
      doc.getElementsByClass("govuk-body").text()      should include("Details:")
      doc.getElementsByClass("govuk-body").text()      should include(expectedDetails)
      doc.getElementsByClass("govuk-heading-m").text() should include("Planned work happening later")

      doc.getElementsByClass("govuk-heading-m").text() should include("Planned work happening later")
      doc.getElementById("refresh-link").attr("href").contains("/customs-service-status/service-availability/planned-work")
      doc.getElementsByClass("govuk-body").text() should include("Find out when future outages are happening")

      doc.getElementsByClass("govuk-heading-m").text() should include("Other HMRC services")
      doc.getElementsByClass("govuk-body").text()      should include("Track availability for other HMRC services")

    }

    "show dashboard content as expected when status is unknown" in {
      val serviceStatus:   CustomsServiceStatus = CustomsServiceStatus("haulier", "Haulier", "description", Some(UNKNOWN), Some(now), Some(now))
      val serviceStatuses: ServiceStatuses      = ServiceStatuses(List(serviceStatus))

      when(mockService.getStatus()(any())).thenReturn(Future.successful(serviceStatuses))

      when(mockOutageService.getLatest(any())(any())).thenReturn(Future.successful(Some(validUnplannedOutageData)))
      when(mockPlannedWorkService.getAllPlannedWorks()(any())).thenReturn(Future.successful(List(fakePlannedWork)))

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
