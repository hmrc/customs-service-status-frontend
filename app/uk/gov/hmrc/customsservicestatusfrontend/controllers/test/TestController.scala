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
import uk.gov.hmrc.customsservicestatusfrontend.models.OutageType.Unplanned
import uk.gov.hmrc.customsservicestatusfrontend.models.State.{AVAILABLE, UNAVAILABLE, UNKNOWN}
import uk.gov.hmrc.customsservicestatusfrontend.services.OutageService
import uk.gov.hmrc.customsservicestatusfrontend.services.test.TestService
import uk.gov.hmrc.customsservicestatusfrontend.views.html.DashboardPage
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import java.time.{Instant, LocalDate}
import scala.concurrent.ExecutionContext

@Singleton
class TestController @Inject() (
  dashboardPage: DashboardPage,
  testService:   TestService,
  outageService: OutageService
)(implicit val ec: ExecutionContext, mcc: MessagesControllerComponents)
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
    outageService.getLatest(outageType = Unplanned).map { outageData =>
      Ok(dashboardPage(AVAILABLE, Instant.now(), "haulier", outageData, None, LocalDate.now(), Some(LocalDate.now()), Some(LocalDate.now())))
    }
  }

  val showUnavailable: Action[AnyContent] = Action.async { implicit request =>
    outageService.getLatest(outageType = Unplanned).map { outageData =>
      Ok(dashboardPage(UNAVAILABLE, Instant.now(), "haulier", outageData, None, LocalDate.now(), Some(LocalDate.now()), Some(LocalDate.now())))
    }
  }

  val showUnknown: Action[AnyContent] = Action.async { implicit request =>
    outageService.getLatest(outageType = Unplanned).map { outageData =>
      Ok(dashboardPage(UNKNOWN, Instant.now(), "haulier", outageData, None, LocalDate.now(), Some(LocalDate.now()), Some(LocalDate.now())))
    }
  }
}
