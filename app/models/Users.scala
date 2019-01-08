package models

import javax.inject.{Inject, Singleton}
import org.joda.time.DateTime
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile
import com.github.tototoshi.slick.MySQLJodaSupport._
import scala.concurrent.Future

case class User(id: Option[Int], name: String, email: String, password: String, token: String, createdAt: DateTime, loginAt: DateTime, status: Int)

@Singleton
class Users @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)  extends HasDatabaseConfigProvider[JdbcProfile]{
  import profile.api._
  private class UserTableDef(tag: Tag) extends Table[User](tag, "users") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")
    def email = column[String]("email")
    def password = column[String]("password")
    def token = column[String]("token")
    def createdAt= column[DateTime]("created_at")
    def loginAt = column[DateTime]("login_at")
    def status = column[Int]("status")
    def * = (id.?, name, email, password, token, createdAt, loginAt, status)<>(User.tupled, User.unapply _)
  }

  private def userData = TableQuery[UserTableDef]
  private def autoInc = userData returning userData.map(_.id)

  def save(user: User) : Future[Int] = {
    db.run(autoInc += user)
  }


}
