    /*******************************************************************************
 * Copyright 2010-2014 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
 *
 * This file is part of SITools2.
 *
 * SITools2 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SITools2 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with SITools2.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package fr.cnes.sitools.datasource.jdbc.dbexplorer;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Map;
import java.util.logging.Logger;

import org.restlet.engine.Engine;

/**
 * Encapsulation of a result set with its database connection in order to release it after use.
 * 
 * TODO DESIGN AbstractResultSet > AbstractResultSet générique (XMLDBResultSet, DBSQLResultSet) > implements closable
 * 
 * @author jp.boignard (AKKA Technologies)
 * 
 */
public class DBResultSet implements ResultSet {

  /** inner SQL ResultSet */
  private ResultSet localResultSet = null;

  /** inner SQL connection to close when closing resultset */
  private Connection conn = null;

  /** inner SQL statement */
  private Statement statement = null;

  /**
   * Complete constructor
   * 
   * @param localResultSet
   *          ResultSet
   * @param stat
   *          Statement
   * @param conn
   *          SQL Connection
   */
  public DBResultSet(ResultSet localResultSet, Statement stat, Connection conn) {
    super();
    this.localResultSet = localResultSet;
    this.statement = stat;
    this.conn = conn;
  }


  public final boolean absolute(int row) throws SQLException {
    return localResultSet.absolute(row);
  }


  public final void afterLast() throws SQLException {
    localResultSet.afterLast();
  }


  public final void beforeFirst() throws SQLException {
    localResultSet.beforeFirst();
  }


  public final void cancelRowUpdates() throws SQLException {
    localResultSet.cancelRowUpdates();
  }


  public final void clearWarnings() throws SQLException {
    localResultSet.clearWarnings();
  }


  public final void close() throws SQLException {
    try {
      statement.close();
      localResultSet.close();
    }
    catch (Exception e) {
      getLogger().warning(e.getMessage());
    }
    finally {
      try {
        conn.close();
      }
      catch (Exception e) {
        getLogger().warning(e.getMessage());
      }
    }
  }

  /**
   * Get the logger
   * 
   * @return the logger
   */
  private Logger getLogger() {
    return Engine.getLogger(this.getClass().getName());
  }


  public final void deleteRow() throws SQLException {
    localResultSet.deleteRow();
  }


  public final int findColumn(String columnLabel) throws SQLException {
    return localResultSet.findColumn(columnLabel);
  }


  public final boolean first() throws SQLException {
    return localResultSet.first();
  }


  public final Array getArray(int columnIndex) throws SQLException {
    return localResultSet.getArray(columnIndex);
  }


  public final Array getArray(String columnLabel) throws SQLException {
    return localResultSet.getArray(columnLabel);
  }


  public final InputStream getAsciiStream(int columnIndex) throws SQLException {
    return localResultSet.getAsciiStream(columnIndex);
  }


  public final InputStream getAsciiStream(String columnLabel) throws SQLException {
    return localResultSet.getAsciiStream(columnLabel);
  }


  public final BigDecimal getBigDecimal(int columnIndex, int scale) throws SQLException {
    return localResultSet.getBigDecimal(columnIndex, scale);
  }


  public final BigDecimal getBigDecimal(int columnIndex) throws SQLException {
    return localResultSet.getBigDecimal(columnIndex);
  }


  public final BigDecimal getBigDecimal(String columnLabel, int scale) throws SQLException {
    return localResultSet.getBigDecimal(columnLabel, scale);
  }


  public final BigDecimal getBigDecimal(String columnLabel) throws SQLException {
    return localResultSet.getBigDecimal(columnLabel);
  }


  public final InputStream getBinaryStream(int columnIndex) throws SQLException {
    return localResultSet.getBinaryStream(columnIndex);
  }


  public final InputStream getBinaryStream(String columnLabel) throws SQLException {
    return localResultSet.getBinaryStream(columnLabel);
  }


  public final Blob getBlob(int columnIndex) throws SQLException {
    return localResultSet.getBlob(columnIndex);
  }


  public final Blob getBlob(String columnLabel) throws SQLException {
    return localResultSet.getBlob(columnLabel);
  }


