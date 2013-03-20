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
package fr.cnes.sitools.security.authentication;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

import org.restlet.engine.security.RoleMapping;
import org.restlet.security.Group;
import org.restlet.security.Role;
import org.restlet.security.User;

import fr.cnes.sitools.common.SitoolsSettings;
import fr.cnes.sitools.common.exception.SitoolsException;
import fr.cnes.sitools.common.model.Resource;
import fr.cnes.sitools.common.store.SitoolsStore;
import fr.cnes.sitools.security.UsersAndGroupsStore;

/**
 * Class for building a Realm with the store of users, groups and roles
 * 
 * SitoolsMemoryRealm is a fixed and improved version of Restlet MemoryRealm
 * 
 * @author jp.boignard (AKKA Technologies)
 */
public class SitoolsMemoryRealm extends SitoolsRealm {

  /** The modifiable list of root groups. */
  private final List<Group> rootGroups;

  /** The modifiable list of users. */
  private final List<User> users;

  /** Users and Groups store */
  private UsersAndGroupsStore storeUsersAndGroups = null;
  
  /**
   * Constructor
   * 
   * @param storeUsersAndGroups
   *          storage of users and groups
   * @param storeRoles
   *          storage of roles
   * @param settings
   *          the SitoolsSettings object where to find realm name, ...
   */
  public SitoolsMemoryRealm(UsersAndGroupsStore storeUsersAndGroups, SitoolsStore<fr.cnes.sitools.role.model.Role> storeRoles, SitoolsSettings settings) {
    super(storeRoles, settings);
    
    this.rootGroups = new CopyOnWriteArrayList<Group>();
    this.users = new CopyOnWriteArrayList<User>();
    this.storeUsersAndGroups = storeUsersAndGroups;

    // this.algorithm = settings.getAuthenticationALGORITHM();

    build();
  }

  /**
   * Recursively adds groups where a given user is a member.
   * 
   * @param user
   *          The member user.
   * @param userGroups
   *          The set of user groups to update.
   * @param currentGroup
   *          The current group to inspect.
   * @param stack
   *          The stack of ancestor groups.
   * @param inheritOnly
   *          Indicates if only the ancestors groups that have their "inheritRoles" property enabled should be added.
   */
  private void addGroups(User user, Set<Group> userGroups, Group currentGroup, List<Group> stack, boolean inheritOnly) {
    if ((currentGroup != null) && !stack.contains(currentGroup)) {
      stack.add(currentGroup);

      if (currentGroup.getMemberUsers().contains(user)) {
        userGroups.add(currentGroup);

        // Add the ancestor groups as well
        boolean inherit = !inheritOnly || currentGroup.isInheritingRoles();
        Group group;

        for (int i = stack.size() - 2; inherit && (i >= 0); i--) {
          group = stack.get(i);
          userGroups.add(group);
          inherit = !inheritOnly || group.isInheritingRoles();
        }
      }

      for (Group group : currentGroup.getMemberGroups()) {
        addGroups(user, userGroups, group, stack, inheritOnly);
      }
    }
  }

  /**
   * Finds the set of groups where a given user is a member. Note that inheritable ancestors groups are also returned.
   * 
   * @param user
   *          The member user.
   * @return The set of groups.
   */
  @Override
  public Set<Group> findGroups(User user) {
    return findGroups(user, true);
  }

  /**
   * Finds the set of groups where a given user is a member.
   * 
   * @param user
   *          The member user.
   * @param inheritOnly
   *          Indicates if only the ancestors groups that have their "inheritRoles" property enabled should be added.
   * @return The set of groups.
   */
  private Set<Group> findGroups(User user, boolean inheritOnly) {
    Set<Group> result = new HashSet<Group>();
    List<Group> stack;

    // Recursively find user groups
    for (Group group : getRootGroups()) {
      stack = new ArrayList<Group>();
      addGroups(user, result, group, stack, inheritOnly);
    }

    return result;
  }


  /**
   * Finds a user in the organization based on its identifier.
   * 
   * @param userIdentifier
   *          The identifier to match.
   * @return The matched user or null.
   */
  @Override
  public User findUser(String userIdentifier) {
    User result = null;
    User user;

    for (int i = 0; (result == null) && (i < getUsers().size()); i++) {
      user = getUsers().get(i);

      if (user.getIdentifier().equals(userIdentifier)) {
        result = user;
        break; // JPB
      }
    }

    return result;
  }

  /**
   * Returns the modifiable list of root groups.
   * 
   * @return The modifiable list of root groups.
   */
  public List<Group> getRootGroups() {
    return rootGroups;
  }

///**
//* Get the groups
//* 
//* @return a list of groups
//*/
//public List<Group> getReferenceGroups() {
// return this.getRootGroups();
//}
  
