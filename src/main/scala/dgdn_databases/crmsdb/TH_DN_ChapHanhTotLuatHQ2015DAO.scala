package dgdn_databases.crmsdb

import dgdn_databases.oracledb
import utils.loghelpers
import java.sql.{CallableStatement, ResultSet}
import dgdn_models.{TH_DANHGIA_DOANHNGHIEP, TH_DOANHNGHIEP}

import scala.collection.mutable.ListBuffer
import oracle.jdbc.OracleConnection

class TH_DN_ChapHanhTotLuatHQ2015DAO extends oracledb {
  def TaoPhienBan(_nguoiNM: String, _loaiPB: Int): Int = {
    var _phienBanID: Int = 0
    try {
      this.__connect__()
//      val stmt: CallableStatement = this._connection.prepareCall("{call PKG_TD_TIEUCHI.TH_THDL_TaoPhienBan(?,?,?)}")
      val stmt: CallableStatement = this._connection.prepareCall("{call PKG_TD_TIEUCHI.TH_THDL_TaoPhienBan(?,?,?)}"
        , ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY
        )

      stmt.setString(1, _nguoiNM)
      stmt.setInt(2, _loaiPB)
      stmt.registerOutParameter(3, oracle.jdbc.OracleTypes.CURSOR)
//      stmt.executeUpdate()
      stmt.executeQuery()
      val rs: ResultSet = stmt.getObject(3).asInstanceOf[ResultSet]
      while (rs.next()) _phienBanID = rs.getInt(1)
      if (stmt != null) stmt.close()
    }
    catch {
      case ex: Exception =>
        loghelpers(this.getClass()).error(ex.getMessage)
    }
    finally {
      this.__disconnect__()
    }
    _phienBanID
  }

  ////chú ý chỗ này có thể lỗi, debug để check
  // add batch with transaction
  // https://examples.javacodegeeks.com/core-java/sql/jdbc-batch-insert-example/
  def GhiKetQua(id_PhienBan: java.lang.Integer,
                hDN: Map[String, TH_DANHGIA_DOANHNGHIEP],
                hDN_LyDo: Map[String, TH_DANHGIA_DOANHNGHIEP]): Unit = {
    var lstTH_DN: ListBuffer[Array[Any]] = null
    var lstTH_DN_LyDo: ListBuffer[Array[Any]] = null
    try {
      this.__connect__()
      val stmt1: CallableStatement = this._connection.prepareCall("{call PKG_TD_TIEUCHI.th_dn_chaphanhtot_insert(?)}")
      val stmt2: CallableStatement = this._connection.prepareCall("{call PKG_TD_TIEUCHI.th_dn_chaphanhtot_lydo_insert(?)}")
      lstTH_DN = new ListBuffer()
      for (dn <- hDN.values) {
        val item: Array[Any] = Array(id_PhienBan, dn.MA_DN)
        lstTH_DN += item
      }

      val structsTH_DN: ListBuffer[java.sql.Struct] = new ListBuffer()
      for (d <- lstTH_DN) {
        structsTH_DN += this._connection.createStruct("TH_DN_DANHGIA", d.asInstanceOf[Array[AnyRef]])
      }
      val arrayTH_DN: java.sql.Array = this._connection.asInstanceOf[OracleConnection].createOracleArray("TH_DN", structsTH_DN.toArray)
      stmt1.setArray(1, arrayTH_DN)
      stmt1.executeUpdate()
      if (stmt1 != null) stmt1.close()

      lstTH_DN_LyDo = new ListBuffer[Array[Any]]()
      for (dn_lyDo <- hDN_LyDo.values; ct <- dn_lyDo.TD_TIEUCHI_CTs) {
        val item: Array[Any] = Array(id_PhienBan, dn_lyDo.MA_DN, ct.ID_TIEUCHI, ct.ID) //chú ý chỗ này có thể lỗi, debug để check
        lstTH_DN_LyDo += item
      }

      val structsTH_DN_LyDo: ListBuffer[java.sql.Struct] = new ListBuffer[java.sql.Struct]()
      for (d <- lstTH_DN_LyDo) {
        structsTH_DN_LyDo += this._connection.createStruct("TH_DN_DANHGIA_LYDO", d.asInstanceOf[Array[AnyRef]])
      }
      val arrayTH_DN_LyDo: java.sql.Array = this._connection.asInstanceOf[OracleConnection].createOracleArray("TH_DN_LYDO", structsTH_DN_LyDo.toArray)
      stmt2.setArray(1, arrayTH_DN_LyDo)
      stmt2.executeUpdate()
      if (stmt2 != null) stmt2.close()
    } catch {
      case ex: Exception => {
        loghelpers(this.getClass()).error(ex.getMessage)
        throw new Exception(
          "Co loi xay ra khi cap nhat ket qua danh gia vao CSDL:" +
            ex.getMessage)
      }

    }
    finally {
      this.__disconnect__()
    }

  }

