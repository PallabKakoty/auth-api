package controllers

import dao.UserDao
import javax.inject._
import play.api.mvc._
import play.api.Logger
import globals.Configurations
import models.User
import org.joda.time.DateTime
import play.api.libs.json.Json
import utils.Tools

@Singleton
class HomeController @Inject()(configurations: Configurations, userDao: UserDao, tools: Tools) extends Controller {

  implicit val logAddress = "controllers.HomeController"

  def index() = Action { implicit request: Request[AnyContent] =>
    Logger.debug("Hello")
    Ok(views.html.index())
  }

  def createProfile() = Action { implicit request =>
    request.body.asJson.map { json =>
      (json \ "name").asOpt[String].map { name =>
        (json \ "email").asOpt[String].map { email =>
          (json \ "password").asOpt[String].map { password =>
            val tokenToValidateUser: String  = tools.TokenGenerateBySize(8)
            val res = userDao.UsersDao.saveUserDetail(User(None, name, email, tools.EncodedPassword(password), tokenToValidateUser, DateTime.now, DateTime.now, 0))
            if (res!=0)
              userDao.MailServiceDao.sendValidateLinkEmail(name, email, "http://localhost:9000/v1/activateprofile?verifyToken="+tokenToValidateUser)
            Ok(Json.obj("res" -> "success"))
          }.getOrElse(NotFound(Json.obj("err"->"Password not found")))
        }.getOrElse(NotFound(Json.obj("err"->"Email not found")))
      }.getOrElse(NotFound(Json.obj("err"->"Name not found")))
    }.getOrElse(NotFound(Json.obj("err"->"Json data not found")))
  }

  def validateUser(verifyToken: String) = Action {
    if (verifyToken != null) {
      println("+++++++=> "+verifyToken)

      //val email = Email("Test", "pallabkakoty@gmail.com",Seq("pallabkakoty407@gmail.com"))
      //mailerClient.send(email)

      Ok("")
    } else NotFound("invalid verification token. please try again later")
  }

}
