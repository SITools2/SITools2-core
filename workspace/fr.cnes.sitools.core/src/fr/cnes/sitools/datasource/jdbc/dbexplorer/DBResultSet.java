/*******************************************************************************
 * Copyright 2011, 2012 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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

/**
 * Encapsulation of a result set with its database connection
 * in order to release it after use. 
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

  @Override
  public final boolean absolute(int row) throws SQLException {
    return localResultSet.absolute(row);
  }

  @Override
  public final void afterLast() throws SQLException {
    localResultSet.afterLast();
  }

  @Override
  public final void beforeFirst() throws SQLException {
    localResultSet.beforeFirst();
  }

  @Override
  public final void cancelRowUpdates() throws SQLException {
    localResultSet.cancelRowUpdates();
  }

  @Override
  public final void clearWarnings() throws SQLException {
    localResultSet.clearWarnings();
  }

  @Override
  public final void close() throws SQLException {
    try {
      statement.close();
    }
    catch (Exception e) {
      getLogger().warning(e.getMessage());
    }
    try {
      localResultSet.close();
    }
    catch (Exception e) {
      getLogger().warning(e.getMessage());
    }
    try {
      conn.close();
    }
    catch (Exception e) {
      getLogger().warning(e.getMessage());
    }
  }

  /**
   * Get the logger
   * @return the logger
   */
  private Logger getLogger() {
    return Logger.getLogger(this.getClass().getName());
  }

  @Override
  public final void deleteRow() throws SQLException {
    localResultSet.deleteRow();
  }

  @Override
  public final int findColumn(String columnLabel) throws SQLException {
    return localResultSet.findColumn(columnLabel);
  }

  @Override
  public final boolean first() throws SQLException {
    return localResultSet.first();
  }

  @Override
  public final Array getArray(int columnIndex) throws SQLException {
    return localResultSet.getArray(columnIndex);
  }

  @Override
  public final Array getArray(String columnLabel) throws SQLException {
    return localResultSet.getArray(columnLabel);
  }

  @Override
  public final InputStream getAsciiStream(int columnIndex) throws SQLException {
    return localResultSet.getAsciiStream(columnIndex);
  }

  @Override
  public final InputStream getAsciiStream(String columnLabel) throws SQLException {
    return localResultSet.getAsciiStream(columnLabel);
  }

  @Override
  public final BigDecimal getBigDecimal(int columnIndex, int scale) throws SQLException {
    return localResultSet.getBigDecimal(columnIndex, scale);
  }

  @Override
  public final BigDecimal getBigDecimal(int columnIndex) throws SQLException {
    return localResultSet.getBigDecimal(columnIndex);
  }

  @Override
  public final BigDecimal getBigDecimal(String columnLabel, int scale) throws SQLException {
    return localResultSet.getBigDecimal(columnLabel, scale);
  }

  @Override
  public final BigDecimal getBigDecimal(String columnLabel) throws SQLException {
    return localResultSet.getBigDecimal(columnLabel);
  }

  @Override
  public final InputStream getBinaryStream(int columnIndex) throws SQLException {
    return localResultSet.getBinaryStream(columnIndex);
  }

  @Override
  public final InputStream getBinaryStream(String columnLabel) throws SQLException {
    return localResultSet.getBinaryStream(columnLabel);
  }

  @Override
  public final Blob getBlob(int columnIndex) throws SQLException {
    return localResultSet.getBlob(columnIndex);
  }

  @Override
  public final Blob getBlob(String columnLabel) throws SQLException {
    return localResultSet.getBlob(columnLabel);
  }

  @Override
  public final boolean getBoolean(int columnIndex) throws SQLException {
    return localResultSet.getBoolean(columnIndex);
  }

  @Override
  public final boolean getBoolean(String columnLabel) throws SQLException {
    return localResultSet.getBoolean(columnLabel);
  }

  @Override
  public final byte getByte(int columnIndex) throws SQLException {
    return localResultSet.getByte(columnIndex);
  }

  @Override
  public final byte getByte(String columnLabel) throws SQLException {
    return localResultSet.getByte(columnLabel);
  }

  @Override
  public final byte[] getBytes(int columnIndex) throws SQLException {
    return localResultSet.getBytes(columnIndex);
  }

  @Override
  public final byte[] getBytes(String columnLabel) throws SQLException {
    return localResultSet.getBytes(columnLabel);
  }

  @Override
  public final Reader getCharacterStream(int columnIndex) throws SQLException {
    return localResultSet.getCharacterStream(columnIndex);
  }

  @Override
  public final Reader getCharacterStream(String columnLabel) throws SQLException {
    return localResultSet.getCharacterStream(columnLabel);
  }

  @Override
  public final Clob getClob(int columnIndex) throws SQLException {
    return localResultSet.getClob(columnIndex);
  }

  @Override
  public final Clob getClob(String columnLabel) throws SQLException {
    return localResultSet.getClob(columnLabel);
  }

  @Override
  public final int getConcurrency() throws SQLException {
    return localResultSet.getConcurrency();
  }

  @Override
  public final String getCursorName() throws SQLException {
    return localResultSet.getCursorName();
  }

  @Override
  public final Date getDate(int columnIndex, Calendar cal) throws SQLException {
    return localResultSet.getDate(columnIndex, cal);
  }

  @Override
  public final Date getDate(int columnIndex) throws SQLException {
    return localResultSet.getDate(columnIndex);
  }

  @Override
  public final Date getDate(String columnLabel, Calendar cal) throws SQLException {
    return localResultSet.getDate(columnLabel, cal);
  }

  @Override
  public final Date getDate(String columnLabel) throws SQLException {
    return localResultSet.getDate(columnLabel);
  }

  @Override
  public final double getDouble(int columnIndex) throws SQLException {
    return localResultSet.getDouble(columnIndex);
  }

  @Override
  public final double getDouble(String columnLabel) throws SQLException {
    return localResultSet.getDouble(columnLabel);
  }

  @Override
  public final int getFetchDirection() throws SQLException {
    return localResultSet.getFetchDirection();
  }

  @Override
  public final int getFetchSize() throws SQLException {
    return localResultSet.getFetchSize();
  }

  @Override
  public final float getFloat(int columnIndex) throws SQLException {
    return localResultSet.getFloat(columnIndex);
  }

  @Override
  public final float getFloat(String columnLabel) throws SQLException {
    return localResultSet.getFloat(columnLabel);
  }

  @Override
  public final int getHoldability() throws SQLException {
    return localResultSet.getHoldability();
  }

  @Override
  public final int getInt(int columnIndex) throws SQLException {
    return localResultSet.getInt(columnIndex);
  }

  @Override
  public final int getInt(String columnLabel) throws SQLException {
    return localResultSet.getInt(columnLabel);
  }

  @Override
  public final long getLong(int columnIndex) throws SQLException {
    return localResultSet.getLong(columnIndex);
  }

  @Override
  public final long getLong(String columnLabel) throws SQLException {
    return localResultSet.getLong(columnLabel);
  }

  @Override
  public final ResultSetMetaData getMetaData() throws SQLException {
    return localResultSet.getMetaData();
  }

  @Override
  public final Reader getNCharacterStream(int columnIndex) throws SQLException {
    return localResultSet.getNCharacterStream(columnIndex);
  }

  @Override
  public final Reader getNCharacterStream(String columnLabel) throws SQLException {
    return localResultSet.getNCharacterStream(columnLabel);
  }

  @Override
  public final NClob getNClob(int columnIndex) throws SQLException {
    return localResultSet.getNClob(columnIndex);
  }

  @Override
  public final NClob getNClob(String columnLabel) throws SQLException {
    return localResultSet.getNClob(columnLabel);
  }

  @Override
  public final String getNString(int columnIndex) throws SQLException {
    return localResultSet.getNString(columnIndex);
  }

  @Override
  public final String getNString(String columnLabel) throws SQLException {
    return localResultSet.getNString(columnLabel);
  }

  @Override
  public final Object getObject(int columnIndex, Map<String, Class<?>> map) throws SQLException {
    return localResultSet.getObject(columnIndex, map);
  }

  @Override
  public final Object getObject(int columnIndex) throws SQLException {
    return localResultSet.getObject(columnIndex);
  }

  @Override
  public final Object getObject(String columnLabel, Map<String, Class<?>> map) throws SQLException {
    return localResultSet.getObject(columnLabel, map);
  }

  @Override
  public final Object getObject(String columnLabel) throws SQLException {
    return localResultSet.getObject(columnLabel);
  }

  @Override
  public final Ref getRef(int columnIndex) throws SQLException {
    return localResultSet.getRef(columnIndex);
  }

  @Override
  public final Ref getRef(String columnLabel) throws SQLException {
    return localResultSet.getRef(columnLabel);
  }

  @Override
  public final int getRow() throws SQLException {
    return localResultSet.getRow();
  }

  @Override
  public final RowId getRowId(int columnIndex) throws SQLException {
    return localResultSet.getRowId(columnIndex);
  }

  @Override
  public final RowId getRowId(String columnLabel) throws SQLException {
    return localResultSet.getRowId(columnLabel);
  }

  @Override
  public final SQLXML getSQLXML(int columnIndex) throws SQLException {
    return localResultSet.getSQLXML(columnIndex);
  }

  @Override
  public final SQLXML getSQLXML(String columnLabel) throws SQLException {
    return localResultSet.getSQLXML(columnLabel);
  }

  @Override
  public final short getShort(int columnIndex) throws SQLException {
    return localResultSet.getShort(columnIndex);
  }

  @Override
  public final short getShort(String columnLabel) throws SQLException {
    return localResultSet.getShort(columnLabel);
  }

  @Override
  public final Statement getStatement() throws SQLException {
    return localResultSet.getStatement();
  }

  @Override
  public final String getString(int columnIndex) throws SQLException {
    return localResultSet.getString(columnIndex);
  }

  @Override
  public final String getString(String columnLabel) throws SQLException {
    return localResultSet.getString(columnLabel);
  }

  @Override
  public final Time getTime(int columnIndex, Calendar cal) throws SQLException {
    return localResultSet.getTime(columnIndex, cal);
  }

  @Override
  public final Time getTime(int columnIndex) throws SQLException {
    return localResultSet.getTime(columnIndex);
  }

  @Override
  public final Time getTime(String columnLabel, Calendar cal) throws SQLException {
    return localResultSet.getTime(columnLabel, cal);
  }

  @Override
  public final Time getTime(String columnLabel) throws SQLException {
    return localResultSet.getTime(columnLabel);
  }

  @Override
  public final Timestamp getTimestamp(int columnIndex, Calendar cal) throws SQLException {
    return localResultSet.getTimestamp(columnIndex, cal);
  }

  @Override
  public final Timestamp getTimestamp(int columnIndex) throws SQLException {
    return localResultSet.getTimestamp(columnIndex);
  }

  @Override
  public final Timestamp getTimestamp(String columnLabel, Calendar cal) throws SQLException {
    return localResultSet.getTimestamp(columnLabel, cal);
  }

  @Override
  public final Timestamp getTimestamp(String columnLabel) throws SQLException {
    return localResultSet.getTimestamp(columnLabel);
  }

  @Override
  public final int getType() throws SQLException {
    return localResultSet.getType();
  }

  @Override
  public final URL getURL(int columnIndex) throws SQLException {
    return localResultSet.getURL(columnIndex);
  }

  @Override
  public final URL getURL(String columnLabel) throws SQLException {
    return localResultSet.getURL(columnLabel);
  }

  @Override
  public final InputStream getUnicodeStream(int columnIndex) throws SQLException {
    return localResultSet.getUnicodeStream(columnIndex);
  }

  @Override
  public final InputStream getUnicodeStream(String columnLabel) throws SQLException {
    return localResultSet.getUnicodeStream(columnLabel);
  }

  @Override
  public final SQLWarning getWarnings() throws SQLException {
    return localResultSet.getWarnings();
  }

  @Override
  public final void insertRow() throws SQLException {
    localResultSet.insertRow();
  }

  @Override
  public final boolean isAfterLast() throws SQLException {
    return localResultSet.isAfterLast();
  }

  @Override
  public final boolean isBeforeFirst() throws SQLException {
    return localResultSet.isBeforeFirst();
  }

  @Override
  public final boolean isClosed() throws SQLException {
    return localResultSet.isClosed();
  }

  @Override
  public final boolean isFirst() throws SQLException {
    return localResultSet.isFirst();
  }

  @Override
  public final boolean isLast() throws SQLException {
    return localResultSet.isLast();
  }

  @Override
  public final boolean isWrapperFor(Class<?> iface) throws SQLException {
    return localResultSet.isWrapperFor(iface);
  }

  @Override
  public final boolean last() throws SQLException {
    return localResultSet.last();
  }

  @Override
  public final void moveToCurrentRow() throws SQLException {
    localResultSet.moveToCurrentRow();
  }

  @Override
  public final void moveToInsertRow() throws SQLException {
    localResultSet.moveToInsertRow();
  }

  @Override
  public final boolean next() throws SQLException {
    return localResultSet.next();
  }

  @Override
  public final boolean previous() throws SQLException {
    return localResultSet.previous();
  }

  @Override
  public final void refreshRow() throws SQLException {
    localResultSet.refreshRow();
  }

  @Override
  public final boolean relative(int rows) throws SQLException {
    return localResultSet.relative(rows);
  }

  @Override
  public final boolean rowDeleted() throws SQLException {
    return localResultSet.rowDeleted();
  }

  @Override
  public final boolean rowInserted() throws SQLException {
    return localResultSet.rowInserted();
  }

  @Override
  public final boolean rowUpdated() throws SQLException {
    return localResultSet.rowUpdated();
  }

  @Override
  public final void setFetchDirection(int direction) throws SQLException {
    localResultSet.setFetchDirection(direction);
  }

  @Override
  public final void setFetchSize(int rows) throws SQLException {
    localResultSet.setFetchSize(rows);
  }

  @Override
  public final <T> T unwrap(Class<T> iface) throws SQLException {
    return localResultSet.unwrap(iface);
  }

  @Override
  public final void updateArray(int columnIndex, Array x) throws SQLException {
    localResultSet.updateArray(columnIndex, x);
  }

  @Override
  public final void updateArray(String columnLabel, Array x) throws SQLException {
    localResultSet.updateArray(columnLabel, x);
  }

  @Override
  public final void updateAsciiStream(int columnIndex, InputStream x, int length) throws SQLException {
    localResultSet.updateAsciiStream(columnIndex, x, length);
  }

  @Override
  public final void updateAsciiStream(int columnIndex, InputStream x, long length) throws SQLException {
    localResultSet.updateAsciiStream(columnIndex, x, length);
  }

  @Override
  public final void updateAsciiStream(int columnIndex, InputStream x) throws SQLException {
    localResultSet.updateAsciiStream(columnIndex, x);
  }

  @Override
  public final void updateAsciiStream(String columnLabel, InputStream x, int length) throws SQLException {
    localResultSet.updateAsciiStream(columnLabel, x, length);
  }

  @Override
  public final void updateAsciiStream(String columnLabel, InputStream x, long length) throws SQLException {
    localResultSet.updateAsciiStream(columnLabel, x, length);
  }

  @Override
  public final void updateAsciiStream(String columnLabel, InputStream x) throws SQLException {
    localResultSet.updateAsciiStream(columnLabel, x);
  }

  @Override
  public final void updateBigDecimal(int columnIndex, BigDecimal x) throws SQLException {
    localResultSet.updateBigDecimal(columnIndex, x);
  }

  @Override
  public final void updateBigDecimal(String columnLabel, BigDecimal x) throws SQLException {
    localResultSet.updateBigDecimal(columnLabel, x);
  }

  @Override
  public final void updateBinaryStream(int columnIndex, InputStream x, int length) throws SQLException {
    localResultSet.updateBinaryStream(columnIndex, x, length);
  }

  @Override
  public final void updateBinaryStream(int columnIndex, InputStream x, long length) throws SQLException {
    localResultSet.updateBinaryStream(columnIndex, x, length);
  }

  @Override
  public final void updateBinaryStream(int columnIndex, InputStream x) throws SQLException {
    localResultSet.updateBinaryStream(columnIndex, x);
  }

  @Override
  public final void updateBinaryStream(String columnLabel, InputStream x, int length) throws SQLException {
    localResultSet.updateBinaryStream(columnLabel, x, length);
  }

  @Override
  public final void updateBinaryStream(String columnLabel, InputStream x, long length) throws SQLException {
    localResultSet.updateBinaryStream(columnLabel, x, length);
  }

  @Override
  public final void updateBinaryStream(String columnLabel, InputStream x) throws SQLException {
    localResultSet.updateBinaryStream(columnLabel, x);
  }

  @Override
  public final void updateBlob(int columnIndex, Blob x) throws SQLException {
    localResultSet.updateBlob(columnIndex, x);
  }

  @Override
  public final void updateBlob(int columnIndex, InputStream inputStream, long length) throws SQLException {
    localResultSet.updateBlob(columnIndex, inputStream, length);
  }

  @Override
  public final void updateBlob(int columnIndex, InputStream inputStream) throws SQLException {
    localResultSet.updateBlob(columnIndex, inputStream);
  }

  @Override
  public final void updateBlob(String columnLabel, Blob x) throws SQLException {
    localResultSet.updateBlob(columnLabel, x);
  }

  @Override
  public final void updateBlob(String columnLabel, InputStream inputStream, long length) throws SQLException {
    localResultSet.updateBlob(columnLabel, inputStream, length);
  }

  @Override
  public final void updateBlob(String columnLabel, InputStream inputStream) throws SQLException {
    localResultSet.updateBlob(columnLabel, inputStream);
  }

  @Override
  public final void updateBoolean(int columnIndex, boolean x) throws SQLException {
    localResultSet.updateBoolean(columnIndex, x);
  }

  @Override
  public final void updateBoolean(String columnLabel, boolean x) throws SQLException {
    localResultSet.updateBoolean(columnLabel, x);
  }

  @Override
  public final void updateByte(int columnIndex, byte x) throws SQLException {
    localResultSet.updateByte(columnIndex, x);
  }

  @Override
  public final void updateByte(String columnLabel, byte x) throws SQLException {
    localResultSet.updateByte(columnLabel, x);
  }

  @Override
  public final void updateBytes(int columnIndex, byte[] x) throws SQLException {
    localResultSet.updateBytes(columnIndex, x);
  }

  @Override
  public final void updateBytes(String columnLabel, byte[] x) throws SQLException {
    localResultSet.updateBytes(columnLabel, x);
  }

  @Override
  public final void updateCharacterStream(int columnIndex, Reader x, int length) throws SQLException {
    localResultSet.updateCharacterStream(columnIndex, x, length);
  }

  @Override
  public final void updateCharacterStream(int columnIndex, Reader x, long length) throws SQLException {
    localResultSet.updateCharacterStream(columnIndex, x, length);
  }

  @Override
  public final void updateCharacterStream(int columnIndex, Reader x) throws SQLException {
    localResultSet.updateCharacterStream(columnIndex, x);
  }

  @Override
  public final void updateCharacterStream(String columnLabel, Reader reader, int length) throws SQLException {
    localResultSet.updateCharacterStream(columnLabel, reader, length);
  }

  @Override
  public final void updateCharacterStream(String columnLabel, Reader reader, long length) throws SQLException {
    localResultSet.updateCharacterStream(columnLabel, reader, length);
  }

  @Override
  public final void updateCharacterStream(String columnLabel, Reader reader) throws SQLException {
    localResultSet.updateCharacterStream(columnLabel, reader);
  }

  @Override
  public final void updateClob(int columnIndex, Clob x) throws SQLException {
    localResultSet.updateClob(columnIndex, x);
  }

  @Override
  public final void updateClob(int columnIndex, Reader reader, long length) throws SQLException {
    localResultSet.updateClob(columnIndex, reader, length);
  }

  @Override
  public final void updateClob(int columnIndex, Reader reader) throws SQLException {
    localResultSet.updateClob(columnIndex, reader);
  }

  @Override
  public final void updateClob(String columnLabel, Clob x) throws SQLException {
    localResultSet.updateClob(columnLabel, x);
  }

  @Override
  public final void updateClob(String columnLabel, Reader reader, long length) throws SQLException {
    localResultSet.updateClob(columnLabel, reader, length);
  }

  @Override
  public final void updateClob(String columnLabel, Reader reader) throws SQLException {
    localResultSet.updateClob(columnLabel, reader);
  }

  @Override
  public final void updateDate(int columnIndex, Date x) throws SQLException {
    localResultSet.updateDate(columnIndex, x);
  }

  @Override
  public final void updateDate(String columnLabel, Date x) throws SQLException {
    localResultSet.updateDate(columnLabel, x);
  }

  @Override
  public final void updateDouble(int columnIndex, double x) throws SQLException {
    localResultSet.updateDouble(columnIndex, x);
  }

  @Override
  public final void updateDouble(String columnLabel, double x) throws SQLException {
    localResultSet.updateDouble(columnLabel, x);
  }

  @Override
  public final void updateFloat(int columnIndex, float x) throws SQLException {
    localResultSet.updateFloat(columnIndex, x);
  }

  @Override
  public final void updateFloat(String columnLabel, float x) throws SQLException {
    localResultSet.updateFloat(columnLabel, x);
  }

  @Override
  public final void updateInt(int columnIndex, int x) throws SQLException {
    localResultSet.updateInt(columnIndex, x);
  }

  @Override
  public final void updateInt(String columnLabel, int x) throws SQLException {
    localResultSet.updateInt(columnLabel, x);
  }

  @Override
  public final void updateLong(int columnIndex, long x) throws SQLException {
    localResultSet.updateLong(columnIndex, x);
  }

  @Override
  public final void updateLong(String columnLabel, long x) throws SQLException {
    localResultSet.updateLong(columnLabel, x);
  }

  @Override
  public final void updateNCharacterStream(int columnIndex, Reader x, long length) throws SQLException {
    localResultSet.updateNCharacterStream(columnIndex, x, length);
  }

  @Override
  public final void updateNCharacterStream(int columnIndex, Reader x) throws SQLException {
    localResultSet.updateNCharacterStream(columnIndex, x);
  }

  @Override
  public final void updateNCharacterStream(String columnLabel, Reader reader, long length) throws SQLException {
    localResultSet.updateNCharacterStream(columnLabel, reader, length);
  }

  @Override
  public final void updateNCharacterStream(String columnLabel, Reader reader) throws SQLException {
    localResultSet.updateNCharacterStream(columnLabel, reader);
  }

  @Override
  public final void updateNClob(int columnIndex, NClob nClob) throws SQLException {
    localResultSet.updateNClob(columnIndex, nClob);
  }

  @Override
  public final void updateNClob(int columnIndex, Reader reader, long length) throws SQLException {
    localResultSet.updateNClob(columnIndex, reader, length);
  }

  @Override
  public final void updateNClob(int columnIndex, Reader reader) throws SQLException {
    localResultSet.updateNClob(columnIndex, reader);
  }

  @Override
  public final void updateNClob(String columnLabel, NClob nClob) throws SQLException {
    localResultSet.updateNClob(columnLabel, nClob);
  }

  @Override
  public final void updateNClob(String columnLabel, Reader reader, long length) throws SQLException {
    localResultSet.updateNClob(columnLabel, reader, length);
  }

  @Override
  public final void updateNClob(String columnLabel, Reader reader) throws SQLException {
    localResultSet.updateNClob(columnLabel, reader);
  }

  @Override
  public final void updateNString(int columnIndex, String nString) throws SQLException {
    localResultSet.updateNString(columnIndex, nString);
  }

  @Override
  public final void updateNString(String columnLabel, String nString) throws SQLException {
    localResultSet.updateNString(columnLabel, nString);
  }

  @Override
  public final void updateNull(int columnIndex) throws SQLException {
    localResultSet.updateNull(columnIndex);
  }

  @Override
  public final void updateNull(String columnLabel) throws SQLException {
    localResultSet.updateNull(columnLabel);
  }

  @Override
  public final void updateObject(int columnIndex, Object x, int scaleOrLength) throws SQLException {
    localResultSet.updateObject(columnIndex, x, scaleOrLength);
  }

  @Override
  public final void updateObject(int columnIndex, Object x) throws SQLException {
    localResultSet.updateObject(columnIndex, x);
  }

  @Override
  public final void updateObject(String columnLabel, Object x, int scaleOrLength) throws SQLException {
    localResultSet.updateObject(columnLabel, x, scaleOrLength);
  }

  @Override
  public final void updateObject(String columnLabel, Object x) throws SQLException {
    localResultSet.updateObject(columnLabel, x);
  }

  @Override
  public final void updateRef(int columnIndex, Ref x) throws SQLException {
    localResultSet.updateRef(columnIndex, x);
  }

  @Override
  public final void updateRef(String columnLabel, Ref x) throws SQLException {
    localResultSet.updateRef(columnLabel, x);
  }

  @Override
  public final void updateRow() throws SQLException {
    localResultSet.updateRow();
  }

  @Override
  public final void updateRowId(int columnIndex, RowId x) throws SQLException {
    localResultSet.updateRowId(columnIndex, x);
  }

  @Override
  public final void updateRowId(String columnLabel, RowId x) throws SQLException {
    localResultSet.updateRowId(columnLabel, x);
  }

  @Override
  public final void updateSQLXML(int columnIndex, SQLXML xmlObject) throws SQLException {
    localResultSet.updateSQLXML(columnIndex, xmlObject);
  }

  @Override
  public final void updateSQLXML(String columnLabel, SQLXML xmlObject) throws SQLException {
    localResultSet.updateSQLXML(columnLabel, xmlObject);
  }

  @Override
  public final void updateShort(int columnIndex, short x) throws SQLException {
    localResultSet.updateShort(columnIndex, x);
  }

  @Override
  public final void updateShort(String columnLabel, short x) throws SQLException {
    localResultSet.updateShort(columnLabel, x);
  }

  @Override
  public final void updateString(int columnIndex, String x) throws SQLException {
    localResultSet.updateString(columnIndex, x);
  }

  @Override
  public final void updateString(String columnLabel, String x) throws SQLException {
    localResultSet.updateString(columnLabel, x);
  }

  @Override
  public final void updateTime(int columnIndex, Time x) throws SQLException {
    localResultSet.updateTime(columnIndex, x);
  }

  @Override
  public final void updateTime(String columnLabel, Time x) throws SQLException {
    localResultSet.updateTime(columnLabel, x);
  }

  @Override
  public final void updateTimestamp(int columnIndex, Timestamp x) throws SQLException {
    localResultSet.updateTimestamp(columnIndex, x);
  }

  @Override
  public final void updateTimestamp(String columnLabel, Timestamp x) throws SQLException {
    localResultSet.updateTimestamp(columnLabel, x);
  }

  @Override
  public final boolean wasNull() throws SQLException {
    return localResultSet.wasNull();
  }

  public <T> T getObject(int arg0, Class<T> arg1) throws SQLException {
    return (T) localResultSet.getObject(arg0);
  }

  public <T> T getObject(String arg0, Class<T> arg1) throws SQLException {
    return (T) localResultSet.getObject(arg0);
  }

}