  def LayDSDN_TieuChi1_ThoiGianHoatDongXNK(_so_ngay: Int, _so_luong_tk: Int): Map[String, String] = {
    var hmMaDN: Map[String, TH_DOANHNGHIEP] = Map()
    var hMaDN: Map[String, String] = Map()
    hmMaDN = LayDSDN_TieuChi1(_so_ngay)
    for (sMaDN <- hmMaDN.keySet) {
      if (hmMaDN(sMaDN).SLTK >= _so_luong_tk) {
        if (!hMaDN.contains(sMaDN)) {
          hMaDN += (sMaDN -> sMaDN)
        }
      }
    }
    hMaDN
  }

  def LayDSDN_TieuChi1(_so_ngay: Int): Map[String, TH_DOANHNGHIEP] = {
    var hMaDN: Map[String, TH_DOANHNGHIEP] = Map()
    var dn: TH_DOANHNGHIEP = null
    try {
      this.__connect__()
      val stmt: CallableStatement = this._connection.prepareCall("{call PKG_TD_TIEUCHI.TH_CHT_LuatHQ2015_TieuChi_01(?,?)}")
      stmt.setInt(1, _so_ngay)
      stmt.registerOutParameter(2, oracle.jdbc.OracleTypes.CURSOR)
      stmt.executeUpdate()
      val rs: ResultSet = stmt.getObject(2).asInstanceOf[ResultSet]
      while (rs.next()) {
        dn = new TH_DOANHNGHIEP()
        dn.MA_DN = rs.getString("Ma_DN")
        dn.SLTK = rs.getInt("TONG_TK")
        hMaDN += (rs.getString("Ma_DN") -> dn)
      }
      if (stmt != null) stmt.close()
    } catch {
      case ex: Exception =>
        loghelpers(this.getClass()).error(ex.getMessage)
    }
    finally {
      this.__disconnect__()
    }
    hMaDN
  }

  def LayDSDN_TieuChi2_HoatDongTaiTruSoDangKy(): Map[String, String] = {
    var hMaDN: Map[String, String] = Map()
    try {
      this.__connect__()
      val stmt: CallableStatement = this._connection.prepareCall("{call PKG_TD_TIEUCHI.TH_CHT_LuatHQ2015_TieuChi_02(?)}")
      stmt.registerOutParameter(1, oracle.jdbc.OracleTypes.CURSOR)
      stmt.executeUpdate()
      val rs: ResultSet = stmt.getObject(1).asInstanceOf[ResultSet]
      while (rs.next()) hMaDN += (rs.getString("Ma_DN") -> rs.getString("Ma_DN"))
      if (stmt != null) stmt.close()
    } catch {
      case ex: Exception =>
        loghelpers(this.getClass()).error(ex.getMessage)
    }
    finally {
      this.__disconnect__()
    }
    hMaDN
  }

  def LayDSDN_TieuChi3_KimNghach(_amount: Double): Map[String, String] = {
    var hMaDN: Map[String, String] = Map()
    try {
      this.__connect__()
      val stmt: CallableStatement = this._connection.prepareCall("{call PKG_TD_TIEUCHI.TH_CHT_LuatHQ2015_TieuChi_03(?,?)}")
      stmt.setDouble(1, _amount)
      stmt.registerOutParameter(2, oracle.jdbc.OracleTypes.CURSOR)
      stmt.executeUpdate()
      val rs: ResultSet = stmt.getObject(2).asInstanceOf[ResultSet]
      while (rs.next()) hMaDN += (rs.getString("Ma_DN") -> rs.getString("Ma_DN"))
      if (stmt != null) stmt.close()
    } catch {
      case ex: Exception =>
        loghelpers(this.getClass()).error(ex.getMessage)
    }
    finally {
      this.__disconnect__()
    }
    hMaDN
  }

