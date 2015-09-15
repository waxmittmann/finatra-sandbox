package com.example.ping

import com.example.db._
import com.twitter.finagle.httpx.Request
import com.twitter.finatra.http.Controller
import slick.driver.H2Driver.api._
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.util.{Success, Failure}

class PingController extends Controller {
  val db = Database.forConfig("h2mem1")
  val users = TableQuery[Users]
  var at = 0

  get("/ping") { request: Request =>
    info("ping")

    "pong"
  }

  get("/write") { request: Request =>
    info("Inside write")

    val dbUpdate = db.run(DBIO.seq(
      users += User("Person_" + at),
      users.result.map(println)
    ))
    at += 1

    dbUpdate onComplete {
      case Success(posts) => println("Success:" + posts);
      case Failure(t) => println("Error: " + t.getMessage);
    }
    //finally db.close

    "Written"
  }

  get("/query") { request: Request =>
    info("Bo bo bo")

    val result = Await.result(db.run(DBIO.seq(users.result.map(println))), Duration.Inf)
    println("Result is " + result)
    result
  }

  get("/query2") { request: Request =>
    val q = for (u <- users) yield u.name
    val userNames: Seq[String] = Await.result(db.run(q.result), Duration.Inf)
    println(userNames)
    userNames
  }

  post("/posttest") { request: Request =>
    if (request.containsParam("helloPost")) {
      "Contained " + request.getParam("helloPost")
    } else {
      "No param =("
    }
  }

}
