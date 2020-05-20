package configs

import java.util.Properties
import configs.configtype.configtype
import scala.io.Source
import utils.loghelpers
//import com.typesafe.config

object configsettings {
  def apply(configType: configtype): appconfig = {
    loghelpers(this.getClass()).info(s"start Load config !")
    var _appconfig = new appconfig()
    try {
      configType match {
        case configtype.PROPERTIES => {
          val urlConfig = getClass.getResource("/application.properties") // méo hiểu sao thằng scala phải thêm / ở đằng trước.
          if (urlConfig != null) {
            val source = Source.fromURL(urlConfig)
            val properties: Properties = new Properties()
            properties.load(source.bufferedReader())
            _appconfig.driver = properties.getProperty("oracle.driver")
            _appconfig.url = properties.getProperty("oracle.url")
          }
          else {
            loghelpers(this.getClass()).error("File application.properties not found")
          }
        }
      }
    }
    catch {
      case ex: Exception => loghelpers(this.getClass()).error(ex.printStackTrace())
    }
    finally {
      loghelpers(this.getClass()).info(s"final Load config !")
    }
    _appconfig
  }
}

class appconfig {
  var driver: String = _
  var url: String = _
  var username: String = _
  var password: String = _
}

object configtype extends Enumeration {
  type configtype = Value
  val PROPERTIES, CONF = Value
}