  public final boolean getBoolean(int columnIndex) throws SQLException {
    return localResultSet.getBoolean(columnIndex);
  }


  public final boolean getBoolean(String columnLabel) throws SQLException {
    return localResultSet.getBoolean(columnLabel);
  }


  public final byte getByte(int columnIndex) throws SQLException {
    return localResultSet.getByte(columnIndex);
  }


  public final byte getByte(String columnLabel) throws SQLException {
    return localResultSet.getByte(columnLabel);
  }


  public final byte[] getBytes(int columnIndex) throws SQLException {
    return localResultSet.getBytes(columnIndex);
  }


  public final byte[] getBytes(String columnLabel) throws SQLException {
    return localResultSet.getBytes(columnLabel);
  }


  public final Reader getCharacterStream(int columnIndex) throws SQLException {
    return localResultSet.getCharacterStream(columnIndex);
  }


  public final Reader getCharacterStream(String columnLabel) throws SQLException {
    return localResultSet.getCharacterStream(columnLabel);
  }


  public final Clob getClob(int columnIndex) throws SQLException {
    return localResultSet.getClob(columnIndex);
  }


  public final Clob getClob(String columnLabel) throws SQLException {
    return localResultSet.getClob(columnLabel);
  }


  public final int getConcurrency() throws SQLException {
    return localResultSet.getConcurrency();
  }


  public final String getCursorName() throws SQLException {
    return localResultSet.getCursorName();
  }


  public final Date getDate(int columnIndex, Calendar cal) throws SQLException {
    return localResultSet.getDate(columnIndex, cal);
  }


  public final Date getDate(int columnIndex) throws SQLException {
    return localResultSet.getDate(columnIndex);
  }


  public final Date getDate(String columnLabel, Calendar cal) throws SQLException {
    return localResultSet.getDate(columnLabel, cal);
  }


  public final Date getDate(String columnLabel) throws SQLException {
    return localResultSet.getDate(columnLabel);
  }


  public final double getDouble(int columnIndex) throws SQLException {
    return localResultSet.getDouble(columnIndex);
  }


  public final double getDouble(String columnLabel) throws SQLException {
    return localResultSet.getDouble(columnLabel);
  }


  public final int getFetchDirection() throws SQLException {
    return localResultSet.getFetchDirection();
  }


  public final int getFetchSize() throws SQLException {
    return localResultSet.getFetchSize();
  }


  public final float getFloat(int columnIndex) throws SQLException {
    return localResultSet.getFloat(columnIndex);
  }


  public final float getFloat(String columnLabel) throws SQLException {
    return localResultSet.getFloat(columnLabel);
  }


  public final int getHoldability() throws SQLException {
    return localResultSet.getHoldability();
  }


  public final int getInt(int columnIndex) throws SQLException {
    return localResultSet.getInt(columnIndex);
  }


  public final int getInt(String columnLabel) throws SQLException {
    return localResultSet.getInt(columnLabel);
  }


  public final long getLong(int columnIndex) throws SQLException {
    return localResultSet.getLong(columnIndex);
  }


  public final long getLong(String columnLabel) throws SQLException {
    return localResultSet.getLong(columnLabel);
  }


  public final ResultSetMetaData getMetaData() throws SQLException {
    return localResultSet.getMetaData();
  }


  public final Reader getNCharacterStream(int columnIndex) throws SQLException {
    return localResultSet.getNCharacterStream(columnIndex);
  }


  public final Reader getNCharacterStream(String columnLabel) throws SQLException {
    return localResultSet.getNCharacterStream(columnLabel);
  }


  public final NClob getNClob(int columnIndex) throws SQLException {
    return localResultSet.getNClob(columnIndex);
  }


  public final NClob getNClob(String columnLabel) throws SQLException {
    return localResultSet.getNClob(columnLabel);
  }


  public final String getNString(int columnIndex) throws SQLException {
    return localResultSet.getNString(columnIndex);
  }


  public final String getNString(String columnLabel) throws SQLException {
    return localResultSet.getNString(columnLabel);
  }


