package dgdn_services
import  dgdn_models.TD_TIEUCHI

trait IXuLyTuDong {
  def process(tc: TD_TIEUCHI): Unit
}
