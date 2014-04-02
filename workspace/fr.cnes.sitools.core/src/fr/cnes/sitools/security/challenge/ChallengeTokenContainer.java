package fr.cnes.sitools.security.challenge;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

/**
 * ChallengeToken implementation based on a Guava Cache
 * 
 * 
 * @author m.gond
 */
public class ChallengeTokenContainer implements ChallengeToken {
  /**
   * The cache to store the tokens and values
   */
  private Cache<String, String> cache;

  /**
   * Instantiates a new challenge token container.
   * 
   * @param cacheLimitTime
   *          the cache limit time
   * @param cacheMaxSize
   *          the cache max size
   */
  public ChallengeTokenContainer(long cacheLimitTime, long cacheMaxSize) {
    cache = CacheBuilder.newBuilder().expireAfterWrite(cacheLimitTime, TimeUnit.MINUTES).maximumSize(cacheMaxSize) // TTL
        .build();
  }

  @Override
  public String getToken(String value) {
    String token = UUID.randomUUID().toString();
    cache.put(token, value);
    return token;
  }

  @Override
  public String getTokenValue(String token) {
    return cache.getIfPresent(token);
  }

  @Override
  public boolean isValid(String token) {
    return getTokenValue(token) != null;
  }

  @Override
  public void invalidToken(String token) {
    cache.invalidate(token);
  }

}
