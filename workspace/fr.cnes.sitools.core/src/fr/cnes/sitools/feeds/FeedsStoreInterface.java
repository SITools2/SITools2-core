package fr.cnes.sitools.feeds;

import fr.cnes.sitools.common.store.SitoolsStore;
import fr.cnes.sitools.feeds.model.FeedModel;

public interface FeedsStoreInterface extends SitoolsStore<FeedModel> {
  /**
   * Update the feedDetails in the store
   * 
   * @param feed
   *          the {@link FeedModel} to get the details from
   * @return the updated {@link FeedModel}
   */
  FeedModel updateDetails(FeedModel feed);

}
