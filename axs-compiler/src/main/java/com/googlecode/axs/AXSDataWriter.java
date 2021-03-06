/*
 * This file is part of AXS, Annotation-XPath for SAX.
 * 
 * Copyright (c) 2013 Benjamin K. Stuhl
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.googlecode.axs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

/**
 * This class provides the static method which supports the writing of
 * _AXSData classes.
 * @author Ben
 *
 */
class AXSDataWriter {
	private AXSDataWriter() {
	}
	
	private static boolean sWriteGeneratedAnnotation = true;
	
	public static void setUseGeneratedAnnotation(boolean write) {
		sWriteGeneratedAnnotation = write;
	}
	
	/**
	 * Write out the compiled _AXSData as Java source code to the given writer
	 * @param w the writer to which the code should be written.
	 * @param axsData the _AXSData to write
	 */
	public static void writeAXSData(Writer w, CompiledAXSData axsData) throws IOException {
		// we make no attempt to reduce the number of times a given string
		// is written out, since javac will unique-ify repeated string literals
		// anyways
		
		// we write out all the big arrays as delay-initialized to first use,
		// to avoid having the dynamic initialization suck start-up time
		
		int nrTexts = axsData.numberOfXPathMethods();
		int nrEnds = axsData.numberOfXPathEndMethods();
		int nrStarts = axsData.numberOfXPathStartMethods();
		Vector<CompiledAXSData.Method> methods = axsData.methods();
		
		writeHeader(w, axsData);
		writeCallFunction(w, axsData.className(), "callXPathText", "String", methods.subList(0, nrTexts));
		writeCallFunction(w, axsData.className(), "callXPathEnd", null, methods.subList(nrTexts, nrTexts+nrEnds));
		writeCallFunction(w, axsData.className(), "callXPathStart", "Attributes", methods.subList(nrTexts+nrEnds, nrTexts+nrEnds+nrStarts));
		writeInt(w, "getAXSDataVersion", AbstractAnnotatedHandler.AXSDATA_VERSION);
		writeInt(w, "getNumberOfCapturingExpressions", nrTexts);
		writeInt(w, "getNumberOfEndExpressions", nrEnds);
		writeInt(w, "getMaximumPredicateStackDepth", axsData.maximumPredicateStackDepth());
		writeTriggerTags(w, axsData.triggers());
		writeSet(w, "AttributeCaptureTags", "getAttributeCaptureTags", axsData.attributeCaptureTags());
		writeSet(w, "PositionCaptureTags", "getPositionCaptureTags", axsData.positionCaptureTags());
		writeExpressions(w, methods, axsData.instructions(), axsData.literals(), axsData.qNames());
		writeFooter(w);
	}
	
	/**
	 * Add a single import statement to the file.
	 * @param w
	 * @param className the fully-qualified class name
	 * @throws IOException
	 */
	private static void writeImport(Writer w, String className) throws IOException {
		w.write("import ");
		w.write(className);
		w.write(";\n");
	}

	/**
	 * Indent by @p n spaces
	 * @param w
	 * @param n
	 * @throws IOException
	 */
	private static void indent(Writer w, int n) throws IOException {
		while (n-- > 0)
			w.write(' ');
	}

	/**
	 * Write the all-stars comment line of appropriate length for the license block
	 * @param w
	 * @param maxLineLength
	 */
	private static void writeLicenseStarLine(Writer w, int maxLineLength) throws IOException {
		w.write("/* ");
		for (int i = 0; i < maxLineLength + 1; i++)
			w.write('*');
		w.write("*/\n");
	}
	
	/**
	 * Writes the MIT license header as a nice block header.
	 * @param w
	 * @throws IOException
	 */
	private static void writeLicense(Writer w) throws IOException {
		ClassLoader ourLoader = AXSDataWriter.class.getClassLoader();
		InputStream licenseStream = ourLoader.getResourceAsStream("resources/LICENSE");
		BufferedReader licenseReader = new BufferedReader(new InputStreamReader(licenseStream));
		
		int maxLineLength = 0;
		Vector<String> lines = new Vector<String>();
		while (true) {
			String line = licenseReader.readLine();
			
			if (line == null)
				break;
			if (line.length() > maxLineLength)
				maxLineLength = line.length();
			lines.add(line);
		}

		writeLicenseStarLine(w, maxLineLength);
		String lineFormat = "%-" + maxLineLength + "s";
		for (String line : lines) {
			w.write("/* ");
			w.write(String.format(lineFormat, line));
			w.write(" */\n");
		}
		writeLicenseStarLine(w, maxLineLength);
	}

