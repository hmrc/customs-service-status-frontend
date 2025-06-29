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

package uk.gov.hmrc.customsservicestatusfrontend.controllers

import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.customsservicestatusfrontend.views.html.ManageDashboardPage

import javax.inject.{Inject, Singleton}

@Singleton
class ManageDashboardController @Inject (
  mcc:                 MessagesControllerComponents,
  manageDashboardPage: ManageDashboardPage
) extends BaseFrontendController(mcc) {

  val show: Action[AnyContent] = Action { implicit request =>
    Ok(manageDashboardPage())
  }
}
