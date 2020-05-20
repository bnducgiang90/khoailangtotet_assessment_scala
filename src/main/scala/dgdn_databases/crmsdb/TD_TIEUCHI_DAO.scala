package dgdn_databases.crmsdb
import java.sql.{CallableStatement, ResultSet}

import dgdn_models.{TD_TIEUCHI, TD_TIEUCHI_CT}
import utils.objectheplers
import dgdn_databases.oracledb
import scala.collection.mutable.{ListBuffer}
import utils.{loghelpers}

class TD_TIEUCHI_DAO extends oracledb {

  def GetTieuChiChoXuLys(iLoaiTC: Int) : ListBuffer[TD_TIEUCHI] = {
    var tieuchis: ListBuffer[TD_TIEUCHI] = null
    this.__connect__()
    val sqlQueryTieuChi = "{call PKG_TD_TIEUCHI.TD_TIEUCHI_LayDanhSachChoXuLy(?,?)}"
    val _statementTC: CallableStatement = this._connection.prepareCall(sqlQueryTieuChi)
    _statementTC.setInt(1, iLoaiTC)
    _statementTC.registerOutParameter(2, oracle.jdbc.OracleTypes.CURSOR)
    _statementTC.executeUpdate()
    val _rs = _statementTC.getObject(2).asInstanceOf[ResultSet]
    tieuchis = objectheplers.ToListBuffer(classOf[TD_TIEUCHI], _rs)

    //val s = objectheplers.toList(_rs, ResultSet => classOf[TD_TIEUCHI])

    if (_statementTC != null)
      _statementTC.close()

    //get tiêu chí chi tiết
    val sqlQueryTieuChi_CT: String = "{call PKG_TD_TIEUCHI.TD_TIEUCHI_CT_SelectByPID(?,?)}"
    var _lstCT: ListBuffer[TD_TIEUCHI_CT] = null
    for (tc <- tieuchis) {
      val _statementTC_CT: CallableStatement = this._connection.prepareCall(sqlQueryTieuChi_CT)
      _statementTC_CT.setLong(1, tc.ID)
      _statementTC_CT.registerOutParameter(2, oracle.jdbc.OracleTypes.CURSOR)
      _statementTC_CT.executeUpdate()
      val _rsCT = _statementTC_CT.getObject(2).asInstanceOf[ResultSet]
      _lstCT = objectheplers.ToListBuffer(classOf[TD_TIEUCHI_CT], _rsCT)
      for (tcct <- _lstCT) {
        tcct.PARAMs = this.GetGiaTriTieuChi(tc.ID, tcct.ID).toList
      }
      tc.TD_TIEUCHI_CTs = _lstCT.toList
      if (_statementTC_CT != null)
        _statementTC_CT.close()
    }

    this.__disconnect__()
    tieuchis
  }

  def GetGiaTriTieuChi(id_tieuchi: Long, id_tieuchi_ct: Long): ListBuffer[String] =
  {
    var params : ListBuffer[String] = new ListBuffer[String]()
    val sqlQueryTC_CTGT = "{call PKG_TD_TIEUCHI.TD_TIEUCHI_CT_GT_SelectByTCID(?,?,?)}"
//    val _stmt = this._connection.prepareCall(sqlQueryTC_CTGT)
    val _stmt = this._connection.prepareCall(sqlQueryTC_CTGT, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)

    _stmt.setLong(1, id_tieuchi)
    _stmt.setLong(2, id_tieuchi_ct)
    _stmt.registerOutParameter(3, oracle.jdbc.OracleTypes.CURSOR)
//    _stmt.executeUpdate()
    _stmt.executeQuery()
    val rs = _stmt.getObject(3).asInstanceOf[ResultSet]

    while (rs.next)
      {
        params += rs.getString("F_VALUE")
      }
    if(_stmt!=null)
      _stmt.close()
    params
  }

  def KetThucXuLy(tieuChi: TD_TIEUCHI): Unit = {
    try {
      this.__connect__()
//      val stmt: CallableStatement = this._connection.prepareCall("{call PKG_TD_TIEUCHI.TD_TIEUCHI_KetThucXuLy(?)}")
      val stmt: CallableStatement = this._connection.prepareCall("{call PKG_TD_TIEUCHI.TD_TIEUCHI_KetThucXuLy(?)}"
        , ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)
      stmt.setLong(1, tieuChi.ID)
//      stmt.executeUpdate()
      stmt.executeQuery()
      if (stmt != null) stmt.close()
    } catch {
      case ex: Exception =>
        loghelpers(this.getClass).error(ex.getMessage)
    }
  }

}
