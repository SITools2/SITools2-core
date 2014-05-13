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
package fr.cnes.sitools.security.authentication;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.restlet.data.ChallengeScheme;
import org.restlet.data.ClientInfo;
import org.restlet.engine.Engine;
import org.restlet.engine.security.RoleMapping;
import org.restlet.ext.crypto.DigestUtils;
import org.restlet.security.Enroler;
import org.restlet.security.Group;
import org.restlet.security.LocalVerifier;
import org.restlet.security.Realm;
import org.restlet.security.Role;
import org.restlet.security.User;

import fr.cnes.sitools.common.SitoolsSettings;
import fr.cnes.sitools.common.store.SitoolsStore;
import fr.cnes.sitools.security.SecurityUtil;

/**
 * SitoolsRealm based on a memory Role management
 * 
 * @author jp.boignard (AKKA Technologies)
 */
public abstract class SitoolsRealm extends Realm {

  /** Roles store */
  private SitoolsStore<fr.cnes.sitools.role.model.Role> storeRoles = null;

  /** Roles map */
  private ConcurrentHashMap<String, Role> rolesMap = new ConcurrentHashMap<String, Role>();

  /** The modifiable list of role mappings. */
  private List<RoleMapping> roleMappings = null;

  /** Realm */
  private String realm;

  /** Scheme */
  private String scheme;

  /** SitoolsSettings */
  private SitoolsSettings settings;

  /**
   * Constructor
   * 
   * @param storeRoles
   *          the store of Role
   * @param settings
   *          the {@link SitoolsSettings}
   */
  public SitoolsRealm(SitoolsStore<fr.cnes.sitools.role.model.Role> storeRoles, SitoolsSettings settings) {
    super();
    this.storeRoles = storeRoles;
    this.roleMappings = new CopyOnWriteArrayList<RoleMapping>();

    this.realm = settings.getAuthenticationDOMAIN();
    this.scheme = settings.getAuthenticationSCHEME();

    setVerifier(new SitoolsDefaultVerifier());
    setEnroler(new SitoolsDefaultEnroler());

    this.settings = settings;
  }

  /**
   * Enroler based on the default security model.
   */
  private class SitoolsDefaultEnroler implements Enroler {

    @Override
    public void enrole(ClientInfo clientInfo) {
      User user;
      if (clientInfo.getUser() != null) {
        user = findUser(clientInfo.getUser().getIdentifier());
      }
      else {
        user = null;
      }
      // User user = findUser(clientInfo.getUser().getIdentifier());
      if (user != null) {
        // Find all the inherited groups of this user
        Set<Group> userGroups = findGroups(user);

        // Add roles specific to this user
        Set<Role> userRoles = findRoles(user);

        for (Role role : userRoles) {
          clientInfo.getRoles().add(role);
        }

        // Add roles common to group members
        Set<Role> groupRoles = findRoles(userGroups);

        for (Role role : groupRoles) {
          clientInfo.getRoles().add(role);
        }
      }
      else {
        Role publicRole = getReferenceRoles().get(SecurityUtil.PUBLIC_ROLE);
        if (publicRole != null) {
          clientInfo.getRoles().add(publicRole);
        }
      }
    }
  }

  /**
   * Only verify if user exists in realm.
   */
  protected class SitoolsLocalUsernameVerifier extends LocalVerifier {

    @Override
    public char[] getLocalSecret(String arg0) {
      return null;
    }

    @Override
    public boolean verify(String identifier, char[] secret) {
      return (findUser(identifier) != null);
    }

  }

  /**
   * Verifier based on the default security model. It looks up users in the mapped organizations.
   */
  private class SitoolsDefaultVerifier extends LocalVerifier {

    @Override
    public char[] getLocalSecret(String identifier) {
      char[] result = null;
      User user = findUser(identifier);

      if (user != null) {
        result = user.getSecret();
      }

      return result;
    }