  def LayDSDN_TieuChi41_ChuHangKhongBiTruyTo(_so_ngay: Int, _amount: Double, _dshanhvi: String): Map[String, String] = {
    var hMaDN: Map[String, String] = Map()
    try {
      this.__connect__()
      val stmt: CallableStatement = this._connection.prepareCall("{call PKG_TD_TIEUCHI.TH_CHT_LuatHQ2015_TieuChi_41(?,?,?,?)}")
      stmt.setInt(1, _so_ngay)
      stmt.setDouble(2, _amount)
      stmt.setString(3, _dshanhvi)
      stmt.registerOutParameter(4, oracle.jdbc.OracleTypes.CURSOR)
      stmt.executeUpdate()
      val rs: ResultSet = stmt.getObject(4).asInstanceOf[ResultSet]
      while (rs.next()) hMaDN += (rs.getString("Ma_DN") -> rs.getString("Ma_DN"))
      if (stmt != null) stmt.close()
    } catch {
      case ex: Exception =>
        loghelpers(this.getClass()).error(ex.getMessage)
    }
    finally {
      this.__disconnect__()
    }
    hMaDN
  }

  def LayDSDN_TieuChi42_PhatVuotMucChiCuc(_so_ngay: Int, _amount: Double, _dshanhvi: String): Map[String, String] = {
    var hMaDN: Map[String, String] = Map()
    try {
      this.__connect__()
      val stmt: CallableStatement = this._connection.prepareCall("{call PKG_TD_TIEUCHI.TH_CHT_LuatHQ2015_TieuChi_42(?,?,?,?)}")
      stmt.setInt(1, _so_ngay)
      stmt.setDouble(2, _amount)
      stmt.setString(3, _dshanhvi)
      stmt.registerOutParameter(4, oracle.jdbc.OracleTypes.CURSOR)
      stmt.executeUpdate()
      val rs: ResultSet = stmt.getObject(4).asInstanceOf[ResultSet]
      while (rs.next()) hMaDN += (rs.getString("Ma_DN") -> rs.getString("Ma_DN"))
      if (stmt != null) stmt.close()
    } catch {
      case ex: Exception =>
        loghelpers(this.getClass()).error(ex.getMessage)
    }
    finally {
      this.__disconnect__()
    }
    hMaDN
  }

  def LayDSDN_TieuChi43_PhatVuotMucChiCuc_KhaiThue(_so_ngay: Int, _amount: Double, _dshanhvi: String): Map[String, String] = {
    var hMaDN: Map[String, String] = Map()
    try {
      this.__connect__()
      val stmt: CallableStatement = this._connection.prepareCall("{call PKG_TD_TIEUCHI.TH_CHT_LuatHQ2015_TieuChi_43(?,?,?,?)}")
      stmt.setInt(1, _so_ngay)
      stmt.setDouble(2, _amount)
      stmt.setString(3, _dshanhvi)
      stmt.registerOutParameter(4, oracle.jdbc.OracleTypes.CURSOR)
      stmt.executeUpdate()
      val rs: ResultSet = stmt.getObject(4).asInstanceOf[ResultSet]
      while (rs.next()) hMaDN += (rs.getString("Ma_DN") -> rs.getString("Ma_DN"))
      if (stmt != null) stmt.close()
    } catch {
      case ex: Exception =>
        loghelpers(this.getClass()).error(ex.getMessage)
    }
    finally {
      this.__disconnect__()
    }
    hMaDN
  }

  def LayDSDN_TieuChi44_ChuHangKhongBiXuPhatHanhChinhMucPhat(
                                                              _so_ngay: Int,
                                                              _amount: Double,
                                                              _tyLeVP: Double,
                                                              _dsHanhVi: String): Map[String, String] = {
    var hMaDN: Map[String, String] = Map()
    try {
      this.__connect__()
      val stmt: CallableStatement = this._connection.prepareCall("{call PKG_TD_TIEUCHI.TH_CHT_LuatHQ2015_TieuChi_44(?,?,?,?,?)}")
      stmt.setInt(1, _so_ngay)
      stmt.setDouble(2, _amount)
      stmt.setDouble(3, _tyLeVP)
      stmt.setString(4, _dsHanhVi)
      stmt.registerOutParameter(5, oracle.jdbc.OracleTypes.CURSOR)
      stmt.executeUpdate()
      val rs: ResultSet = stmt.getObject(5).asInstanceOf[ResultSet]
      while (rs.next()) hMaDN += (rs.getString("Ma_DN") -> rs.getString("Ma_DN"))
      if (stmt != null) stmt.close()
    } catch {
      case ex: Exception =>
        loghelpers(this.getClass()).error(ex.getMessage)
    }
    finally {
      this.__disconnect__()
    }
    hMaDN
  }

