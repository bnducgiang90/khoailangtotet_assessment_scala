package dgdn_models
import java.util.{Date}

class TD_TIEUCHI {
  var ID: Long = _ //null.asInstanceOf[Long]
  var LOAI: Int = _
  var MATC: String = _
  var TENTC: String = _
  var NGAYTHCUOI: Date = _
  var CHUKY: Int = _
  var ASSEMBLY: String = _
  var CLASSNAME: String = _
  var ACTIVE: Short = _
  var EDITABLE: Short = _
  var TD_TIEUCHI_CTs : List[TD_TIEUCHI_CT] = _
  //var abc : Int = ???

}

class TD_TIEUCHI_CT {
  var ID: Long = _ //null.asInstanceOf[Long]
  var ID_TIEUCHI: Long = _
  var TENTC: String = _
  var F_HANHVI: Short = _
  var DS_HANHVI: String = _
  var ASSEMBLY: String = _
  var CLASSNAME: String = _
  var METHODNAME: String = _
  var ACTIVE: Short = _
  var NHOMTC: Short = _
  var LOAI_DSHS : String = _
  var PARAMs : List[String] = _

}

class TD_TIEUCHI_CT_GT {
  var ID: Long = _ //null.asInstanceOf[Long]
  var ID_TIEUCHI: Long = _
  var ID_TIEUCHI_CT: Long = _
  var F_NAME: String = _
  var F_VALUE: String = _
}


