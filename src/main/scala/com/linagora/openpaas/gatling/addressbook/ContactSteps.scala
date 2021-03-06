package com.linagora.openpaas.gatling.addressbook

import com.linagora.openpaas.gatling.Configuration.ContactCount
import com.linagora.openpaas.gatling.addressbook.SessionKeys._
import com.linagora.openpaas.gatling.provisionning.Authentication.withAuth
import com.linagora.openpaas.gatling.provisionning.SessionKeys._
import com.linagora.openpaas.gatling.utils.RandomUuidGenerator.randomUuidString
import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef._
import io.gatling.http.request.builder.HttpRequestBuilder

import scala.concurrent.duration.DurationInt

object ContactSteps {
  def createContactInDefaultAddressBook(): ChainBuilder = {
    withAuth(http("createContactsInDefaultAddressBook")
      .put(s"/dav/api/addressbooks/$${$UserId}/contacts/$${$ContactUuid}.vcf")
      .body(StringBody(s"""
        [
          "vcard",
          [
            ["version", {}, "text", "4.0"],
            ["uid", {}, "text", "$${$ContactUuid}"],
            ["fn", {}, "text", "Dummy - $${$ContactUuid}"],
            ["n", {}, "text", ["", "Dummy"]]
          ],
          []
        ]
        """))
      .check(status is 201))
  }

  private def listContactFromAddressBookQuery(addressBookLink: String): HttpRequestBuilder = http(s"listContactsFromAddressBook")
    .get(s"/dav/api$addressBookLink?limit=20&offset=0&sort=fn")
    .check(status in(200, 304))

  def listContactsFromAddressBook(addressBookLink: String): ChainBuilder = {
    withAuth(listContactFromAddressBookQuery(addressBookLink))
  }

  def listContactsFromUserAddressBooks(): ChainBuilder =
    group("listContactsFromUserAddressBooks") {
      foreach(session => session(AddressBookLinks).as[Seq[String]], s"$${$AddressBookLink}") {
        exec(listContactsFromAddressBook(s"$${$AddressBookLink}"))
          .pause(1 second)
      }
    }

  def listContactsInCollectedAddressBook(): ChainBuilder = {
    group("listContactsInCollectedAddressBook") {
      exec(listContactsFromAddressBook(s"/addressbooks/$${$UserId}/collected.json"))
    }
  }

  def listContactsInDefaultAddressBook(): ChainBuilder = {
    group("listContactsInDefaultAddressBook") {
      withAuth(
        listContactFromAddressBookQuery(s"/addressbooks/$${$UserId}/contacts.json")
          .check(jsonPath("$._embedded['dav:item']").count.gt(0))
          .check(jsonPath("$._embedded['dav:item'][0]._links.self.href").saveAs(ContactLink))
      )
    }
  }

  def getOneContactInDefaultAddressBook(): ChainBuilder =
    withAuth(http("getOneCreatedContactInDefaultAddressBook")
      .get(s"/dav/api$${$ContactLink}")
      .check(status in (200, 304)))

  def provisionContacts(): ChainBuilder = {
    val contactUuidFeeder = Iterator.continually(Map(ContactUuid -> randomUuidString))

    group("provisionContacts") {
      repeat(ContactCount) {
        feed(contactUuidFeeder)
          .exec(createContactInDefaultAddressBook())
          .pause(1 second)
      }
    }
  }
}