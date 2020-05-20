
import dgdn_models.{TD_TIEUCHI, TH_DANHGIA_DOANHNGHIEP}
import utils.{constants, loghelpers}
import dgdn_services.{IXuLyTuDong, TD_TIEUCHI_SRV, TEST_SRV}

import scala.collection.mutable.ListBuffer
import java.util.Date

object Application extends App {
  var tieuChiSRV: TD_TIEUCHI_SRV = _
  var tieuChis: ListBuffer[TD_TIEUCHI] = _
  //var testSRV: TEST_SRV = _

  try {
    loghelpers(this.getClass()).info(s"Start processing ...")
    /*testSRV = new TEST_SRV()
    var hDN: Map[String, TH_DANHGIA_DOANHNGHIEP] = Map()
    var o1 = new TH_DANHGIA_DOANHNGHIEP("001230123")
    o1.NGAY_TH = new Date()
    o1.TONGDIEM = 12
    hDN += ("001230123" -> o1)
    var o2 = new TH_DANHGIA_DOANHNGHIEP("0000011111")
    o2.NGAY_TH = new Date()
    o2.TONGDIEM = 12
    hDN += ("0000011111" -> o2)
    testSRV.GhiKetQua(999999999,hDN)*/

    tieuChiSRV = new TD_TIEUCHI_SRV()
    tieuChis = tieuChiSRV.GetTieuChiChoXuLys( constants.DANH_GIA_DN_CHAP_HANH_TOT
                                            + constants.DANH_GIA_DN_KHONG_CHAP_HANH_TOT
                                            + constants.DANH_GIA_DN_BAO_LANH_THUE
                                            + constants.DANH_GIA_DN_DUA_HANG_VE_BAO_QUAN
                                            + constants.DANH_GIA_DN_KTSTQ_THEO_KE_HOACH
                                            + constants.DANH_GIA_DN_AN_HAN
                                            + constants.DANH_GIA_DN_XAC_DINH_TRUOC_TRI_GIA
                                            + constants.DANH_GIA_DN_KIEM_TRA_TRUOC_HOAN_THUE_SAU
                                            + constants.DANH_GIA_DN_HOAN_THUE_TRUOC_KIEM_TRA_SAU_365_NGAY
                                            + constants.DANH_GIA_DN_HOAN_THUE_TRUOC_KIEM_TRA_SAU_10_NAM
                                            )
    for (tc <- tieuChis) {
      // get instance tu className
      var td: IXuLyTuDong = tieuChiSRV.getInstance(tc);
      // xu ly
      loghelpers(this.getClass()).info("Processing " + tc.LOAI + "-" + tc.TENTC);
      td.process(tc)

      // ket thuc xu ly// ket thuc xu ly
      tieuChiSRV.KetThucXuLy(tc)

      loghelpers(this.getClass()).info("Successfull processing " + tc.LOAI + "-" + tc.TENTC)

    }
    loghelpers(this.getClass()).info("Successfull process")
  }
  catch {
    case ex: Exception =>
      loghelpers(this.getClass()).error(ex.getMessage)
  }
}
