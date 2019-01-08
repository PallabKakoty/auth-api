package models

import javax.inject.{Inject, Singleton}
import org.joda.time.DateTime
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.Future
import com.github.tototoshi.slick.MySQLJodaSupport._
import com.sun.net.httpserver.Authenticator.Success

case class Product(prod_id: String, mrp: Int, discount: Int, sp: Int, prod_info: String, prod_url: String,
                   prod_img: String, prod_img_arr: String, mer_api_id: Int, prod_cate: String,
                   prod_brand: String, prod_color: String, sizes: String, prod_for: String, updated_at: DateTime, created_at: DateTime,
                   wcProdId: Option[Int], status: Int)

@Singleton
class Products @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  private val products = TableQuery[ProductTableDef]

  def save(product: Product): Future[Int] = {
    db.run(products += product)
  }

  def update(product: Product): Future[Int] = {
     db.run(products.filter(_.prod_id === product.prod_id).update(product))
  }

  def findByProdId(prodId: String): Future[Option[Product]] = {
    db.run(products.filter(_.prod_id === prodId).result.headOption)
  }

  def findAllProductsByStatus(status: Int): Future[List[Product]] = {
    db.run(products.filter(_.status === status).to[List].result)
  }

  def updateWocomIdByProdId(prodId: String, wocomId: Option[Int]): Future[Int] = {
    db.run(products.filter(_.prod_id === prodId).map(_.wc_prod_id).update(wocomId))
  }

  def findAll(): Future[List[Product]] = {
    db.run(products.to[List].result)
  }

  def changeProductStatusByProdId(prodId: String, wocomId: Option[Int], status: Int): Future[Int] = {
    db.run(products.filter((x => x.prod_id === prodId)).map(x => (x.wc_prod_id, x.status)).update(wocomId, status))
  }

  private class ProductTableDef(tag: Tag) extends Table[Product](tag, "products") {

    def prod_id = column[String]("prod_id", O.PrimaryKey)
    def mrp = column[Int]("mrp")
    def discount = column[Int]("discount")
    def sp = column[Int]("sp")
    def prod_info = column[String]("prod_info")
    def prod_url = column[String]("prod_url")
    def prod_img = column[String]("prod_img")
    def prod_img_arr = column[String]("prod_img_arr")
    def mer_api_id = column[Int]("mer_api_id")
    def prod_cate = column[String]("prod_cate")
    def prod_brand = column[String]("prod_brand")
    def prod_color = column[String]("prod_color")
    def sizes = column[String]("sizes")
    def prod_for = column[String]("prod_for")
    def updated_at = column[DateTime]("updated_at")
    def created_at = column[DateTime]("created_at")
    def wc_prod_id = column[Option[Int]]("wc_prod_id")
    def status = column[Int]("status")

    def * = (prod_id, mrp, discount, sp, prod_info, prod_url, prod_img, prod_img_arr, mer_api_id, prod_cate, prod_brand, prod_color, sizes, prod_for, updated_at, created_at, wc_prod_id, status)<>(Product.tupled, Product.unapply _)

  }
}