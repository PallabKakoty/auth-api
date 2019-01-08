
package models

import javax.inject.{Inject, Singleton}

import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.Future


// TODO: Change the type of the `created_at` to `DateTime`
case class MerchantApi(id: Option[Int], merchant: String, apiName: String, codeSlug: String, productsFor: String, apiUrl: String, categoryIds: String, autoCrawl: Boolean, createdAt: Long)

@Singleton
class MerchantApis @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  private val merchantApis = TableQuery[MerchantApiTableDef]
  private val autoInc = merchantApis returning merchantApis.map(_.id)
  private val queryById = Compiled((id: Rep[Int]) => merchantApis.filter(_.id === id))

  def save(merchantApi: MerchantApi): Future[Int] = {
    db.run(autoInc += merchantApi)
  }

  def findById(id: Int) : Future[Option[MerchantApi]] = {
    db.run(merchantApis.filter(_.id === id).result.headOption)
  }

  def findMerchantApis(offset: Int, limit: Int): Future[List[MerchantApi]] = {
    db.run(merchantApis.drop(offset).take(limit).to[List].result)
  }

  def findByMerchant(merchant: String): Future[List[MerchantApi]] = {
    db.run(merchantApis.filter(_.merchant === merchant).filter(_.autoCrawl === true).to[List].result)
  }

  def findByCodeOrSlug(code: String): Future[Option[MerchantApi]] = {
    db.run(merchantApis.filter(_.codeSlug === code).result.headOption)
  }

  def findByMerchantAndName(merchant: String, name: String): Future[Option[MerchantApi]] = {
    db.run(merchantApis.filter(_.merchant === merchant).filter(_.apiName === name).result.headOption)
  }

  /* Schema */
  private class MerchantApiTableDef(tag: Tag) extends Table[MerchantApi](tag, "merchant_apis") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)

    def merchant = column[String]("merchant")

    def codeSlug = column[String]("code_slug")

    def apiName = column[String]("api_name")

    def productsFor = column[String]("products_for")

    def apiUrl = column[String]("api_url")

    def categoryIds = column[String]("category_ids")

    def autoCrawl = column[Boolean]("auto_crawl")

    def createdAt = column[Long]("created_at")

    def * = (id.?, merchant, apiName, codeSlug, productsFor, apiUrl, categoryIds, autoCrawl, createdAt) <>(MerchantApi.tupled, MerchantApi.unapply _)
  }

}

