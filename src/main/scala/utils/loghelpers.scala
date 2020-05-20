package utils
import org.apache.logging.log4j.{LogManager, Logger}

object loghelpers {
   var _logger: Logger = null

  def logger[T](clazz: Class[T]): Logger = {
    if (this._logger == null)
      this._logger = LogManager.getLogger(clazz)
    this._logger
  }

  def apply[T](clazz: Class[T]): Logger = {
    if (this._logger == null)
      this._logger = LogManager.getLogger(clazz)
    this._logger
  }
}
