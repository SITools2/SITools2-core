    /*******************************************************************************
 * Copyright 2010-2013 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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
package fr.cnes.sitools.userstorage.business;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;

import org.restlet.Context;

import fr.cnes.sitools.common.SitoolsSettings;
import fr.cnes.sitools.common.application.ContextAttributes;
import fr.cnes.sitools.userstorage.model.UserStorage;
import fr.cnes.sitools.userstorage.model.UserStorageStatus;
import fr.cnes.sitools.util.DateUtils;
import fr.cnes.sitools.util.FileUtils;

/**
 * static functions on UserStorage
 * 
 * @author jp.boignard (AKKA Technologies)
 * 
 */
public final class UserStorageManager {

  /**
   * Private constructor for utility class
   */
  private UserStorageManager() {
    super();
  }

  /**
   * Checks if quota exceeded
   * 
   * @param context
   *          Context containing SitoolsSettings instance attribute
   * @param storage
   *          UserStorage
   * @return boolean true if quota exceeded.
   */
  public static boolean checkDiskSpace(Context context, UserStorage storage) {
    String formattedUserStoragePath = getSettings(context)
        .getFormattedString(storage.getStorage().getUserStoragePath());
    return (storage.getStorage().getQuota() == null)
        || FileUtils.getFileSize(new File(formattedUserStoragePath)) > storage.getStorage().getQuota();
  }

  /**
   * Builds user directory
   * 
   * @param context
   *          Context containing SitoolsSettings instance attribute
   * @param storage
   *          UserStorage
   */
  public static void build(Context context, UserStorage storage) {
    if (storage.getStorage() != null) {
      String formattedUserStoragePath = getSettings(context).getFormattedString(
          storage.getStorage().getUserStoragePath());
      File userDir = new File(formattedUserStoragePath);
      if (!userDir.exists()) {
        userDir.mkdirs();
        // else warning
      }
      else {
        refresh(context, storage);
      }
    }
    // mk others sub directories.
    storage.setStatus(UserStorageStatus.ACTIVE);
  }

  /**
   * Refresh UserStorage definition according to the physical user directory
   * 
   * @param context
   *          Context containing SitoolsSettings instance attribute
   * @param storage
   *          UserStorage to refresh
   */
  public static void refresh(Context context, UserStorage storage) {
    if (storage.getStorage() != null) {

      Date currentDate = new Date();
      Date lastUpdate = storage.getStorage().getLastUpdate();

      int refreshDelay = getSettings(context).getUserStorageRefreshDelay();
      if (lastUpdate == null || DateUtils.add(lastUpdate, Calendar.MINUTE, refreshDelay).before(currentDate)) {
        context.getLogger().finest("UserStorageManager.refresh(" + storage.getUserId() + ")");

        String path = getSettings(context).getFormattedString(storage.getStorage().getUserStoragePath());

        File userDir = new File(path);
        if (userDir.exists() && userDir.isDirectory()) {
          long size = FileUtils.getFileSize(userDir);
          storage.getStorage().setBusyUserSpace(size);
          storage.getStorage().setFreeUserSpace(
              (storage.getStorage().getQuota() == null ? 0 : storage.getStorage().getQuota()) - size);
        }
        else {
          storage.getStorage().setBusyUserSpace(new Long(0));
        }
        storage.getStorage().setLastUpdate(currentDate);
      }
    }
  }

  /**
   * Delete recursively all files except config files.
   * 
   * @param context
   *          Context containing SitoolsSettings instance attribute
   * @param storage
   *          UserStorage to clean
   */
  public static void clean(Context context, UserStorage storage) {
    if (storage.getStorage() != null) {
      // Format UserStoragePath
      String formattedUserStoragePath = getSettings(context).getFormattedString(
          storage.getStorage().getUserStoragePath());
      File userDir = new File(formattedUserStoragePath);
      if (userDir.exists() && userDir.isDirectory()) {
        try {
          FileUtils.cleanDirectory(userDir, false);
        }
        catch (IOException e) {
          context.getLogger().log(Level.FINE, "UserStorageManager.clean(" + userDir.getAbsolutePath() + ")", e);
        }
      }
      userDir.mkdir();
    }
  }

  /**
   * Delete recursively all files.
   * 
   * @param context
   *          Context containing SitoolsSettings instance attribute
   * @param storage
   *          UserStorage to clean
   */
  public static void delete(Context context, UserStorage storage) {
    if (storage.getStorage() != null) {
      // Format UserStoragePath
      String formattedUserStoragePath = getSettings(context).getFormattedString(
          storage.getStorage().getUserStoragePath());
      File userDir = new File(formattedUserStoragePath);
      if (userDir.exists() && userDir.isDirectory()) {
        try {
          FileUtils.cleanDirectory(userDir, false);
        }
        catch (IOException e) {
          context.getLogger().log(Level.FINE, "UserStorageManager.clean(" + userDir.getAbsolutePath() + ")", e);
        }
      }
      // userDir.mkdir();
    }
  }

  /**
   * getSettings shortcut function
   * 
   * @param context
   *          Context containing SitoolsSettings instance attribute
   * @return SitoolsSettings
   */
  private static SitoolsSettings getSettings(Context context) {
    return (SitoolsSettings) context.getAttributes().get(ContextAttributes.SETTINGS);
  }

}
