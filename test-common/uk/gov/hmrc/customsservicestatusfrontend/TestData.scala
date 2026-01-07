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

package uk.gov.hmrc.customsservicestatusfrontend

import uk.gov.hmrc.customsservicestatusfrontend.models.DetailType.{CommsText, InternalReference}
import uk.gov.hmrc.customsservicestatusfrontend.models.OutageType.*
import uk.gov.hmrc.customsservicestatusfrontend.factories.ServiceStatusFactory.*
import uk.gov.hmrc.customsservicestatusfrontend.factories.OutageDataFactory.*
import uk.gov.hmrc.customsservicestatusfrontend.models.State.AVAILABLE
import uk.gov.hmrc.customsservicestatusfrontend.models.{CustomsServiceStatus, OutageData, OutageType, ServiceStatuses}

import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.UUID

object TestData {

  val now: Instant = Instant.now()

  val availabilityForOtherServicesUrl = "https://www.gov.uk/government/collections/hm-revenue-and-customs-service-availability-and-issues"

  val serviceStatuses: ServiceStatuses = ServiceStatuses(
    List(serviceStatus(state = Some(AVAILABLE), stateChangedAt = Some(now), lastUpdated = Some(now)))
  )

  val testDate: Instant = Instant.parse("2020-01-01T00:00:00.000Z")

  val unplannedOutage: OutageData = fakeOutageData(outageType = Unplanned)

  val plannedWork: OutageData = fakeOutageData(
    outageType = Planned,
    endDateTime = Some(now.plus(1, ChronoUnit.DAYS)),
    clsNotes = Some("Notes")
  )

  val plannedWorkWithCurrentDateAsEndDate: OutageData = fakeOutageData(outageType = Planned, endDateTime = Some(now))

  val plannedWorkWithCurrentDateAsStartDate: OutageData =
    fakeOutageData(outageType = Planned, endDateTime = Some(now.plus(1, ChronoUnit.DAYS)), clsNotes = Some("Notes"))

  val plannedWorkWithCurrentDateAsStartAndEndDate: OutageData =
    fakeOutageData(outageType = Planned, startDateTime = now, endDateTime = Some(now))

}
