package fr.cnes.sitools.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class PasswordGenerator {
  /**
   * Generate a password
   * 
   * @param length
   *          the length of password
   * @return password
   */
  public static String generate(int length) {
    List<Character> list = new ArrayList<Character>();

    long nbEachClass = Math.round(Math.floor(length / 3));
    Random random = new Random();
    for (int i = 0; i < nbEachClass; i++) {
      // generate a number
      list.add(String.valueOf(random.nextInt(10)).charAt(0));
      // generate a lowercase character
      list.add((char) (random.nextInt(26) + 'a'));
      // generate an uppercase character
      list.add((char) (random.nextInt(26) + 'A'));
    }

    for (long i = (nbEachClass * 3); i < length; i++) {
      list.add((char) (random.nextInt(26) + 'a'));
    }

    Collections.shuffle(list);

    String out = new String();
    for (Character character : list) {
      out += character;
    }
    return out;
  }
}
