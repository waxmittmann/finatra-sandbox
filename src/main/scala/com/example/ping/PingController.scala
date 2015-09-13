package com.example.ping

import com.twitter.finagle.httpx.Request
import com.twitter.finatra.http.Controller
import slick.driver.H2Driver.api._

class PingController extends Controller {

  get("/ping") { request: Request =>
  	info("ping")

    Database.forURL("jdbc:h2:mem:test1", driver = "org.h2.Driver") withSession {
      implicit session => {
        println("At least I can do something");
      }
      // <- write queries here
    }

    "pong"
  }

  get("/bong") { request: Request =>
    info("Smoke da weed")
    "smokey smokey"
  }
}