  public final Object getObject(int columnIndex, Map<String, Class<?>> map) throws SQLException {
    return localResultSet.getObject(columnIndex, map);
  }


  public final Object getObject(int columnIndex) throws SQLException {
    return localResultSet.getObject(columnIndex);
  }


  public final Object getObject(String columnLabel, Map<String, Class<?>> map) throws SQLException {
    return localResultSet.getObject(columnLabel, map);
  }


  public final Object getObject(String columnLabel) throws SQLException {
    return localResultSet.getObject(columnLabel);
  }


  public final Ref getRef(int columnIndex) throws SQLException {
    return localResultSet.getRef(columnIndex);
  }


  public final Ref getRef(String columnLabel) throws SQLException {
    return localResultSet.getRef(columnLabel);
  }


  public final int getRow() throws SQLException {
    return localResultSet.getRow();
  }


  public final RowId getRowId(int columnIndex) throws SQLException {
    return localResultSet.getRowId(columnIndex);
  }


  public final RowId getRowId(String columnLabel) throws SQLException {
    return localResultSet.getRowId(columnLabel);
  }


  public final SQLXML getSQLXML(int columnIndex) throws SQLException {
    return localResultSet.getSQLXML(columnIndex);
  }


  public final SQLXML getSQLXML(String columnLabel) throws SQLException {
    return localResultSet.getSQLXML(columnLabel);
  }


  public final short getShort(int columnIndex) throws SQLException {
    return localResultSet.getShort(columnIndex);
  }


  public final short getShort(String columnLabel) throws SQLException {
    return localResultSet.getShort(columnLabel);
  }


  public final Statement getStatement() throws SQLException {
    return localResultSet.getStatement();
  }


  public final String getString(int columnIndex) throws SQLException {
    return localResultSet.getString(columnIndex);
  }


  public final String getString(String columnLabel) throws SQLException {
    return localResultSet.getString(columnLabel);
  }


  public final Time getTime(int columnIndex, Calendar cal) throws SQLException {
    return localResultSet.getTime(columnIndex, cal);
  }


  public final Time getTime(int columnIndex) throws SQLException {
    return localResultSet.getTime(columnIndex);
  }


  public final Time getTime(String columnLabel, Calendar cal) throws SQLException {
    return localResultSet.getTime(columnLabel, cal);
  }


  public final Time getTime(String columnLabel) throws SQLException {
    return localResultSet.getTime(columnLabel);
  }


  public final Timestamp getTimestamp(int columnIndex, Calendar cal) throws SQLException {
    return localResultSet.getTimestamp(columnIndex, cal);
  }


  public final Timestamp getTimestamp(int columnIndex) throws SQLException {
    return localResultSet.getTimestamp(columnIndex);
  }


  public final Timestamp getTimestamp(String columnLabel, Calendar cal) throws SQLException {
    return localResultSet.getTimestamp(columnLabel, cal);
  }


  public final Timestamp getTimestamp(String columnLabel) throws SQLException {
    return localResultSet.getTimestamp(columnLabel);
  }


  public final int getType() throws SQLException {
    return localResultSet.getType();
  }


  public final URL getURL(int columnIndex) throws SQLException {
    return localResultSet.getURL(columnIndex);
  }


  public final URL getURL(String columnLabel) throws SQLException {
    return localResultSet.getURL(columnLabel);
  }


  public final InputStream getUnicodeStream(int columnIndex) throws SQLException {
    return localResultSet.getUnicodeStream(columnIndex);
  }


  public final InputStream getUnicodeStream(String columnLabel) throws SQLException {
    return localResultSet.getUnicodeStream(columnLabel);
  }


  public final SQLWarning getWarnings() throws SQLException {
    return localResultSet.getWarnings();
  }


  public final void insertRow() throws SQLException {
    localResultSet.insertRow();
  }


  public final boolean isAfterLast() throws SQLException {
    return localResultSet.isAfterLast();
  }


  public final boolean isBeforeFirst() throws SQLException {
    return localResultSet.isBeforeFirst();
  }


  public final boolean isClosed() throws SQLException {
    return localResultSet.isClosed();
  }


