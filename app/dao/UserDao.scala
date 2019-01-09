package dao

import akka.japi.Option.Some
import javax.inject.{Inject, Singleton}
import models.{User, UserVerification, UserVerificationToken, Users}
import org.joda.time.{DateTime, Days}
import play.api.libs.mailer.Email
import services.MailService
import utils.Tools

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

@Singleton
class UserDao @Inject()(users: Users, mailService: MailService, userVerification:UserVerification, tools: Tools) {

  object UsersDao {
    private implicit val logAddress = "dao.userDao.UsersDao"
    def saveUserDetail(user: User): Int = {
      val userFuture: Future[Int] = users.save(user)
      Await.result(userFuture, 5 seconds)
    }

    def checkUserLoginData(email: String, password: String): Option[User] = {
      val userData = getUserDataByEmail(email)
      if (userData.nonEmpty) {
        if (tools.decodedPassword(userData.get.password) == password) {
          userData
        } else None
      } else None
    }

    def getUserById(userId: Int): Option[User] = {
      val userFuture = users.getUserById(userId)
      Await.result(userFuture, 5 seconds)
    }

    def changeUserPassword(userId: Int, password: String): Int = {
      val userFuture: Future[Int] = users.changePassword(userId, password)
      Await.result(userFuture, 2 seconds)
    }

    def getUserDataByEmail(email: String): Option[User] = {
      val userFuture: Future[Option[User]] = users.checkUserLogin(email)
      Await.result(userFuture, 5 seconds)
    }

    def checkUserTokenValidityAndRefresh(user: User): (String, DateTime)= {
      val days: Int = Days.daysBetween(user.sessionTokenDate, DateTime.now).getDays
      if ( days > 2) {
        val date: DateTime = DateTime.now
        val token: String = tools.TokenGenerateBySize(15)
        val userFuture = users.refreshTokenById(user.id.get, token, date)
        (token, date)
      } else (user.sessionToken,user.sessionTokenDate)
    }

    def updateLoginDateTime(userId: Int): Int = {
      val userFuture = users.updateLoginDate(userId)
      Await.result(userFuture, 2 seconds)
    }

    def updateSessionToken(userId: Int): (String, DateTime) = {
      val date: DateTime = DateTime.now
      val token: String = tools.TokenGenerateBySize(15)
      val userFuture: Future[Int] = users.refreshTokenById(userId, token, DateTime.now)
      Await.result(userFuture, 5 seconds)
      (token, date)
    }
  }

  object MailServiceDao {
    private implicit val logAddress = "dao.userDao.MailServiceDao"
    def sendValidateLinkEmail(name: String, emailId: String, validateLink: String) = {
      val email = Email("Validate your account using this link", "pallabkakoty@gmail.com",Seq(emailId), Some(validateLink))
      mailService.sendMail(email)
    }
  }

  object UserVerificationDao {
    private implicit val logAddress = "dao.userDao.UserVerificationDao"
    def saveVerificationData(userVerificationToken: UserVerificationToken): Int = {
      val userFuture: Future[Int] = userVerification.save(userVerificationToken)
      Await.result(userFuture, 5 seconds)
    }

    def getUserByToken(token: String): Option[UserVerificationToken] = {
      val userFuture: Future[Option[UserVerificationToken]] = userVerification.verifyToken(token)
      Await.result(userFuture, 5 seconds)
    }

    def checkTokenDetails(token: String): Option[UserVerificationToken] = {
      val userTokenStatus = getUserByToken(token)
      if (userTokenStatus.nonEmpty) {
        val days = Days.daysBetween(userTokenStatus.get.createdAt, DateTime.now).getDays
        if ( days == 1 || days == 0) {
          userTokenStatus
        } else None
      } else None
    }

    def updateStatus(id: Int, status: Int): Int = {
      val userFuture: Future[Int] = userVerification.changeStatus(id, status)
      Await.result(userFuture, 2 seconds)
    }
  }

}
