/* Generated By:JJTree&JavaCC: Do not edit this line. ParserConstants.java */
package com.googlecode.axs.xpath;


/**
 * Token literal values and constants.
 * Generated by org.javacc.parser.OtherFilesGen#start()
 */
public interface ParserConstants {

  /** End of File. */
  int EOF = 0;
  /** RegularExpression Id. */
  int COMMENT = 6;
  /** RegularExpression Id. */
  int INTEGER_LITERAL = 8;
  /** RegularExpression Id. */
  int DQUOT_LITERAL = 9;
  /** RegularExpression Id. */
  int SQUOT_LITERAL = 10;
  /** RegularExpression Id. */
  int EQ = 11;
  /** RegularExpression Id. */
  int NE = 12;
  /** RegularExpression Id. */
  int LT = 13;
  /** RegularExpression Id. */
  int GT = 14;
  /** RegularExpression Id. */
  int LE = 15;
  /** RegularExpression Id. */
  int GE = 16;
  /** RegularExpression Id. */
  int AND = 17;
  /** RegularExpression Id. */
  int OR = 18;
  /** RegularExpression Id. */
  int NOT = 19;
  /** RegularExpression Id. */
  int CONTAINS = 20;
  /** RegularExpression Id. */
  int STARTS_WITH = 21;
  /** RegularExpression Id. */
  int ENDS_WITH = 22;
  /** RegularExpression Id. */
  int MATCHES = 23;
  /** RegularExpression Id. */
  int POSITION = 24;
  /** RegularExpression Id. */
  int CAPTUREATTRS = 25;
  /** RegularExpression Id. */
  int NAME = 26;
  /** RegularExpression Id. */
  int NAME_START_CHAR = 27;
  /** RegularExpression Id. */
  int NAME_CHAR = 28;

  /** Lexical state. */
  int DEFAULT = 0;
  /** Lexical state. */
  int IN_COMMENT = 1;

  /** Literal token values. */
  String[] tokenImage = {
    "<EOF>",
    "\" \"",
    "\"\\t\"",
    "\"\\r\"",
    "\"\\n\"",
    "\"(:\"",
    "\"*)\"",
    "<token of kind 7>",
    "<INTEGER_LITERAL>",
    "<DQUOT_LITERAL>",
    "<SQUOT_LITERAL>",
    "\"=\"",
    "\"!=\"",
    "\"<\"",
    "\">\"",
    "\"<=\"",
    "\">=\"",
    "\"and\"",
    "\"or\"",
    "\"not\"",
    "\"contains\"",
    "\"starts-with\"",
    "\"ends-with\"",
    "\"matches\"",
    "\"position\"",
    "\"captureattrs\"",
    "<NAME>",
    "<NAME_START_CHAR>",
    "<NAME_CHAR>",
    "\"|\"",
    "\"/\"",
    "\"//\"",
    "\"[\"",
    "\"]\"",
    "\"(\"",
    "\")\"",
    "\",\"",
    "\"@\"",
  };

}
