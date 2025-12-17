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

package uk.gov.hmrc.customsservicestatusfrontend.utils

import uk.gov.hmrc.customsservicestatusfrontend.helpers.BaseSpec
import uk.gov.hmrc.customsservicestatusfrontend.TestData.*
import uk.gov.hmrc.customsservicestatusfrontend.models.OutageType.Planned
import uk.gov.hmrc.customsservicestatusfrontend.utils.DateUtils.*

import java.time.temporal.ChronoUnit

class DateUtilsSpec extends BaseSpec {

  implicit val now: Now = Now()

  "isWithinDates" should {
    "return true" when {
      "start date is before current date and end date is after current date" in {

        isWithinDates(fakePlannedWork.startDateTime, fakePlannedWork.endDateTime) shouldBe true
      }

      "start date being the current date and end date is after current date" in {

        isWithinDates(fakePlannedWorkWithCurrentDateAsStartDate.startDateTime, fakePlannedWorkWithCurrentDateAsStartDate.endDateTime) shouldBe true
      }

      "start date is before current date and end date being the current date" in {

        isWithinDates(fakePlannedWorkWithCurrentDateAsEndDate.startDateTime, fakePlannedWorkWithCurrentDateAsEndDate.endDateTime) shouldBe true
      }

      "start and end date being the current date" in {

        isWithinDates(
          fakePlannedWorkWithCurrentDateAsStartAndEndDate.startDateTime,
          fakePlannedWorkWithCurrentDateAsStartAndEndDate.endDateTime
        ) shouldBe true
      }
    }

    "return false" when {
      "start date is after current date and end date being the current date" in {
        val date = fakePlannedWorkWithCurrentDateAsEndDate.copy(startDateTime = now.apply.plus(1, ChronoUnit.DAYS))
        isWithinDates(date.startDateTime, date.endDateTime) shouldBe false
      }

      "start and end date are after the current date" in {
        val date = fakePlannedWork.copy(startDateTime = now.apply.plus(1, ChronoUnit.DAYS))
        isWithinDates(date.startDateTime, date.endDateTime) shouldBe false
      }

      "start date is the current date and end date before the current date" in {
        val date = fakeOutageDataWithCurrentDateAsStartDate(Planned, Some(fakeDate))
        isWithinDates(date.startDateTime, date.endDateTime) shouldBe false
      }

      "start and end date are before the current date" in {
        val date = fakeOutageData(Planned, Some(fakeDate))
        isWithinDates(date.startDateTime, date.endDateTime) shouldBe false
      }
    }
  }

}
