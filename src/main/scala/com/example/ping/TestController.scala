package com.example.ping

import com.example.db._
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.google.inject.Inject
import com.twitter.finagle.httpx.Request
import com.twitter.finatra.http.Controller
import slick.driver.H2Driver.api._
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.util.{Success, Failure}

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule

import scalaj.http.Http

/*
    Having this as an inner class breaks jackson with a weird error.
    (ObjectMapper.config() defined twice, something like that)
 */
case class ContentMap(title: String, content: String, byline: String)
case class PersonJson(name: String, age: Int)
@JsonIgnoreProperties(ignoreUnknown = true)
case class GithubContentResponse(name: String, path: String, sha: String)


class TestController @Inject()(service: FooService) extends Controller {
  val db = Database.forConfig("h2mem1")
  val users = TableQuery[Users]
  var at = 0

  val mapper = new ObjectMapper().registerModule(DefaultScalaModule)

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

  post("/jsontest") { request: Request =>
    println("Hello kitty")
    try {
      val contentJson = scala.io.Source.fromInputStream(request.getInputStream()).mkString

      val parsedContentMap: PersonJson = mapper.readValue(contentJson, classOf[PersonJson])

      "Read json with " + parsedContentMap.name + ", " + parsedContentMap.age
    } catch {
      case ex: Exception => println(ex.getMessage)
    }
  }

  get("/jsontest2") { request: Request =>
    println("Hi!")

    try {
      val contentJson = "{\"title\":\"Charlie Sheen continues his epic rant against Kim Kardashian... then apologizes\",\"content\":\"Dear Kim, my extreme bad. really embarrassed by my actions. I was already pissed about some other crap that had nothing to do with you. I heard a story that bothered me. wrote some trash you didn't deserve. Ever. I'm an idiot as often as I'm a genius. that day, clearly I was the former. xox c #ShutUpSheen  Read\",\"byline\":\"TMZ\"}"
      val contentParsed = mapper.readValue(contentJson, classOf[ContentMap])

      println(contentParsed.title)
      "woo woo"
    } catch {
      case ex: Throwable => println("Error is: " + ex.getMessage)
    }
    "goo goo"
  }

  get("/http") { request: Request =>
    Http("https://api.github.com/repos/mybatis/spring/contents/license.txt").asString
    //    Http("http://www.google.com").asString
  }

  get("/httpJson") { request: Request =>
    try {
      val result: String = Http("https://api.github.com/repos/mybatis/spring/contents/license.txt").asString.body
      val contentParsed: GithubContentResponse = mapper.readValue(result, classOf[GithubContentResponse])
      "Got: " + contentParsed.name + ", " + contentParsed.sha
    } catch {
      case ex: Throwable => "Error is: " + ex.getMessage
    }
  }

  get("/callService") { request: Request =>
    service.doIt()
  }
}