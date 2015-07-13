package fr.cnes.sitools.role;

import java.util.ArrayList;
import java.util.List;

import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Put;

import fr.cnes.sitools.common.model.Resource;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.role.model.Role;

/**
 * The Class RoleNotifyUserResource.
 * 
 * @author m.gond
 */
public class RoleNotifyUserResource extends AbstractRoleResource {

  /** The user id. */
  private String userId;

  @Override
  public void sitoolsDescribe() {
    setName("RoleNotifyUserResource");
    setDescription("Resource for updating roles according to a user");
    setNegotiated(false);
  }

  @Override
  public final void doInit() {
    super.doInit();
    userId = (String) this.getRequest().getAttributes().get("userId");
  }

  /**
   * Delete user from roles.
   * 
   * @param variant
   *          the variant
   * @param representation
   *          the representation
   * @return the representation
   */
  @Put
  public Representation deleteUserFromRoles(Representation representation, Variant variant) {

    RoleStoreInterface store = getStore();
    List<Role> rolesFromStore = store.getList();
    // duplicate the original list because the store will modify it when a role is updated
    List<Role> roles = new ArrayList<Role>(rolesFromStore);
    for (Role role : roles) {
      deleteUserFromRole(role, userId, store);
    }
    Response response = new Response(true, "ROLES_UPDATED");
    return getRepresentation(response, variant);
  }

  /**
   * Delete user from role.
   * 
   * @param role
   *          the role
   * @param userId
   *          the user id
   * @param store
   *          the store
   */
  private void deleteUserFromRole(Role role, String userId, RoleStoreInterface store) {
    List<Resource> users = role.getUsers();
    if (users != null) {
      for (Resource resource : users) {
        if (userId.equals(resource.getId())) {
          users.remove(resource);
          store.update(role);
          break;
        }
      }
    }
  }

}
