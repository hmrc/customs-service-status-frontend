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

package uk.gov.hmrc.customsservicestatusfrontend.controllers.test

import com.google.inject.*
import play.api.Logging
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.customsservicestatusfrontend.models.DetailType.{CommsText, InternalReference}
import uk.gov.hmrc.customsservicestatusfrontend.models.OutageData
import uk.gov.hmrc.customsservicestatusfrontend.models.OutageType.{Planned, Unplanned}
import uk.gov.hmrc.customsservicestatusfrontend.models.State.{AVAILABLE, UNAVAILABLE, UNKNOWN}
import uk.gov.hmrc.customsservicestatusfrontend.services.OutageService
import uk.gov.hmrc.customsservicestatusfrontend.services.test.TestService
import uk.gov.hmrc.customsservicestatusfrontend.views.html.DashboardPage
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import uk.gov.hmrc.customsservicestatusfrontend.utils.Now

import java.time.Instant
import java.util.UUID
import scala.concurrent.ExecutionContext

@Singleton
class TestController @Inject() (
  dashboardPage: DashboardPage,
  testService:   TestService,
  outageService: OutageService
)(implicit val ec: ExecutionContext, mcc: MessagesControllerComponents, now: Now)
    extends FrontendController(mcc)
    with Logging {

  def clearAllData: Action[AnyContent] =
    Action.async { implicit request =>
      logger.warn("clear all data called")
      testService.clearAllData.map(_ => Ok).recover { case e: Exception =>
        logger.warn("clear all data failed", e)
        InternalServerError(s"Failed to clear data: ${e.getMessage}")
      }
    }

  val showAvailable: Action[AnyContent] = Action.async { implicit request =>
    val plannedWorksHappeningToday = List(
      OutageData(
        id = UUID.randomUUID(),
        outageType = Planned,
        internalReference = InternalReference("Test reference"),
        startDateTime = Instant.parse("2020-01-01T00:00:00.000Z"),
        endDateTime = Some(Instant.now()),
        commsText = CommsText("Test details"),
        publishedDateTime = Instant.parse("2020-01-01T00:00:00.000Z"),
        clsNotes = Some("Notes")
      )
    )
    outageService.getLatest(outageType = Unplanned).map { unplannedOutageData =>
      Ok(dashboardPage(AVAILABLE, Instant.now(), "haulier", unplannedOutageData, plannedWorksHappeningToday, now.apply))
    }
  }

  val showUnavailable: Action[AnyContent] = Action.async { implicit request =>
    val plannedWorksHappeningToday = List(
      OutageData(
        id = UUID.randomUUID(),
        outageType = Planned,
        internalReference = InternalReference("Test reference"),
        startDateTime = Instant.parse("2020-01-01T00:00:00.000Z"),
        endDateTime = Some(Instant.now()),
        commsText = CommsText("Test details"),
        publishedDateTime = Instant.parse("2020-01-01T00:00:00.000Z"),
        clsNotes = Some("Notes")
      )
    )
    outageService.getLatest(outageType = Unplanned).map { unplannedOutageData =>
      Ok(dashboardPage(UNAVAILABLE, Instant.now(), "haulier", unplannedOutageData, plannedWorksHappeningToday, now.apply))
    }
  }

  val showUnknown: Action[AnyContent] = Action.async { implicit request =>
    val plannedWorksHappeningToday = List(
      OutageData(
        id = UUID.randomUUID(),
        outageType = Planned,
        internalReference = InternalReference("Test reference"),
        startDateTime = Instant.parse("2020-01-01T00:00:00.000Z"),
        endDateTime = Some(Instant.now()),
        commsText = CommsText("Test details"),
        publishedDateTime = Instant.parse("2020-01-01T00:00:00.000Z"),
        clsNotes = Some("Notes")
      )
    )
    outageService.getLatest(outageType = Unplanned).map { unplannedOutageData =>
      Ok(dashboardPage(UNKNOWN, Instant.now(), "haulier", unplannedOutageData, plannedWorksHappeningToday, now.apply))
    }
  }
}
