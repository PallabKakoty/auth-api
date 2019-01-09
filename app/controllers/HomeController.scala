package controllers

import dao.UserDao
import javax.inject._
import play.api.mvc._
import play.api.Logger
import globals.Configurations
import models.{User, UserVerificationToken}
import org.joda.time.DateTime
import play.api.data.Form
import play.api.data.Forms.mapping
import play.api.libs.json.Json
import utils.Tools
import play.api.data.Forms._

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
            val res = userDao.UsersDao.saveUserDetail(User(None, name, email, tools.EncodedPassword(password), tools.TokenGenerateBySize(8), DateTime.now, DateTime.now, DateTime.now, 0))
            if (res!=0) {
              val tokenToValidateUser: String = tools.TokenGenerateBySize(15)
              userDao.UserVerificationDao.saveVerificationData(UserVerificationToken(None, res, tokenToValidateUser, 0, DateTime.now))
              userDao.MailServiceDao.sendValidateLinkEmail(name, email, "http://localhost:9000/v1/activateprofile?verifyToken=" + tokenToValidateUser)
            }
            Ok(Json.obj("res" -> "success"))
          }.getOrElse(NotFound(Json.obj("err"->"Password not found")))
        }.getOrElse(NotFound(Json.obj("err"->"Email not found")))
      }.getOrElse(NotFound(Json.obj("err"->"Name not found")))
    }.getOrElse(NotFound(Json.obj("err"->"Json data not found")))
  }

  def validateUser(verifyToken: String) = Action {
    if (verifyToken != null) {
      Logger.debug("+++++++=> "+verifyToken)

      // TODO verify user token and validate account
      // TODO send email as account verified

      Ok("")
    } else NotFound("invalid verification token. please try again later")
  }

  def userLogin() = Action { implicit request =>
    request.body.asJson.map { json =>
      (json \ "email").asOpt[String].map { email =>
        (json \ "password").asOpt[String].map { password =>
          userDao.UsersDao.checkUserLoginData(email, password).map { user =>
            userDao.UsersDao.updateLoginDateTime(user.id.get)
            val sessionTokenCheck = userDao.UsersDao.checkUserTokenValidityAndRefresh(user)
            Ok(Json.obj("res"-> Json.obj("name"->user.name, "email"->email, "sessionToken" -> sessionTokenCheck._1, "tokenValidity"->sessionTokenCheck._2.toString)))
          }.getOrElse(NotFound(Json.obj("err" -> "Invalid user credentials")))
        }.getOrElse(NotFound(Json.obj("err"->"Password not found")))
      }.getOrElse(NotFound(Json.obj("err"->"Email not found")))
    }.getOrElse(NotFound(Json.obj("err"->"Json data not found")))
  }

  def forgotPasswordLinkToken() = Action { implicit request =>
    request.body.asJson.map { json =>
      (json \ "email").asOpt[String].map { email =>
        userDao.UsersDao.getUserDataByEmail(email).map { user =>
          val tokenToValidateUser: String = tools.TokenGenerateBySize(15)
          userDao.UserVerificationDao.saveVerificationData(UserVerificationToken(None, user.id.get, tokenToValidateUser, 0, DateTime.now))
          userDao.MailServiceDao.sendValidateLinkEmail(user.name, email, "http://localhost:9000/v1/setpassword?verifyToken=" + tokenToValidateUser)
          Ok(Json.obj("res" -> "Email send to the registered mail"))
        }.getOrElse(NotFound(Json.obj("err"->"Email not found")))
      }.getOrElse(NotFound(Json.obj("err"->"Email not found")))
    }.getOrElse(NotFound(Json.obj("err"->"Json data not found")))
  }

  def setNewPassword(verifyToken: String) = Action {
    println(verifyToken)
    //setNewPasswordNow
    userDao.UserVerificationDao.checkTokenDetails(verifyToken).map { tokenDetail =>
      Ok(views.html.changePassword(tokenDetail.id.get, tokenDetail.userId))
    }.getOrElse(NotFound("invalid verification token. please try again later"))
  }

  case class UserPwdData(id: String, userId: String, password: String, confirmPassword: String)
  val userPwdData = Form(
    mapping(
      "id"->text,
      "userId" -> text,
      "password" -> text,
      "confirmPassword" -> text
    )(UserPwdData.apply)(UserPwdData.unapply)
  )
  def setNewPasswordNow() = Action { implicit request =>
    userPwdData.bindFromRequest.fold(
      formWithErrors => {
        // binding failure, you retrieve the form containing errors:
        BadRequest("Get out\n" + formWithErrors)
      },
      formData => {
        val id = formData.id
        val userId = formData.userId
        val password = formData.password
        val confirmPassword = formData.confirmPassword
        println("FormData: "+userId+" - "+password)
        if (userId!=null && password!=null && password == confirmPassword) {
          userDao.UsersDao.getUserById(userId.toInt).map { user =>
            userDao.UsersDao.changeUserPassword(user.id.get, password)
            userDao.UserVerificationDao.updateStatus(id.toInt, 1)
            val updatedSessionData: (String, DateTime) = userDao.UsersDao.updateSessionToken(user.id.get)
            Ok("Password changed")
          }.getOrElse(NotFound("User not found"))
        } else NotFound("Invalid data. Please try again later.")
      }
    )
  }

  def resetPassword() = Action { implicit request =>
    request.body.asJson.map { json =>
      (json \ "email").asOpt[String].map { email =>
        (json \ "oldPassword").asOpt[String].map { oldPassword =>
          (json \ "newPassword").asOpt[String].map { newPassword =>
            userDao.UsersDao.checkUserLoginData(email, oldPassword).map { user =>
              userDao.UsersDao.changeUserPassword(user.id.get, newPassword)
              Ok("Password changed")
            }.getOrElse(NotFound(Json.obj("err" -> "Invalid user credentials")))
          }.getOrElse(NotFound(Json.obj("err"->"New password not found")))
        }.getOrElse(NotFound(Json.obj("err"->"Old password not found")))
      }.getOrElse(NotFound(Json.obj("err"->"Email not found")))
    }.getOrElse(NotFound(Json.obj("err"->"Json data not found")))
  }

}