  public final boolean isFirst() throws SQLException {
    return localResultSet.isFirst();
  }


  public final boolean isLast() throws SQLException {
    return localResultSet.isLast();
  }


  public final boolean isWrapperFor(Class<?> iface) throws SQLException {
    return localResultSet.isWrapperFor(iface);
  }


  public final boolean last() throws SQLException {
    return localResultSet.last();
  }


  public final void moveToCurrentRow() throws SQLException {
    localResultSet.moveToCurrentRow();
  }


  public final void moveToInsertRow() throws SQLException {
    localResultSet.moveToInsertRow();
  }


  public final boolean next() throws SQLException {
    return localResultSet.next();
  }


  public final boolean previous() throws SQLException {
    return localResultSet.previous();
  }


  public final void refreshRow() throws SQLException {
    localResultSet.refreshRow();
  }


  public final boolean relative(int rows) throws SQLException {
    return localResultSet.relative(rows);
  }


  public final boolean rowDeleted() throws SQLException {
    return localResultSet.rowDeleted();
  }


  public final boolean rowInserted() throws SQLException {
    return localResultSet.rowInserted();
  }


  public final boolean rowUpdated() throws SQLException {
    return localResultSet.rowUpdated();
  }


  public final void setFetchDirection(int direction) throws SQLException {
    localResultSet.setFetchDirection(direction);
  }


  public final void setFetchSize(int rows) throws SQLException {
    localResultSet.setFetchSize(rows);
  }


  public final <T> T unwrap(Class<T> iface) throws SQLException {
    return localResultSet.unwrap(iface);
  }


  public final void updateArray(int columnIndex, Array x) throws SQLException {
    localResultSet.updateArray(columnIndex, x);
  }


  public final void updateArray(String columnLabel, Array x) throws SQLException {
    localResultSet.updateArray(columnLabel, x);
  }


  public final void updateAsciiStream(int columnIndex, InputStream x, int length) throws SQLException {
    localResultSet.updateAsciiStream(columnIndex, x, length);
  }


  public final void updateAsciiStream(int columnIndex, InputStream x, long length) throws SQLException {
    localResultSet.updateAsciiStream(columnIndex, x, length);
  }


  public final void updateAsciiStream(int columnIndex, InputStream x) throws SQLException {
    localResultSet.updateAsciiStream(columnIndex, x);
  }


  public final void updateAsciiStream(String columnLabel, InputStream x, int length) throws SQLException {
    localResultSet.updateAsciiStream(columnLabel, x, length);
  }


  public final void updateAsciiStream(String columnLabel, InputStream x, long length) throws SQLException {
    localResultSet.updateAsciiStream(columnLabel, x, length);
  }


  public final void updateAsciiStream(String columnLabel, InputStream x) throws SQLException {
    localResultSet.updateAsciiStream(columnLabel, x);
  }


  public final void updateBigDecimal(int columnIndex, BigDecimal x) throws SQLException {
    localResultSet.updateBigDecimal(columnIndex, x);
  }


  public final void updateBigDecimal(String columnLabel, BigDecimal x) throws SQLException {
    localResultSet.updateBigDecimal(columnLabel, x);
  }


  public final void updateBinaryStream(int columnIndex, InputStream x, int length) throws SQLException {
    localResultSet.updateBinaryStream(columnIndex, x, length);
  }


  public final void updateBinaryStream(int columnIndex, InputStream x, long length) throws SQLException {
    localResultSet.updateBinaryStream(columnIndex, x, length);
  }


  public final void updateBinaryStream(int columnIndex, InputStream x) throws SQLException {
    localResultSet.updateBinaryStream(columnIndex, x);
  }


  public final void updateBinaryStream(String columnLabel, InputStream x, int length) throws SQLException {
    localResultSet.updateBinaryStream(columnLabel, x, length);
  }


  public final void updateBinaryStream(String columnLabel, InputStream x, long length) throws SQLException {
    localResultSet.updateBinaryStream(columnLabel, x, length);
  }


  public final void updateBinaryStream(String columnLabel, InputStream x) throws SQLException {
    localResultSet.updateBinaryStream(columnLabel, x);
  }


