package dao

import akka.japi.Option.Some
import javax.inject.{Inject, Singleton}
import models.{User, Users}
import play.api.libs.mailer.Email
import services.MailService

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

@Singleton
class UserDao @Inject()(users: Users, mailService: MailService) {

  object UsersDao {
    private implicit val logAddress = "dao.userDao.UsersDao"

    def saveUserDetail(user: User): Int = {
      val userFuture: Future[Int] = users.save(user)
      Await.result(userFuture, 5 seconds)
    }
  }

  object MailServiceDao {
    private implicit val logAddress = "dao.userDao.MailServiceDao"
    def sendValidateLinkEmail(name: String, email: String, validateLink: String) = {
      val email = Email("Validate your account using this link", "pallabkakoty@gmail.com",Seq("pallabkakoty407@gmail.com"), Some(validateLink))
      mailService.sendMail(email)
    }

  }

}
