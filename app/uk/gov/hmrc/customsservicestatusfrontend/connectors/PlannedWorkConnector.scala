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

package uk.gov.hmrc.customsservicestatusfrontend.connectors

import uk.gov.hmrc.customsservicestatusfrontend.models.PlannedWork
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, StringContextOps}
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig
import uk.gov.hmrc.http.HttpReads.Implicits

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class PlannedWorkConnector @Inject() (
  httpClient:     HttpClientV2,
  servicesConfig: ServicesConfig
)(implicit ec: ExecutionContext) {

  lazy val serviceUrl: String = servicesConfig.baseUrl("customs-service-status")

  def getPlannedWork()(implicit headerCarrier: HeaderCarrier): Future[List[PlannedWork]] =
    httpClient
      .get(url"$serviceUrl/customs-service-status/services/planned-work")
      .execute[List[PlannedWork]]
      .map { plannedWork =>
        println("BBBB" + plannedWork)
        plannedWork
      }

}
