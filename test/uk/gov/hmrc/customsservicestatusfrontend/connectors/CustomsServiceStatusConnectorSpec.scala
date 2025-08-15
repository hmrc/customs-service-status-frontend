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

package uk.gov.hmrc.customsservicestatusfrontend.connectors

import org.mockito.ArgumentMatchers.any
import uk.gov.hmrc.customsservicestatusfrontend.helpers.BaseSpec
import uk.gov.hmrc.customsservicestatusfrontend.helpers.TestData.{fakePlannedWork, serviceStatuses}
import uk.gov.hmrc.customsservicestatusfrontend.models.{OutageData, ServiceStatuses}
import uk.gov.hmrc.http.*
import org.mockito.Mockito.*
import uk.gov.hmrc.http.client.RequestBuilder

import java.net.URL
import scala.concurrent.{ExecutionContext, Future}

class CustomsServiceStatusConnectorSpec extends BaseSpec {

  val url = "http://localhost:8991/customs-service-status"

  val connector = new CustomsServiceStatusConnector(mockHttpClient, url)

  "getStatus" should {
    "return response as expected" in {

      when(mockHttpClient.get(any())(any())).thenReturn(mockRequestBuilder)
      when(mockRequestBuilder.execute(any(), any())).thenReturn(Future.successful(serviceStatuses))

      connector.getStatus().futureValue shouldBe serviceStatuses
    }
  }

  "getPlannedWork" should {
    "return planned work" in {
      when(
        mockHttpClient
          .get(any())(any())
      )
        .thenReturn(mockRequestBuilder)

      when(
        mockRequestBuilder
          .execute(any(), any())
      )
        .thenReturn(Future(List(fakePlannedWork)))

      connector.getAllPlannedWorks().futureValue shouldBe List(fakePlannedWork)
    }
  }
}
