package services

import javax.inject.Inject
import play.api.libs.mailer._

class MailService @Inject() (mailerClient: MailerClient){
  def sendMail(email: Email): Unit = {
    mailerClient.send(email)
  }
}