  /**
   * Sets the modifiable list of root groups. This method clears the current list and adds all entries in the parameter
   * list.
   * 
   * @param rootGroups
   *          A list of root groups.
   */
  public void setRootGroups(List<Group> rootGroups) {
    synchronized (getRootGroups()) {
      if (rootGroups != getRootGroups()) {
        getRootGroups().clear();

        if (rootGroups != null) {
          getRootGroups().addAll(rootGroups);
        }
      }
    }
  }
  
  /**
   * Returns the modifiable list of users.
   * 
   * @return The modifiable list of users.
   */
  public List<User> getUsers() {
    return users;
  }

//  /**
//   * Get users
//   * 
//   * @return a list of users
//   */
//  public List<User> getReferenceUsers() {
//    return this.getUsers();
//  }
  
  /**
   * Sets the modifiable list of users. This method clears the current list and adds all entries in the parameter list.
   * 
   * @param users
   *          A list of users.
   */
  public void setUsers(List<User> users) {
    synchronized (getUsers()) {
      if (users != getUsers()) {
        getUsers().clear();

        if (users != null) {
          getUsers().addAll(users);
        }
      }
    }
  }

  /**
   * Maps a group defined in a component to a role defined in the application.
   * 
   * @param group
   *          The source group.
   * @param role
   *          The target role.
   */
  private void map(Group group, Role role) {
    getRoleMappings().add(new RoleMapping(group, role));
  }

  /**
   * Maps a user defined in a component to a role defined in the application.
   * 
   * @param user
   *          The source user.
   * @param role
   *          The target role.
   */
  private void map(User user, Role role) {
    getRoleMappings().add(new RoleMapping(user, role));
  }
  
  /**
   * Unmaps a group defined in a component from a role defined in the application.
   * 
   * @param group
   *          The source group.
   * @param role
   *          The target role.
   */
  private void unmap(Group group, Role role) {
    unmap((Object) group, role);
  }

  /**
   * Unmaps an element (user, group or organization) defined in a component from a role defined in the application.
   * 
   * @param source
   *          The source object.
   * @param role
   *          The target role.
   */
  private void unmap(Object source, Role role) {
    for (Iterator<RoleMapping> iterator = getRoleMappings().iterator(); iterator.hasNext();) {
      RoleMapping mapping = (RoleMapping) iterator.next();
      if (mapping.getSource().equals(source) && mapping.getTarget().equals(role)) {
        getRoleMappings().remove(mapping);
      }
    }
  }

  /**
   * Unmaps a user defined in a component from a role defined in the application.
   * 
   * @param user
   *          The source user.
   * @param role
   *          The target role.
   */
  private void unmap(User user, Role role) {
    unmap((Object) user, role);
  }

  /**
   * Building the maps
   */
  private void build() {
    Map<String, User> usersMap = new ConcurrentHashMap<String, User>();
    Map<String, Group> groupsMap = new ConcurrentHashMap<String, Group>();

    try {
      // Ajout de tous les utilisateurs
      List<fr.cnes.sitools.security.model.User> allUsers = storeUsersAndGroups.getUsers();
      for (fr.cnes.sitools.security.model.User user : allUsers) {
        User u = user.wrap();
        usersMap.put(user.getIdentifier(), u);
        this.getUsers().add(u);
      }
      // Ajout de tous les groups
      List<fr.cnes.sitools.security.model.Group> allGroups = storeUsersAndGroups.getGroups();
      for (fr.cnes.sitools.security.model.Group group : allGroups) {
        Group g = group.wrap();
        groupsMap.put(group.getName(), g);
        this.getRootGroups().add(g);
      }

      // Pour chacun des roles definis dans le store
      List<fr.cnes.sitools.role.model.Role> roles = getStoreRoles().getList();
      for (fr.cnes.sitools.role.model.Role roleStore : roles) {
        Role role = roleStore.wrap();
        // si la rolesMap n'est pas vide on conserve les roles existants.
        if (getReferenceRoles().get(role.getName()) != null) {
          role = getReferenceRoles().get(role.getName());
        }
        else {
          getReferenceRoles().put(role.getName(), role);
        }

        // Ajout des utilisateurs associes au role
        List<Resource> usersStored = roleStore.getUsers();
        if (usersStored != null) {
          for (Resource userResource : usersStored) {
            User existingUser = usersMap.get(userResource.getId());
            if (null != existingUser) {
              this.map(existingUser, role);
            }
          }
        }

        // Ajout des groupes associes au role
        List<Resource> groupsStore = roleStore.getGroups();
        if (groupsStore != null) {
          for (Resource groupResource : groupsStore) {
            Group existingGroup = groupsMap.get(groupResource.getId());
            if (null != existingGroup) {
              this.map(existingGroup, role);
            }

            // Ajout des utilisateurs associes au groupe
            List<fr.cnes.sitools.security.model.User> usersByGroup = storeUsersAndGroups.getUsersByGroup(groupResource
                .getId());
            if (usersByGroup != null) {
              for (fr.cnes.sitools.security.model.User userGroup : usersByGroup) {
                User existingUserGroup = usersMap.get(userGroup.getIdentifier());
                if (null != existingUserGroup) {
                  existingGroup.getMemberUsers().add(existingUserGroup);
                }
              }
            }
          }
        }
      }
    }
    catch (SitoolsException e) {
      Logger.getLogger(this.getClass().getName()).warning(e.getMessage());
    }
  }

