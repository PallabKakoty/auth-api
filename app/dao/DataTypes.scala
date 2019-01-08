package dao

case class ProductNested (prod_id: String, mrp: Int, discount: Int, sp: Int, prod_info: String, prod_url: String,
                          prod_img: String, prod_img_arr: String, mer_name: String, mer_id: Int, prod_cate: String,
                          prod_brand: String, prod_color: String, sizes: String, prod_for: String, updated_at: Long, created_at: Option[Long])


