package utils
import java.sql.ResultSet
import util.control.Breaks._
import scala.collection.mutable.ListBuffer

object objectheplers {

  def toList[T](_rs : ResultSet, row: ResultSet => T): List[T] = {
    var buffer = ListBuffer[T]()
    try {
      while (_rs.next()) {
        buffer += row(_rs)
      }
    } finally {
      _rs.close()
    }
    buffer.toList
  }

  def ToListBuffer[T](clazz: Class[T], _rs : ResultSet): ListBuffer[T] =
    {
      var _lst: ListBuffer[T] = new ListBuffer[T]()
      while (_rs.next) {
        var t: T = clazz.newInstance()
        ToObject(_rs, t.asInstanceOf[Object]) // Point 4
        _lst += t
      }
      _lst
    }

  def ToObject[T](rst: ResultSet, obj: Object): Unit = {
    val rsmd = rst.getMetaData()
    val zclass: Class[_] = obj.getClass()
    var columnName: String = ""
    var fieldName: String = ""
    for(i: Int <- 1 to rsmd.getColumnCount())
      {
        columnName = rsmd.getColumnName(i)
        for (field <- zclass.getDeclaredFields) {
          field.setAccessible(true)
          fieldName = field.getName()
          breakable{
            if(!columnName.equalsIgnoreCase(fieldName))
              break
            var _value: AnyRef = rst.getObject(columnName)
            val _type : String = field.getType().getName()
            _type match {
              case "short" | "java.lang.Short" =>  field.set(obj, rst.getShort(columnName))
              case "int" | "java.lang.Integer" => field.set(obj, rst.getInt(columnName))
              case "long" | "java.lang.Long" => field.set(obj, rst.getLong(columnName))
              case "float" | "java.lang.Float" => field.set(obj,rst.getFloat(columnName))
              case "double" | "java.lang.Double" => field.set(obj, rst.getDouble(columnName))
              case "java.math.BigDecimal" => field.set(obj, rst.getBigDecimal(columnName))
              case "java.math.BigInteger" => field.set(obj, rst.getLong(columnName))
              case "java.util.Date" | "java.sql.Date" | "java.sql.Timestamp" => field.set(obj, rst.getTimestamp(columnName))
              case "boolean" | "java.lang.Boolean" => field.set(obj, rst.getBoolean(columnName))
              case "byte" | "java.lang.Byte" => field.set(obj, rst.getByte(columnName))
              case "byte[]" | "java.lang.Byte[]" => field.set(obj, rst.getBytes(columnName))
              case "string" | "java.lang.String" => field.set(obj,_value.asInstanceOf[String])
              case _ => field.set(obj,_value)
            }
          }
        }
      }
  }
}
