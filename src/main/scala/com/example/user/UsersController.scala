package com.example.user

import com.example.db.{User, Users}
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.google.inject.Inject
import com.twitter.finagle.httpx.Request
import com.twitter.finatra.http.Controller
import slick.driver.H2Driver.api._
import slick.lifted.TableQuery

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

class UsersController @Inject()(usersService: UsersService) extends Controller  {
  val db = Database.forConfig("h2mem1")
  val users = TableQuery[Users]
  val mapper = new ObjectMapper().registerModule(DefaultScalaModule)
  val rootUri = "/users"

  post(rootUri) { request: Request =>
    try {
      val userJson = scala.io.Source.fromInputStream(request.getInputStream()).mkString
      val user: User = mapper.readValue(userJson, classOf[User])

      usersService.addUser(user) onComplete {
        case Success(posts) => "User created successfully"
        case Failure(t) => "There was a nasty error: '" + t.getMessage + "'"
      }
    } catch {
      case ex: Exception => "Things got crazy: " + ex.getMessage
    }
  }

  get(rootUri) { request: Request =>
    usersService.listUsersSync()
  }

}
