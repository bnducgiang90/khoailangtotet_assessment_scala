package dgdn_databases
import java.sql.{Connection, DriverManager}
import configs.{configsettings, configtype}

class oracledb {
  private val _config = configsettings(configType = configtype.PROPERTIES)
  private val _driver = _config.driver //"oracle.jdbc.driver.OracleDriver"
  private val _url = _config.url //"jdbc:oracle:thin:CRMS/Oracle123@10.15.68.159:1521/FRAUDB"

  protected var _connection: Connection = null


  protected def __connect__(): Unit = {
    if (this._connection == null || this._connection.isClosed()) {
      Class.forName(this._driver)
      this._connection = DriverManager.getConnection(this._url)
    }
  }

  protected def __disconnect__(): Unit = {
    if (this._connection != null && !this._connection.isClosed()) {
      this._connection.close()
      this._connection = null
    }
  }

  protected  def __begintrans__():Unit ={
    this.__connect__()
    this._connection.setAutoCommit(false)
  }

  protected  def __committrans__():Unit = {
    this._connection.commit()
    this._connection.setAutoCommit(true)
  }

  protected  def __rollbacktrans__():Unit = {
    this._connection.rollback()
    this._connection.setAutoCommit(true)
  }
}
