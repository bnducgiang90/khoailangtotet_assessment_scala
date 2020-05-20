package dgdn_services
import dgdn_databases.crmsdb.TD_TIEUCHI_DAO
import dgdn_models.TD_TIEUCHI
import scala.collection.mutable.ListBuffer
class TD_TIEUCHI_SRV {
  private val tcDAO: TD_TIEUCHI_DAO = new TD_TIEUCHI_DAO()

  def getInstance(tieuChi: TD_TIEUCHI): IXuLyTuDong = {
    Class.forName(tieuChi.CLASSNAME).getConstructor().newInstance().asInstanceOf[IXuLyTuDong]
  }

  def GetTieuChiChoXuLys(iLoaiTC: Int) : ListBuffer[TD_TIEUCHI] = {
    val tieuchis = this.tcDAO.GetTieuChiChoXuLys(iLoaiTC)
    tieuchis
  }

  def KetThucXuLy(tieuChi: TD_TIEUCHI): Unit = {
    this.tcDAO.KetThucXuLy(tieuChi)
  }

}
