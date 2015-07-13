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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.ChallengeResponse;
import org.restlet.data.ChallengeScheme;
import org.restlet.data.Digest;
import org.restlet.data.Reference;
import org.restlet.engine.security.AuthenticatorUtils;
import org.restlet.ext.crypto.DigestAuthenticator;
import org.restlet.ext.crypto.DigestUtils;
import org.restlet.ext.crypto.internal.HttpDigestHelper;
import org.restlet.ext.crypto.internal.HttpDigestVerifier;
import org.restlet.security.Authenticator;
import org.restlet.security.ChallengeAuthenticator;
import org.restlet.security.LocalVerifier;
import org.restlet.security.MapVerifier;
import org.restlet.security.User;

import fr.cnes.sitools.common.SitoolsSettings;
import fr.cnes.sitools.common.application.ContextAttributes;
import fr.cnes.sitools.login.LoginResource;

/**
 * Class utility for managing Authenticators by domain
 * 
 * Sitools V1: One domain ("SITOOLS") and One strategy (BASIC)
 * 
 * Usage : Un authenticator est créé avec un verifier pour user+password
 * 
 * Objectifs :
 * 
 * 1. Initialiser tous les authenticators conformément à la stratégie de sécurité définie (schema BASIC / DIGEST)
 * 
 * 2. Rafraichir globalement tous les authenticators si la stratégie est modifiée.
 * 
 * @author jp.boignard (AKKA Technologies)
 */
public final class AuthenticatorFactory {

  /** first authenticators created by domain */
  private static Map<String, ChallengeAuthenticator> authenticators = new ConcurrentHashMap<String, ChallengeAuthenticator>();

  /**
   * Private constructor for utility class
   */
  private AuthenticatorFactory() {

  }

  /**
   * Gets new Basic Restlet Authenticator with user + password + domain
   * 
   * @param context
   *          Restlet Host Context
   * @param user
   *          login
   * @param password
   *          password
   * @param domain
   *          realm
   * @return Authenticator
   */
  public static Authenticator getBasicAuthenticator(Context context, String user, String password, String domain) {

    MapVerifier verifier = new MapVerifier();
    verifier.getLocalSecrets().put(user, password.toCharArray());

    ChallengeAuthenticator authenticatorBASIC = new ChallengeAuthenticator(context, ChallengeScheme.HTTP_BASIC, domain);
    authenticatorBASIC.setVerifier(verifier);

    // Default setNext
    authenticatorBASIC.setNext(LoginResource.class);

    authenticators.put(domain, authenticatorBASIC);

    return authenticatorBASIC;
  }

  /**
   * Gets new Digest Restlet Authenticator with user + password + domain + secret key
   * 
   * @param context
   *          Restlet Host Context
   * @param user
   *          login
   * @param password
   *          password
   * @param domain
   *          realm
   * @param secretKey
   *          server secret key
   * @return Authenticator
   */
  public static ChallengeAuthenticator getDigestAuthenticator(Context context, String user, String password,
      String domain, String secretKey) {

    MapVerifier verifier = new MapVerifier();
    verifier.getLocalSecrets().put(user, password.toCharArray());

    DigestAuthenticator authenticatorDIGEST = new DigestAuthenticator(context, domain, secretKey);
    authenticatorDIGEST.setVerifier(verifier);

    // Default setNext
    authenticatorDIGEST.setNext(LoginResource.class);

    authenticators.put(domain, authenticatorDIGEST);

    return authenticatorDIGEST;
  }

