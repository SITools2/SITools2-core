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
package fr.cnes.sitools.common;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.jar.Attributes.Name;
import java.util.jar.Manifest;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * Class utility for updating the Classpath of a JAR file
 * 
 * @author malapert
 */
public class JarFile {

  /** The name of the temporary Jar file */
  private static final String TMP_JAR_FILE_NAME = "manifest";
  /** Jar file where the classpath must be updated */
  private final File jarFileName;
  /** the temporary directory */
  private File directoryTmp = null;

  /**
   * Create a new instance of JarFile
   * 
   * @param filename
   *          jar File
   */
  public JarFile(final File filename) {
    this.jarFileName = filename;
  }

  /**
   * Create a new instance of JarFile with a fileName and a temporary directory
   * 
   * @param filename
   *          the name of the Jar
   * @param directoryTmp
   *          the temporary directory
   */
  public JarFile(final File filename, final File directoryTmp) {
    this.jarFileName = filename;
    this.directoryTmp = directoryTmp;
  }

  /**
   * Add one library to classpath
   * 
   * @param jarFileLibrary
   *          jar file to add to the classpath
   * @throws JarFileException
   *           Exception
   */
  public void addLibraryToClasspath(File jarFileLibrary) throws JarFileException {
    this.addLibrariesToClasspath(Arrays.asList(jarFileLibrary));
  }

  /**
   * Add several libraries in the classpath
   * 
   * @param jarFileLibraries
   *          Jar files to add to the classpath
   * @throws JarFileException
   *           if there is an error
   */
  public void addLibrariesToClasspath(List<File> jarFileLibraries) throws JarFileException {
    java.util.jar.JarFile jarFileInput = null;
    ZipOutputStream zipFileTarget = null;
    if (this.directoryTmp.isDirectory()) {
      File targetFile = null;
      try {
        jarFileInput = new java.util.jar.JarFile(this.jarFileName);
        // AKKA, MG
        Manifest baseMf = getBaseManifest(jarFileInput);

        Manifest mf = jarFileInput.getManifest();

        targetFile = new File(this.directoryTmp + File.separator + JarFile.TMP_JAR_FILE_NAME + ".jar");
        zipFileTarget = new ZipOutputStream(new FileOutputStream(targetFile));

        if (baseMf == null) {
          // lets create the new base manifest
          addAManifestInJarFile(mf, zipFileTarget, "META-INF/MANIFEST.MF.BASE");
        }
        else {
          mf = baseMf;
        }

        // if (!isOnelibraryNotIncludedInClasspath(mf, jarFileLibraries)) {
        // // do nothing
        // }
        // else {
        this.addClasspathToManifest(jarFileLibraries, mf);
        this.copyJarToTmpJar(jarFileInput, zipFileTarget);
        File tmpFile = new File(this.directoryTmp + File.separator + JarFile.TMP_JAR_FILE_NAME);
        this.addManifestInJarFile(tmpFile, zipFileTarget);
        tmpFile.delete();
        // }
      }
      catch (ZipException ex) {
        ex.printStackTrace();
        Logger.getLogger(JarFile.class.getName()).log(Level.SEVERE, null, ex);
      }
      catch (IOException ex) {
        ex.printStackTrace();
        Logger.getLogger(JarFile.class.getName()).log(Level.SEVERE, null, ex);
      }
      finally {
        try {
          if (jarFileInput != null) {
            jarFileInput.close();
          }
          if (zipFileTarget != null) {
            zipFileTarget.close();
          }
          if (targetFile != null) {
            // delete the original jar file
            this.jarFileName.delete();
            // rename the jar with the updated classpath
            targetFile.renameTo(this.jarFileName);
          }
        }
        catch (IOException ex) {
          ex.printStackTrace();
          Logger.getLogger(JarFile.class.getName()).log(Level.SEVERE, null, ex);
        }
      }
    }
    else {
      throw new JarFileException("directory_tmp is not set");
    }

  }

  /**
   * Copy the JAR source to a tmp JAR
   * 
   * @param zipfile
   *          JAR source
   * @param zipFileTarget
   *          JAR target
   * @throws JarFileException if there is an error while copying the jar
   */
  private void copyJarToTmpJar(ZipFile zipfile, ZipOutputStream zipFileTarget) throws JarFileException {
    Enumeration<? extends ZipEntry> e = zipfile.entries();
    while (e.hasMoreElements()) {
      ZipEntry ze = (ZipEntry) e.nextElement();
      if (!ze.getName().equals("META-INF/MANIFEST.MF")) {
        try {
          copyZiptoTmpZip(ze, new BufferedInputStream(zipfile.getInputStream(ze)), zipFileTarget);
        }
        catch (IOException ex) {
          Logger.getLogger(JarFile.class.getName()).log(Level.SEVERE, null, ex);
          throw new JarFileException(ex.getMessage());
        }
      }
    }
  }

  /**
   * Get the Base Manifest from the given zipFile, return null if no Base
   * manifest is found
   * 
   * @param zipFile
   *          the zipFile to look in
   * @return the Base Manifest file or null if not found
   * @throws JarFileException
   *           if there is an error while reading the Jar file
   */
  private Manifest getBaseManifest(ZipFile zipFile) throws JarFileException {
    Enumeration<? extends ZipEntry> e = zipFile.entries();
    Manifest mf = null;
    while (e.hasMoreElements() && mf == null) {
      ZipEntry ze = (ZipEntry) e.nextElement();
      if (ze.getName().equals("META-INF/MANIFEST.MF.BASE")) {
        try {
          mf = new Manifest(zipFile.getInputStream(ze));
        }
        catch (IOException ex) {
          Logger.getLogger(JarFile.class.getName()).log(Level.SEVERE, null, ex);
          throw new JarFileException(ex.getMessage());
        }
      }

    }
    return mf;

  }

