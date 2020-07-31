// BrandController.scala
package controllers

import java.util.NoSuchElementException

import actors.{HelloActor, SayHello}
import akka.actor.{ActorRef, ActorSystem}
import javax.inject.{Inject, Singleton}
import models.{Brand, BrandForm, BrandRepository}
import play.api.db.slick.DbName
import play.api.libs.functional.syntax.unlift
import play.api.libs.json.{JsPath, Json, Writes}
import play.api.mvc.{BaseController, ControllerComponents}
import play.api.libs.functional.syntax._
import play.api.libs.json._
import slick.driver.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}
import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import akka.pattern.{AskTimeoutException, ask}
import akka.util.Timeout

// Play, by default, there is one actor system initialized
// There must be only one actor system per process/JVM instance

@Singleton
class BrandController @Inject() (repo: BrandRepository,
                                 system: ActorSystem,
                                 val controllerComponents: ControllerComponents) (implicit ec: ExecutionContext) extends  BaseController {

  println("**Default actor system name ", system.name)
  // create instance for the actor
  // we cannot access instance of HelloActor directly
  // Ref != not exact instance of class Actor
  val helloActor: ActorRef = system.actorOf(HelloActor.props, "helloActor")
  implicit  val timeout: akka.util.Timeout = 5.seconds

  def sayHello(name: String) = Action.async( {

    // ask, ?, need response
    // ask need a timeout, as this cannnot wait for longer time
    // (helloActor ? SayHello(name)) this code send a request to actor
    // got the response from actor, convert/casting the response to String type
    // .map {message (resolve the future, to the get the actual message
    (helloActor ? SayHello(name))
                .mapTo[String]
                .map { message =>
                  Ok(message)
                }
              .recover {
                case to: AskTimeoutException => Status(500) ("Timeout from actor " + to.getMessage)
                case e => Status(500) ("Unknown error" + e.getMessage)
              }
  })

  def getBrand(id: Long) = Action.async({ implicit  request =>
       repo.findById(id)
        .map (brand => Ok(Json.toJson(brand))) // success 200 OK
        .recover {
          case noEleEx: NoSuchElementException => NotFound // 404 Not Found
          case e => InternalServerError(e.getMessage)
          // add as many typed exceptions handling
          case _ => BadRequest
        }
  })

    def getBrands = Action.async { implicit request =>
      repo.list().map { brands =>
        Ok(Json.toJson(brands))
      }
        .recover {
          case e => InternalServerError(e.getMessage)
          // add as many typed exceptions handling
          case _ => BadRequest
        }
    }

    def deleteBrand(id: Long) = Action.async { implicit request =>
      repo.delete(id).map { _ =>
        Ok("")
      }
        .recover {
          case e => InternalServerError(e.getMessage)
          case _ => BadRequest
        }
    }

    // the body of request is overwritten with
    // Brand pojo instance
    def createBrand() = Action.async(parse.json[BrandForm]) {
      implicit request => {
        println("request", request.body)

        val requestBrand: BrandForm = request.body
        repo.create(requestBrand.name, requestBrand.year)
          .map (brand => Ok(Json.toJson(brand))) // success 200 OK
          .recover {
            case _ => InternalServerError
          }

        // Ok("done " + request.body.name)
      }
    }


}
//
//  def getBrand(id: Long) = Action.async { implicit request =>
//    repo.findById(id).map { brand =>
//      Ok(Json.toJson(brand))
//    }
//      .recover {
//        case f: NoSuchElementException => NotFound
//        case e => InternalServerError(e.getMessage)
//        case _ => BadRequest
//      }
//  }
//}

//
//  def deleteBrand(id: Long) = Action.async { implicit request =>
//    repo.delete(id).map { _ =>
//      Ok("")
//    }
//      .recover {
//        case f: NoSuchElementException => NotFound
//        case e => InternalServerError(e.getMessage)
//        case _ => BadRequest
//      }
//  }
//
//
//

//
//  def createBrand() = Action(parse.json[Brand]) {
//    implicit request => {
//      println("request", request.body)
//
//      repo.create(request.body.name, request.body.year)
//
//       Ok("done " + request.body.name)
//    }
//  }
//
