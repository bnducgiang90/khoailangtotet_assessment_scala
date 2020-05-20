package dgdn_services

import java.lang.reflect.Method
import dgdn_models.{TD_TIEUCHI, TD_TIEUCHI_CT}
import dgdn_databases.crmsdb.TH_DN_ChapHanhTotLuatHQ2015DAO
import dgdn_models.TH_DANHGIA_DOANHNGHIEP
import utils.{constants, loghelpers}
import scala.collection.mutable.{ListBuffer}

class TH_DN_ChapHanhTotLuatHQ2015 extends IXuLyTuDong {
  private var chtDAO: TH_DN_ChapHanhTotLuatHQ2015DAO = new TH_DN_ChapHanhTotLuatHQ2015DAO()
  private var hDN: Map[String, TH_DANHGIA_DOANHNGHIEP] = Map()
  private var hDN_LyDo: Map[String, TH_DANHGIA_DOANHNGHIEP] = Map()
  private var o: TH_DANHGIA_DOANHNGHIEP = _
  private var bBatDauChay: Boolean = false

  override def process(tc: TD_TIEUCHI): Unit = {
    if (tc.TD_TIEUCHI_CTs == null || tc.TD_TIEUCHI_CTs.size <= 0) return
    for (tc_ct <- tc.TD_TIEUCHI_CTs) {
      loghelpers(this.getClass).info(tc_ct.METHODNAME)
      //var method: Method = this.getClass().getMethod(tc_ct.METHODNAME, classOf[TD_TIEUCHI_CT])
      //val sysClass = classOf[TH_DN_ChapHanhTotLuatHQ2015]
      //var method: Method = classOf[TH_DN_ChapHanhTotLuatHQ2015].getDeclaredMethod(tc_ct.METHODNAME, classOf[TD_TIEUCHI_CT])

      var method: Method = this.getClass().getDeclaredMethod(tc_ct.METHODNAME, classOf[TD_TIEUCHI_CT])
      method.invoke(this, tc_ct)
    }
    if (hDN.size > 0 || hDN_LyDo.size > 0) {
      // tao phien ban danh gia
      var id_PhienBan: Int = -1
      id_PhienBan = chtDAO.TaoPhienBan("SYSTEM", constants.LOAI_PHIEN_BAN_DN_CHAP_HANH_TOT)
      // ghi ket qua danh gia theo phien ban
      chtDAO.GhiKetQua(id_PhienBan, hDN, hDN_LyDo)
    }
  }

  def CapNhatDanhSachDN(hMaDN: Map[String, String],
                        tieuChiCT: TD_TIEUCHI_CT,
                        bLoaiTru: Boolean): Unit = {
    var lstDNKhongThoaMan: ListBuffer[String] = null
    if (bBatDauChay) {
      // khoi tao du lieu doanh nghiep danh gia thoa man tieu chi dau tien
      for (sMaDN <- hMaDN.keys) {
        o = new TH_DANHGIA_DOANHNGHIEP(sMaDN)
        o.TD_TIEUCHI_CTs += tieuChiCT
        hDN += (sMaDN -> o)
      }
      hDN_LyDo ++= hDN
    } else {
      // lay danh sach DN khong thoa man
      lstDNKhongThoaMan = new ListBuffer[String]()
      for (sMaDN <- hDN.keySet) {
        if ((!bLoaiTru && hMaDN.size > 0 && !hMaDN.contains(sMaDN)) || (bLoaiTru && hMaDN.contains(sMaDN))) {
          lstDNKhongThoaMan += sMaDN
        }
      }

      // loai bo khoi danh sach
      //      for (sMaDN <- lstDNKhongThoaMan) {
      //        hDN -= sMaDN
      //      }
      // loai bo khoi danh sach theo lô
      hDN --= lstDNKhongThoaMan
      // gan ly do cho nhung dn thoa man
      for (sMaDN <- hDN.keySet) {
        hDN_LyDo(sMaDN).TD_TIEUCHI_CTs += tieuChiCT
      }
    }
  }

