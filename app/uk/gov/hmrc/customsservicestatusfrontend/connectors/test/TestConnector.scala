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

package uk.gov.hmrc.customsservicestatusfrontend.connectors.test

import com.google.inject.Singleton
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, HttpReads, HttpResponse, StringContextOps}
import uk.gov.hmrc.http.HttpReads.Implicits.*

import javax.inject.{Inject, Named}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class TestConnector @Inject() (
  httpClient:                                                    HttpClientV2,
  @Named("customsServiceStatusUrl") customsServiceStatusBaseUrl: String
)(implicit ec: ExecutionContext) {

  def clearAllData()(implicit headerCarrier: HeaderCarrier): Future[HttpResponse] =
    httpClient
      .get(url"$customsServiceStatusBaseUrl/customs-service-status/test-only/clear-all")
      .execute

}
