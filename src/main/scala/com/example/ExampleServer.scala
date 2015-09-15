package com.example

//import com.example.db.{Coffees, Suppliers}
import com.example.ping.PingController
import com.twitter.finagle.httpx.{Response, Request}
import com.twitter.finatra.http.HttpServer
import com.twitter.finatra.http.filters.CommonFilters
import com.twitter.finatra.http.routing.HttpRouter
import com.twitter.finatra.logging.filter.{TraceIdMDCFilter, LoggingMDCFilter}
import com.twitter.finatra.logging.modules.Slf4jBridgeModule

import slick.backend.DatabasePublisher
import scala.concurrent.{Future, Await}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import slick.backend.DatabasePublisher
import slick.driver.H2Driver.api._

import scala.concurrent.Future

import com.example.db.{User, Users}

object ExampleServerMain extends ExampleServer

class ExampleServer extends HttpServer {
  val db = Database.forConfig("h2mem1")

  try {
    val users = TableQuery[Users]
    val setupAction: DBIO[Unit] = DBIO.seq(
      users.schema.create
    )
    val setupFuture: Future[Unit] = db.run(setupAction)
    Await.result(setupFuture, Duration.Inf)
  } finally db.close

  override def modules = Seq(Slf4jBridgeModule)

  override def defaultFinatraHttpPort = ":9999"

  override def configureHttp(router: HttpRouter) {
    router
      .filter[LoggingMDCFilter[Request, Response]]
      .filter[TraceIdMDCFilter[Request, Response]]
      .filter[CommonFilters]
      .add[PingController]
  }

}