  public final void updateBlob(int columnIndex, Blob x) throws SQLException {
    localResultSet.updateBlob(columnIndex, x);
  }


  public final void updateBlob(int columnIndex, InputStream inputStream, long length) throws SQLException {
    localResultSet.updateBlob(columnIndex, inputStream, length);
  }


  public final void updateBlob(int columnIndex, InputStream inputStream) throws SQLException {
    localResultSet.updateBlob(columnIndex, inputStream);
  }


  public final void updateBlob(String columnLabel, Blob x) throws SQLException {
    localResultSet.updateBlob(columnLabel, x);
  }


  public final void updateBlob(String columnLabel, InputStream inputStream, long length) throws SQLException {
    localResultSet.updateBlob(columnLabel, inputStream, length);
  }


  public final void updateBlob(String columnLabel, InputStream inputStream) throws SQLException {
    localResultSet.updateBlob(columnLabel, inputStream);
  }


  public final void updateBoolean(int columnIndex, boolean x) throws SQLException {
    localResultSet.updateBoolean(columnIndex, x);
  }


  public final void updateBoolean(String columnLabel, boolean x) throws SQLException {
    localResultSet.updateBoolean(columnLabel, x);
  }


  public final void updateByte(int columnIndex, byte x) throws SQLException {
    localResultSet.updateByte(columnIndex, x);
  }


  public final void updateByte(String columnLabel, byte x) throws SQLException {
    localResultSet.updateByte(columnLabel, x);
  }


  public final void updateBytes(int columnIndex, byte[] x) throws SQLException {
    localResultSet.updateBytes(columnIndex, x);
  }


  public final void updateBytes(String columnLabel, byte[] x) throws SQLException {
    localResultSet.updateBytes(columnLabel, x);
  }


  public final void updateCharacterStream(int columnIndex, Reader x, int length) throws SQLException {
    localResultSet.updateCharacterStream(columnIndex, x, length);
  }


  public final void updateCharacterStream(int columnIndex, Reader x, long length) throws SQLException {
    localResultSet.updateCharacterStream(columnIndex, x, length);
  }


  public final void updateCharacterStream(int columnIndex, Reader x) throws SQLException {
    localResultSet.updateCharacterStream(columnIndex, x);
  }


  public final void updateCharacterStream(String columnLabel, Reader reader, int length) throws SQLException {
    localResultSet.updateCharacterStream(columnLabel, reader, length);
  }


  public final void updateCharacterStream(String columnLabel, Reader reader, long length) throws SQLException {
    localResultSet.updateCharacterStream(columnLabel, reader, length);
  }


  public final void updateCharacterStream(String columnLabel, Reader reader) throws SQLException {
    localResultSet.updateCharacterStream(columnLabel, reader);
  }


  public final void updateClob(int columnIndex, Clob x) throws SQLException {
    localResultSet.updateClob(columnIndex, x);
  }


  public final void updateClob(int columnIndex, Reader reader, long length) throws SQLException {
    localResultSet.updateClob(columnIndex, reader, length);
  }


  public final void updateClob(int columnIndex, Reader reader) throws SQLException {
    localResultSet.updateClob(columnIndex, reader);
  }


  public final void updateClob(String columnLabel, Clob x) throws SQLException {
    localResultSet.updateClob(columnLabel, x);
  }


  public final void updateClob(String columnLabel, Reader reader, long length) throws SQLException {
    localResultSet.updateClob(columnLabel, reader, length);
  }


  public final void updateClob(String columnLabel, Reader reader) throws SQLException {
    localResultSet.updateClob(columnLabel, reader);
  }


  public final void updateDate(int columnIndex, Date x) throws SQLException {
    localResultSet.updateDate(columnIndex, x);
  }


  public final void updateDate(String columnLabel, Date x) throws SQLException {
    localResultSet.updateDate(columnLabel, x);
  }


  public final void updateDouble(int columnIndex, double x) throws SQLException {
    localResultSet.updateDouble(columnIndex, x);
  }


  public final void updateDouble(String columnLabel, double x) throws SQLException {
    localResultSet.updateDouble(columnLabel, x);
  }


