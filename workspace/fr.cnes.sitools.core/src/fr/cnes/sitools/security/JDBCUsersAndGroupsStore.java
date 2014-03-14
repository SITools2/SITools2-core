/*******************************************************************************
 * Copyright 2010-2013 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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
package fr.cnes.sitools.security;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sql.DataSource;

import org.restlet.Context;
import org.restlet.engine.Engine;

import fr.cnes.sitools.common.SitoolsSettings;
import fr.cnes.sitools.common.application.ContextAttributes;
import fr.cnes.sitools.common.exception.SitoolsException;
import fr.cnes.sitools.common.model.Resource;
import fr.cnes.sitools.common.model.ResourceCollectionFilter;
import fr.cnes.sitools.datasource.jdbc.business.SitoolsSQLDataSource;
import fr.cnes.sitools.datasource.jdbc.business.SitoolsSQLDataSourceFactory;
import fr.cnes.sitools.security.model.Group;
import fr.cnes.sitools.security.model.User;
import fr.cnes.sitools.util.Property;

/**
 * Implementation of UsersAndGroupsStore with SQL JDBC Persistence
 * 
 * <a href="https://sourceforge.net/tracker/?func=detail&aid=3317773&group_id=531341&atid=2158259">[3317773]</a><br/>
 * 21/06/2011 m.gond {Use of a transaction when creating and updating a user so that if there is an error the
 * modifications aren't executed} <br/>
 * 
 * @author jp.boignard (AKKA Technologies)
 * 
 */
public final class JDBCUsersAndGroupsStore implements UsersAndGroupsStore {
  /** Logger */
  private static Logger logger = Engine.getLogger(JDBCUsersAndGroupsStore.class.getName());

  // TODO important : ehCACHE des users, au moins (id, password) pour
  // Authentication - Verifier
  // TODO rendre paramétrable les propriétés/requêtes du JDBCUsersAndGroupsStore

  // TODO si BD CMS => users/groups non modifiables ?

  // TODO schema peut etre fourni par le datasource / database ?
  // TODO Gestion transactionnelle de la suppression des users, groups

  /** User field filter */
  private static final String USER_FIELD_FILTER = "lastname";

  /** Group field filter */
  private static final String GROUP_FIELD_FILTER = "name";

  /** Store name */
  private String name;

  /** Datasource */
  private DataSource ds = null;

  /** Resources */
  private JDBCUsersAndGroupsStoreResource jdbcStoreResource = null;
  /** The sitools settings */
  private SitoolsSettings settings;

  // /** Credentials of users */
  // private Map<String, String> credentials = new ConcurrentHashMap<String, String>();

  /**
   * Constructor with a DataSource
   * 
   * @param name
   *          Store name
   * @param ds
   *          DataSource
   * @param context
   *          The Context
   * @throws SitoolsException
   *           if the connection fail
   */
  public JDBCUsersAndGroupsStore(String name, SitoolsSQLDataSource ds, Context context) throws SitoolsException {
    this.name = name;
    this.ds = ds;

    settings = (SitoolsSettings) context.getAttributes().get(ContextAttributes.SETTINGS);

    try {
      ds.getConnection();
    }
    catch (SQLException e) {
      throw new SitoolsException("User database connection error", e);
    }

    if (ds.getDsModel().getDriverClass().equals("org.postgresql.Driver")) {
      jdbcStoreResource = new PGSQLUsersAndGroupsStoreResource(ds);
    }
    else if (ds.getDsModel().getDriverClass().equals("org.gjt.mm.mysql.Driver")) {
      jdbcStoreResource = new MYSQLUsersAndGroupsStoreResource();
    }
    else if (ds.getDsModel().getDriverClass().equals("org.hsqldb.jdbcDriver")) {
      jdbcStoreResource = new HSQLDBUsersAndGroupsStoreResource(ds);
    }
    else {
      logger.severe("Incorrect JDBC Driver for JDBCUsersAndGroupsStore file");
    }
  }

  /**
   * Constructor with a DataSource name
   * 
   * @param name
   *          Store name
   * @param dataSourceName
   *          DataSource name for getting from SitoolsDataSourceFactory
   */
  public JDBCUsersAndGroupsStore(String name, String dataSourceName) {
    this.name = name;
    DataSource dsFound = SitoolsSQLDataSourceFactory.getDataSource(dataSourceName);
    if (ds != null) {
      this.ds = dsFound;
    }
    else {
      logger.severe("DATASOURCE NOT FOUND");
    }
  }

