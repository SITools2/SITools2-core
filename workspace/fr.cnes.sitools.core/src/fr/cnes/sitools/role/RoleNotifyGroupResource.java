package fr.cnes.sitools.role;

import java.util.ArrayList;
import java.util.List;

import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Put;

import fr.cnes.sitools.common.model.Resource;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.common.store.SitoolsStore;
import fr.cnes.sitools.role.model.Role;

/**
 * The Class RoleNotifyGroupResource.
 * 
 * @author m.gond
 */
public class RoleNotifyGroupResource extends AbstractRoleResource {

  /** The group id. */
  private String groupId;

  @Override
  public void sitoolsDescribe() {
    setName("RoleNotifyGroupResource");
    setDescription("Resource for updating roles according to a group");
    setNegotiated(false);
  }

  @Override
  public final void doInit() {
    super.doInit();
    groupId = (String) this.getRequest().getAttributes().get("groupId");
  }

  /**
   * Delete group from roles.
   * 
   * @param variant
   *          the variant
   * @param representation
   *          the representation
   * @return the representation
   */
  @Put
  public Representation deleteGroupFromRoles(Representation representation, Variant variant) {
    RoleStoreInterface store = getStore();
    List<Role> rolesFromStore = store.getList();
    List<Role> roles = new ArrayList<Role>(rolesFromStore);
    for (Role role : roles) {
      deleteGroupFromRole(role, groupId, store);
    }
    Response response = new Response(true, "ROLES_UPDATED");
    return getRepresentation(response, variant);
  }

  /**
   * Delete group from role.
   * 
   * @param role
   *          the role
   * @param groupId
   *          the group id
   * @param store
   *          the store
   */
  private void deleteGroupFromRole(Role role, String groupId, RoleStoreInterface store) {
    List<Resource> groups = role.getGroups();
    if (groups != null) {
      for (Resource resource : groups) {
        if (groupId.equals(resource.getId())) {
          groups.remove(resource);
          store.update(role);
          break;
        }
      }
    }
  }

}