  /**
   * Refresh users / groups
   */
  @Override
  public synchronized void refreshUsersAndGroups() {
    // List<Group> rootGroups = getRootGroups();
    for (Group group : rootGroups) {
      unmap(group);
    }
    rootGroups.clear();

    // List<User> users = getUsers();
    for (User user : users) {
      unmap(user);
    }
    users.clear();

    // rolesMap.clear();

    // rebuild authorisations ...
    build();
  }

  /**
   * Find the roles of a group
   * 
   * @param group
   *          the group to look at
   */
  private void unmap(Group group) {
    Set<Role> roles = findRoles(group);
    if ((roles != null) && (roles.size() > 0)) {
      for (Iterator<Role> iterator = roles.iterator(); iterator.hasNext();) {
        Role role = (Role) iterator.next();
        unmap(group, role); // FIXME BUG RESTLET SUR LE UNMAP
      }
    }
  }

  /**
   * Find the roles of a user
   * 
   * @param user
   *          the user to look at
   */
  private void unmap(User user) {
    Set<Role> roles = findRoles(user);
    if ((roles != null) && (roles.size() > 0)) {
      for (Iterator<Role> iterator = roles.iterator(); iterator.hasNext();) {
        Role role = (Role) iterator.next();
        unmap(user, role); // FIXME BUG RESTLET SUR LE UNMAP
      }
    }
  }

  /**
   * Remove User from realm
   * 
   * @param userIdentifier
   *          Group identifier
   */
  @Override
  public void removeUser(String userIdentifier) {
    User user = this.findUser(userIdentifier);
    if (user != null) {
      unmap(user); // FIXME BUG RESTLET SUR LE UNMAP
      getUsers().remove(user);
    }
  }

  /**
   * Remove Group from realm
   * 
   * @param groupName
   *          Group name
   * 
   */
  @Override
  public void removeGroup(String groupName) {
    // List<Group> rootGroups = getRootGroups();
    if (rootGroups != null) {
      for (Iterator<Group> iterator = rootGroups.iterator(); iterator.hasNext();) {
        Group group = (Group) iterator.next();
        if (group.getName().equals(groupName)) {
          unmap(group); // FIXME BUG RESTLET SUR LE UNMAP
          rootGroups.remove(group);
          break;
        }
      }
    }
  }

  /**
   * Reload Role from store and rebuild all mappings for groups and users.
   * 
   * @param roleStore
   *          fr.cnes.sitools.role.model.Role
   */
  @Override
  public void refreshRoleMappings(fr.cnes.sitools.role.model.Role roleStore) {
    super.refreshRoleMappings(roleStore);
    
    Role role = getReferenceRoles().get(roleStore.getName());
    
    List<Resource> groupsStore = roleStore.getGroups();
    if (groupsStore != null) {
      for (Iterator<Resource> iterator = roleStore.getGroups().iterator(); iterator.hasNext();) {
        Resource res = (Resource) iterator.next();
        try {
          fr.cnes.sitools.security.model.Group groupStore = storeUsersAndGroups.getGroupById(res.getId());
          Group group = findGroup(groupStore.getName());
          if (group != null) {
            map(group, role);
          }
        }
        catch (SitoolsException e) {
          Logger.getLogger(this.getClass().getName()).warning(e.getMessage());
        }
      }
    }

    List<Resource> usersStore = roleStore.getUsers();
    if (usersStore != null) {
      for (Iterator<Resource> iterator = usersStore.iterator(); iterator.hasNext();) {
        Resource res = (Resource) iterator.next();
        try {
          fr.cnes.sitools.security.model.User userStore = storeUsersAndGroups.getUserById(res.getId());
          User user = findUser(userStore.getIdentifier());
          if (user != null) {
            map(user, role);
          }
        }
        catch (SitoolsException e) {
          Logger.getLogger(this.getClass().getName()).warning(e.getMessage());
        }
      }
    }
  }

  /**
   * Finds a Group based on its name.
   * 
   * @param name
   *          The name to match.
   * @return The matched group or null.
   */
  private Group findGroup(String name) {
    Group result = null;
    Group group;

    for (int i = 0; (result == null) && (i < getUsers().size()); i++) {
      group = getRootGroups().get(i);

      if (group.getName().equals(name)) {
        result = group;
        break;
      }
    }

    return result;
  }
  
}
