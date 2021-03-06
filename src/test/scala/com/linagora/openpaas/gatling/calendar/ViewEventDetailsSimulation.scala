package com.linagora.openpaas.gatling.calendar

import com.linagora.openpaas.gatling.Configuration._
import io.gatling.core.Predef._
import com.linagora.openpaas.gatling.calendar.scenari._
import io.gatling.core.feeder.SourceFeederBuilder
import io.gatling.core.structure.ScenarioBuilder

class ViewEventDetailsSimulation extends Simulation {
  val userFeeder: SourceFeederBuilder[String] = csv("users.csv").queue
  val scn: ScenarioBuilder = scenario("ViewEventDetailsScenario")
    .feed(userFeeder)
    .exec(CalendarSteps.openCalendarSPA())
    .exec(ViewEventDetailsScenari.generate())

  setUp(scn.inject(rampUsers(UserCount) during(InjectDuration))).protocols(HttpProtocol)
}