  /**
   * Checks Users modification
   * 
   * @param user
   *          The User
   * 
   * @throws SitoolsException
   *           if users are not modifiable
   */
  private void checkUser(User user) throws SitoolsException {

    String userRegExp = settings.getString("STARTER.SECURITY.USER_LOGIN_REGEX", null);
    if (user.getIdentifier() != null && (userRegExp != null && !user.getIdentifier().matches(userRegExp))) {
      throw new SitoolsException("WRONG_USER_LOGIN");
    }
    // at that point, the password is encoded, so no verification can be done
    // String passwordRegExp = settings.getString("STARTER.SECURITY.USER_PASSWORD_REGEX", null);
    // if (user.getSecret() != null && (passwordRegExp != null && !user.getSecret().matches(passwordRegExp))) {
    // throw new SitoolsException("WRONG USER PASSWORD");
    // }

  }

  /**
   * Checks Groups modification
   * 
   * @throws SitoolsException
   *           if groups are not modifiable
   */
  private void checkGroup() throws SitoolsException {
    if (!isGroupModifiable()) {
      throw new SitoolsException("Operation refused");
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.cnes.sitools.security.UsersAndGroupsStore#getUsers()
   */
  @Override
  public List<User> getUsers() throws SitoolsException {
    return getUsers(null);
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.cnes.sitools.security.UsersAndGroupsStore#getUsers(fr.cnes.sitools.common .model.ResourceCollectionFilter)
   */
  /*
   * (non-Javadoc)
   * 
   * @see fr.cnes.sitools.security.UsersAndGroupsStore#getUsers(fr.cnes.sitools.common .model.ResourceCollectionFilter)
   */
  @Override
  public List<User> getUsers(ResourceCollectionFilter filter) throws SitoolsException {
    Connection cx = null;
    ResultSet rsCount = null;
    ResultSet rs = null;
    try {
      cx = ds.getConnection();

      if (filter != null) {
        String countQuery = filter.toSqlCount(jdbcStoreResource.SELECT_USERS, USER_FIELD_FILTER);
        PreparedStatement stCount = cx.prepareStatement(countQuery);
        rsCount = stCount.executeQuery();
        if (rsCount.next()) {
          filter.setTotalCount(rsCount.getInt(1));
        }
      }

      String request = (filter != null) ? filter.toSQL(jdbcStoreResource.SELECT_USERS, USER_FIELD_FILTER)
          : jdbcStoreResource.SELECT_USERS;

      Statement st = null;
      try {
        st = cx.createStatement();
        rs = st.executeQuery(request);

        List<User> ul = new ArrayList<User>();

        while (rs.next()) {
          User u = parseUser(rs);
          ul.add(u);
        }
        return ul;
      }
      finally {
        if (st != null) {
          st.close();
        }
      }

    }
    catch (SQLException e) {
      throw new SitoolsException("getUsers", e);
    }
    finally {
      closeConnection(cx);
      closeResultSet(rs);
      closeResultSet(rsCount);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.cnes.sitools.security.UsersAndGroupsStore#getUsers(int, int, java.lang.String)
   */
  @Override
  public List<User> getUsers(int start, int limit, String query) throws SitoolsException {
    return getUsers(new ResourceCollectionFilter(start, limit, query));
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.cnes.sitools.security.UsersAndGroupsStore#getGroups()
   */
  @Override
  public List<Group> getGroups() throws SitoolsException {
    return getGroups(null);
  }

  @Override
  public List<Group> getGroups(ResourceCollectionFilter filter) throws SitoolsException {
    Connection cx = null;
    ResultSet rsCount = null;
    ResultSet rs = null;
    try {
      cx = ds.getConnection();

      if (filter != null) {
        String countQuery = filter.toSqlCount(jdbcStoreResource.SELECT_GROUPS, GROUP_FIELD_FILTER);
        PreparedStatement stCount = cx.prepareStatement(countQuery);
        rsCount = stCount.executeQuery();
        if (rsCount.next()) {
          filter.setTotalCount(rsCount.getInt(1));
        }
      }

      String request = (filter != null) ? filter.toSQL(jdbcStoreResource.SELECT_GROUPS, GROUP_FIELD_FILTER)
          : jdbcStoreResource.SELECT_GROUPS;

      ArrayList<Group> gl;
      Statement st = null;
      try {
        st = cx.createStatement();
        rs = st.executeQuery(request);

        gl = new ArrayList<Group>();

        while (rs.next()) {
          Group g = parseGroup(rs);
          gl.add(g);
        }
      }
      finally {
        if (st != null) {
          st.close();
        }
      }
      return gl;

    }
    catch (SQLException e) {
      throw new SitoolsException("getGroups " + e.getMessage(), e);
    }
    finally {
      closeConnection(cx);
      closeResultSet(rs);
      closeResultSet(rsCount);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.cnes.sitools.security.UsersAndGroupsStore#getGroups(int, int, java.lang.String)
   */
  @Override
  public List<Group> getGroups(int start, int limit, String query) throws SitoolsException {
    return getGroups(new ResourceCollectionFilter(start, limit, query));
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.cnes.sitools.security.UsersAndGroupsStore#getUsersByGroup(java.lang. String)
   */
  @Override
  public List<User> getUsersByGroup(String name) throws SitoolsException {
    return getUsersByGroup(name, null);
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.cnes.sitools.security.UsersAndGroupsStore#getUsersByGroup(java.lang. String,
   * fr.cnes.sitools.common.model.ResourceCollectionFilter)
   */
  @Override
  public List<User> getUsersByGroup(String name, ResourceCollectionFilter filter) throws SitoolsException {
    Connection cx = null;
    ResultSet rsCount = null;
    ResultSet rs = null;
    try {
      cx = ds.getConnection();

      if (filter != null) {
        String countQuery = filter.toSqlCount(jdbcStoreResource.SELECT_USERS_BY_GROUP, USER_FIELD_FILTER);
        PreparedStatement stCount = cx.prepareStatement(countQuery);
        stCount.setString(1, name);
        rsCount = stCount.executeQuery();
        if (rsCount.next()) {
          filter.setTotalCount(rsCount.getInt(1));
        }
      }

      String query = (filter != null) ? filter.toSQL(jdbcStoreResource.SELECT_USERS_BY_GROUP, USER_FIELD_FILTER)
          : jdbcStoreResource.SELECT_USERS_BY_GROUP;

      PreparedStatement st = cx.prepareStatement(query);
      st.setString(1, name);
      rs = st.executeQuery();

      ArrayList<User> ul = new ArrayList<User>();

      while (rs.next()) {
        User u = parseUser(rs);
        ul.add(u);
      }
      st.close();

      return ul;

    }
    catch (SQLException e) {
      throw new SitoolsException("getUsersByGroup " + e.getMessage(), e);
    }
    finally {
      closeConnection(cx);
      closeResultSet(rs);
      closeResultSet(rsCount);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.cnes.sitools.security.UsersAndGroupsStore#getGroupsByUser(java.lang. String)
   */
  @Override
  public List<Group> getGroupsByUser(String identifier) throws SitoolsException {
    return getGroupsByUser(identifier, null);
  }

  @Override
  public List<Group> getGroupsByUser(String identifier, ResourceCollectionFilter filter) throws SitoolsException {
    Connection cx = null;
    ResultSet rsCount = null;
    ResultSet rs = null;
    try {
      cx = ds.getConnection();

      if (filter != null) {
        String countQuery = filter.toSqlCount(jdbcStoreResource.SELECT_GROUPS_BY_USER, GROUP_FIELD_FILTER);
        PreparedStatement stCount = cx.prepareStatement(countQuery);
        stCount.setString(1, identifier);
        rsCount = stCount.executeQuery();
        if (rsCount.next()) {
          filter.setTotalCount(rsCount.getInt(1));
        }
      }

      String query = (filter != null) ? filter.toSQL(jdbcStoreResource.SELECT_GROUPS_BY_USER, GROUP_FIELD_FILTER)
          : jdbcStoreResource.SELECT_GROUPS_BY_USER;

      PreparedStatement st = cx.prepareStatement(query);
      st.setString(1, identifier);
      rs = st.executeQuery();

      ArrayList<Group> gl = new ArrayList<Group>();

      while (rs.next()) {
        Group g = parseGroup(rs);
        gl.add(g);
      }

      st.close();

      return gl;

    }
    catch (SQLException e) {
      throw new SitoolsException("getGroupsByUser " + e.getMessage(), e);
    }
    finally {
      closeConnection(cx);
      closeResultSet(rs);
      closeResultSet(rsCount);
    }
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public boolean isUserModifiable() {
    return true;
  }

  @Override
  public boolean isGroupModifiable() {
    return true;
  }

  @Override
  public User getUserById(String identifier) throws SitoolsException {
    Connection cx = null;
    ResultSet rs = null;
    try {
      cx = ds.getConnection();

      PreparedStatement st = cx.prepareStatement(jdbcStoreResource.SELECT_USER_BY_ID);
      st.setString(1, identifier);
      rs = st.executeQuery();

      User u = null;
      while (rs.next()) {
        u = parseUser(rs);

        // load user properties
        loadProperties(u, cx);
      }

      st.close();
      if (u == null) {
        logger.warning("UNKNOWN_USER");
      }

      return u;

    }
    catch (SQLException e) {
      throw new SitoolsException("getUserById " + e.getMessage(), e);
    }
    finally {
      closeConnection(cx);
      closeResultSet(rs);
    }
  }

  @Override
  public Group getGroupById(String name) throws SitoolsException {
    Connection cx = null;
    ResultSet rs = null;
    try {
      cx = ds.getConnection();
      PreparedStatement st = cx.prepareStatement(jdbcStoreResource.SELECT_GROUP_BY_ID);
      st.setString(1, name);
      rs = st.executeQuery();

      Group g = null;
      while (rs.next()) {
        g = parseGroup(rs);
        break;
      }
      st.close();

      if (g == null) {
        logger.warning("UNKNOWN_GROUP");
      }
      return g;

    }
    catch (Exception e) {
      throw new SitoolsException("getGroupById " + e.getMessage(), e);
    }
    finally {
      closeConnection(cx);
      closeResultSet(rs);
    }
  }

  /**
   * Read main information for a Group in a ResultSet
   * 
   * @param rs
   *          the resultset at the current position
   * @return a new Group object
   * @throws SQLException
   *           if can not read group in ResultSet
   */
  private Group parseGroup(ResultSet rs) throws SQLException {
    String groupName = rs.getString(1);
    String groupDescription = rs.getString(2);

    return new Group(groupName, groupDescription);
  }

  /**
   * Read main information for a User in a ResultSet
   * 
   * @param rs
   *          the resultset at the current position
   * @return a new User object
   * @throws SQLException
   *           if can not read user in ResultSet
   */
  private User parseUser(ResultSet rs) throws SQLException {
    int i = 1;
    String identifier = rs.getString(i++);
    String fistname = rs.getString(i++);
    String lastname = rs.getString(i++);
    String secret = rs.getString(i++);
    String email = rs.getString(i++);
    return new User(identifier, secret, fistname, lastname, email);
  }

  @Override
  public User createUser(User bean) throws SitoolsException {
    if (!isUserModifiable()) {
      throw new SitoolsException("Operation refused");
    }
    checkUser(bean);

    Connection cx = null;
    try {
      cx = ds.getConnection();
      cx.setAutoCommit(false);
      PreparedStatement st = cx.prepareStatement(jdbcStoreResource.CREATE_USER);
      int i = 1;
      st.setString(i++, bean.getIdentifier());
      st.setString(i++, bean.getFirstName());
      st.setString(i++, bean.getLastName());

      st.setString(i++, bean.getSecret());
      st.setString(i++, bean.getEmail());
      st.executeUpdate();
      st.close();

      // store user properties
      createProperties(bean, cx);

      // commit transaction
      if (!cx.getAutoCommit()) {
        cx.commit();
      }
    }
    catch (SQLException e) {
      try {
        cx.rollback();
      }
      catch (SQLException e1) {
        logger.log(Level.INFO, null, e1);
        throw new SitoolsException("CREATE_USER ROLLBACK" + e1.getMessage(), e1);
      }
      logger.log(Level.INFO, null, e);
      throw new SitoolsException("CREATE_USER " + e.getMessage(), e);
    }
    finally {
      closeConnection(cx);
    }
    return getUserById(bean.getIdentifier());
  }

  /**
   * Create properties
   * 
   * @param bean
   *          a user object
   * @param cx
   *          connection
   * @throws SQLException
   *           if occurs
   */
  private void createProperties(User bean, Connection cx) throws SQLException {
    if ((bean.getProperties() != null) && (bean.getProperties().size() > 0)) {
      PreparedStatement stp = cx.prepareStatement(jdbcStoreResource.CREATE_USER_PROPERTY);

      for (Property property : bean.getProperties()) {
        int i = 1;
        stp.setString(i++, bean.getIdentifier());
        stp.setString(i++, property.getName());
        stp.setString(i++, property.getValue());
        stp.setString(i++, property.getScope());
        stp.executeUpdate();
      }

      stp.close();
    }
  }

  /**
   * Load properties to user
   * 
   * @param user
   *          the user
   * @param cx
   *          connection used
   * @throws SQLException
   *           if occurs
   */
  private void loadProperties(User user, Connection cx) throws SQLException {
    PreparedStatement st = cx.prepareStatement(jdbcStoreResource.SELECT_USER_PROPERTY);
    st.setString(1, user.getIdentifier());
    ResultSet rs = st.executeQuery();

    List<Property> properties = new ArrayList<Property>();
    while (rs.next()) {
      int i = 1;
      String key = rs.getString(i++);
      String value = rs.getString(i++);
      String scope = rs.getString(i++);
      properties.add(new Property(key, value, scope));
    }
    st.close();
    user.setProperties(properties);
    closeResultSet(rs);
  }

  /**
   * Delete properties of user
   * 
   * @param identifier
   *          the user id
   * @param cx
   *          the connection used
   * @throws SQLException
   *           if occurs
   */
  private void deleteProperties(String identifier, Connection cx) throws SQLException {
    PreparedStatement stp = cx.prepareStatement(jdbcStoreResource.DELETE_USER_PROPERTY);
    stp.setString(1, identifier);
    stp.executeUpdate();
    stp.close();
  }

  /**
   * Important : Password is modified only if it is not null and different from "". otherwise it is unchanged. A user
   * cannot have a password null or "".
   * 
   * @param bean
   *          the user bean to update
   * @return the user updated
   * @throws SitoolsException
   *           when occurs
   * @see fr.cnes.sitools.security.UsersAndGroupsStore#updateUser(fr.cnes.sitools .security.model.User)
   */
  @Override
  public User updateUser(User bean) throws SitoolsException {
    if (!isUserModifiable()) {
      throw new SitoolsException("Operation refused");
    }
    checkUser(bean);
    Connection cx = null;
    try {
      cx = ds.getConnection();
      cx.setAutoCommit(false);
      PreparedStatement st;

      // MODIFICATION DU MOT DE PASSE SI RENSEIGNE
      int i = 1;
      if (bean.getSecret() != null && !"".equals(bean.getSecret())) {
        st = cx.prepareStatement(jdbcStoreResource.UPDATE_USER_WITH_PW);

        st.setString(i++, bean.getFirstName());
        st.setString(i++, bean.getLastName());

        st.setString(i++, bean.getSecret());
        st.setString(i++, bean.getEmail());
        st.setString(i++, bean.getIdentifier());
      }
      else {
        // SI MOT DE PASSE ABSENT => INCHANGE
        st = cx.prepareStatement(jdbcStoreResource.UPDATE_USER_WITHOUT_PW);
        st.setString(i++, bean.getFirstName());
        st.setString(i++, bean.getLastName());
        st.setString(i++, bean.getEmail());
        st.setString(i++, bean.getIdentifier());
      }

      st.executeUpdate();
      st.close();

      // TODO update properties ? ...
      if (bean.getProperties() != null) {
        deleteProperties(bean.getIdentifier(), cx);
        createProperties(bean, cx);
      }

      // commit transaction
      if (!cx.getAutoCommit()) {
        cx.commit();
      }

    }
    catch (SQLException e) {
      try {
        cx.rollback();
      }
      catch (SQLException e1) {
        throw new SitoolsException("UPDATE_USER ROLLBACK" + e1.getMessage(), e1);
      }
      throw new SitoolsException("UPDATE_USER " + e.getMessage(), e);
    }
    finally {
      closeConnection(cx);
    }
    return getUserById(bean.getIdentifier());
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.cnes.sitools.security.UsersAndGroupsStore#deleteUser(java.lang.String)
   */
  @Override
  public boolean deleteUser(String identifier) throws SitoolsException {
    if (!isUserModifiable()) {
      throw new SitoolsException("Operation refused");
    }
    Connection cx = null;
    try {
      cx = ds.getConnection();
      PreparedStatement st = cx.prepareStatement(jdbcStoreResource.DELETE_USER);
      st.setString(1, identifier);
      st.executeUpdate();
      st.close();

      // delete user properties
      deleteProperties(identifier, cx);

      // commit transaction
      if (!cx.getAutoCommit()) {
        cx.commit();
      }
    }
    catch (SQLException e) {
      throw new SitoolsException("DELETE_USER " + e.getMessage(), e);
    }
    finally {
      closeConnection(cx);
    }
    return true;
  }

  @Override
  public Group createGroup(Group bean) throws SitoolsException {
    checkGroup();
    Connection cx = null;
    try {
      cx = ds.getConnection();
      PreparedStatement st = cx.prepareStatement(jdbcStoreResource.CREATE_GROUP);
      int i = 1;
      st.setString(i++, bean.getName());
      st.setString(i++, bean.getDescription());
      st.executeUpdate();
      st.close();

      // commit transaction
      if (!cx.getAutoCommit()) {
        cx.commit();
      }

    }
    catch (SQLException e) {
      throw new SitoolsException("CREATE_GROUP " + e.getMessage(), e);
    }
    finally {
      closeConnection(cx);
    }
    return getGroupById(bean.getName());
  }

  @Override
  public Group updateGroup(Group bean) throws SitoolsException {
    checkGroup();
    Connection cx = null;
    try {
      cx = ds.getConnection();
      PreparedStatement st = cx.prepareStatement(jdbcStoreResource.UPDATE_GROUP);
      int i = 1;
      st.setString(i++, bean.getDescription());
      st.setString(i++, bean.getName());
      st.executeUpdate();
      st.close();

      // commit transaction
      if (!cx.getAutoCommit()) {
        cx.commit();
      }
    }
    catch (SQLException e) {
      throw new SitoolsException("UPDATE_GROUP " + e.getMessage(), e);
    }
    finally {
      closeConnection(cx);
    }
    return getGroupById(bean.getName());
  }

  @Override
  public boolean deleteGroup(String name) throws SitoolsException {
    checkGroup();
    Connection cx = null;
    try {
      cx = ds.getConnection();
      PreparedStatement st = cx.prepareStatement(jdbcStoreResource.DELETE_GROUP);
      st.setString(1, name);
      st.executeUpdate();
      st.close();

      // commit transaction
      if (!cx.getAutoCommit()) {
        cx.commit();
      }
    }
    catch (SQLException e) {
      throw new SitoolsException("DELETE_GROUP " + e.getMessage(), e);
    }
    finally {
      closeConnection(cx);
    }
    return true;
  }

  @Override
  public Group updateGroupUsers(Group bean) throws SitoolsException {
    checkGroup();
    Connection cx = null;
    try {
      cx = ds.getConnection();
      PreparedStatement st = cx.prepareStatement(jdbcStoreResource.DELETE_GROUPUSERS);
      st.setString(1, bean.getName());
      st.executeUpdate();
      st.close();

      for (Resource res : bean.getUsers()) {
        PreparedStatement st2 = cx.prepareStatement(jdbcStoreResource.CREATE_GROUPUSERS);
        st2.setString(1, res.getId());
        st2.setString(2, bean.getName());
        st2.executeUpdate();
        st2.close();
      }

      // commit transaction
      if (!cx.getAutoCommit()) {
        cx.commit();
      }
    }
    catch (SQLException e) {
      throw new SitoolsException("UPDATE_GROUPUSERS " + e.getMessage(), e);
    }
    finally {
      closeConnection(cx);
    }
    return getGroupById(bean.getName());
  }

  /**
   * Method to close the connection
   * 
   * @param conn
   *          the connection to close
   */
  private void closeConnection(Connection conn) {
    if (conn != null) {
      try {
        conn.close();
      }
      catch (SQLException e) {
        logger.severe(e.getMessage());
      }
    }
  }

  /**
   * Method to close the result set
   * 
   * @param rs
   *          the result set to close
   */
  private void closeResultSet(ResultSet rs) {
    if (rs != null) {
      try {
        rs.close();
      }
      catch (SQLException e) {
        logger.severe(e.getMessage());
      }
    }
  }

}
