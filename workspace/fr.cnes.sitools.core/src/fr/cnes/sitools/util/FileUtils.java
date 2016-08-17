/*******************************************************************************
 * Copyright 2010-2016 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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

import java.io.Closeable;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.restlet.engine.Engine;

/**
 * 
 * <DIV lang="en"></DIV> <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class FileUtils {

  /** Class logger */
  private static Logger logger = Engine.getLogger(FileUtils.class.getName());

  /** Private constructor for utility class */
  private FileUtils() {
  }

  /**
   * Get temporary director
   * 
   * @return a temp directory
   */
  public static File getTempDir() {
    String tmpdir = System.getProperty("java.io.tmpdir");
    return new File(tmpdir);
  }

  /**
   * Get the file size
   * 
   * @param file
   *          file to look at
   * @return the size
   */
  public static long getFileSize(File file) {
    if (!file.exists()) {
      return -1L;
    }
    long size = 0;
    if (file.isDirectory()) {
      File[] files = file.listFiles();
      if (files != null && files.length > 0) {
        for (File f : files) {
          size += getFileSize(f);
        }
      }
    }
    else {
      size += file.length();
    }
    return size;
  }

  /**
   * Finds files within a given directory (and optionally its subdirectories). All files found are filtered by an
   * IOFileFilter.
   * 
   * @param directory
   *          the directory to list
   * @param fileFilter
   *          the filter file
   * @param dirFilter
   *          the directory filter
   * @return a list of files
   */
  public static List<File> listFiles(File directory, IOFileFilter fileFilter, IOFileFilter dirFilter) {
    // if(!directory.isDirectory()) {
    // throw new
    // IllegalArgumentException("Parameter 'directory' is not a directory");
    // }
    if (fileFilter == null) {
      throw new NullPointerException("Parameter 'fileFilter' is null");
    }
    // Setup effective file filter
    IOFileFilter effFileFilter = new AndFileFilter(fileFilter, new NotFileFilter(DirectoryFileFilter.INSTANCE));
    // Setup effective directory filter
    final IOFileFilter effDirFilter;
    if (dirFilter == null) {
      effDirFilter = FalseFileFilter.INSTANCE;
    }
    else {
      effDirFilter = new AndFileFilter(dirFilter, DirectoryFileFilter.INSTANCE);
    }
    // Find files
    List<File> files = new ArrayList<File>(12);
    innerListFiles(files, directory, new OrFileFilter(effFileFilter, effDirFilter));
    return files;
  }

  /**
   * Finds files within a given directory (and optionally its subdirectories). All files found are filtered by an
   * IOFileFilter.
   * 
   * @param directory
   *          the directory to list
   * @param files
   *          the files
   * @param filter
   *          the filter
   */
  private static void innerListFiles(Collection<File> files, File directory, IOFileFilter filter) {
    File[] found = directory.listFiles((FileFilter) filter);
    if (found != null) {
      for (int i = 0; i < found.length; i++) {
        if (found[i].isDirectory()) {
          innerListFiles(files, found[i], filter);
        }
        else {
          files.add(found[i]);
        }
      }
    }
  }

  /**
   * Create a list of files
   * 
   * @param directory
   *          to search in
   * @param suffix
   *          the suffix to look for
   * @param recursive
   *          true indicates recursive
   * @return a list of files
   */
  public static List<File> listFiles(File directory, String suffix, boolean recursive) {
    return listFiles(directory, new String[] {suffix}, recursive);
  }

  /**
   * Finds files within a given directory (and optionally its subdirectories) which match an array of suffixes.
   * 
   * @param directory
   *          File directory to clean up
   * @param suffixes
   *          the suffixes to look for
   * @param recursive
   *          true indicates recursive
   * @return a list of files
   */
  public static List<File> listFiles(File directory, String[] suffixes, boolean recursive) {
    final IOFileFilter filter;
    if (suffixes == null) {
      filter = TrueFileFilter.INSTANCE;
    }
    else {
      filter = new SuffixFileFilter(suffixes);
    }
    return listFiles(directory, filter, (recursive ? TrueFileFilter.INSTANCE : FalseFileFilter.INSTANCE));
  }

  /**
   * Finds files within a given directory (and optionally its subdirectories) which match an array of suffixes.
   * 
   * @param directory
   *          to search in
   * @param prefixes
   *          the prefixes to look for
   * @param suffixes
   *          the suffixes to look for
   * @param recursive
   *          true indicates recursive
   * @return a list of files
   */
  public static List<File> listFiles(File directory, String[] prefixes, String[] suffixes, boolean recursive) {
    IOFileFilter fileFiler = null;
    if (prefixes != null && prefixes.length > 0) {
      fileFiler = new PrefixFileFilter(prefixes);
    }
    if (suffixes != null && suffixes.length > 0) {
      fileFiler = new AndFileFilter(fileFiler, new SuffixFileFilter(suffixes));
    }
    return listFiles(directory, (fileFiler == null ? TrueFileFilter.INSTANCE : fileFiler),
        (recursive ? TrueFileFilter.INSTANCE : FalseFileFilter.INSTANCE));
  }

  /**
   * Clean a directory
   * 
   * @param dir
   *          the directory to clean
   * @param suffixes
   *          the suffixes to delete
   * @param recursive
   *          to clean specified directory and inner directories recursively
   * @throws IOException
   *           when file exceptions occur
   */
  public static void cleanDirectory(File dir, String[] suffixes, boolean recursive) throws IOException {
    if (!dir.exists()) {
      throw new IllegalArgumentException(dir + " does not exist");
    }
    if (!dir.isDirectory()) {
      throw new IllegalArgumentException(dir + " is not a directory");
    }

    IOFileFilter suffixeFilter = new SuffixFileFilter(suffixes);
    IOFileFilter folderFileFilter = new DirectoryFileFilter();

    FileFilter fileFilter = new OrFileFilter(new IOFileFilter[] {suffixeFilter, folderFileFilter});

    final File[] files;
    if (suffixes.length == 0) {
      files = dir.listFiles();
    }
    else {
      files = dir.listFiles((FileFilter) fileFilter);
    }

    if (files == null) { // null if security restricted
      throw new IOException("Failed to list contents of " + dir);
    }

    for (int i = 0; i < files.length; i++) {
      if (files[i].isDirectory() && recursive) {
        cleanDirectory(files[i], suffixes, recursive);
      }
      else if (!files[i].delete()) {
        throw new IOException("Unable to delete file: " + files[i].getAbsolutePath());
      }
    }
  }

  /**
   * Clean a directory
   * 
   * @param dir
   *          the directory to clean
   * @param deleteDir
   *          true to delete the directory, false otherwise
   * @return true if the directory is correctly cleaned
   * @throws IOException
   *           if reading directory fails
   */
  public static boolean cleanDirectory(File dir, boolean deleteDir) throws IOException {
    if (!dir.exists()) {
      throw new IllegalArgumentException(dir + " does not exist");
    }
    if (!dir.isDirectory()) {
      throw new IllegalArgumentException(dir + " is not a directory");
    }

    boolean resultat = true;

    if (dir.exists()) {
      File[] files = dir.listFiles();
      for (int i = 0; i < files.length; i++) {
        if (files[i].isDirectory()) {
          resultat &= cleanDirectory(files[i], true);
        }
        else {
          logger.finest("Delete: " + files[i].getName());
          resultat &= files[i].delete();
        }
      }
    }
    if (deleteDir) {
      resultat &= dir.delete();
    }
    return (resultat);

  }

  // /**
  // * Converts an array of file extensions to suffixes for use with IOFileFilters.
  // *
  // * @param extensions
  // * file extensions to use
  // * @return String[] suffixes array with form : "."+extension
  // */
  // @Deprecated
  // private static String[] toSuffixes(String[] extensions) {
  // String[] suffixes = new String[extensions.length];
  // for (int i = 0; i < extensions.length; i++) {
  // suffixes[i] = "." + extensions[i];
  // }
  // return suffixes;
  // }

  /**
   * Get the file name
   * 
   * @param file
   *          file to look at
   * @return the file name
   */
  public static String getFileName(File file) {
    assert (file != null);
    if (!file.exists()) {
      return null;
    }
    String filepath = file.getName();
    int i = filepath.lastIndexOf(File.separator);
    return (i >= 0) ? filepath.substring(i + 1) : filepath;
  }

  /**
   * Get base name of the path
   * 
   * @param filepath
   *          the path to the file
   * @return the base name
   */
  public static String basename(String filepath) {
    final int index = filepath.lastIndexOf(File.separatorChar);
    if (-1 == index) {
      return filepath;
    }
    else {
      return filepath.substring(index + 1);
    }
  }

  /**
   * Get base name of the path
   * 
   * @param filepath
   *          the path to the file
   * @param separator
   *          the separator to use
   * @return the base name
   */
  public static String basename(String filepath, char separator) {
    final int index = filepath.lastIndexOf(separator);
    if (-1 == index) {
      return filepath;
    }
    else {
      return filepath.substring(index + 1);
    }
  }

  /**
   * Get the directory name
   * 
   * @param filepath
   *          path of the directory
   * @param separatorChar
   *          character of separation
   * @return the directory name
   */
  public static String dirName(String filepath, char separatorChar) {
    final int index = filepath.lastIndexOf(separatorChar);
    if (-1 == index) {
      return new String(new char[] {separatorChar});
    }
    else {
      return filepath.substring(0, index);
    }
  }

  /**
   * Truncate the file
   * 
   * @param file
   *          file to truncate
   */
  public static void truncateFile(File file) {
    final RandomAccessFile raf;
    try {
      raf = new RandomAccessFile(file, "rw");
    }
    catch (FileNotFoundException fnfe) {
      throw new IllegalStateException(fnfe);
    }
    try {
      raf.setLength(0);
    }
    catch (IOException ioe) {
      throw new IllegalStateException(ioe);
    }
    finally {
      closeQuietly(raf);
    }
  }

  /**
   * Close a channel
   * 
   * @param channel
   *          channel to close
   */
  public static void closeQuietly(final Closeable channel) {
    if (channel != null) {
      try {
        channel.close();
      }
      catch (IOException e) {
        logger.log(Level.INFO, null, e);
      }
    }
  }

  /**
   * Interface for File filters, joining both File and File name filters
   * 
   * @author m.marseille (AKKA Technologies)
   * 
   */
  public interface IOFileFilter extends FileFilter, FilenameFilter {
  }

  /**
   * Main file filter
   * 
   * @author AKKA
   * 
   */
  public static final class TrueFileFilter implements IOFileFilter {

    /**
     * Instance of filter
     */
    static final TrueFileFilter INSTANCE = new TrueFileFilter();

    /**
     * Constructor
     */
    private TrueFileFilter() {
    }

    @Override
    public boolean accept(File pathname) {
      return true;
    }

    @Override
    public boolean accept(File dir, String name) {
      return true;
    }
  }

  /**
   * False filter
   * 
   * @author AKKA
   */
  public static final class FalseFileFilter implements IOFileFilter {

    /**
     * Instance of filter
     */
    static final FalseFileFilter INSTANCE = new FalseFileFilter();

    /**
     * Constructor
     */
    private FalseFileFilter() {
    }

    @Override
    public boolean accept(File pathname) {
      return false;
    }

    @Override
    public boolean accept(File dir, String name) {
      return false;
    }
  }

  /**
   * Prefix filter
   * 
   * @author m.marseille (AKKA Technologies)
   * 
   */
  public static final class PrefixFileFilter implements IOFileFilter {

    /**
     * List of prefixes
     */
    private final String[] prefixes;

    /**
     * Constructor
     * 
     * @param prefixes
     *          list of prefixes
     */
    public PrefixFileFilter(String... prefixes) {
      if (prefixes == null) {
        throw new IllegalArgumentException("The array of prefixes must not be null");
      }
      this.prefixes = prefixes;
    }

    @Override
    public boolean accept(File file) {
      String name = file.getName();
      for (int i = 0; i < this.prefixes.length; i++) {
        if (name.startsWith(this.prefixes[i])) {
          return true;
        }
      }
      return false;
    }

    @Override
    public boolean accept(File file, String name) {
      for (int i = 0; i < prefixes.length; i++) {
        if (name.startsWith(prefixes[i])) {
          return true;
        }
      }
      return false;
    }
  }

  /**
   * Suffic file filter
   * 
   * @author AKKA
   * 
   */
  public static final class SuffixFileFilter implements IOFileFilter {
    /**
     * Array of suffixes
     */
    private final String[] suffixes;

    /**
     * Constructor
     * 
     * @param suffixes
     *          list of suffixes
     */
    public SuffixFileFilter(String... suffixes) {
      if (suffixes == null) {
        throw new IllegalArgumentException("The array of suffixes must not be null");
      }
      this.suffixes = suffixes;
    }

    @Override
    public boolean accept(File file) {
      String name = file.getName();
      for (int i = 0; i < this.suffixes.length; i++) {
        if (name.endsWith(this.suffixes[i])) {
          return true;
        }
      }
      return false;
    }

    @Override
    public boolean accept(File file, String name) {
      for (int i = 0; i < this.suffixes.length; i++) {
        if (name.endsWith(this.suffixes[i])) {
          return true;
        }
      }
      return false;
    }
  }

  /**
   * And file filter
   * 
   * @author m.marseille (AKKA Technologies)
   * 
   */
  public static final class AndFileFilter implements IOFileFilter {

    /**
     * Array of filters
     */
    private final IOFileFilter[] fileFilters;

    /**
     * Constructor
     * 
     * @param filter
     *          list of filters
     */
    public AndFileFilter(IOFileFilter... filter) {
      assert (filter != null);
      this.fileFilters = filter;
    }

    @Override
    public boolean accept(final File file) {
      if (this.fileFilters.length == 0) {
        return false;
      }
      for (IOFileFilter fileFilter : fileFilters) {
        if (!fileFilter.accept(file)) {
          return false;
        }
      }
      return true;
    }

    @Override
    public boolean accept(final File file, final String name) {
      if (this.fileFilters.length == 0) {
        return false;
      }
      for (IOFileFilter fileFilter : fileFilters) {
        if (!fileFilter.accept(file, name)) {
          return false;
        }
      }
      return true;
    }
  }

  /**
   * Or file filter
   * 
   * @author AKKA
   * 
   */
  public static final class OrFileFilter implements IOFileFilter {

    /**
     * Array of filters
     */
    private final IOFileFilter[] fileFilters;

    /**
     * Constructor
     * 
     * @param filter
     *          a list of filters
     */
    public OrFileFilter(IOFileFilter... filter) {
      assert (filter != null);
      this.fileFilters = filter;
    }

    @Override
    public boolean accept(final File file) {
      for (IOFileFilter fileFilter : fileFilters) {
        if (fileFilter.accept(file)) {
          return true;
        }
      }
      return false;
    }

    @Override
    public boolean accept(final File file, final String name) {
      for (IOFileFilter fileFilter : fileFilters) {
        if (fileFilter.accept(file, name)) {
          return true;
        }
      }
      return false;
    }
  }

  /**
   * Not file filter
   * 
   * @author AKKA
   * 
   */
  public static final class NotFileFilter implements IOFileFilter {

    /**
     * Associated filter
     */
    private final IOFileFilter filter;

    /**
     * Constructor
     * 
     * @param filter
     *          the filter to associate
     */
    public NotFileFilter(IOFileFilter filter) {
      if (filter == null) {
        throw new IllegalArgumentException("The filter must not be null");
      }
      this.filter = filter;
    }

    @Override
    public boolean accept(File file) {
      return !filter.accept(file);
    }

    @Override
    public boolean accept(File file, String name) {
      return !filter.accept(file, name);
    }
  }

  /**
   * Directory file filter
   * 
   * @author AKKA
   * 
   */
  public static final class DirectoryFileFilter implements IOFileFilter {

    /**
     * Instance of filter
     */
    public static final DirectoryFileFilter INSTANCE = new DirectoryFileFilter();

    /**
     * Constructor
     */
    private DirectoryFileFilter() {
      super();
    }

    @Override
    public boolean accept(File file) {
      return file.isDirectory();
    }

    @Override
    public boolean accept(File dir, String name) {
      return accept(new File(dir, name));
    }
  }

  /**
   * Name file filter
   * 
   * @author m.marseille (AKKA Technologies)
   * 
   */
  public static final class NameFileFilter implements IOFileFilter {

    /**
     * Array of names
     */
    private final String[] names;

    /**
     * Constructor
     * 
     * @param names
     *          names to associate
     */
    public NameFileFilter(String... names) {
      if (names == null) {
        throw new IllegalArgumentException("The array of names must not be null");
      }
      this.names = names;
    }

    @Override
    public boolean accept(File file) {
      String name = file.getName();
      for (int i = 0; i < this.names.length; i++) {
        if (name.equals(this.names[i])) {
          return true;
        }
      }
      return false;
    }

    @Override
    public boolean accept(File file, String name) {
      for (int i = 0; i < this.names.length; i++) {
        if (name.equals(this.names[i])) {
          return true;
        }
      }
      return false;
    }
  }
}
