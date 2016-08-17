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
package fr.cnes.sitools.common;

import gnu.getopt.Getopt;
import gnu.getopt.LongOpt;
import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;

/**
 * Main application for automatic Jar manifest update
 * 
 * @author malapert
 */
public final class JarFileApplication {

  /**
   * JarFileApplication default constructor
   */
  private JarFileApplication() {
    super();
  }

  /**
   * Main method
   * 
   * @param argv
   *          the arguments : [-h|--help] --tmp_directory=<> --directory=<> [|--jar_library=<>] --jar_target=<>
   *          <ul> 
   *          <li>--tmp_directory : tmp directory (required)</li> 
   *          <li>--directory : list all jars in this  directory and add them in the classpath when they are not present</li>
   *          <li>--jar_library : add only one jar in the classpath</li>
   *          <li>--jar_target : Jar in which the Manifest will be updated (required)</li>
   *          </ul>
   * @throws Exception
   *           if there is an error while working with Jar file or with the
   *           arguments
   */
  public static void main(String[] argv) throws Exception {

    int c;
    String arg;
    LongOpt[] longopts = new LongOpt[5];
    //
    StringBuilder sb = new StringBuilder();
    longopts[0] = new LongOpt("help", LongOpt.NO_ARGUMENT, null, 'h');
    longopts[1] = new LongOpt("tmp_directory", LongOpt.REQUIRED_ARGUMENT, null, 1);
    longopts[2] = new LongOpt("directory", LongOpt.REQUIRED_ARGUMENT, null, 2);
    longopts[3] = new LongOpt("jar_library", LongOpt.REQUIRED_ARGUMENT, null, 3);
    longopts[4] = new LongOpt("jar_target", LongOpt.REQUIRED_ARGUMENT, null, 4);
    //
    Getopt g = new Getopt("UpdateClasspath", argv, "h", longopts);
    g.setOpterr(false);
    //
    String tmpDirectory = null;
    String directory = null;
    String jarLibrary = null;
    String jarTarget = null;

    while ((c = g.getopt()) != -1) {
      switch (c) {
        case 0:
          arg = g.getOptarg();
          System.out.println("Got long option with value '" + (char) (new Integer(sb.toString())).intValue()
              + "' with argument " + ((arg != null) ? arg : "null"));
          break;
        //
        case 1:
          tmpDirectory = g.getOptarg();
          break;
        //
        case 2:
          directory = g.getOptarg();
          break;
        //
        case 3:
          jarLibrary = g.getOptarg();
          break;
        //
        case 4:
          jarTarget = g.getOptarg();
          break;
        //
        case 'h':
          usage();
          break;
  
        default:
          usage();
          break;
      }
    }
    //
    for (int i = g.getOptind(); i < argv.length; i++) {
      System.out.println("Non option argv element: " + argv[i] + "\n");
    }

    if (tmpDirectory == null && jarTarget == null) {
      usage();
      System.exit(1);
    }
    else {
      if (jarLibrary == null && directory == null) {
        usage();
        System.exit(1);
      }
      else if (jarLibrary != null && directory != null) {
        usage();
        System.exit(1);
      }
      else {
        // do nothing
      }

    }

    JarFile jarFile = new JarFile(new File(jarTarget));
    jarFile.setDirectoryTmp(new File(tmpDirectory));
    if (jarLibrary != null) {
      jarFile.addLibraryToClasspath(new File(jarLibrary));
    }
    else if (directory != null) {
      File d = new File(directory);
      if (!d.isDirectory()) {
        System.exit(2);
      }
      FilenameFilter filter = new FilenameFilter() {

        @Override
        public boolean accept(File dir, String name) {
          return name.endsWith("jar");
        }
      };
      File[] files = d.listFiles(filter);
      jarFile.addLibrariesToClasspath(Arrays.asList(files));
    }
    System.exit(0);
  }

  /**
   * Usage explaination method
   */
  public static void usage() {
    System.out
        .println("USAGE: UpdateClasspath [-h|--help] --tmp_directory=<> --directory=<>[|--jar_library=<>] --jar_target=<>");
    System.out.println("--tmp_directory : tmp directory (required)");
    System.out
        .println("--directory : list all jars in this directory and add them in the classpath when they are not present");
    System.out.println("--jar_library : add only one jar in the classpath");
    System.out.println("--jar_target : Jar in which the Manifest will be updated (required)");
    System.exit(1);
  }
}