    /**
     * Verify method for HTTP_BASIC Authentication
     * 
     * @param identifier
     *          the identifier
     * @param secret
     *          the password
     * @return true if check is OK
     */
    @Override
    public boolean verify(String identifier, char[] secret) {
      if ((identifier == null) || identifier.equals("null")) {
        Engine.getLogger(this.getClass().getName()).info("Authentication with NO identifier failed");
        return false;
      }
      char[] localSecret = getLocalSecret(identifier);
      if (localSecret == null) {
        return false;
      }
      String localSecretString = String.copyValueOf(localSecret);

      // par défaut mot de passe local supposé non crypté
      char[] requestSecret = secret;

      if (scheme.equalsIgnoreCase(ChallengeScheme.HTTP_BASIC.toString())) {
        // Tous les algos de stockage sont possibles.
        // On crypte le mot de passe en clair dans le meme algo que celui de stockage

        // si le mot de passe est en md5 (sitools v0.2)
        if (localSecretString.startsWith("md5://")) {
          requestSecret = ("md5://" + DigestUtils.toMd5(String.copyValueOf(secret))).toCharArray();
        }
        // si le mot de passe est digest MD5 cad md5(username:realm:password) (sitools v0.3)
        else if (localSecretString.startsWith(SecurityUtil.DIGEST_MD5_PREFIX)) {
          requestSecret = SecurityUtil.digestMd5(identifier, secret, realm).toCharArray();
        }
        // si le mot de passe est "{MD5}" + md5(password)
        else if (localSecretString.startsWith(SecurityUtil.OPENLDAP_MD5_PREFIX)) {
          requestSecret = SecurityUtil.openldapDigestMd5(String.copyValueOf(secret)).toCharArray();
        }

        // sinon il est suppose en clair (sitools v0.1 et +)

        return compare(requestSecret, localSecret);
      }

      if (scheme.equalsIgnoreCase(ChallengeScheme.HTTP_DIGEST.toString())) {
        // localsecret doit etre NON crypte ou MD5://
        // la clé est digest MD5 cad md5(username:realm:password) (sitools v0.3)
        if (localSecretString.startsWith(SecurityUtil.DIGEST_MD5_PREFIX)) {
          return compare((SecurityUtil.DIGEST_MD5_PREFIX + String.copyValueOf(requestSecret)).toCharArray(),
              localSecret);
        }
        else {
          // on tente qd meme avec le mot de passe local suppose en clair que l'on crypte
          localSecretString = SecurityUtil.digestMd5(identifier, localSecret, realm);
          return compare(requestSecret, localSecretString.toCharArray());
        }
      }

      // scheme inconnu ...
      Engine.getLogger(this.getClass().getName()).warning("AUTHENTIFICATION SCHEME UNKNOWN : " + scheme);
      return false;
    }

  }

  /**
   * Gets the role store
   * 
   * @return a map of roles
   */
  public SitoolsStore<fr.cnes.sitools.role.model.Role> getStoreRoles() {
    return storeRoles;
  }

  /**
   * Get the role map
   * 
   * @return a map of roles
   */
  public ConcurrentHashMap<String, Role> getReferenceRoles() {
    return this.rolesMap;
  }

  /**
   * Returns the modifiable list of role mappings.
   * 
   * @return The modifiable list of role mappings.
   */
  protected List<RoleMapping> getRoleMappings() {
    return roleMappings;
  }

  /**
   * Public Role
   * 
   * @return Role
   */
  public Role getPublicRole() {
    return getReferenceRoles().get(SecurityUtil.PUBLIC_ROLE);
  }

  /**
   * Remove Role from realm
   * 
   * @param roleName
   *          Role identifier
   */
  public void removeRole(String roleName) {
    // Remove all mappings for this role
    for (Iterator<RoleMapping> iterator = getRoleMappings().iterator(); iterator.hasNext();) {
      RoleMapping mapping = (RoleMapping) iterator.next();
      if (mapping.getTarget().getName().equals(roleName)) {
        getRoleMappings().remove(mapping);
      }
    }
    getReferenceRoles().remove(roleName);
  }

