package uk.gov.hmrc.customsservicestatusfrontend.utils

object EqualUtils {
  implicit final class AnyOps[A](self: A) {
    def ===(other: A): Boolean = self == other

    def =!=(other: A): Boolean = self != other
  }
}
