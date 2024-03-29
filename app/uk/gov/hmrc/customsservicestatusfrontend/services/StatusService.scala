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

package uk.gov.hmrc.customsservicestatusfrontend.services

import com.google.inject.{Inject, Singleton}
import play.api.Logging
import uk.gov.hmrc.customsservicestatusfrontend.connectors.CustomsServiceStatusConnector
import uk.gov.hmrc.customsservicestatusfrontend.models.ServiceStatuses
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.Future

@Singleton
class StatusService @Inject() (customsServiceStatusConnector: CustomsServiceStatusConnector) extends Logging {

  def getStatus()(implicit hc: HeaderCarrier): Future[ServiceStatuses] =
    customsServiceStatusConnector.getStatus()
}
