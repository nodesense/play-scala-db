// BrandController.scala
package controllers

import java.util.NoSuchElementException

import akka.actor.ActorSystem
import javax.inject.{Inject, Singleton}
import models.{Brand, BrandRepository}
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


@Singleton
class BrandController @Inject() (repo: BrandRepository,
                                 val controllerComponents: ControllerComponents) (implicit ec: ExecutionContext) extends  BaseController {

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
//  def getBrands = Action.async { implicit request =>
//    repo.list().map { brands =>
//      Ok(Json.toJson(brands))
//    }
//  }
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