  /**
   * Add a Manifest to the given {@link ZipOutputStream} with the given
   * fileName. Used to create the base manifest file
   * 
   * @param mf
   *          the manifest file to add in the Jar file
   * @param outputFileZip
   *          Jar file
   * @param fileName
   *          the complete file name to create
   * @throws IOException
   *           if there is an while reading or writing the Manifest file
   * 
   */
  private void addAManifestInJarFile(Manifest mf, ZipOutputStream outputFileZip, String fileName) throws IOException {

    // Add ZIP entry to output stream.
    outputFileZip.putNextEntry(new ZipEntry(fileName));

    mf.write(outputFileZip);

    // Complete the entry
    outputFileZip.closeEntry();
  }

  /**
   * Add jarFileLibrary to the manifest of the classpath
   * 
   * @param jarFileLibraries
   *          Jar file libraries to add in the classpath
   * @param mf
   *          Manifest file
   * @throws IOException
   *           if there is an error while writing in the manifest file
   */
  private void addClasspathToManifest(List<File> jarFileLibraries, Manifest mf) throws IOException {
    String classpathValue = mf.getMainAttributes().getValue(Name.CLASS_PATH);
    StringBuilder libraries = new StringBuilder();
    for (Iterator<File> iterFile = jarFileLibraries.iterator(); iterFile.hasNext();) {
      libraries.append(" ");
      libraries.append(iterFile.next().getPath());
    }
    mf.getMainAttributes().putValue(Name.CLASS_PATH.toString(), classpathValue + libraries.toString());
    FileOutputStream fos = new FileOutputStream(
        new File(this.directoryTmp + File.separator + JarFile.TMP_JAR_FILE_NAME));
    mf.write(fos);
    fos.close();
  }

  // /**
  // * Check existing jar libraries in the classpath
  // *
  // * @param mf
  // * Manifest
  // * @param jarLibraries
  // * Libraries to check
  // * @return Returns true when at least one jarLibrary is not included in the
  // * classpath otherwise false
  // */
  // private boolean isOnelibraryNotIncludedInClasspath(Manifest mf, List<File>
  // jarLibraries) {
  // List<File> newJarLibraries = new ArrayList<File>(jarLibraries);
  // String classpathValue = mf.getMainAttributes().getValue(Name.CLASS_PATH);
  // for (Iterator<File> iterFile = jarLibraries.iterator();
  // iterFile.hasNext();) {
  // File file = iterFile.next();
  // if (classpathValue.contains(file.getAbsolutePath())) {
  // newJarLibraries.remove(file);
  // }
  // }
  // jarLibraries = newJarLibraries;
  // return (!jarLibraries.isEmpty()) ? true : false;
  // }

  /**
   * Copy Entry from a zip file to another zip file
   * 
   * @param entry
   *          Zip entry
   * @param in
   *          Stream of the zipEntry
   * @param outputFileZip
   *          output zip
   * @exception IOException
   *              if there is an error while reading or writing the entry in the
   *              Jar
   */
  private void copyZiptoTmpZip(ZipEntry entry, BufferedInputStream in, ZipOutputStream outputFileZip)
    throws IOException {

    byte[] buf = new byte[1024];

    // Add ZIP entry to output stream.
    outputFileZip.putNextEntry(entry);

    // Transfer bytes from the file to the ZIP file
    int len;
    while ((len = in.read(buf)) > 0) {
      outputFileZip.write(buf, 0, len);
    }
    // Complete the entry
    outputFileZip.closeEntry();
    in.close();
  }

  /**
   * Add Manifest file in a Jar file
   * 
   * @param manifestFile
   *          Manifest File
   * @param outputFileZip
   *          Jar file
   * @throws IOException
   *           if there is an error while reading or writing the manifest file
   *           to the Jar
   */
  private void addManifestInJarFile(File manifestFile, ZipOutputStream outputFileZip) throws IOException {

    // ByteArrayInputStream in = new
    // ByteArrayInputStream(mf.toString().getBytes());
    InputStream in = new FileInputStream(manifestFile);
    byte[] buf = new byte[1024];
    // FileInputStream in = new FileInputStream(filename);

    // Add ZIP entry to output stream.
    outputFileZip.putNextEntry(new ZipEntry("META-INF/MANIFEST.MF"));

    // Transfer bytes from the file to the ZIP file
    int len;
    while ((len = in.read(buf)) > 0) {
      outputFileZip.write(buf, 0, len);
    }
    // Complete the entry
    outputFileZip.closeEntry();
    in.close();
  }

  /**
   * Set the tmp directory where the manifest will be created
   * 
   * @param directoryTmp
   *          the directory_tmp to set
   * @throws JarFileException
   *           if the given file is not a directory
   */
  public void setDirectoryTmp(File directoryTmp) throws JarFileException {
    if (directoryTmp.isDirectory()) {
      this.directoryTmp = directoryTmp;
    }
    else {
      throw new JarFileException(directoryTmp + " is not a directory");
    }
  }

  /**
   * Main method to do quick testing
   * 
   * @param args
   *          the args
   * @throws JarFileException
   *           if there is an error
   */
  public static void main(String[] args) throws JarFileException {
    JarFile jarFile = new JarFile(new File("fr.cnes.sitools.core.jar"));
    jarFile.setDirectoryTmp(new File("ext"));
    jarFile.addLibraryToClasspath(new File("ext/fr.cnes.sitools.ext.jar"));    
  }
}
