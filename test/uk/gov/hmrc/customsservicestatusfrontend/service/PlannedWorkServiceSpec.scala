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

package uk.gov.hmrc.customsservicestatusfrontend.service

import uk.gov.hmrc.customsservicestatusfrontend.connectors.CustomsServiceStatusConnector
import uk.gov.hmrc.customsservicestatusfrontend.helpers.BaseSpec
import uk.gov.hmrc.customsservicestatusfrontend.services.PlannedWorkService
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.customsservicestatusfrontend.helpers.TestData.fakePlannedWork
import org.mockito.Mockito.*
import org.mockito.ArgumentMatchers.any

import scala.concurrent.Future

class PlannedWorkServiceSpec extends BaseSpec {

  val mockConnector: CustomsServiceStatusConnector = mock[CustomsServiceStatusConnector]
  val service = new PlannedWorkService(mockConnector)

  "getAllPlannedWork" should {
    "return planned work as expected" in {
      when(
        mockConnector
          .getAllPlannedWorks()(any())
      )
        .thenReturn(Future.successful(List(fakePlannedWork)))

      service.getAllPlannedWorks().futureValue shouldBe List(fakePlannedWork)
    }
  }

}
