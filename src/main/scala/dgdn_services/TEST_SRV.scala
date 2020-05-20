package dgdn_services

import dgdn_databases.crmsdb.TEST_DAO
import dgdn_models.TH_DANHGIA_DOANHNGHIEP
import scala.collection.mutable.ListBuffer

class TEST_SRV {
  def GhiKetQua(id_PhienBan: java.lang.Integer,
                hDN: Map[String, TH_DANHGIA_DOANHNGHIEP]
               ): Unit = {
      val _testDAO = new TEST_DAO()
    _testDAO.GhiKetQua(id_PhienBan, hDN)
  }
}
