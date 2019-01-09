package models

import javax.inject.{Inject, Singleton}
import org.joda.time.DateTime
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile
import scala.concurrent.Future
import com.github.tototoshi.slick.MySQLJodaSupport._

case class UserVerificationToken(id: Option[Int], userId: Int, verificationToken: String, status: Int, createdAt: DateTime)

@Singleton
class UserVerification @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)  extends HasDatabaseConfigProvider[JdbcProfile] {
  import profile.api._
  private class UserVerificationTableDef(tag: Tag) extends Table[UserVerificationToken](tag, "user_verification") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def userId = column[Int]("userId")
    def verificationToken = column[String]("verificationToken")
    def status = column[Int]("status")
    def createdAt = column[DateTime]("created_at")
    def * = (id.?, userId, verificationToken, status, createdAt)<>(UserVerificationToken.tupled, UserVerificationToken.unapply _)
  }

  private def userVerificationData = TableQuery[UserVerificationTableDef]
  private def autoInc = userVerificationData returning userVerificationData.map(_.id)

  def save(userVerificationToken: UserVerificationToken): Future[Int] = {
    db.run(autoInc += userVerificationToken)
  }

  def verifyToken(token: String): Future[Option[UserVerificationToken]] = {
    db.run(userVerificationData.filter(_.verificationToken === token).filter(_.status === 0).result.headOption)
  }

  def changeStatus(id: Int, status: Int): Future[Int] = {
    db.run(userVerificationData.filter(_.id === id).map(x => (x.status)).update(status))
  }

}