  /**
   * Gets new Authenticator with scheme realm verifier enroler
   * 
   * @param context
   *          the restlet context
   * @param optional
   *          true if optional
   * @param realm
   *          the realm
   * @param sitoolsRealm
   *          SitoolsRealm
   * @return ChallengeAuthenticator
   */
  public static ChallengeAuthenticator getAuthenticator(Context context, boolean optional, String realm,
      SitoolsRealm sitoolsRealm) {
    ChallengeAuthenticator authenticator = null;
    ChallengeAuthenticator innerAuthenticator = null;

    SitoolsSettings settings = (SitoolsSettings) context.getAttributes().get(ContextAttributes.SETTINGS);
    ChallengeScheme scheme = ChallengeScheme.valueOf(settings.getAuthenticationSCHEME());

    // =============================================
    if (scheme.equals(ChallengeScheme.HTTP_BASIC)) {

      innerAuthenticator = new ChallengeAuthenticator(context, scheme, realm);
      innerAuthenticator.setOptional(optional);
      authenticator = new SitoolsChallengeAuthenticator(context, optional, scheme, realm, sitoolsRealm,
          sitoolsRealm.getVerifier(), sitoolsRealm.getEnroler(), innerAuthenticator);
    }

    // ===================================================
    else if (scheme.equals(ChallengeScheme.HTTP_DIGEST)) {
      String secretKey = settings.getSecretKey();
      String algorithm = Digest.ALGORITHM_HTTP_DIGEST;

      innerAuthenticator = new DigestAuthenticator(context, realm, secretKey);
      innerAuthenticator.setOptional(optional);
      // ((DigestAuthenticator)innerAuthenticator).getVerifier().setAlgorithm(algorithm);
      HttpDigestVerifier httpVerifier = new HttpDigestVerifier((DigestAuthenticator) innerAuthenticator,
          (LocalVerifier) sitoolsRealm.getVerifier(), algorithm) {
        @Override
        public char[] getWrappedSecretDigest(String identifier) {
          char[] result = null;
          String localSecret = "";
          char[] charsecret = null;
          if (identifier != null) {
            charsecret = getWrappedSecret(identifier);
            if (charsecret == null) {
              return charsecret;
            }
            localSecret = String.copyValueOf(charsecret);
          }
          if (localSecret.startsWith("MD5://")) {
            result = localSecret.substring(6).toCharArray();
          }
          else if (localSecret.startsWith("md5://")) {
            Context.getCurrentLogger().log(Level.WARNING, "The digest algorithms can't be different.");
          }
          else if (localSecret.startsWith("{MD5}")) {
            Context.getCurrentLogger().log(Level.WARNING, "The digest algorithms can't be different.");
          }
          else {
            result = digest(identifier, charsecret, getAlgorithm());
          }

          return result;
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.restlet.ext.crypto.internal.HttpDigestVerifier#verify(org.restlet.Request, org.restlet.Response)
         */
        @Override
        public int verify(Request request, Response response) {
          int result = RESULT_VALID;
          ChallengeResponse cr = request.getChallengeResponse();

          if (cr == null) {
            result = RESULT_MISSING;
          }
          else {
            String nonce = cr.getServerNonce();
            String uri = (cr.getDigestRef() == null) ? null : cr.getDigestRef().toString();
            String qop = cr.getQuality();
            int nc = cr.getServerNounceCount();
            String cnonce = cr.getClientNonce();
            String username = getIdentifier(request, response);
            String cresponse = null;
            char[] secret = getSecret(request, response);
            if (secret != null) {
              cresponse = new String(secret);
            }
            else {
              result = RESULT_INVALID;
            }

            try {
              if (!HttpDigestHelper.isNonceValid(nonce, getDigestAuthenticator().getServerKey(),
                  getDigestAuthenticator().getMaxServerNonceAge())) {
                // Nonce expired, send challenge request with stale=true
                result = RESULT_STALE;
              }
            }
            catch (Exception ce) {
              // Invalid nonce, probably doesn't match serverKey
              result = RESULT_INVALID;
            }

            if (result == RESULT_VALID) {
              if (AuthenticatorUtils.anyNull(nonce, uri)) {
                result = RESULT_MISSING;
              }
              else {
                Reference resourceRef = request.getResourceRef();
                String requestUri = resourceRef.getPath();

                if ((resourceRef.getQuery() != null) && (uri.indexOf('?') > -1)) {
                  // IE neglects to include the query string, so
                  // the workaround is to leave it off
                  // unless both the calculated URI and the
                  // specified URI contain a query string
                  requestUri += "?" + resourceRef.getQuery();
                }

                if (uri.equals(requestUri)) {
                  char[] a1 = getWrappedSecretDigest(username);
                  if (a1 != null) {
                    String a2 = DigestUtils.toMd5(request.getMethod().toString() + ":" + requestUri);
                    StringBuilder expectedResponse = new StringBuilder().append(a1).append(':').append(nonce);
                    if (!AuthenticatorUtils.anyNull(qop, cnonce, nc)) {
                      expectedResponse.append(':').append(AuthenticatorUtils.formatNonceCount(nc)).append(':')
                          .append(cnonce).append(':').append(qop);
                    }
                    expectedResponse.append(':').append(a2);

                    if (!DigestUtils.toMd5(expectedResponse.toString()).equals(cresponse)) {
                      result = RESULT_INVALID;
                    }
                  }
                  else {
                    // The HA1 is null
                    result = RESULT_INVALID;
                  }
                }
                else {
                  // The request URI doesn't match
                  result = RESULT_INVALID;
                }
              }
            }

            if (result == RESULT_VALID) {
              request.getClientInfo().setUser(new User(username));
            }
          }

          return result;
        }

      };

      ((DigestAuthenticator) innerAuthenticator).setVerifier(httpVerifier);
      innerAuthenticator.setEnroler(sitoolsRealm.getEnroler());

      authenticator = new SitoolsChallengeAuthenticator(context, optional, scheme, realm, sitoolsRealm,
          innerAuthenticator);
    }

    // Default setNext
    authenticator.setNext(LoginResource.class);

    authenticators.put(realm, authenticator);

    return authenticator;
  }

}