	private static void writeHeader(Writer w, CompiledAXSData axsData) throws IOException {
		w.write("/* This class is autogenerated by the AXS compiler. Do not edit! */\n\n");
		writeLicense(w);
		w.write("\npackage ");
		w.write(axsData.packageName());
		w.write(";\n\n");
		writeImport(w, "java.util.HashMap");
		writeImport(w, "java.util.HashSet");
		writeImport(w, "java.util.Map");
		writeImport(w, "java.util.Set");
		if (sWriteGeneratedAnnotation) {
			writeImport(w, "javax.annotation.Generated");
		}
		writeImport(w, "org.xml.sax.Attributes");
		writeImport(w, "org.xml.sax.SAXException");
		w.write("\n");
		writeImport(w, "com.googlecode.axs.AXSData");
		writeImport(w, "com.googlecode.axs.AbstractAnnotatedHandler");
		writeImport(w, "com.googlecode.axs.HandlerCallError");
		writeImport(w, "com.googlecode.axs.QName");
		writeImport(w, "com.googlecode.axs.XPathExpression");
		w.write("\n");
		writeImport(w, axsData.packageName() + "." + axsData.className());
		w.write("\n");
		if (sWriteGeneratedAnnotation) {
			w.write("\n@Generated(value = { \"com.googlecode.axs.AnnotationProcessor\", \"");
			w.write(axsData.packageName() + "." + axsData.className() + "\"})\n");
		}
		w.write("public class "); w.write(axsData.className() + "_AXSData"); w.write(" implements AXSData {\n");
		indent(w, 4); w.write("private static Object Lock = new Object();\n\n");
	}
	
	private static void writeCallFunction(Writer w, String className, String fnName,
			String argType, List<CompiledAXSData.Method> methods) throws IOException {
		indent(w, 4); w.write("@Override\n");
		indent(w, 4); w.write("public void ");
		w.write(fnName);
		w.write("(AbstractAnnotatedHandler abstractHandler, int exprIx");
		if (argType != null) {
			w.write(", ");
			w.write(argType);
			w.write(" callbackArg");
		}
		w.write(") throws SAXException {\n");
		indent(w, 8); w.write(className + " handler = (" + className + ") abstractHandler;\n\n");
		indent(w, 8); w.write("switch (exprIx) {\n");
		
		for (int i = 0, len = methods.size(); i < len; i++) {
			indent(w, 8); w.write("case " + methods.get(i).index() + ": \n");
			indent(w, 12); w.write("// \"" + methods.get(i).expression() + "\"\n");
			indent(w, 12); w.write("handler." + methods.get(i).name() + "(");
			if (argType != null)
				w.write("callbackArg");
			w.write(");\n");
			indent(w, 12); w.write("break;\n");
		}
		
		indent(w, 8); w.write("default: throw new HandlerCallError(\"unhandled call #\" + exprIx);\n");
		indent(w, 8); w.write("}\n");
		indent(w, 4); w.write("}\n\n");
	}
	
	private static void writeInt(Writer w, String getterName, int value) throws IOException {
		indent(w, 4); w.write("@Override\n");
		indent(w, 4); w.write("public int " + getterName + "() {\n");
		indent(w, 8); w.write("return " + value + ";\n");
		indent(w, 4); w.write("}\n\n");
	}
	
	private static void writeTriggerTags(Writer w, Map<String, Vector<Integer>> triggers) throws IOException {
		indent(w, 4); w.write("private static HashMap<String, int[]> Triggers = null;\n\n");
		indent(w, 4); w.write("@Override\n");
		indent(w, 4); w.write("public Map<String, int[]> getTriggerTags() {\n");
		indent(w, 8); w.write("synchronized (Lock) {\n");
		indent(w, 12); w.write("if (Triggers != null)\n");
		indent(w, 16); w.write("return Triggers;\n");
		indent(w, 12); w.write("Triggers = new HashMap<String, int[]>();\n");
		
		for (Map.Entry<String, Vector<Integer>> trigger : triggers.entrySet()) {
			indent(w, 12); w.write("Triggers.put(\"" + trigger.getKey());
			w.write("\", \n");
			indent(w, 20); w.write("new int[] { ");
			for (int ix : trigger.getValue()) {
				w.write(String.valueOf(ix));
				w.write(", ");
			}
			w.write("});\n");
		}
		indent(w, 8); w.write("}\n");
		indent(w, 8); w.write("return Triggers;\n");
		indent(w, 4); w.write("}\n\n");
	}
	
	private static void writeSet(Writer w, String setName, String getterName, Set<String> set) throws IOException {
		indent(w, 4); w.write("private static HashSet<String> " + setName + " = null;\n\n");
		indent(w, 4); w.write("@Override\n");
		indent(w, 4); w.write("public Set<String> " + getterName + "() {\n");
		indent(w, 8); w.write("synchronized (Lock) {\n");
		indent(w, 12); w.write("if (" + setName + " != null)\n");
		indent(w, 16); w.write("return " + setName + ";\n\n");
		indent(w, 12); w.write(setName + " = new HashSet<String>();\n");
		for (String s : set) {
			indent(w, 12); w.write(setName + ".add(\"" + s + "\");\n");
		}
		indent(w, 8); w.write("}\n");
		indent(w, 8); w.write("return " + setName + ";\n");
		indent(w, 4); w.write("}\n\n");
	}
	
