package fr.cnes.sitools.security.userblacklist;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.ext.wadl.ApplicationInfo;
import org.restlet.ext.wadl.DocumentationInfo;
import org.restlet.routing.Filter;
import org.restlet.routing.Router;

import fr.cnes.sitools.common.application.ContextAttributes;
import fr.cnes.sitools.common.application.SitoolsApplication;
import fr.cnes.sitools.common.model.Category;
import fr.cnes.sitools.notification.business.NotifierFilter;

/**
 * The Class UserBlackListApplication.
 * 
 * @author m.gond
 */
public class UserBlackListApplication extends SitoolsApplication {

  /** store */
  private UserBlackListStoreInterface store = null;

  /**
   * Category
   * 
   * @param context
   *          parent context
   */
  @SuppressWarnings("unchecked")
  public UserBlackListApplication(Context context) {
    super(context);

    this.store = (UserBlackListStoreInterface) context.getAttributes().get(ContextAttributes.APP_STORE);
  }

  @Override
  public void sitoolsDescribe() {
    setCategory(Category.ADMIN);
    setName("UserBlackListApplication");
    setDescription("User BlackList application to GET and DELETE blacklist");
  }

  @Override
  public Restlet createInboundRoot() {

    Router router = new Router(getContext());

    router.attachDefault(UserBlackListCollectionResource.class);

    router.attach("/{userId}", UserBlackListResource.class);

    Filter filter = new NotifierFilter(getContext());
    filter.setNext(router);

    return filter;
  }

  @Override
  public ApplicationInfo getApplicationInfo(Request request, Response response) {
    ApplicationInfo result = super.getApplicationInfo(request, response);
    DocumentationInfo docInfo = new DocumentationInfo("User BlackList application to GET and DELETE blacklist");
    docInfo.setTitle("API documentation.");
    result.getDocumentations().add(docInfo);
    return result;
  }

  /**
   * Gets the store value
   * 
   * @return the store
   */
  public UserBlackListStoreInterface getStore() {
    return store;
  }

}
