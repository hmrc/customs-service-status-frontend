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

import uk.gov.hmrc.customsservicestatusfrontend.models.DetailType.{Details, InternalReference}
import uk.gov.hmrc.customsservicestatusfrontend.models.OutageType.{Planned, Unplanned}
import uk.gov.hmrc.customsservicestatusfrontend.models.State.AVAILABLE
import uk.gov.hmrc.customsservicestatusfrontend.models.{CustomsServiceStatus, OutageData, ServiceStatuses}

import java.time.Instant
import java.util.UUID

object TestData {

  val now: Instant = Instant.now()

  val serviceStatus: CustomsServiceStatus = CustomsServiceStatus("haulier", "Haulier", "description", Some(AVAILABLE), Some(now), Some(now))

  val serviceStatuses: ServiceStatuses = ServiceStatuses(List(serviceStatus))

  val validUnplannedOutageData: OutageData = OutageData(
    id = UUID.randomUUID(),
    outageType = Unplanned,
    internalReference = InternalReference("Test reference"),
    startDateTime = Instant.parse("2025-01-01T00:00:00.000Z"),
    endDateTime = None,
    details = Details("Test details"),
    publishedDateTime = Instant.parse("2025-01-01T00:00:00.000Z"),
    clsNotes = Some("Notes for CLS users")
  )

  val validPlannedOutageData: OutageData = OutageData(
    id = UUID.randomUUID(),
    outageType = Planned,
    internalReference = InternalReference("Test reference"),
    startDateTime = Instant.parse("2025-01-01T00:00:00.000Z"),
    endDateTime = Some(Instant.parse("2025-01-01T00:00:00.000Z")),
    details = Details("Test details"),
    publishedDateTime = Instant.parse("2025-01-01T00:00:00.000Z"),
    clsNotes = Some("Notes for CLS users")
  )
}
