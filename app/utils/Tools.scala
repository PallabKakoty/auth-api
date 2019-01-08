package utils

import com.google.common.base.Charsets
import com.google.common.io.BaseEncoding

class Tools {

  val random = new scala.util.Random
  def randomString(alphabet: String)(n: Int): String = Stream.continually(random.nextInt(alphabet.size)).map(alphabet).take(n).mkString
  def TokenGenerateBySize(n: Int) = randomString("abcdefghijklmnopqrstuvwxyz0123456789")(n)

  def EncodedPassword(password: String): String = new String(java.util.Base64.getEncoder.encode(password.getBytes()))
  def decodedPassword(encodedPassword: String): String = new String(java.util.Base64.getDecoder.decode(encodedPassword))

}
