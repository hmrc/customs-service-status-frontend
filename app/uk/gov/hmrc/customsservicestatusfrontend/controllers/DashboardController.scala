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

import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.customsservicestatusfrontend.models.OutageType.{Planned, Unplanned}
import uk.gov.hmrc.customsservicestatusfrontend.models.State.{AVAILABLE, UNAVAILABLE, UNKNOWN}
import uk.gov.hmrc.customsservicestatusfrontend.services.{OutageService, StatusService}
import uk.gov.hmrc.customsservicestatusfrontend.views.html.DashboardPage

import java.time.{Instant, LocalDate, ZoneId}
import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class DashboardController @Inject() (
  mcc:           MessagesControllerComponents,
  dashboardPage: DashboardPage,
  statusService: StatusService,
  outageService: OutageService
)(implicit ec: ExecutionContext)
    extends BaseFrontendController(mcc) {

  val show: Action[AnyContent] = Action.async { implicit request =>
    for {
      statuses            <- statusService.getStatus()
      unplannedOutageData <- outageService.getLatest(outageType = Unplanned)
      plannedOutageData   <- outageService.getLatest(outageType = Planned)
    } yield {
      val uiState =
        if (statuses.services.forall(_.state.contains(AVAILABLE)))
          AVAILABLE
        else if (statuses.services.exists(_.state.contains(UNAVAILABLE)))
          UNAVAILABLE
        else
          UNKNOWN

      val stateChangedAt = statuses.services.find(_.state.contains(UNAVAILABLE)).flatMap(_.stateChangedAt).getOrElse(Instant.now())

      val today = LocalDate.now(ZoneId.systemDefault())
      val plannedOutageStartDate: Option[LocalDate] = plannedOutageData.map(_.startDateTime.atZone(ZoneId.systemDefault()).toLocalDate)
      val plannedOutageEndDate:   Option[LocalDate] = plannedOutageData.flatMap(_.endDateTime.map(_.atZone(ZoneId.systemDefault()).toLocalDate))

      Ok(
        dashboardPage(uiState, stateChangedAt, "haulier", unplannedOutageData, plannedOutageData, today, plannedOutageStartDate, plannedOutageEndDate)
      )
    }
  }
}