  public final void updateFloat(int columnIndex, float x) throws SQLException {
    localResultSet.updateFloat(columnIndex, x);
  }


  public final void updateFloat(String columnLabel, float x) throws SQLException {
    localResultSet.updateFloat(columnLabel, x);
  }


  public final void updateInt(int columnIndex, int x) throws SQLException {
    localResultSet.updateInt(columnIndex, x);
  }


  public final void updateInt(String columnLabel, int x) throws SQLException {
    localResultSet.updateInt(columnLabel, x);
  }


  public final void updateLong(int columnIndex, long x) throws SQLException {
    localResultSet.updateLong(columnIndex, x);
  }


  public final void updateLong(String columnLabel, long x) throws SQLException {
    localResultSet.updateLong(columnLabel, x);
  }


  public final void updateNCharacterStream(int columnIndex, Reader x, long length) throws SQLException {
    localResultSet.updateNCharacterStream(columnIndex, x, length);
  }


  public final void updateNCharacterStream(int columnIndex, Reader x) throws SQLException {
    localResultSet.updateNCharacterStream(columnIndex, x);
  }


  public final void updateNCharacterStream(String columnLabel, Reader reader, long length) throws SQLException {
    localResultSet.updateNCharacterStream(columnLabel, reader, length);
  }


  public final void updateNCharacterStream(String columnLabel, Reader reader) throws SQLException {
    localResultSet.updateNCharacterStream(columnLabel, reader);
  }


  public final void updateNClob(int columnIndex, NClob nClob) throws SQLException {
    localResultSet.updateNClob(columnIndex, nClob);
  }


  public final void updateNClob(int columnIndex, Reader reader, long length) throws SQLException {
    localResultSet.updateNClob(columnIndex, reader, length);
  }


  public final void updateNClob(int columnIndex, Reader reader) throws SQLException {
    localResultSet.updateNClob(columnIndex, reader);
  }


  public final void updateNClob(String columnLabel, NClob nClob) throws SQLException {
    localResultSet.updateNClob(columnLabel, nClob);
  }


  public final void updateNClob(String columnLabel, Reader reader, long length) throws SQLException {
    localResultSet.updateNClob(columnLabel, reader, length);
  }


  public final void updateNClob(String columnLabel, Reader reader) throws SQLException {
    localResultSet.updateNClob(columnLabel, reader);
  }


  public final void updateNString(int columnIndex, String nString) throws SQLException {
    localResultSet.updateNString(columnIndex, nString);
  }


  public final void updateNString(String columnLabel, String nString) throws SQLException {
    localResultSet.updateNString(columnLabel, nString);
  }


  public final void updateNull(int columnIndex) throws SQLException {
    localResultSet.updateNull(columnIndex);
  }


  public final void updateNull(String columnLabel) throws SQLException {
    localResultSet.updateNull(columnLabel);
  }


  public final void updateObject(int columnIndex, Object x, int scaleOrLength) throws SQLException {
    localResultSet.updateObject(columnIndex, x, scaleOrLength);
  }


  public final void updateObject(int columnIndex, Object x) throws SQLException {
    localResultSet.updateObject(columnIndex, x);
  }


  public final void updateObject(String columnLabel, Object x, int scaleOrLength) throws SQLException {
    localResultSet.updateObject(columnLabel, x, scaleOrLength);
  }


  public final void updateObject(String columnLabel, Object x) throws SQLException {
    localResultSet.updateObject(columnLabel, x);
  }


  public final void updateRef(int columnIndex, Ref x) throws SQLException {
    localResultSet.updateRef(columnIndex, x);
  }


  public final void updateRef(String columnLabel, Ref x) throws SQLException {
    localResultSet.updateRef(columnLabel, x);
  }


  public final void updateRow() throws SQLException {
    localResultSet.updateRow();
  }


  public final void updateRowId(int columnIndex, RowId x) throws SQLException {
    localResultSet.updateRowId(columnIndex, x);
  }


  public final void updateRowId(String columnLabel, RowId x) throws SQLException {
    localResultSet.updateRowId(columnLabel, x);
  }


