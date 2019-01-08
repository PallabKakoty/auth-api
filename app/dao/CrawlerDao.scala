package dao

import akka.actor.Status.Success
//import com.gargoylesoftware.htmlunit.WebConsole.Logger
import javax.inject.{Inject, Singleton}
import models._
import org.joda.time.DateTime
import utils.Log
import scala.collection.mutable.ListBuffer
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

/** @author pallab */

@Singleton
class CrawlerDao @Inject()(merchantApis: MerchantApis, products: Products) {

  object MerchantApiDao {
    private implicit val logAddress = "dao.crawlerDao.MerchantApiDao"

    def findByMerchant(merchant: String): List[MerchantApi] = {
      println("Mer "+merchant)
      val merchantApiFuture = merchantApis.findByMerchant(merchant)
      Await.result(merchantApiFuture, 5 seconds)
    }

    def findByCodeOrSlug(code: String): Option[MerchantApi] = {
      println("Code: "+code)
      val merchantApiFuture = merchantApis.findByCodeOrSlug(code)
      Await.result(merchantApiFuture, 5 seconds)
    }

    def findByMerchantAndName(merchant: String, name: String): Option[MerchantApi] = {
      val merchantApiFuture = merchantApis.findByMerchantAndName(merchant, name)
      Await.result(merchantApiFuture, 5 seconds)
    }

    def findByid(id: Int): Option[MerchantApi] = {
      val merchantApiFuture = merchantApis.findById(id)
      Await.result(merchantApiFuture, 5 seconds)
    }
  }

  object ProductDao {
    private implicit val logAddress = "dao.crawlerDao.ProductDao"

    def getByProdId(prodId: String): Option[Product] = {
      val prodFuture: Future[Option[Product]] = products.findByProdId(prodId)
      Await.result(prodFuture, 5 seconds)
    }

    def findProductsByStatus(status: Int): List[Product] = {
      val prodFuture: Future[List[Product]] = products.findAllProductsByStatus(status)
      Await.result(prodFuture, 5 seconds)
    }

    def getAllProducts(): List[Product] = {
      val productFuture: Future[List[Product]] = products.findAll()
      Await.result(productFuture, 5 seconds)
    }

    def chanageProductStatusByProdId(prodId: String, wocomId: Option[Int], status: Int) = {
      val productFuture: Future[Int] = products.changeProductStatusByProdId(prodId, wocomId, status)
      Await.result(productFuture, 5 seconds)
    }

//    prod_id: String, mrp: Int, discount: Int, sp: Int, prod_info: String, prod_url: String,
//    prod_img: String, prod_img_arr: String, mer_name: String, mer_id: Int, prod_cate: String,
//    prod_brand: String, prod_color: String, sizes: String, prod_for: String, updated_at: Long, created_at: Option[Long]

    /*def createSqlProduct(prodNested: ProductNested): Product = {
      Product(prodNested.prod_id, prodNested.mrp, prodNested.discount, prodNested.sp, prodNested.prod_info,
        prodNested.prod_url, prodNested.prod_img, prodNested.prod_img_arr, prodNested.mer_name, prodNested.mer_id,
        prodNested.prod_cate, prodNested.prod_brand, prodNested.prod_color, prodNested.sizes, prodNested.prod_for,
        prodNested.updated_at, prodNested.created_at.getOrElse(Time.unixTime))
    }*/

    def saveOrUpdate(product: Product): Option[String] = {
      val oldProd = getByProdId(product.prod_id)
      if (oldProd.isDefined) {
        products.update(product.copy(created_at = oldProd.get.created_at, mrp = product.mrp, discount = product.discount, sp = product.sp,
          updated_at = product.updated_at, status = product.status))
        Some("Updating product info for "+product.mer_api_id)
      } else {
        products.save(product)
        Some("Saving product info for "+product.mer_api_id)
      }
    }

    def updateWocomIdByProdId(prodId: String, wocomId: Option[Int]): Unit = {
      val response = products.updateWocomIdByProdId(prodId, wocomId)
      val result = Await.result(response, 5 seconds)
      result match {
        case 0 => Log.error("Wocom Product id update failed for ProdId: "+prodId+" & WocomId: "+wocomId)
        case 1 => Log.info("Wocom Product id update successful for ProdId: "+prodId+" & WocomId: "+wocomId)
        case _ => Log.error("Unknown Response for ProdId: "+prodId+" & WocomId: "+wocomId)
      }
    }

  }

}
