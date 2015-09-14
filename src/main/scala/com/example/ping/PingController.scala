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

//  val users: TableQuery[Users] = TableQuery[Users]


  get("/ping") { request: Request =>
    info("ping")

    Database.forURL("jdbc:h2:mem:test1", driver = "org.h2.Driver") withSession {
      implicit session => {
        println("At least I can do something")
      }
      // <- write queries here
    }

    "pong"
  }

  get("/bong") { request: Request =>
    info("Smoke da weed")
    "smokey smokey"
  }

  get("/write") { request: Request =>
    info("Ho ho ho")

    try {
      //      Await.result(db.run(DBIO.seq(
      //        users += User("Person_" + at),
      //        users.result.map(println)
      //      )), Duration.Inf)

      println("I'm tryin!")

      val dbUpdate = db.run(DBIO.seq(
//        users.schema.create,

        users += User("Person_" + at),
        users.result.map(println)
      ))

      dbUpdate onComplete {
        case Success(posts) => println("Something and " + posts); //db.close
        case Failure(t) => println("An error has occured: " + t.getMessage); //db.close
      }
    }
    //finally db.close

    "Written"
  }

  get("/query") { request: Request =>
    info("Bo bo bo")

//    try {
//      Await.result(db.run(DBIO.seq(
//        users.result.map(println)
//      )), Duration.Inf)
//    }

    val result = Await.result(db.run(DBIO.seq(users.result)), Duration.Inf)
    println("Result is " + result)
    result
    //finally db.close
  }

  get("/write2") { request: Request =>

  }

  get("/read2") { request: Request =>
    val q = for (u <- users) yield u.name
    val a = q.result
    val f: Seq[String] = Await.result(db.run(a), Duration.Inf)
    println(f)
    f
  }

}