  def TieuChi1_ThoiGianHoatDongXNK(tieuChiCT: TD_TIEUCHI_CT): Unit = {
    var hMaDN: Map[String, String] = null
    hMaDN = chtDAO.LayDSDN_TieuChi1_ThoiGianHoatDongXNK(tieuChiCT.PARAMs(0).toInt, tieuChiCT.PARAMs(1).toInt)
    CapNhatDanhSachDN(hMaDN, tieuChiCT, false)
    hMaDN.empty
    hMaDN = null
    bBatDauChay = false
  }

  def TieuChi2_HoatDongTaiTruSoDangKy(tieuChiCT: TD_TIEUCHI_CT): Unit = {
    var hMaDN: Map[String, String] = null
    hMaDN = chtDAO.LayDSDN_TieuChi2_HoatDongTaiTruSoDangKy()
    CapNhatDanhSachDN(hMaDN, tieuChiCT, false)
    hMaDN.empty
    hMaDN = null
    bBatDauChay = false
  }

  def TieuChi3_KimNghach(tieuChiCT: TD_TIEUCHI_CT): Unit = {
    var hMaDN: Map[String, String] = null
    hMaDN = chtDAO.LayDSDN_TieuChi3_KimNghach(tieuChiCT.PARAMs(0).toDouble)
    CapNhatDanhSachDN(hMaDN, tieuChiCT, false)
    hMaDN.empty
    hMaDN = null
    bBatDauChay = false
  }

  def TieuChi41_ChuHangKhongBiTruyTo(tieuChiCT: TD_TIEUCHI_CT): Unit = {
    var hMaDN: Map[String, String] = null
    hMaDN = chtDAO.LayDSDN_TieuChi41_ChuHangKhongBiTruyTo(tieuChiCT.PARAMs(0).toInt, tieuChiCT.PARAMs(1).toDouble, tieuChiCT.DS_HANHVI)
    CapNhatDanhSachDN(hMaDN, tieuChiCT, true)
    hMaDN.empty
    hMaDN = null
    bBatDauChay = false
  }

  def TieuChi42_PhatVuotMucChiCuc(tieuChiCT: TD_TIEUCHI_CT): Unit = {
    var hMaDN: Map[String, String] = null
    hMaDN = chtDAO.LayDSDN_TieuChi42_PhatVuotMucChiCuc(tieuChiCT.PARAMs(0).toInt, tieuChiCT.PARAMs(1).toDouble, tieuChiCT.DS_HANHVI)
    CapNhatDanhSachDN(hMaDN, tieuChiCT, true)
    hMaDN.empty
    hMaDN = null
    bBatDauChay = false
  }

  def TieuChi43_PhatVuotMucChiCuc_KhaiThue(tieuChiCT: TD_TIEUCHI_CT): Unit = {
    var hMaDN: Map[String, String] = null
    hMaDN = chtDAO.LayDSDN_TieuChi43_PhatVuotMucChiCuc_KhaiThue(tieuChiCT.PARAMs(0).toInt, tieuChiCT.PARAMs(1).toDouble, tieuChiCT.DS_HANHVI)
    CapNhatDanhSachDN(hMaDN, tieuChiCT, true)
    hMaDN.empty
    hMaDN = null
    bBatDauChay = false
  }

  def TieuChi44_ChuHangKhongBiXuPhatHanhChinhMucPhat(tieuChiCT: TD_TIEUCHI_CT): Unit = {
    var hMaDN: Map[String, String] = null
    hMaDN = chtDAO.LayDSDN_TieuChi44_ChuHangKhongBiXuPhatHanhChinhMucPhat(tieuChiCT.PARAMs(0).toInt, tieuChiCT.PARAMs(1).toDouble,
      tieuChiCT.PARAMs(2).toDouble, tieuChiCT.DS_HANHVI
    )
    CapNhatDanhSachDN(hMaDN, tieuChiCT, true)
    hMaDN.empty
    hMaDN = null
    bBatDauChay = false
  }

  def TieuChi45_ViPham_LinhVucKeToanThue(tieuChiCT: TD_TIEUCHI_CT): Unit = {
    var hMaDN: Map[String, String] = null
    hMaDN = chtDAO.LayDSDN_TieuChi45_ViPham_LinhVucKeToanThue(tieuChiCT.PARAMs(0).toInt, tieuChiCT.PARAMs(1).toDouble, "N/A")
    CapNhatDanhSachDN(hMaDN, tieuChiCT, true)
    hMaDN.empty
    hMaDN = null
    bBatDauChay = false
  }

