/*
 * Copyright 2025 HM Revenue & Customs
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

package uk.gov.hmrc.customsservicestatusfrontend.views

import uk.gov.hmrc.customsservicestatusfrontend.TestData.*
import uk.gov.hmrc.customsservicestatusfrontend.models.OutageType.{Planned, Unplanned}
import uk.gov.hmrc.customsservicestatusfrontend.models.State.{AVAILABLE, UNAVAILABLE, UNKNOWN}
import uk.gov.hmrc.customsservicestatusfrontend.models.{OutageData, State}
import uk.gov.hmrc.customsservicestatusfrontend.utils.Formatters
import uk.gov.hmrc.customsservicestatusfrontend.views.html.DashboardPage

import java.time.Instant
import java.time.temporal.ChronoUnit

class DashboardPageViewSpec extends ViewBehaviours {

  "/service-availability/status page" should {

    val dashboardPageView = DashboardPage(layout)

    def view(
      state:                      State = AVAILABLE,
      stateChangedAt:             Instant = Instant.now(),
      unplannedOutageData:        Option[OutageData] = None,
      plannedWorksHappeningToday: List[OutageData]
    ) = dashboardPageView(state, stateChangedAt, "haulier", unplannedOutageData, plannedWorksHappeningToday, Instant.now)

    List(
      (AVAILABLE, None, List()),
      (UNAVAILABLE, None, List()),
      (AVAILABLE, None, List(fakeOutageData(Planned, Some(fakeEndDate)))),
      (UNAVAILABLE, None, List(fakeOutageData(Planned, Some(fakeEndDate)))),
      (AVAILABLE, Some(fakeOutageData(Unplanned, None)), List()),
      (UNAVAILABLE, Some(fakeOutageData(Unplanned, None)), List()),
      (AVAILABLE, Some(fakeOutageData(Unplanned, None)), List(fakeOutageData(Planned, Some(fakeEndDate)))),
      (UNAVAILABLE, Some(fakeOutageData(Unplanned, None)), List(fakeOutageData(Planned, Some(fakeEndDate))))
    ).foreach { (state, unplannedOutageData, plannedWorksHappeningToday) =>
      s"rendered, in the scenario where state: $state, unplannedOutageData: $unplannedOutageData and plannedWorksHappeningToday: $plannedWorksHappeningToday" should {
        behave like normalPage("dashboard.haulier.h1")(
          view(state = state, unplannedOutageData = unplannedOutageData, plannedWorksHappeningToday = plannedWorksHappeningToday)
        )
        behave like pageWithoutBackLink(
          view(state = state, unplannedOutageData = unplannedOutageData, plannedWorksHappeningToday = plannedWorksHappeningToday)
        )
        behave like pageWithPageNotWorkingLink(
          view(state = state, unplannedOutageData = unplannedOutageData, plannedWorksHappeningToday = plannedWorksHappeningToday)
        )
      }
    }

    "show dashboard content as expected" when {
      "rendered and there are no issues, no planned work and no CLS updates posted" in {

        val document = view(AVAILABLE, unplannedOutageData = None, plannedWorksHappeningToday = List()).asDocument

        document.getElementsByClass("govuk-heading-l").text()  shouldBe "Service availability for GVMS"
        document.getElementsByClass("govuk-heading-m").text()    should include("Live service status")
        document.getElementsByClass("govuk-tag--green").text() shouldBe "No issues detected"

        document.getElementById("refresh-link").attr("href").contains("/customs-service-status/service-availability/status")
        document.getElementsByClass("govuk-body").text() should include("Refresh this page to check for changes.")

        document.getElementsByClass("govuk-heading-m").text() should include("Planned work")
        document.getElementsByClass("govuk-body").text()      should include("Find out when future outages are happening")

        document.getElementsByClass("govuk-heading-m").text() should include("Other HMRC services")
        document.getElementsByClass("govuk-body").text()      should include("Track availability for other HMRC services")

      }

      "there are no issues, no planned work and there is a CLS update" in {

        val document = view(AVAILABLE, unplannedOutageData = Some(fakeOutageData(Unplanned, None)), plannedWorksHappeningToday = List()).asDocument

        document.getElementsByClass("govuk-heading-l").text() shouldBe "Service availability for GVMS"
        document.getElementsByClass("govuk-heading-m").text()   should include("Live service status")

        document.getElementsByClass("govuk-tag--green").text() shouldBe "No issues detected"
        document.getElementById("refresh-link").attr("href").contains("/customs-service-status/service-availability/status")
        document.getElementsByClass("govuk-body").text() should include("Refresh this page to check for changes.")

        document.getElementsByClass("hmrc-timeline__event-title govuk-table__caption--s").text() should include(
          s"Update at ${Formatters.instantFormatHours(validUnplannedOutageData.publishedDateTime)} on ${Formatters.instantFormatDate(validUnplannedOutageData.publishedDateTime)}"
        )

        document.getElementsByClass("govuk-heading-m").text() should include("Planned work")
        document.getElementById("plannedwork-link").attr("href").contains("/customs-service-status/service-availability/planned-work")
        document.getElementsByClass("govuk-body").text() should include("Find out when future outages are happening")

        document.getElementsByClass("govuk-heading-m").text() should include("Other HMRC services")
        document
          .getElementById("available_p3_link")
          .attr("href")
          .contains("https://www.gov.uk/government/collections/hm-revenue-and-customs-service-availability-and-issues")
        document.getElementsByClass("govuk-body").text() should include("Track availability for other HMRC services")

      }

      "there are no issues, there is planned work and there is a CLS update" in {

        val document = view(
          AVAILABLE,
          unplannedOutageData = Some(fakeOutageData(Unplanned, None)),
          plannedWorksHappeningToday = List(fakePlannedWork)
        ).asDocument

        document.getElementsByClass("govuk-heading-l").text() shouldBe "Service availability for GVMS"
        document.getElementsByClass("govuk-heading-m").text()   should include("Live service status")

        document.getElementsByClass("govuk-tag--green").text() shouldBe "No issues detected"
        document.getElementById("refresh-link").attr("href").contains("/customs-service-status/service-availability/status")
        document.getElementsByClass("govuk-body").text() should include("Refresh this page to check for changes.")

        document.getElementsByClass("hmrc-timeline__event-title govuk-table__caption--s").text() should include(
          s"Update at ${Formatters.instantFormatHours(validUnplannedOutageData.publishedDateTime)} on ${Formatters.instantFormatDate(validUnplannedOutageData.publishedDateTime)}"
        )

        val expectedDateFrom: String =
          s"From: ${Formatters.instantFormatHours(fakePlannedWork.startDateTime)} on ${Formatters.instantFormatDate(fakePlannedWork.startDateTime)}"
        val expectedDateTo: String =
          s"To: ${Formatters.instantFormatHours(fakePlannedWork.endDateTime.get)} on ${Formatters.instantFormatDate(fakePlannedWork.endDateTime.get)}"
        val expectedDetails: String = fakePlannedWork.commsText.html

        document.getElementsByClass("govuk-heading-m").text() should include("Planned work happening today")
        document.getElementsByClass("govuk-body").text()      should include(s"$expectedDateFrom")
        document.getElementsByClass("govuk-body").text()      should include(s"$expectedDateTo")
        document.getElementsByClass("govuk-body").text()      should include("Details:")
        document.getElementsByClass("govuk-body").text()      should include(expectedDetails)
        document.getElementsByClass("govuk-heading-m").text() should include("Planned work happening later")

        document.getElementById("plannedwork-link").attr("href").contains("/customs-service-status/service-availability/planned-work")
        document.getElementsByClass("govuk-body").text() should include("Find out when future outages are happening")

        document.getElementsByClass("govuk-heading-m").text() should include("Other HMRC services")
        document
          .getElementById("available_p3_link")
          .attr("href")
          .contains("https://www.gov.uk/government/collections/hm-revenue-and-customs-service-availability-and-issues")
        document.getElementsByClass("govuk-body").text() should include("Track availability for other HMRC services")

      }

      "there are no issues, there is planned work with start date before current date, end date being after current date and no CLS update" in {

        val document =
          view(
            AVAILABLE,
            unplannedOutageData = None,
            plannedWorksHappeningToday = List(fakeOutageData(Planned, Some(fakeCurrentDate.plus(1, ChronoUnit.DAYS))))
          ).asDocument

        document.getElementsByClass("govuk-heading-l").text() shouldBe "Service availability for GVMS"
        document.getElementsByClass("govuk-heading-m").text()   should include("Live service status")

        document.getElementsByClass("govuk-tag--green").text() shouldBe "No issues detected"
        document.getElementById("refresh-link").attr("href").contains("/customs-service-status/service-availability/status")
        document.getElementsByClass("govuk-body").text() should include("Refresh this page to check for changes.")

        val expectedDateFrom: String =
          s"From: ${Formatters.instantFormatHours(fakePlannedWork.startDateTime)} on ${Formatters.instantFormatDate(fakePlannedWork.startDateTime)}"
        val expectedDateTo: String =
          s"To: ${Formatters.instantFormatHours(fakePlannedWork.endDateTime.get)} on ${Formatters.instantFormatDate(fakePlannedWork.endDateTime.get)}"
        val expectedDetails: String = fakePlannedWork.commsText.html

        document.getElementsByClass("govuk-heading-m").text() should include("Planned work happening today")
        document.getElementsByClass("govuk-body").text()      should include(s"$expectedDateFrom")
        document.getElementsByClass("govuk-body").text()      should include(s"$expectedDateTo")
        document.getElementsByClass("govuk-body").text()      should include("Details:")
        document.getElementsByClass("govuk-body").text()      should include(expectedDetails)
        document.getElementsByClass("govuk-heading-m").text() should include("Planned work happening later")

        document.getElementsByClass("govuk-heading-m").text() should include("Planned work happening later")
        document.getElementById("refresh-link").attr("href").contains("/customs-service-status/service-availability/planned-work")
        document.getElementsByClass("govuk-body").text() should include("Find out when future outages are happening")

        document.getElementsByClass("govuk-heading-m").text() should include("Other HMRC services")
        document
          .getElementById("available_p3_link")
          .attr("href")
          .contains("https://www.gov.uk/government/collections/hm-revenue-and-customs-service-availability-and-issues")
        document.getElementsByClass("govuk-body").text() should include("Track availability for other HMRC services")

      }

      "there are no issues, there is planned work with start date being the current date, end date being after current date and no CLS update" in {

        val document =
          view(
            AVAILABLE,
            unplannedOutageData = None,
            plannedWorksHappeningToday = List(fakePlannedWorkWithCurrentDateAsStartDate)
          ).asDocument

        document.getElementsByClass("govuk-heading-l").text() shouldBe "Service availability for GVMS"
        document.getElementsByClass("govuk-heading-m").text()   should include("Live service status")

        document.getElementsByClass("govuk-tag--green").text() shouldBe "No issues detected"
        document.getElementById("refresh-link").attr("href").contains("/customs-service-status/service-availability/status")
        document.getElementsByClass("govuk-body").text() should include("Refresh this page to check for changes.")

        val expectedDateFrom: String =
          s"From: ${Formatters.instantFormatHours(fakePlannedWorkWithCurrentDateAsStartDate.startDateTime)} on ${Formatters.instantFormatDate(fakePlannedWorkWithCurrentDateAsStartDate.startDateTime)}"
        val expectedDateTo: String =
          s"To: ${Formatters.instantFormatHours(fakePlannedWorkWithCurrentDateAsStartDate.endDateTime.get)} on ${Formatters.instantFormatDate(fakePlannedWorkWithCurrentDateAsStartDate.endDateTime.get)}"
        val expectedDetails: String = fakePlannedWork.commsText.html

        document.getElementsByClass("govuk-heading-m").text() should include("Planned work happening today")
        document.getElementsByClass("govuk-body").text()      should include(s"$expectedDateFrom")
        document.getElementsByClass("govuk-body").text()      should include(s"$expectedDateTo")
        document.getElementsByClass("govuk-body").text()      should include("Details:")
        document.getElementsByClass("govuk-body").text()      should include(expectedDetails)
        document.getElementsByClass("govuk-heading-m").text() should include("Planned work happening later")

        document.getElementsByClass("govuk-heading-m").text() should include("Planned work happening later")
        document.getElementById("refresh-link").attr("href").contains("/customs-service-status/service-availability/planned-work")
        document.getElementsByClass("govuk-body").text() should include("Find out when future outages are happening")

        document.getElementsByClass("govuk-heading-m").text() should include("Other HMRC services")
        document
          .getElementById("available_p3_link")
          .attr("href")
          .contains("https://www.gov.uk/government/collections/hm-revenue-and-customs-service-availability-and-issues")
        document.getElementsByClass("govuk-body").text() should include("Track availability for other HMRC services")

      }

      "there are no issues, there is planned work with start date before current date, end date being the current date and no CLS update" in {

        val document =
          view(
            AVAILABLE,
            unplannedOutageData = None,
            plannedWorksHappeningToday = List(fakePlannedWorkWithCurrentDateAsEndDate)
          ).asDocument

        document.getElementsByClass("govuk-heading-l").text() shouldBe "Service availability for GVMS"
        document.getElementsByClass("govuk-heading-m").text()   should include("Live service status")

        document.getElementsByClass("govuk-tag--green").text() shouldBe "No issues detected"
        document.getElementById("refresh-link").attr("href").contains("/customs-service-status/service-availability/status")
        document.getElementsByClass("govuk-body").text() should include("Refresh this page to check for changes.")

        val expectedDateFrom: String =
          s"From: ${Formatters.instantFormatHours(fakePlannedWorkWithCurrentDateAsEndDate.startDateTime)} on ${Formatters.instantFormatDate(fakePlannedWorkWithCurrentDateAsEndDate.startDateTime)}"
        val expectedDateTo: String =
          s"To: ${Formatters.instantFormatHours(fakePlannedWorkWithCurrentDateAsEndDate.endDateTime.get)} on ${Formatters.instantFormatDate(fakePlannedWorkWithCurrentDateAsEndDate.endDateTime.get)}"
        val expectedDetails: String = fakePlannedWork.commsText.html

        document.getElementsByClass("govuk-heading-m").text() should include("Planned work happening today")
        document.getElementsByClass("govuk-body").text()      should include(s"$expectedDateFrom")
        document.getElementsByClass("govuk-body").text()      should include(s"$expectedDateTo")
        document.getElementsByClass("govuk-body").text()      should include("Details:")
        document.getElementsByClass("govuk-body").text()      should include(expectedDetails)
        document.getElementsByClass("govuk-heading-m").text() should include("Planned work happening later")

        document.getElementsByClass("govuk-heading-m").text() should include("Planned work happening later")
        document.getElementById("refresh-link").attr("href").contains("/customs-service-status/service-availability/planned-work")
        document.getElementsByClass("govuk-body").text() should include("Find out when future outages are happening")

        document.getElementsByClass("govuk-heading-m").text() should include("Other HMRC services")
        document
          .getElementById("available_p3_link")
          .attr("href")
          .contains("https://www.gov.uk/government/collections/hm-revenue-and-customs-service-availability-and-issues")
        document.getElementsByClass("govuk-body").text() should include("Track availability for other HMRC services")

      }

      "there are no issues, there is planned work with start and end date being the current date and no CLS update" in {

        val document =
          view(
            AVAILABLE,
            unplannedOutageData = None,
            plannedWorksHappeningToday = List(fakePlannedWorkWithCurrentDateAsStartAndEndDate)
          ).asDocument

        document.getElementsByClass("govuk-heading-l").text() shouldBe "Service availability for GVMS"
        document.getElementsByClass("govuk-heading-m").text()   should include("Live service status")

        document.getElementsByClass("govuk-tag--green").text() shouldBe "No issues detected"
        document.getElementById("refresh-link").attr("href").contains("/customs-service-status/service-availability/status")
        document.getElementsByClass("govuk-body").text() should include("Refresh this page to check for changes.")

        val expectedDateFrom: String =
          s"From: ${Formatters.instantFormatHours(fakePlannedWorkWithCurrentDateAsStartAndEndDate.startDateTime)} on ${Formatters.instantFormatDate(fakePlannedWorkWithCurrentDateAsStartAndEndDate.startDateTime)}"
        val expectedDateTo: String =
          s"To: ${Formatters.instantFormatHours(fakePlannedWorkWithCurrentDateAsStartAndEndDate.endDateTime.get)} on ${Formatters.instantFormatDate(fakePlannedWorkWithCurrentDateAsStartAndEndDate.endDateTime.get)}"
        val expectedDetails: String = fakePlannedWork.commsText.html

        document.getElementsByClass("govuk-heading-m").text() should include("Planned work happening today")
        document.getElementsByClass("govuk-body").text()      should include(s"$expectedDateFrom")
        document.getElementsByClass("govuk-body").text()      should include(s"$expectedDateTo")
        document.getElementsByClass("govuk-body").text()      should include("Details:")
        document.getElementsByClass("govuk-body").text()      should include(expectedDetails)
        document.getElementsByClass("govuk-heading-m").text() should include("Planned work happening later")

        document.getElementsByClass("govuk-heading-m").text() should include("Planned work happening later")
        document.getElementById("refresh-link").attr("href").contains("/customs-service-status/service-availability/planned-work")
        document.getElementsByClass("govuk-body").text() should include("Find out when future outages are happening")

        document.getElementsByClass("govuk-heading-m").text() should include("Other HMRC services")
        document
          .getElementById("available_p3_link")
          .attr("href")
          .contains("https://www.gov.uk/government/collections/hm-revenue-and-customs-service-availability-and-issues")
        document.getElementsByClass("govuk-body").text() should include("Track availability for other HMRC services")

      }

      "there are issues, but no planned work and no CLS updates posted" in {

        val document =
          view(
            UNAVAILABLE,
            unplannedOutageData = None,
            plannedWorksHappeningToday = List()
          ).asDocument

        document.getElementsByClass("govuk-heading-l").text()   shouldBe "Service availability for GVMS"
        document.getElementsByClass("govuk-heading-m").text()     should include("Live service status")
        document.getElementsByClass("govuk-tag--orange").text() shouldBe "Issue detected"

        document.getElementById("refresh-link").attr("href").contains("/customs-service-status/service-availability/status")
        document.getElementsByClass("govuk-body").text() should include("Refresh this page to check for changes.")

        document.getElementsByClass("hmrc-timeline__event-title govuk-table__caption--s").text() should include(
          s"Issue detected at ${Formatters.instantFormatHours(Instant.now)} on ${Formatters.instantFormatDate(Instant.now)}"
        )

        val timelineText = document.getElementsByClass("hmrc-timeline__event-content")
        timelineText.size shouldBe 2

        timelineText.get(0).text() should include(
          "You may not be able to create a Goods Movement Reference (GMR) or manage your GMRs."
        )
        timelineText.get(1).text() should include(
          "Do not travel to the border without a valid GMR. We’ll post any further updates here."
        )

        document.getElementsByClass("govuk-heading-m").text() should include("Planned work")
        document.getElementsByClass("govuk-body").text()      should include("Find out when future outages are happening")

        document.getElementsByClass("govuk-heading-m").text() should include("Other HMRC services")
        document.getElementsByClass("govuk-body").text()      should include("Track availability for other HMRC services")

      }

      "there are issues, no planned work and there is a CLS update" in {

        val document =
          view(
            UNAVAILABLE,
            unplannedOutageData = Some(fakeOutageData(Unplanned, None)),
            plannedWorksHappeningToday = List()
          ).asDocument

        document.getElementsByClass("govuk-heading-l").text()   shouldBe "Service availability for GVMS"
        document.getElementsByClass("govuk-heading-m").text()     should include("Live service status")
        document.getElementsByClass("govuk-tag--orange").text() shouldBe "Issue detected"

        document.getElementById("refresh-link").attr("href").contains("/customs-service-status/service-availability/status")
        document.getElementsByClass("govuk-body").text() should include("Refresh this page to check for changes.")

        val timelineHeaders = document.getElementsByClass("hmrc-timeline__event-title govuk-table__caption--s")
        timelineHeaders.size shouldBe 2

        timelineHeaders.get(0).text() should include(
          s"Update at ${Formatters.instantFormatHours(validUnplannedOutageData.publishedDateTime)} on ${Formatters.instantFormatDate(validUnplannedOutageData.publishedDateTime)}"
        )

        timelineHeaders.get(1).text() should include(
          s"Issue detected at ${Formatters.instantFormatHours(validUnplannedOutageData.publishedDateTime)} on ${Formatters.instantFormatDate(validPlannedOutageData.publishedDateTime)}"
        )

        document.getElementsByClass("govuk-heading-m").text() should include("Planned work")
        document.getElementsByClass("govuk-body").text()      should include("Find out when future outages are happening")

        document.getElementsByClass("govuk-heading-m").text() should include("Other HMRC services")
        document.getElementsByClass("govuk-body").text()      should include("Track availability for other HMRC services")

      }

      "there are issues, there is planned work and there is a CLS update" in {

        val document =
          view(
            UNAVAILABLE,
            unplannedOutageData = Some(fakeOutageData(Unplanned, None)),
            plannedWorksHappeningToday = List(fakePlannedWork)
          ).asDocument

        document.getElementsByClass("govuk-heading-l").text()   shouldBe "Service availability for GVMS"
        document.getElementsByClass("govuk-heading-m").text()     should include("Live service status")
        document.getElementsByClass("govuk-tag--orange").text() shouldBe "Issue detected"

        document.getElementById("refresh-link").attr("href").contains("/customs-service-status/service-availability/status")
        document.getElementsByClass("govuk-body").text() should include("Refresh this page to check for changes.")

        val timelineHeaders = document.getElementsByClass("hmrc-timeline__event-title govuk-table__caption--s")
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

        document.getElementsByClass("govuk-heading-m").text() should include("Planned work happening today")
        document.getElementsByClass("govuk-body").text()      should include(s"$expectedDateFrom")
        document.getElementsByClass("govuk-body").text()      should include(s"$expectedDateTo")
        document.getElementsByClass("govuk-body").text()      should include("Details:")
        document.getElementsByClass("govuk-body").text()      should include(expectedDetails)
        document.getElementsByClass("govuk-heading-m").text() should include("Planned work happening later")

        document.getElementsByClass("govuk-heading-m").text() should include("Planned work happening later")
        document.getElementById("refresh-link").attr("href").contains("/customs-service-status/service-availability/planned-work")
        document.getElementsByClass("govuk-body").text() should include("Find out when future outages are happening")

        document.getElementsByClass("govuk-heading-m").text() should include("Other HMRC services")
        document.getElementsByClass("govuk-body").text()      should include("Track availability for other HMRC services")

      }

      "status is unknown" in {

        val document =
          view(
            UNKNOWN,
            unplannedOutageData = Some(validUnplannedOutageData),
            plannedWorksHappeningToday = List(fakePlannedWork)
          ).asDocument

        document.getElementsByClass("govuk-heading-l").text() shouldBe "GVMS availability unknown"
        document.getElementsByClass("govuk-body").text()        should include("The Check GVMS availability service is not working right now.")
        document.getElementsByClass("govuk-body").text() should include(
          "You can check if the service is working by logging in to the Goods Vehicle Movement Service."
        )
        document
          .getElementById("service_url")
          .attr("href") shouldBe "https://www.gov.uk/guidance/get-a-goods-movement-reference#get-a-goods-movement-reference"
      }
    }
  }

}
