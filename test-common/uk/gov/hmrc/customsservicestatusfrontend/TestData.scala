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
import uk.gov.hmrc.customsservicestatusfrontend.models.State.AVAILABLE
import uk.gov.hmrc.customsservicestatusfrontend.models.{CustomsServiceStatus, OutageData, OutageType, ServiceStatuses}

import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.UUID

object TestData {

  val now: Instant = Instant.now()

  val availabilityForOtherServicesUrl = "https://www.gov.uk/government/collections/hm-revenue-and-customs-service-availability-and-issues"

  val serviceStatus: CustomsServiceStatus = CustomsServiceStatus("haulier", "Haulier", "description", Some(AVAILABLE), Some(now), Some(now))

  val serviceStatuses: ServiceStatuses = ServiceStatuses(List(serviceStatus))

  val fakeDate: Instant = Instant.parse("2020-01-01T00:00:00.000Z")

  val fakeCurrentDate: Instant = Instant.now

  val validUnplannedOutageData: OutageData = OutageData(
    id = UUID.randomUUID(),
    outageType = Unplanned,
    internalReference = InternalReference("Test reference"),
    startDateTime = fakeDate,
    endDateTime = None,
    commsText = CommsText("Test details"),
    publishedDateTime = fakeDate,
    clsNotes = Some("Notes")
  )

  val validPlannedOutageData: OutageData = OutageData(
    id = UUID.randomUUID(),
    outageType = Planned,
    internalReference = InternalReference("Test reference"),
    startDateTime = fakeDate,
    endDateTime = Some(fakeDate.plus(1, ChronoUnit.DAYS)),
    commsText = CommsText("Test details"),
    publishedDateTime = fakeDate,
    clsNotes = Some("Notes")
  )

  def fakeOutageData(outageType: OutageType, endDateTime: Option[Instant]): OutageData =
    OutageData(
      id = UUID.randomUUID(),
      outageType = outageType,
      internalReference = InternalReference("Test reference"),
      startDateTime = fakeDate,
      endDateTime = endDateTime,
      commsText = CommsText("Test details"),
      publishedDateTime = fakeDate,
      clsNotes = Some("Notes")
    )

  def fakeOutageDataWithCurrentDateAsStartDate(outageType: OutageType, endDateTime: Option[Instant]): OutageData =
    OutageData(
      id = UUID.randomUUID(),
      outageType = outageType,
      internalReference = InternalReference("Test reference"),
      startDateTime = fakeCurrentDate,
      endDateTime = endDateTime,
      commsText = CommsText("Test details"),
      publishedDateTime = fakeCurrentDate,
      clsNotes = Some("Notes")
    )

  val fakePlannedWork: OutageData = fakeOutageData(Planned, Some(now.plus(1, ChronoUnit.DAYS)))
  val fakePlannedWorkWithCurrentDateAsStartDate: OutageData =
    fakeOutageDataWithCurrentDateAsStartDate(Planned, Some(now.plus(1, ChronoUnit.DAYS)))
  val fakePlannedWorkWithCurrentDateAsEndDate:         OutageData = fakeOutageData(Planned, Some(fakeCurrentDate))
  val fakePlannedWorkWithCurrentDateAsStartAndEndDate: OutageData = fakeOutageDataWithCurrentDateAsStartDate(Planned, Some(fakeCurrentDate))
  val fakePlannedWorks: List[OutageData] = List(
    fakePlannedWork.copy(commsText = CommsText("Test one")),
    fakePlannedWork.copy(commsText = CommsText("Test two")),
    fakePlannedWork.copy(commsText = CommsText("Test three"))
  )
}
