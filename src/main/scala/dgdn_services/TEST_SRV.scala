package dgdn_services

import java.util.Date

import dgdn_databases.crmsdb.TEST_DAO
import dgdn_models.TH_DANHGIA_DOANHNGHIEP

import scala.collection.mutable.ListBuffer

class TEST_SRV {
  def GhiKetQua(id_PhienBan: java.lang.Integer,
                hDN: Map[String, TH_DANHGIA_DOANHNGHIEP]
               ): Unit = {

    var hDN: Map[String, TH_DANHGIA_DOANHNGHIEP] = Map()
    var o1 = new TH_DANHGIA_DOANHNGHIEP("001230123")
    o1.NGAY_TH = new Date()
    o1.TONGDIEM = 12
    hDN += ("001230123" -> o1)
    var o2 = new TH_DANHGIA_DOANHNGHIEP("0000011111")
    o2.NGAY_TH = new Date()
    o2.TONGDIEM = 12
    hDN += ("0000011111" -> o2)

    var o3 = new TH_DANHGIA_DOANHNGHIEP("00000333333")
    o3.NGAY_TH = new Date()
    o3.TONGDIEM = 12
    hDN += ("00000333333" -> o3)

    val _testDAO = new TEST_DAO()
    _testDAO.GhiKetQua(999999999, hDN)
  }
}
