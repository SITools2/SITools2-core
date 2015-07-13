package fr.cnes.sitools.security.challenge;

/**
 * Challenge token container interface
 * 
 * 
 * @author m.gond
 */
public interface ChallengeToken {
  /**
   * Gets a new valid Token. Stores the given value associated to that token
   * 
   * @param value
   *          the value to associate
   * @return the token String
   */
  String getToken(String value);

  /**
   * Get the value associated to the given token. null if the token doesn't exists
   * 
   * @param token
   *          the token
   * @return the value associated to the given token or null if the token doesn't exists
   */
  String getTokenValue(String token);

  /**
   * Check if the given token is valid
   * 
   * @param token
   *          the token String to check
   * @return true if the token is valid, false otherwise
   */
  boolean isValid(String token);

  /**
   * Manually invalidate a token
   * 
   * @param token
   *          the token to invalidate
   */
  void invalidToken(String token);

}
