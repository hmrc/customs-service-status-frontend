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

package uk.gov.hmrc.customsservicestatusfrontend.helpers

import uk.gov.hmrc.customsservicestatusfrontend.models.State.OK
import uk.gov.hmrc.customsservicestatusfrontend.models.{CustomsServiceStatus, CustomsServiceStatusAll, Status}

import java.time.Instant

object TestData {

  val now: Instant = Instant.now()

  val customsServiceStatus: CustomsServiceStatus = CustomsServiceStatus("Haulier", Status(Some(OK), Some(now)), "some description")

  val customsServiceStatusAll: CustomsServiceStatusAll = CustomsServiceStatusAll(List(customsServiceStatus))
}