package dgdn_databases.crmsdb

import dgdn_databases.oracledb
import utils.loghelpers
import java.sql.{CallableStatement}
import dgdn_models.{TH_DANHGIA_DOANHNGHIEP}
import scala.collection.mutable.ListBuffer
import oracle.jdbc.OracleConnection

class TEST_DAO extends oracledb {
  def GhiKetQua(id_PhienBan: java.lang.Integer,
                hDN: Map[String, TH_DANHGIA_DOANHNGHIEP]
               ): Unit = {
    var lstTH_DN: ListBuffer[Array[Any]] = null

    try {
      this.__connect__()
      val stmt1: CallableStatement = this._connection.prepareCall("{call th_dn_chaphanhtot_insert(?)}")
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

}