  def LayDSDN_TieuChi45_ViPham_LinhVucKeToanThue(
                                                  _so_ngay: Int,
                                                  _amount: Double,
                                                  _dsHanhVi: String): Map[String, String] = {
    var hMaDN: Map[String, String] = Map()
    try {
      this.__connect__()
      val stmt: CallableStatement = this._connection.prepareCall("{call PKG_TD_TIEUCHI.TH_CHT_LuatHQ2015_TieuChi_45(?,?,?,?)}")
      stmt.setInt(1, _so_ngay)
      stmt.setDouble(2, _amount)
      stmt.setString(3, _dsHanhVi)
      stmt.registerOutParameter(4, oracle.jdbc.OracleTypes.CURSOR)
      stmt.executeUpdate()
      val rs: ResultSet = stmt.getObject(4).asInstanceOf[ResultSet]
      while (rs.next()) hMaDN += (rs.getString("Ma_DN") -> rs.getString("Ma_DN"))
      if (stmt != null) stmt.close()
    } catch {
      case ex: Exception =>
        loghelpers(this.getClass()).error(ex.getMessage)
    }
    finally {
      this.__disconnect__()
    }
    hMaDN
  }

  def LayDSDN_TieuChi46_KhongChapHanhYeuCauHaiQuan(
                                                    _so_ngay: Int,
                                                    _dsHanhVi: String): Map[String, String] = {
    var hMaDN: Map[String, String] = Map()
    try {
      this.__connect__()
      val stmt: CallableStatement = this._connection.prepareCall("{call PKG_TD_TIEUCHI.TH_CHT_LuatHQ2015_TieuChi_46(?,?,?)}")
      stmt.setInt(1, _so_ngay)
      stmt.setString(2, _dsHanhVi)
      stmt.registerOutParameter(3, oracle.jdbc.OracleTypes.CURSOR)
      stmt.executeUpdate()
      val rs: ResultSet = stmt.getObject(3).asInstanceOf[ResultSet]
      while (rs.next()) hMaDN += (rs.getString("Ma_DN") -> rs.getString("Ma_DN"))
      if (stmt != null) stmt.close()
    } catch {
      case ex: Exception =>
        loghelpers(this.getClass()).error(ex.getMessage)
    }
    finally {
      this.__disconnect__()
    }
    hMaDN
  }

  def LayDSDN_TieuChi5_ChuHangKhongBiXuPhatHanhChinhVaKhaiSai(
                                                               _so_ngay: Int,
                                                               _tyLeVP: Double): Map[String, String] = {
    var hMaDN: Map[String, String] = Map()
    try {
      this.__connect__()
      val stmt: CallableStatement = this._connection.prepareCall("{call PKG_TD_TIEUCHI.TH_CHT_LuatHQ2015_TieuChi_05(?,?,?)}")
      stmt.setInt(1, _so_ngay)
      stmt.setDouble(2, _tyLeVP)
      stmt.registerOutParameter(3, oracle.jdbc.OracleTypes.CURSOR)
      stmt.executeUpdate()
      val rs: ResultSet = stmt.getObject(3).asInstanceOf[ResultSet]
      while (rs.next()) hMaDN += (rs.getString("Ma_DN") -> rs.getString("Ma_DN"))
      if (stmt != null) stmt.close()
    } catch {
      case ex: Exception =>
        loghelpers(this.getClass()).error(ex.getMessage)
    }
    finally {
      this.__disconnect__()
    }
    hMaDN
  }

  def LayDSDN_TieuChi61_KhongKhaiHaiQuan(_so_ngay: Int): Map[String, String] = {
    var hMaDN: Map[String, String] = Map()
    try {
      this.__connect__()
      val stmt: CallableStatement = this._connection.prepareCall("{call PKG_TD_TIEUCHI.TH_CHT_LuatHQ2015_TieuChi_61(?,?)}")
      stmt.setInt(1, _so_ngay)
      stmt.registerOutParameter(2, oracle.jdbc.OracleTypes.CURSOR)
      stmt.executeUpdate()
      val rs: ResultSet = stmt.getObject(2).asInstanceOf[ResultSet]
      while (rs.next()) hMaDN += (rs.getString("Ma_DN") -> rs.getString("Ma_DN"))
      if (stmt != null) stmt.close()
    } catch {
      case ex: Exception =>
        loghelpers(this.getClass()).error(ex.getMessage)
    }
    finally {
      this.__disconnect__()
    }
    hMaDN
  }

