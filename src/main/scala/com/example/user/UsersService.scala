package com.example.user

import com.example.db.{User, Users}

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import slick.driver.H2Driver.api._
import slick.lifted.TableQuery

class UsersService {
  val db = Database.forConfig("h2mem1")
  val users = TableQuery[Users]

  def addUser(user: User) = {
    val dbUpdate = db.run(DBIO.seq(
      users += user
    ))
    dbUpdate
  }

  def listUsersSync() = {
    val q = for (u <- users) yield u.name
    val userNames: Seq[String] = Await.result(db.run(q.result), Duration.Inf)
    userNames
  }

}
