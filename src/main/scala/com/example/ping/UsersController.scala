package com.example.ping

import com.example.db.{User, Users}
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.twitter.finatra.http.Controller
import slick.lifted.TableQuery
import slick.driver.H2Driver.api._
import com.twitter.finagle.httpx.Request

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.util.{Failure, Success}

import scala.concurrent.ExecutionContext.Implicits.global

class UsersController extends Controller  {
  val db = Database.forConfig("h2mem1")
  val users = TableQuery[Users]
  val mapper = new ObjectMapper().registerModule(DefaultScalaModule)
  val rootUri = "/users"

  post(rootUri) { request: Request =>
    try {
      val userJson = scala.io.Source.fromInputStream(request.getInputStream()).mkString
      val user: User = mapper.readValue(userJson, classOf[User])

      val dbUpdate = db.run(DBIO.seq(
        users += user
      ))

      dbUpdate onComplete {
        case Success(posts) => "User created successfully"
        case Failure(t) => "There was a nasty error: '" + t.getMessage + "'"
      }
    } catch {
      case ex: Exception => "Things got crazy: " + ex.getMessage
    }
  }

  get(rootUri) { request: Request =>
    val q = for (u <- users) yield u.name
    val userNames: Seq[String] = Await.result(db.run(q.result), Duration.Inf)
    println(userNames)
    userNames
  }

}
