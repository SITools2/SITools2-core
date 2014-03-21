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
package javax.measure.unit.format;

import javax.measure.unit.Unit;
import javax.measure.unit.converter.LogConverter;

/** */
class UnitParser implements UnitParserConstants {

    private static class Exponent {
        final int pow;
        final int root;
        public Exponent (int pow, int root) {
            this.pow = pow;
            this.root = root;
        }
    }

    private SymbolMapImpl _symbols;

    UnitParser (SymbolMapImpl symbols, java.io.Reader in) {
        this(in);
        _symbols = symbols;
    }

//
// Parser productions
//
    javax.measure.unit.Unit<?> parseUnit() throws ParseException {
        javax.measure.unit.Unit<?> result;
    result = compoundExpr();
    jj_consume_token(0);
        {if (true) return result;}
    throw new Error("Missing return statement in function");
  }

  Unit<?> compoundExpr() throws ParseException {
        Unit<?> result = javax.measure.unit.Unit.ONE;
        Unit temp = javax.measure.unit.Unit.ONE;
    result = addExpr();
    label_1:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case COLON:
        ;
        break;
      default:
        jj_la1[0] = jj_gen;
        break label_1;
      }
      jj_consume_token(COLON);
      temp = addExpr();
   //                            result=result.compound(temp);
    }
      {if (true) return result;}
    throw new Error("Missing return statement in function");
  }

  Unit<?> addExpr() throws ParseException {
        Unit<?> result = javax.measure.unit.Unit.ONE;
        Number n1 = null;
        Token sign1 = null;
        Number n2 = null;
        Token sign2 = null;
    if (jj_2_1(2147483647)) {
      n1 = numberExpr();
      sign1 = sign();
    } else {
      ;
    }
    result = mulExpr();
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case PLUS:
    case MINUS:
      sign2 = sign();
      n2 = numberExpr();
      break;
    default:
      jj_la1[1] = jj_gen;
      ;
    }
        if (n1 != null) {
            if (sign1.image.equals("-")) {
                result = result.multiply(-1);
            }
            result = result.add(n1.doubleValue());
        }
        if (n2 != null) {
            double offset = n2.doubleValue();
            if (sign2.image.equals("-")) {
                offset = -offset;
            }
            result = result.add(offset);
        }
        {if (true) return result;}
    throw new Error("Missing return statement in function");
  }

    javax.measure.unit.Unit mulExpr() throws ParseException {
        javax.measure.unit.Unit result = javax.measure.unit.Unit.ONE;
        javax.measure.unit.Unit temp = javax.measure.unit.Unit.ONE;
    result = exponentExpr();
    label_2:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case ASTERISK:
      case MIDDLE_DOT:
      case SOLIDUS:
        ;
        break;
      default:
        jj_la1[2] = jj_gen;
        break label_2;
      }
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case ASTERISK:
      case MIDDLE_DOT:
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case ASTERISK:
          jj_consume_token(ASTERISK);
          break;
        case MIDDLE_DOT:
          jj_consume_token(MIDDLE_DOT);
          break;
        default:
          jj_la1[3] = jj_gen;
          jj_consume_token(-1);
          throw new ParseException();
        }
        temp = exponentExpr();
                                                          result=result.multiply(temp);
        break;
      case SOLIDUS:
        jj_consume_token(SOLIDUS);
        temp = exponentExpr();
                                        result=result.divide(temp);
        break;
      default:
        jj_la1[4] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
    }
      {if (true) return result;}
    throw new Error("Missing return statement in function");
  }

    javax.measure.unit.Unit exponentExpr() throws ParseException {
        javax.measure.unit.Unit result = javax.measure.unit.Unit.ONE;
        javax.measure.unit.Unit temp = javax.measure.unit.Unit.ONE;
        Exponent exponent = null;
        Token token = null;
    if (jj_2_2(2147483647)) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case INTEGER:
        token = jj_consume_token(INTEGER);
        break;
      case E:
        token = jj_consume_token(E);
        break;
      default:
        jj_la1[5] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
      jj_consume_token(CARET);
      result = atomicExpr();
        double base;
        if (token.kind == INTEGER) {
            base = Integer.parseInt(token.image);
        } else {
            base = StrictMath.E;
        }
        {if (true) return result.transform(new javax.measure.unit.converter.LogConverter(base).inverse());}
    } else {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case OPEN_PAREN:
      case INTEGER:
      case FLOATING_POINT:
      case UNIT_IDENTIFIER:
        result = atomicExpr();
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case CARET:
        case SUPERSCRIPT_INTEGER:
          exponent = exp();
          break;
        default:
          jj_la1[6] = jj_gen;
          ;
        }
        if (exponent != null) {
            if (exponent.pow != 1) {
                result = result.pow(exponent.pow);
            }
            if (exponent.root != 1) {
                result = result.root(exponent.root);
            }
        }
        {if (true) return result;}
        break;
      case LOG:
      case NAT_LOG:
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case LOG:
          jj_consume_token(LOG);
          switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
          case INTEGER:
            token = jj_consume_token(INTEGER);
            break;
          default:
            jj_la1[7] = jj_gen;
            ;
          }
          break;
        case NAT_LOG:
          token = jj_consume_token(NAT_LOG);
          break;
        default:
          jj_la1[8] = jj_gen;
          jj_consume_token(-1);
          throw new ParseException();
        }
        jj_consume_token(OPEN_PAREN);
        result = addExpr();
        jj_consume_token(CLOSE_PAREN);
        double base = 10;
        if (token != null) {
            if (token.kind == INTEGER) {
                base = Integer.parseInt(token.image);
            } else if (token.kind == NAT_LOG) {
                base = StrictMath.E;
            }
        }
        {if (true) return result.transform(new LogConverter(base));}
        break;
      default:
        jj_la1[9] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
    }
    throw new Error("Missing return statement in function");
  }

  Unit<?> atomicExpr() throws ParseException {
        Unit<?> result = Unit.ONE;
        Unit<?> temp = Unit.ONE;
        Number n = null;
        Token token = null;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case INTEGER:
    case FLOATING_POINT:
      n = numberExpr();
        if (n instanceof Integer) {
            {if (true) return result.multiply(n.intValue());}
        } else {
            {if (true) return result.multiply(n.doubleValue());}
        }
      break;
    case UNIT_IDENTIFIER:
      token = jj_consume_token(UNIT_IDENTIFIER);
        javax.measure.unit.Unit unit = _symbols.getUnit(token.image);
        if (unit == null) {
            ParsePrefix prefix = _symbols.getPrefix(token.image);
            if (prefix != null) {
                String prefixSymbol = _symbols.getSymbol(prefix);
                unit = _symbols.getUnit(token.image.substring(prefixSymbol.length()));
                if (unit != null) {
                    {if (true) return unit.transform(prefix.getConverter());}
                }
            }
            {if (true) throw new ParseException();}
        } else {
            {if (true) return unit;}
        }
      break;
    case OPEN_PAREN:
      jj_consume_token(OPEN_PAREN);
      result = addExpr();
      jj_consume_token(CLOSE_PAREN);
        {if (true) return result;}
      break;
    default:
      jj_la1[10] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    throw new Error("Missing return statement in function");
  }

  Token sign() throws ParseException {
        Token result = null;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case PLUS:
      result = jj_consume_token(PLUS);
      break;
    case MINUS:
      result = jj_consume_token(MINUS);
      break;
    default:
      jj_la1[11] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
        {if (true) return result;}
    throw new Error("Missing return statement in function");
  }

  Number numberExpr() throws ParseException {
        Token token = null;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case INTEGER:
      token = jj_consume_token(INTEGER);
        {if (true) return Long.valueOf(token.image);}
      break;
    case FLOATING_POINT:
      token = jj_consume_token(FLOATING_POINT);
        {if (true) return Double.valueOf(token.image);}
      break;
    default:
      jj_la1[12] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    throw new Error("Missing return statement in function");
  }

  Exponent exp() throws ParseException {
        Token powSign = null;
        Token powToken = null;
        Token rootSign = null;
        Token rootToken = null;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case CARET:
      jj_consume_token(CARET);
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case PLUS:
      case MINUS:
      case INTEGER:
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case PLUS:
        case MINUS:
          powSign = sign();
          break;
        default:
          jj_la1[13] = jj_gen;
          ;
        }
        powToken = jj_consume_token(INTEGER);
            int pow = Integer.parseInt(powToken.image);
            if ((powSign != null) && powSign.image.equals("-")) {
                pow = -pow;
            }
            {if (true) return new Exponent(pow, 1);}
        break;
      case OPEN_PAREN:
        jj_consume_token(OPEN_PAREN);
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case PLUS:
        case MINUS:
          powSign = sign();
          break;
        default:
          jj_la1[14] = jj_gen;
          ;
        }
        powToken = jj_consume_token(INTEGER);
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case SOLIDUS:
          jj_consume_token(SOLIDUS);
          switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
          case PLUS:
          case MINUS:
            rootSign = sign();
            break;
          default:
            jj_la1[15] = jj_gen;
            ;
          }
          rootToken = jj_consume_token(INTEGER);
          break;
        default:
          jj_la1[16] = jj_gen;
          ;
        }
        jj_consume_token(CLOSE_PAREN);
            pow = Integer.parseInt(powToken.image);
            if ((powSign != null) && powSign.image.equals("-")) {
                pow = -pow;
            }
            int root = 1;
            if (rootToken != null) {
                root = Integer.parseInt(rootToken.image);
                if ((rootSign != null) && rootSign.image.equals("-")) {
                    root = -root;
                }
            }
            {if (true) return new Exponent(pow, root);}
      default:
        jj_la1[17] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
    case SUPERSCRIPT_INTEGER:
      powToken = jj_consume_token(SUPERSCRIPT_INTEGER);
        int pow = 0;
        for (int i = 0; i < powToken.image.length(); i += 1) {
            pow *= 10;
            switch (powToken.image.charAt(i)) {
                case '\u00b9': pow += 1; break;
                case '\u00b2': pow += 2; break;
                case '\u00b3': pow += 3; break;
                case '\u2074': pow += 4; break;
                case '\u2075': pow += 5; break;
                case '\u2076': pow += 6; break;
                case '\u2077': pow += 7; break;
                case '\u2078': pow += 8; break;
                case '\u2079': pow += 9; break;
            }
        }
        {if (true) return new Exponent(pow, 1);}
      break;
    default:
      jj_la1[18] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    throw new Error("Missing return statement in function");
  }

  private boolean jj_2_1(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_1(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(0, xla); }
  }

  private boolean jj_2_2(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_2(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(1, xla); }
  }

  private boolean jj_3R_3() {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3R_5()) {
    jj_scanpos = xsp;
    if (jj_3R_6()) return true;
    }
    return false;
  }

  private boolean jj_3R_6() {
    if (jj_scan_token(FLOATING_POINT)) return true;
    return false;
  }

  private boolean jj_3_2() {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_scan_token(14)) {
    jj_scanpos = xsp;
    if (jj_scan_token(19)) return true;
    }
    if (jj_scan_token(CARET)) return true;
    return false;
  }

  private boolean jj_3_1() {
    if (jj_3R_3()) return true;
    if (jj_3R_4()) return true;
    return false;
  }

  private boolean jj_3R_4() {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_scan_token(5)) {
    jj_scanpos = xsp;
    if (jj_scan_token(6)) return true;
    }
    return false;
  }

  private boolean jj_3R_5() {
    if (jj_scan_token(INTEGER)) return true;
    return false;
  }

  /** Generated Token Manager. */
  private UnitParserTokenManager token_source;
  private SimpleCharStream jj_input_stream;
  /** Current token. */
  private Token token;
  /** Next token. */
  private Token jj_nt;
  private int jj_ntk;
  private Token jj_scanpos, jj_lastpos;
  private int jj_la;
  private int jj_gen;
  final private int[] jj_la1 = new int[19];
  static private int[] jj_la1_0;
  static {
      jj_la1_init_0();
   }
   private static void jj_la1_init_0() {
      jj_la1_0 = new int[] {0x800,0x60,0x380,0x180,0x380,0x84000,0x8400,0x4000,0x60000,0x175000,0x115000,0x60,0x14000,0x60,0x60,0x60,0x200,0x5060,0x8400,};
   }
  final private JJCalls[] jj_2_rtns = new JJCalls[2];
  private boolean jj_rescan = false;
  private int jj_gc = 0;

  /** Constructor with InputStream. */
  UnitParser(java.io.InputStream stream) {
     this(stream, null);
  }
  /** Constructor with InputStream and supplied encoding */
  UnitParser(java.io.InputStream stream, String encoding) {
    try { jj_input_stream = new SimpleCharStream(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
    token_source = new UnitParserTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 19; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Reinitialise. */
  private void reInit(java.io.InputStream stream) {
     reInit(stream, null);
  }
  /** Reinitialise. */
  private void reInit(java.io.InputStream stream, String encoding) {
    try { jj_input_stream.ReInit(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
    token_source.reInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 19; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Constructor. */
  UnitParser(java.io.Reader stream) {
    jj_input_stream = new SimpleCharStream(stream, 1, 1);
    token_source = new UnitParserTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 19; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Reinitialise. */
  private void reInit(java.io.Reader stream) {
    jj_input_stream.ReInit(stream, 1, 1);
    token_source.reInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 19; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Constructor with generated Token Manager. */
  UnitParser(UnitParserTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 19; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Reinitialise. */
  private void reInit(UnitParserTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 19; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  private Token jj_consume_token(int kind) throws ParseException {
    Token oldToken;
    if ((oldToken = token).next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    if (token.kind == kind) {
      jj_gen++;
      if (++jj_gc > 100) {
        jj_gc = 0;
        for (int i = 0; i < jj_2_rtns.length; i++) {
          JJCalls c = jj_2_rtns[i];
          while (c != null) {
            if (c.gen < jj_gen) c.first = null;
            c = c.next;
          }
        }
      }
      return token;
    }
    token = oldToken;
    jj_kind = kind;
    throw generateParseException();
  }

  static private final class LookaheadSuccess extends java.lang.Error {

  /**
   *
   */
  private static final long serialVersionUID = -8192240240676284081L; }
  final private LookaheadSuccess jj_ls = new LookaheadSuccess();
  private boolean jj_scan_token(int kind) {
    if (jj_scanpos == jj_lastpos) {
      jj_la--;
      if (jj_scanpos.next == null) {
        jj_lastpos = jj_scanpos = jj_scanpos.next = token_source.getNextToken();
      } else {
        jj_lastpos = jj_scanpos = jj_scanpos.next;
      }
    } else {
      jj_scanpos = jj_scanpos.next;
    }
    if (jj_rescan) {
      int i = 0; Token tok = token;
      while (tok != null && tok != jj_scanpos) { i++; tok = tok.next; }
      if (tok != null) jj_add_error_token(kind, i);
    }
    if (jj_scanpos.kind != kind) return true;
    if (jj_la == 0 && jj_scanpos == jj_lastpos) throw jj_ls;
    return false;
  }


/** Get the next Token. */
  final Token getNextToken() {
    if (token.next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    jj_gen++;
    return token;
  }

/** Get the specific Token. */
  final Token getToken(int index) {
    Token t = token;
    for (int i = 0; i < index; i++) {
      if (t.next != null) t = t.next;
      else t = t.next = token_source.getNextToken();
    }
    return t;
  }

  private int jj_ntk() {
    if ((jj_nt=token.next) == null)
      return (jj_ntk = (token.next=token_source.getNextToken()).kind);
    else
      return (jj_ntk = jj_nt.kind);
  }

  private java.util.List<int[]> jj_expentries = new java.util.ArrayList<int[]>();
  private int[] jj_expentry;
  private int jj_kind = -1;
  private int[] jj_lasttokens = new int[100];
  private int jj_endpos;

  private void jj_add_error_token(int kind, int pos) {
    if (pos >= 100) return;
    if (pos == jj_endpos + 1) {
      jj_lasttokens[jj_endpos++] = kind;
    } else if (jj_endpos != 0) {
      jj_expentry = new int[jj_endpos];
      for (int i = 0; i < jj_endpos; i++) {
        jj_expentry[i] = jj_lasttokens[i];
      }
      jj_entries_loop: for (java.util.Iterator<?> it = jj_expentries.iterator(); it.hasNext();) {
        int[] oldentry = (int[])(it.next());
        if (oldentry.length == jj_expentry.length) {
          for (int i = 0; i < jj_expentry.length; i++) {
            if (oldentry[i] != jj_expentry[i]) {
              continue jj_entries_loop;
            }
          }
          jj_expentries.add(jj_expentry);
          break jj_entries_loop;
        }
      }
      if (pos != 0) jj_lasttokens[(jj_endpos = pos) - 1] = kind;
    }
  }

  /** Generate ParseException. */
  ParseException generateParseException() {
    jj_expentries.clear();
    boolean[] la1tokens = new boolean[21];
    if (jj_kind >= 0) {
      la1tokens[jj_kind] = true;
      jj_kind = -1;
    }
    for (int i = 0; i < 19; i++) {
      if (jj_la1[i] == jj_gen) {
        for (int j = 0; j < 32; j++) {
          if ((jj_la1_0[i] & (1<<j)) != 0) {
            la1tokens[j] = true;
          }
        }
      }
    }
    for (int i = 0; i < 21; i++) {
      if (la1tokens[i]) {
        jj_expentry = new int[1];
        jj_expentry[0] = i;
        jj_expentries.add(jj_expentry);
      }
    }
    jj_endpos = 0;
    jj_rescan_token();
    jj_add_error_token(0, 0);
    int[][] exptokseq = new int[jj_expentries.size()][];
    for (int i = 0; i < jj_expentries.size(); i++) {
      exptokseq[i] = jj_expentries.get(i);
    }
    return new ParseException(token, exptokseq, tokenImage);
  }

  /** Enable tracing. */
  final void enable_tracing() {
  }

  /** Disable tracing. */
  final void disable_tracing() {
  }

  private void jj_rescan_token() {
    jj_rescan = true;
    for (int i = 0; i < 2; i++) {
    try {
      JJCalls p = jj_2_rtns[i];
      do {
        if (p.gen > jj_gen) {
          jj_la = p.arg; jj_lastpos = jj_scanpos = p.first;
          switch (i) {
            case 0: jj_3_1(); break;
            case 1: jj_3_2(); break;
          }
        }
        p = p.next;
      } while (p != null);
      } catch(LookaheadSuccess ls) { }
    }
    jj_rescan = false;
  }

  private void jj_save(int index, int xla) {
    JJCalls p = jj_2_rtns[index];
    while (p.gen > jj_gen) {
      if (p.next == null) { p = p.next = new JJCalls(); break; }
      p = p.next;
    }
    p.gen = jj_gen + xla - jj_la; p.first = token; p.arg = xla;
  }

  static final class JJCalls {
    int gen;
    Token first;
    int arg;
    JJCalls next;
  }

}
