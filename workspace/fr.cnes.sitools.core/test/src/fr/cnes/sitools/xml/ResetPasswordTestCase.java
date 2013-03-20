package fr.cnes.sitools.xml;

import org.restlet.data.MediaType;

import fr.cnes.sitools.AbstractResetPasswordTestCase;
import fr.cnes.sitools.api.DocAPI;

/**
 * Test Reset an user Password
 * 
 * @since UserStory : Réinitialiser le mot de passe, Release 4 - Sprint : 3
 * 
 * @author b.fiorito (AKKA Technologies)
 * 
 */
public class ResetPasswordTestCase extends AbstractResetPasswordTestCase {

  static {
    setMediaTest(MediaType.APPLICATION_XML);

    docAPI = new DocAPI(ResetPasswordTestCase.class, "Reset User password API with XML format");
    docAPI.setActive(true);
    docAPI.setMediaTest(MediaType.APPLICATION_XML);

  }

  /**
   * Default constructor
   */
  public ResetPasswordTestCase() {
    super();

  }
}