  def LayDSDN_TieuChi62_KhaiSaiSanPhamXuatKhau(_so_ngay: Int): Map[String, String] = {
    var hMaDN: Map[String, String] = Map()
    try {
      this.__connect__()
      val stmt: CallableStatement = this._connection.prepareCall("{call PKG_TD_TIEUCHI.TH_CHT_LuatHQ2015_TieuChi_62(?,?)}")
      stmt.setInt(1, _so_ngay)
      stmt.registerOutParameter(2, oracle.jdbc.OracleTypes.CURSOR)
      stmt.executeUpdate()
      val rs: ResultSet = stmt.getObject(2).asInstanceOf[ResultSet]
      while (rs.next()) hMaDN += (rs.getString("Ma_DN") -> rs.getString("Ma_DN"))
      if (stmt != null) stmt.close()
    } catch {
      case ex: Exception =>
        loghelpers(this.getClass()).error(ex.getMessage)
    }
    finally {
      this.__disconnect__()
    }
    hMaDN
  }

  def LayDSDN_TieuChi63_KhaiSaiAnHan275Ngay(_so_ngay: Int): Map[String, String] = {
    var hMaDN: Map[String, String] = Map()
    try {
      this.__connect__()
      val stmt: CallableStatement = this._connection.prepareCall("{call PKG_TD_TIEUCHI.TH_CHT_LuatHQ2015_TieuChi_63(?,?)}")
      stmt.setInt(1, _so_ngay)
      stmt.registerOutParameter(2, oracle.jdbc.OracleTypes.CURSOR)
      stmt.executeUpdate()
      val rs: ResultSet = stmt.getObject(2).asInstanceOf[ResultSet]
      while (rs.next()) hMaDN += (rs.getString("Ma_DN") -> rs.getString("Ma_DN"))
      if (stmt != null) stmt.close()
    } catch {
      case ex: Exception =>
        loghelpers(this.getClass()).error(ex.getMessage)
    }
    finally {
      this.__disconnect__()
    }
    hMaDN
  }

  def LayDSDN_TieuChi7_TuanThuPhapLuat(): Map[String, String] = {
    var hMaDN: Map[String, String] = Map()
    try {
      this.__connect__()
      val stmt: CallableStatement = this._connection.prepareCall("{call PKG_TD_TIEUCHI.TH_CHT_LuatHQ2015_TieuChi_07(?)}")
      stmt.registerOutParameter(1, oracle.jdbc.OracleTypes.CURSOR)
      stmt.executeUpdate()
      val rs: ResultSet = stmt.getObject(1).asInstanceOf[ResultSet]
      while (rs.next()) hMaDN += (rs.getString("Ma_DN") -> rs.getString("Ma_DN"))
      if (stmt != null) stmt.close()
    } catch {
      case ex: Exception =>
        loghelpers(this.getClass()).error(ex.getMessage)
    }
    finally {
      this.__disconnect__()
    }
    hMaDN
  }

  def LayDSDN_TieuChi8_KhongNoThueQuaHan(): Map[String, String] = {
    var hMaDN: Map[String, String] = Map()
    try {
      this.__connect__()
      val stmt: CallableStatement = this._connection.prepareCall("{call PKG_TD_TIEUCHI.TH_CHT_LuatHQ2015_TieuChi_08(?)}")
      stmt.registerOutParameter(1, oracle.jdbc.OracleTypes.CURSOR)
      stmt.executeUpdate()
      val rs: ResultSet = stmt.getObject(1).asInstanceOf[ResultSet]
      while (rs.next()) hMaDN += (rs.getString("Ma_DN") -> rs.getString("Ma_DN"))
      if (stmt != null) stmt.close()
    } catch {
      case ex: Exception =>
        loghelpers(this.getClass()).error(ex.getMessage)
    }
    finally {
      this.__disconnect__()
    }
    hMaDN
  }

  def LayDSDN_TieuChi9_ChuHangXNKHaiQuanDienTu(_so_ngay: Int): Map[String, String] = {
    var hMaDN: Map[String, String] = Map()
    try {
      this.__connect__()
      val stmt: CallableStatement = this._connection.prepareCall("{call PKG_TD_TIEUCHI.TH_CHT_LuatHQ2015_TieuChi_09(?,?)}")
      stmt.setInt(1, _so_ngay)
      stmt.registerOutParameter(2, oracle.jdbc.OracleTypes.CURSOR)
      stmt.executeUpdate()
      val rs: ResultSet = stmt.getObject(2).asInstanceOf[ResultSet]
      while (rs.next()) hMaDN += (rs.getString("Ma_DN") -> rs.getString("Ma_DN"))
      if (stmt != null) stmt.close()
    } catch {
      case ex: Exception =>
        loghelpers(this.getClass()).error(ex.getMessage)
    }
    finally {
      this.__disconnect__()
    }
    hMaDN
  }

}
