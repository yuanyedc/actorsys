package com.dao

import java.sql.{Connection, DriverManager, SQLException}

/**
  * Created by yuanye on 2017/2/15.
  */
object OracleUtil {
  val driverClass = "oracle.jdbc.driver.OracleDriver"
  val jdbcUrl = "jdbc:oracle:thin:@(DESCRIPTION=(ADDRESS_LIST=(ADDRESS=(PROTOCOL=TCP)(HOST=172.30.12.26)(PORT=1521))(FAILOVER=ON)(LOAD_BALANCE=ON))(CONNECT_DATA= (SERVICE_NAME=msds)))"
  val username = "acctpaymertest1"
  val password = "acctpaymer*1"

  try {
    Class.forName(driverClass)
  } catch {
    case e: ClassNotFoundException => throw e
    case e: Exception => throw e
  }

  @throws(classOf[SQLException])
  def getConnection: Connection = {
    DriverManager.getConnection(jdbcUrl, username, password)
  }

  @throws(classOf[SQLException])
  def doTrancation(trancations: Set[String]): Unit = {
    val connection = getConnection
    connection.setAutoCommit(false)
    trancations.foreach(connection.createStatement().execute(_))
    connection.commit
    connection.close
  }
}