  /**
   * Reload Role from store and rebuild all mappings for groups and users.
   * 
   * @param roleStore
   *          fr.cnes.sitools.role.model.Role
   */
  public void refreshRoleMappings(fr.cnes.sitools.role.model.Role roleStore) {
    // Remove all mappings for this role
    for (Iterator<RoleMapping> iterator = getRoleMappings().iterator(); iterator.hasNext();) {
      RoleMapping mapping = (RoleMapping) iterator.next();
      if (mapping.getTarget().getName().equals(roleStore.getName())) {
        getRoleMappings().remove(mapping);
      }
    }

    Role role = getReferenceRoles().get(roleStore.getName());
    if (role == null) {
      role = new Role();
      role.setName(roleStore.getName());
      role.setDescription(roleStore.getDescription());
      getReferenceRoles().put(role.getName(), role);
    }
  }

  /**
   * Finds the roles mapped to given user groups.
   * 
   * @param userGroups
   *          The user groups.
   * @return The roles found.
   */
  public Set<Role> findRoles(Set<Group> userGroups) {
    Set<Role> result = new HashSet<Role>();

    Object source;
    for (RoleMapping mapping : getRoleMappings()) {
      source = mapping.getSource();

      if ((userGroups != null) && userGroups.contains(source)) {
        result.add(mapping.getTarget());
      }
    }

    return result;
  }

  /**
   * Finds the roles mapped to a given user.
   * 
   * @param user
   *          The user.
   * @return The roles found.
   */
  public Set<Role> findRoles(User user) {
    Set<Role> result = new HashSet<Role>();

    Object source;
    for (RoleMapping mapping : getRoleMappings()) {
      source = mapping.getSource();

      if ((user != null) && user.equals(source)) {
        result.add(mapping.getTarget());
      }
    }

    return result;
  }

  /**
   * Finds the roles mapped to given user group.
   * 
   * @param userGroup
   *          The user group.
   * @return The roles found.
   */
  public Set<Role> findRoles(Group userGroup) {
    Set<Role> result = new HashSet<Role>();

    Object source;
    for (RoleMapping mapping : getRoleMappings()) {
      source = mapping.getSource();

      if ((userGroup != null) && userGroup.equals(source)) {
        result.add(mapping.getTarget());
      }
    }

    return result;
  }

  /**
   * Find a user from its identifier
   * 
   * @param userIdentifier
   *          the user identifier
   * @return the {@link User}
   */
  public abstract User findUser(String userIdentifier);

  /**
   * Find the List of {@link Group} containing the given {@link User}
   * 
   * @param user
   *          the {@link User}
   * @return the list of {@link Group} containing the given {@link User}
   */
  public abstract Set<Group> findGroups(User user);

  // public abstract Set<Group> findGroups(User user, boolean inheritOnly);

  // public abstract Group findGroup(String name);

  /**
   * Remove a user from the Realm
   * <p>
   * when a user is removed role mappings need to be refreshed
   * </p>
   * 
   * @param userIdentifier
   *          the user identifier
   */
  public abstract void removeUser(String userIdentifier);

  //
  /**
   * Remove a Group from the Realm
   * <p>
   * when a group is removed role mappings need to be refreshed
   * </p>
   * 
   * @param groupName
   *          the Name of the group
   */
  public abstract void removeGroup(String groupName);

  /**
   * Refresh users and group in the Realm
   * <p>
   * when a user or group is created or deleted mappings need to be refreshed
   * </p>
   */
  public abstract void refreshUsersAndGroups();
  
  /**
   *  Update realm last modified information 
   */
  public abstract void updateUsersAndGroupsLastModified();

  /**
   * Verifiers can call this event after the verification is done
   * 
   * @param result
   *          the result
   * @param user
   *          the user
   * @return true if the verification is successful, false otherwise
   */
  public abstract boolean onVerify(boolean result, fr.cnes.sitools.security.model.User user);

  /**
   * Gets the settings.
   * 
   * @return the settings
   */
  public SitoolsSettings getSettings() {
    return settings;
  }

}