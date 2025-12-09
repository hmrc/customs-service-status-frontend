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

import java.time.temporal.ChronoUnit

class PlannedWorksHappeningTodaySpec extends BaseSpec {

  val plannedWorksUtil = new PlannedWorksHappeningToday

  "plannedWorksHappeningToday" should {
    "return a list with a single planned work" when {
      "there is a single planned work with start date before current date and end date after current date" in {

        plannedWorksUtil.plannedWorksHappeningToday(List(fakePlannedWork)) shouldBe List(fakePlannedWork)
      }

      "there is a single planned work with start date being the current date and end date after current date" in {

        plannedWorksUtil.plannedWorksHappeningToday(List(fakePlannedWorkWithCurrentDateAsStartDate)) shouldBe List(
          fakePlannedWorkWithCurrentDateAsStartDate
        )
      }

      "there is a single planned work with start date before current date and end date being the current date" in {

        plannedWorksUtil.plannedWorksHappeningToday(List(fakePlannedWorkWithCurrentDateAsEndDate)) shouldBe List(
          fakePlannedWorkWithCurrentDateAsEndDate
        )
      }

      "there is a single planned work with start and end date being the current date" in {

        plannedWorksUtil.plannedWorksHappeningToday(List(fakePlannedWorkWithCurrentDateAsStartAndEndDate)) shouldBe List(
          fakePlannedWorkWithCurrentDateAsStartAndEndDate
        )
      }

      "there is a planned work with start and end date being the current date and a planned work not happening today" in {

        plannedWorksUtil.plannedWorksHappeningToday(List(fakePlannedWork, fakePlannedWork.copy(startDateTime = fakeDateInTheFuture))) shouldBe List(
          fakePlannedWork
        )
      }
    }

    "return a list with multiple planned works" when {
      "both days have start date before current date and end date after current date" in {
        plannedWorksUtil.plannedWorksHappeningToday(List(fakePlannedWork, fakePlannedWork)) shouldBe List(fakePlannedWork, fakePlannedWork)
      }
    }

    "return an empty list" when {
      "there is no planned work scheduled as retrieved from the database" in {
        plannedWorksUtil.plannedWorksHappeningToday(List()) shouldBe List()
      }

      "there is no planned work scheduled today" in {
        plannedWorksUtil.plannedWorksHappeningToday(
          List(fakeOutageData(Planned, Some(fakeDate.plus(2, ChronoUnit.DAYS))).copy(startDateTime = fakeDate.plus(1, ChronoUnit.DAYS)))
        ) shouldBe List()
      }
    }
  }

}