  public final void updateSQLXML(int columnIndex, SQLXML xmlObject) throws SQLException {
    localResultSet.updateSQLXML(columnIndex, xmlObject);
  }


  public final void updateSQLXML(String columnLabel, SQLXML xmlObject) throws SQLException {
    localResultSet.updateSQLXML(columnLabel, xmlObject);
  }


  public final void updateShort(int columnIndex, short x) throws SQLException {
    localResultSet.updateShort(columnIndex, x);
  }


  public final void updateShort(String columnLabel, short x) throws SQLException {
    localResultSet.updateShort(columnLabel, x);
  }


  public final void updateString(int columnIndex, String x) throws SQLException {
    localResultSet.updateString(columnIndex, x);
  }


  public final void updateString(String columnLabel, String x) throws SQLException {
    localResultSet.updateString(columnLabel, x);
  }


  public final void updateTime(int columnIndex, Time x) throws SQLException {
    localResultSet.updateTime(columnIndex, x);
  }


  public final void updateTime(String columnLabel, Time x) throws SQLException {
    localResultSet.updateTime(columnLabel, x);
  }


  public final void updateTimestamp(int columnIndex, Timestamp x) throws SQLException {
    localResultSet.updateTimestamp(columnIndex, x);
  }


  public final void updateTimestamp(String columnLabel, Timestamp x) throws SQLException {
    localResultSet.updateTimestamp(columnLabel, x);
  }


  public final boolean wasNull() throws SQLException {
    return localResultSet.wasNull();
  }

  /**
   * <p>
   * Retrieves the value of the designated column in the current row of this <code>ResultSet</code> object and will
   * convert from the SQL type of the column to the requested Java data type, if the conversion is supported. If the
   * conversion is not supported or null is specified for the type, a <code>SQLException</code> is thrown.
   * <p>
   * At a minimum, an implementation must support the conversions defined in Appendix B, Table B-3 and conversion of
   * appropriate user defined SQL types to a Java type which implements {@code SQLData}, or {@code Struct}. Additional
   * conversions may be supported and are vendor defined.
   * 
   * @param <T>
   *          Class representing the Java data type to convert the designated column to.
   * @param columnIndex
   *          the first column is 1, the second is 2, ...
   * @param type
   *          Class representing the Java data type to convert the designated column to.
   * @return an instance of {@code type} holding the column value
   * @throws SQLException
   *           if conversion is not supported, type is null or another error occurs. The getCause() method of the
   *           exception may provide a more detailed exception, for example, if a conversion error occurs
   * @throws SQLException
   *           if the JDBC driver does not support this method
   */
  public <T> T getObject(int columnIndex, Class<T> type) throws SQLException {
    @SuppressWarnings("unchecked")
    T object = (T) localResultSet.getObject(columnIndex);
    return object;
  }

  /**
   * <p>
   * Retrieves the value of the designated column in the current row of this <code>ResultSet</code> object and will
   * convert from the SQL type of the column to the requested Java data type, if the conversion is supported. If the
   * conversion is not supported or null is specified for the type, a <code>SQLException</code> is thrown.
   * <p>
   * At a minimum, an implementation must support the conversions defined in Appendix B, Table B-3 and conversion of
   * appropriate user defined SQL types to a Java type which implements {@code SQLData}, or {@code Struct}. Additional
   * conversions may be supported and are vendor defined.
   * 
   * @param <T>
   *          Class representing the Java data type to convert the designated column to.
   * @param columnLabel
   *          the label for the column specified with the SQL AS clause. If the SQL AS clause was not specified, then
   *          the label is the name of the column
   * @param type
   *          Class representing the Java data type to convert the designated column to.
   * @return an instance of {@code type} holding the column value
   * @throws SQLException
   *           if conversion is not supported, type is null or another error occurs. The getCause() method of the
   *           exception may provide a more detailed exception, for example, if a conversion error occurs
   * @throws SQLException
   *           if the JDBC driver does not support this method
   */
  public <T> T getObject(String columnLabel, Class<T> type) throws SQLException {
    @SuppressWarnings("unchecked")
    T object = (T) localResultSet.getObject(columnLabel);
    return object;
  }

}
