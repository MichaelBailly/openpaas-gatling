package com.linagora.openpaas.gatling.calendar.provisioning

import com.linagora.openpaas.gatling.Configuration._
import com.linagora.openpaas.gatling.calendar.scenari.provisioning._
import io.gatling.core.Predef._
import io.gatling.core.feeder.SourceFeederBuilder

class ProvisionCalendarsSimulation extends Simulation {
  val userFeeder: SourceFeederBuilder[String] = csv("users.csv").queue

  setUp(ProvisionCalendarsScenari.generate(userFeeder)
    .inject(rampUsers(UserCount) during(InjectDuration)))
    .protocols(HttpProtocol)
}