  def TieuChi46_KhongChapHanhYeuCauHaiQuan(tieuChiCT: TD_TIEUCHI_CT): Unit = {
    var hMaDN: Map[String, String] = null
    hMaDN = chtDAO.LayDSDN_TieuChi46_KhongChapHanhYeuCauHaiQuan(tieuChiCT.PARAMs(0).toInt, tieuChiCT.DS_HANHVI)
    CapNhatDanhSachDN(hMaDN, tieuChiCT, true)
    hMaDN.empty
    hMaDN = null
    bBatDauChay = false
  }

  def TieuChi5_ChuHangKhongBiXuPhatHanhChinhVaKhaiSai(tieuChiCT: TD_TIEUCHI_CT): Unit = {
    var hMaDN: Map[String, String] = null
    hMaDN = chtDAO.LayDSDN_TieuChi5_ChuHangKhongBiXuPhatHanhChinhVaKhaiSai(tieuChiCT.PARAMs(0).toInt, tieuChiCT.PARAMs(1).toDouble)
    CapNhatDanhSachDN(hMaDN, tieuChiCT, true)
    hMaDN.empty
    hMaDN = null
    bBatDauChay = false
  }

  def TieuChi61_KhongKhaiHaiQuan(tieuChiCT: TD_TIEUCHI_CT): Unit = {
    var hMaDN: Map[String, String] = null
    hMaDN = chtDAO.LayDSDN_TieuChi61_KhongKhaiHaiQuan(tieuChiCT.PARAMs(0).toInt)
    CapNhatDanhSachDN(hMaDN, tieuChiCT, true)
    hMaDN.empty
    hMaDN = null
    bBatDauChay = false
  }

  def TieuChi62_KhaiSaiSanPhamXuatKhau(tieuChiCT: TD_TIEUCHI_CT): Unit = {
    var hMaDN: Map[String, String] = null
    hMaDN = chtDAO.LayDSDN_TieuChi62_KhaiSaiSanPhamXuatKhau(tieuChiCT.PARAMs(0).toInt)
    CapNhatDanhSachDN(hMaDN, tieuChiCT, true)
    hMaDN.empty
    hMaDN = null
    bBatDauChay = false
  }

  def TieuChi63_KhaiSaiAnHan275Ngay(tieuChiCT: TD_TIEUCHI_CT): Unit = {
    var hMaDN: Map[String, String] = null
    hMaDN = chtDAO.LayDSDN_TieuChi63_KhaiSaiAnHan275Ngay(tieuChiCT.PARAMs(0).toInt)
    CapNhatDanhSachDN(hMaDN, tieuChiCT, true)
    hMaDN.empty
    hMaDN = null
    bBatDauChay = false
  }

  def TieuChi7_TuanThuPhapLuat(tieuChiCT: TD_TIEUCHI_CT): Unit = {
    var hMaDN: Map[String, String] = null
    hMaDN = chtDAO.LayDSDN_TieuChi7_TuanThuPhapLuat()
    CapNhatDanhSachDN(hMaDN, tieuChiCT, false)
    hMaDN.empty
    hMaDN = null
    bBatDauChay = false
  }

  def TieuChi8_KhongNoThueQuaHan(tieuChiCT: TD_TIEUCHI_CT): Unit = {
    var hMaDN: Map[String, String] = null
    hMaDN = chtDAO.LayDSDN_TieuChi8_KhongNoThueQuaHan()
    CapNhatDanhSachDN(hMaDN, tieuChiCT, true)
    hMaDN.empty
    hMaDN = null
    bBatDauChay = false
  }

  def TieuChi9_ChuHangXNKHaiQuanDienTu(tieuChiCT: TD_TIEUCHI_CT): Unit = {
    var hMaDN: Map[String, String] = null
    hMaDN = chtDAO.LayDSDN_TieuChi9_ChuHangXNKHaiQuanDienTu(tieuChiCT.PARAMs(0).toInt)
    CapNhatDanhSachDN(hMaDN, tieuChiCT, false)
    hMaDN.empty
    hMaDN = null
    bBatDauChay = false
  }

}