	// these two arrays MUST match the encoding used in XPathExpression
	// they are package-scope, since they're needed in CompiledAXSData as well
	// how many instruction slots a given instruction occupies
	static final int[] InstructionLengths = {
		1,
		1, 2, 1, 2, 2,
		1, 1, 2, 1, 1,
		1, 1, 1, 2, 1,
		1, 1, 1, 1, 1,
		1, 1, 2
	};
	
	// the XPathExpression.* names for each token value
	static final String[] InstructionNames = {
		"(zero)",
		"INSTR_ROOT",
		"INSTR_ELEMENT",
		"INSTR_CONTAINS",
		"INSTR_ATTRIBUTE",
		"INSTR_LITERAL",
		"INSTR_EQ_STR",
		"INSTR_NOT",
		"INSTR_ILITERAL",
		"INSTR_AND",
		"INSTR_OR",
		"INSTR_TEST_PREDICATE",
		"INSTR_ENDS_WITH",
		"INSTR_STARTS_WITH",
		"INSTR_NONCONSECUTIVE_ELEMENT",
		"INSTR_POSITION",
		"INSTR_LT",
		"INSTR_GT",
		"INSTR_EQ",
		"INSTR_NE",
		"INSTR_LE",
		"INSTR_GE",
		"INSTR_MATCHES",
		"INSTR_SOFT_TEST_PREDICATE"
	};
	
	static final int NONE = 0;
	static final int STRING = 1;
	static final int QNAME = 2;
	static final int INTEGER = 3;
	static final int InstructionArguments[] = {
		NONE,
		NONE,
		QNAME,
		NONE,
		QNAME,
		STRING,
		NONE,
		NONE,
		INTEGER,
		NONE,
		NONE,
		NONE,
		NONE,
		NONE,
		QNAME,
		NONE,
		NONE,
		NONE,
		NONE,
		NONE,
		NONE,
		NONE,
		NONE,
		INTEGER
	};
	
	private static void writeInstructionArray(Writer w, int indentSpaces, ShortVector instrs) throws IOException {
		final int in = indentSpaces;
		w.write("new short[] {\n");

		for (int i = 0, len = instrs.size(); i < len; i++) {
			short instr = instrs.get(i);
			
			indent(w, in+4); w.write("XPathExpression."); w.write(InstructionNames[instr]); w.write(",");
			
			if (InstructionLengths[instr] == 2) {
				i++;
				w.write(" ");
				w.write(String.valueOf(instrs.get(i)));
				w.write(",");
			}
			w.write("\n");
		}
		
		indent(w, in); w.write("}");
	}
	
	private static void writeStringArray(Writer w, int indentSpaces, Vector<String> literals) throws IOException {
		final int in = indentSpaces;
		
		w.write("new String[] {\n");
		for (int i = 0, len = literals.size(); i < len; i++) {
			indent(w, in+4);
			w.write('"');
			w.write(literals.get(i).replace("\"", "\\\""));
			w.write("\",\n");
		}
		indent(w, in); w.write("}");
	}
	
	private static void writeQNameArray(Writer w, int indentSpaces, Vector<QName> qNames) throws IOException {
		final int in = indentSpaces;
		
		w.write("new QName[] {\n");
		for (int i = 0, len = qNames.size(); i < len; i++) {
			QName qn = qNames.get(i);
			indent(w, in+4);
			w.write("new QName(\"");
			w.write(qn.getNamespaceURI().replace("\"", "\\\""));
			w.write("\", \"");
			w.write(qn.getLocalPart().replace("\"", "\\\""));
			w.write("\"),\n");
		}
		indent(w, in); w.write("}");
	}

	private static void writeExpressions(Writer w, Vector<CompiledAXSData.Method> methods, Vector<ShortVector> instructions, Vector<String> literals, Vector<QName> qNames) throws IOException {
		indent(w, 4); w.write("private static String[] Literals = ");
		writeStringArray(w, 4, literals);
		w.write(";\n\n");
		indent(w, 4); w.write("private static QName[] QNames = ");
		writeQNameArray(w, 4, qNames);
		w.write(";\n\n");
		indent(w, 4); w.write("private static XPathExpression[] Expressions = new XPathExpression[] {\n");
		
		for (int i = 0, len = instructions.size(); i < len; i++) {
			indent(w, 8); w.write("new XPathExpression(");
			w.write(" // \"");
			w.write(methods.get(i).expression());
			w.write("\"\n");
			indent(w, 8); writeInstructionArray(w, 8, instructions.get(i));
			w.write(", QNames, Literals),\n");
		}
		indent(w, 4); w.write("};\n\n");

		indent(w, 4); w.write("public XPathExpression[] getXPathExpressions() {\n        return Expressions;\n    }\n");
	}
	
	private static void writeFooter(Writer w) throws IOException {
		w.write("}\n");
	}
}
