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
import uk.gov.hmrc.customsservicestatusfrontend.models.OutageType.Unplanned
import uk.gov.hmrc.customsservicestatusfrontend.models.State.{AVAILABLE, UNAVAILABLE, UNKNOWN}
import uk.gov.hmrc.customsservicestatusfrontend.services.{OutageService, PlannedWorkService, StatusService}
import uk.gov.hmrc.customsservicestatusfrontend.views.html.DashboardView
import uk.gov.hmrc.customsservicestatusfrontend.utils.Now
import uk.gov.hmrc.customsservicestatusfrontend.utils.PlannedWorksHappeningToday

import java.time.Instant
import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class DashboardController @Inject() (
  mcc:                MessagesControllerComponents,
  dashboardPage:      DashboardView,
  statusService:      StatusService,
  outageService:      OutageService,
  plannedWorkService: PlannedWorkService,
  plannedWorksUtil:   PlannedWorksHappeningToday
)(implicit ec: ExecutionContext, now: Now)
    extends BaseFrontendController(mcc) {

  val show: Action[AnyContent] = Action.async { implicit request =>
    for {
      statuses            <- statusService.getStatus()
      unplannedOutageData <- outageService.getLatest(outageType = Unplanned)
      plannedOutageData   <- plannedWorkService.getAllPlannedWorks()
    } yield {
      val uiState =
        if (statuses.services.forall(_.state.contains(AVAILABLE)))
          AVAILABLE
        else if (statuses.services.exists(_.state.contains(UNAVAILABLE)))
          UNAVAILABLE
        else
          UNKNOWN

      val stateChangedAt = statuses.services.find(_.state.contains(UNAVAILABLE)).flatMap(_.stateChangedAt).getOrElse(Instant.now())

      Ok(
        dashboardPage(
          uiState,
          stateChangedAt,
          "haulier",
          unplannedOutageData,
          plannedWorksUtil.plannedWorksHappeningToday(plannedOutageData),
          now.apply
        )
      )
    }
  }
}
