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
package fr.cnes.sitools.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Utility class to copy a file or a folder and its sub-folder
 * 
 * @author m.gond (AKKA Technologies)
 */
public final class FileCopyUtils {

  /**
   * Private constructor for utility class
   */
  private FileCopyUtils() {
    super();
  }

  /**
   * Copy a folder
   * 
   * @param sourceUrl
   *          source path
   * @param destUrl
   *          destination path
   */
  public static void copyAFolder(final String sourceUrl, final String destUrl) {
    File sourceFile = new File(sourceUrl);
    copyAFolder(sourceFile, destUrl);
  }

  /**
   * Copy a folder excluding some files
   * 
   * @param sourceUrl
   *          source path
   * @param destUrl
   *          destination path
   * @param exclude
   *          excluding string
   */
  public static void copyAFolderExclude(final String sourceUrl, final String destUrl, String exclude) {
    File sourceFile = new File(sourceUrl);
    File[] fileList = sourceFile.listFiles();
    if (fileList != null) {
      for (int i = 0; i < fileList.length; i++) {
        if (!fileList[i].getName().equals(".svn")) {
          copyAFolderExclude(fileList[i], destUrl, exclude);
        }
      }
    }
  }

  /**
   * Copy the content of a folder and its sub-folders.
   * 
   * @param file
   *          the folder to copy
   * @param destUrl
   *          the destination path
   * @param excludeExt
   *          exclude a folder
   */
  public static void copyAFolderExclude(final File file, final String destUrl, final String excludeExt) {

    if (file != null && file.isFile()) {
      copyAFile(file.getAbsolutePath(), destUrl + "/" + file.getName());
    }
    if (file != null && file.isDirectory() && !file.getName().equals(excludeExt)) {
      // creates the directory
      File fileDir = new File(destUrl + "/" + file.getName());
      fileDir.mkdirs();
      // copy all the files in the directory
      File[] fileList = file.listFiles();
      for (int i = 0; i < fileList.length; i++) {
        copyAFolderExclude(fileList[i], destUrl + "/" + file.getName(), excludeExt);
      }
    }
  }

  /**
   * Copy all folder in a directory excluding the root directory
   * 
   * @param sourceFile
   *          source file
   * @param destUrl
   *          destination url
   * @param excludeRoot
   *          excluding boolean, true to exclude root
   */
  public static void copyAFolder(final File sourceFile, final String destUrl, boolean excludeRoot) {
    if (excludeRoot) {
      if (sourceFile != null && sourceFile.isFile()) {
        copyAFile(sourceFile.getAbsolutePath(), destUrl + "/" + sourceFile.getName());
      }
      if (sourceFile != null && sourceFile.isDirectory()) {
        File[] fileList = sourceFile.listFiles();

        for (int i = 0; i < fileList.length; i++) {
          copyAFolder(fileList[i], destUrl);
        }
      }
    }
    else {
      FileCopyUtils.copyAFolder(sourceFile, destUrl);
    }
  }

  /**
   * Copy the content of a folder and its sub-folders.
   * 
   * @param file
   *          the folder to copy
   * @param destUrl
   *          the destination URL
   */
  public static void copyAFolder(final File file, final String destUrl) {

    if (file != null && file.isFile()) {
      copyAFile(file.getAbsolutePath(), destUrl + "/" + file.getName());
    }
    if (file != null && file.isDirectory()) {
      // creates the directory
      File fileDir = new File(destUrl + "/" + file.getName());
      fileDir.mkdir();
      // copy all the files in the directory
      File[] fileList = file.listFiles();
      for (int i = 0; i < fileList.length; i++) {
        copyAFolder(fileList[i], destUrl + "/" + file.getName());
      }
    }
  }

  /**
   * Copy a file at the source URL to the destination URL.
   * 
   * @param entree
   *          the source file URL
   * @param sortie
   *          the destination file URL
   */
  public static void copyAFile(final String entree, final String sortie) {
    FileChannel in = null; // canal d'entrée
    FileChannel out = null; // canal de sortie

    try {
      // Init
      in = new FileInputStream(entree).getChannel();
      out = new FileOutputStream(sortie).getChannel();

      // Copie depuis le in vers le out
      in.transferTo(0, in.size(), out);
    }
    catch (Exception e) {
      e.printStackTrace(); // n'importe quelle exception
    }
    finally { // finalement on ferme
      if (in != null) {
        try {
          in.close();
        }
        catch (IOException e) {
          e.printStackTrace();
        }
      }
      if (out != null) {
        try {
          out.close();
        }
        catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }

  /**
   * unzipAFile
   * 
   * @param inputFile
   *          complete zip filename
   * @param outputFolder
   *          directory path
   */
  public static void unzipAFile(final String inputFile, final String outputFolder) {

    byte[] buffer = new byte[1024];

    File folder = null;
    ZipInputStream zis = null;
    File newFile = null;
    FileOutputStream fos = null;

    try {
      // create output directory is not exists
      folder = new File(outputFolder);
      if (!folder.exists()) {
        folder.mkdirs();
      }

      // get the zip file content
      zis = new ZipInputStream(new FileInputStream(inputFile));

      // get the zipped file list entry
      ZipEntry ze = zis.getNextEntry();

      while (ze != null) {

        String fileName = ze.getName();
        newFile = new File(outputFolder + File.separator + fileName);

        if (ze.isDirectory()) {
          // create all non exists folders
          newFile.mkdirs();
        }
        else {
          fos = new FileOutputStream(newFile);

          int len;
          while ((len = zis.read(buffer)) > 0) {
            fos.write(buffer, 0, len);
          }

          fos.close();

        }
        //
        // // else you will hit FileNotFoundException for compressed folder
        // new File(newFile.getParent()).mkdirs();

        ze = zis.getNextEntry();
      }

      zis.closeEntry();
      zis.close();
    }
    catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    catch (IOException e) {
      e.printStackTrace();
    }
    finally {
      if (null != zis) {
        try {
          zis.close();
        }
        catch (IOException e) {
          // nothing
        }
      }
      if (null != fos) {
        try {
          fos.close();
        }
        catch (IOException e) {
          // nothing
        }
      }
    }
  }
}
