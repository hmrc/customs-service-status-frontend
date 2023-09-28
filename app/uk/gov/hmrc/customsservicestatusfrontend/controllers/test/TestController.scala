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

import com.google.inject._
import play.api.Logging
import play.api.mvc._
import uk.gov.hmrc.customsservicestatusfrontend.services.test.TestService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import scala.concurrent.ExecutionContext

@Singleton
class TestController @Inject() (
  testService:     TestService
)(implicit val ec: ExecutionContext, cc: MessagesControllerComponents)
    extends FrontendController(cc)
    with Logging {

  def clearAllData: Action[AnyContent] =
    Action.async { implicit request =>
      logger.warn("clear all data called")
      testService.clearAllData.map(_ => Ok).recover { case e: Exception =>
        logger.warn("clear all data failed", e)
        InternalServerError(s"Failed to clear data: ${e.getMessage}")
      }
    }
}
