package models

import javax.inject.{Inject, Singleton}
import org.joda.time.DateTime
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile
import com.github.tototoshi.slick.MySQLJodaSupport._
import utils.Tools

import scala.concurrent.Future

case class User(id: Option[Int], name: String, email: String, password: String, sessionToken: String, sessionTokenDate: DateTime, createdAt: DateTime, loginAt: DateTime, status: Int)

@Singleton
class Users @Inject()(protected val dbConfigProvider: DatabaseConfigProvider, tools: Tools)  extends HasDatabaseConfigProvider[JdbcProfile]{
  import profile.api._
  private class UserTableDef(tag: Tag) extends Table[User](tag, "users") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")
    def email = column[String]("email")
    def password = column[String]("password")
    def sessionToken = column[String]("session_token")
    def sessionTokenDate = column[DateTime]("session_token_date")
    def createdAt= column[DateTime]("created_at")
    def loginAt = column[DateTime]("login_at")
    def status = column[Int]("status")
    def * = (id.?, name, email, password, sessionToken, sessionTokenDate, createdAt, loginAt, status)<>(User.tupled, User.unapply _)
  }

  private def userData = TableQuery[UserTableDef]
  private def autoInc = userData returning userData.map(_.id)

  def save(user: User) : Future[Int] = {
    db.run(autoInc += user)
  }

  def checkUserLogin(email: String): Future[Option[User]] = {
    db.run(userData.filter(_.email === email).result.headOption)
  }

  def refreshTokenById(userId: Int, sessionToken: String, sessionTokenDate: DateTime): Future[Int] = {
    db.run(userData.filter(_.id === userId).map(x => (x.sessionToken, x.sessionTokenDate)).update(sessionToken, sessionTokenDate))
  }

  def updateLoginDate(userId: Int): Future[Int] = {
    db.run(userData.filter(_.id === userId).map(x => (x.loginAt)).update(DateTime.now))
  }

  def changePassword(userId: Int, password: String): Future[Int] = {
    db.run(userData.filter(_.id === userId).map(x => (x.password)).update(tools.EncodedPassword(password)))
  }

  def getUserById(userId: Int): Future[Option[User]] = {
    db.run(userData.filter(_.id === userId).result.headOption)
  }

  def updateAccountStatus(userId: Int, status: Int): Future[Int] = {
    db.run(userData.filter(_.id === userId).map(x => (x.status)).update(status))
  }

}
