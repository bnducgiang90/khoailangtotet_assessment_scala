package dgdn_models
import java.util.{Date}

import scala.collection.mutable.ListBuffer

class TH_DOANHNGHIEP {
  var MA_DN: String = _
  var NGAY_DK: Date = _
  var SLTK: Int = _
  var SLVP: Int = _
  var AMOUNT: Double = _
}

class TH_DANHGIA_DOANHNGHIEP {
  var MA_DN: String = _
  var NGAY_TH: Date = _
  var TONGDIEM: Int = _
  var TD_TIEUCHI_CTs : ListBuffer[TD_TIEUCHI_CT] = _

  def this(_MA_DN: String) ={
    this()
    this.MA_DN = _MA_DN
  }
}

