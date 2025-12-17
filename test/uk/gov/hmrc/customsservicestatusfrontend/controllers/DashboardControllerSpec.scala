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
import uk.gov.hmrc.customsservicestatusfrontend.TestData.*
import uk.gov.hmrc.customsservicestatusfrontend.models.State.{UNAVAILABLE, UNKNOWN}
import uk.gov.hmrc.customsservicestatusfrontend.models.{CustomsServiceStatus, OutageData, OutageType, ServiceStatuses}
import uk.gov.hmrc.customsservicestatusfrontend.services.{OutageService, PlannedWorkService, StatusService}
import uk.gov.hmrc.customsservicestatusfrontend.factories.ServiceStatusFactory.*
import uk.gov.hmrc.customsservicestatusfrontend.views.html.DashboardView
import uk.gov.hmrc.customsservicestatusfrontend.utils.Now

import java.time.Instant
import scala.concurrent.Future

class DashboardControllerSpec extends ControllerBaseSpec {

  private val fakeRequest = FakeRequest("GET", "/service-availability")
  private val dashboardView: DashboardView = new DashboardView(layout)
  private val mockService            = mock[StatusService]
  private val mockOutageService      = mock[OutageService]
  private val mockPlannedWorkService = mock[PlannedWorkService]
  private val fakeNow: Now = new Now {
    override def apply: Instant = fakeDate
  }

  private val controller = new DashboardController(
    stubMessagesControllerComponents(),
    dashboardView,
    mockService,
    mockOutageService,
    mockPlannedWorkService
  )(ec, fakeNow)

  private val serviceStatusesWithUnavailableState: ServiceStatuses = ServiceStatuses(List(serviceStatus(state = Some(UNAVAILABLE))))

  "GET /service-availability" should {
    "show dashboard content as expected" when {
      "there are no issues, there is planned work and there is a CLS update" in {
        when(mockService.getStatus()(any())).thenReturn(Future.successful(serviceStatuses))
        when(mockOutageService.getLatest(ArgumentMatchers.eq(OutageType.Unplanned))(any()))
          .thenReturn(Future.successful(Some(fakeUnplannedOutage)))
        when(mockPlannedWorkService.getAllPlannedWorks()(any())).thenReturn(Future.successful(List(fakePlannedWork)))

        val result = controller.show(fakeRequest)
        status(result) shouldBe Status.OK

        val doc = Jsoup.parse(contentAsString(result))
        doc.getElementsByClass("govuk-heading-l").text() shouldBe "Service availability for GVMS"

      }

      "there are issues, but no planned work and no CLS updates posted" in {
        when(mockService.getStatus()(any())).thenReturn(Future.successful(serviceStatuses))
        when(mockService.getStatus()(any())).thenReturn(Future.successful(serviceStatusesWithUnavailableState))

        when(mockOutageService.getLatest(any())(any())).thenReturn(Future.successful(None))
        when(mockPlannedWorkService.getAllPlannedWorks()(any())).thenReturn(Future.successful(List()))

        val result = controller.show(fakeRequest)
        status(result) shouldBe Status.OK

        val doc = Jsoup.parse(contentAsString(result))
        doc.getElementsByClass("govuk-heading-l").text() shouldBe "Service availability for GVMS"

      }

      "status is unknown" in {
        when(mockService.getStatus()(any())).thenReturn(Future.successful(ServiceStatuses(List(serviceStatus(state = Some(UNKNOWN))))))
        when(mockOutageService.getLatest(any())(any())).thenReturn(Future.successful(Some(fakeUnplannedOutage)))
        when(mockPlannedWorkService.getAllPlannedWorks()(any())).thenReturn(Future.successful(List(fakePlannedWork)))

        val result = controller.show(fakeRequest)
        status(result) shouldBe Status.OK

        val doc = Jsoup.parse(contentAsString(result))
        doc.getElementsByClass("govuk-heading-l").text() shouldBe "GVMS availability unknown"
      }
    }
  }
}
