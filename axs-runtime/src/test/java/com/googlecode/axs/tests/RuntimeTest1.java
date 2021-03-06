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

package com.googlecode.axs.tests;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.googlecode.axs.AbstractAnnotatedHandler;
import com.googlecode.axs.XPath;
import com.googlecode.axs.XPathEnd;
import com.googlecode.axs.XPathNamespaces;
import com.googlecode.axs.XPathStart;

@XPathNamespaces({ "ns1=http://test.values/tests" })
public class RuntimeTest1 extends AbstractAnnotatedHandler {
	private int mNrBElements = 0;
	private int mNrDElements = 0;
	private int mNrEElements = 0;
	
	public int nrBElements() { return mNrBElements; }
	public int nrDElements() { return mNrDElements; }
	public int nrEElements() { return mNrEElements; }
	
	@XPath("b")
	public void bElement(String text) {
		if (!text.equals("testing text ")) {
			System.out.println("[FAIL] found a <b> element containing \"" + text + "\"");
		}
		mNrBElements++;
	}
	
	@XPathStart("d")
	public void dElementStart(Attributes attrs) {
		if (!"true".equals(attrs.getValue("attr3"))) {
			System.out.println("[FAIL] <d> element does not have attr3=\"true\"");
		} else {
			System.out.println("[OK] <d> element has attr3=\"true\"");
		}
		mNrDElements++;
	}
	
	@XPathStart("e")
	public void eElementStart(Attributes attrs) {
		mNrEElements++;
	}

	@XPathEnd("e")
	public void eElementEnd() {
		mNrEElements++;
	}
	
	private void checkText(String text, int n) {
		if (!text.equals("This is some testing text " + n + "!")) {
			System.out.println("[FAIL] Looked for text " + n + ", got \"" + text + "\"");
		} else {
			System.out.println("[OK] Found testing text " + n);
		}
	}
	
	@XPath("//tests/test[position() < 4]/a")
	public void testAElement(String text) {
		if (!text.startsWith("This is some testing text ")) {
			System.out.println("[FAIL] Looked for text, got \"" + text + "\"");
		}
	}
	
	@XPath("/tests/test[2]/a[@attr1 = 'value1' and attribute::value2 != 'value3'][1]")
	public void testAElementAttrs2(String text) {
		checkText(text, 2);
	}

	@XPath("//a[attribute::attr2 = 'value3' and not(@ns1:attrNotFound = 'foo''bar')]")
	public void testAElementAttrs3(String text) {
		checkText(text, 3);
	}

	@XPath("/tests/test[2]/a[starts-with(@attr1,'value') and attribute::attr2 = 'value2' and position() = 3]")
	public void testAElementAttrs4(String text) {
		checkText(text, 4);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		
		if (! (new File(args[0] + "/testData1.xml")).exists()) {
			System.out.println("Usage: RuntimeTest1 path/to/testData");
			System.exit(1);
		}
		
		try {
			SAXParser parser = factory.newSAXParser();
			
			String testDataRoot = args[0];
			RuntimeTest1 test1 = new RuntimeTest1();
	
			parser.parse(testDataRoot + "/testData1.xml", test1);
			
			if (test1.nrBElements() == 6) {
				System.out.println("[OK] Found 6 <b> elements");
			} else {
				System.out.println("[FAIL] Found " + test1.nrBElements() + " <b> elements?!");
			}
			
			if (test1.nrDElements() == 1) {
				System.out.println("[OK] Found 1 <d> element");
			} else {
				System.out.println("[FAIL] Found " + test1.nrDElements() + " <d> elements?!");
			}
	
			if (test1.nrEElements() == 0) {
				System.out.println("[OK] Found 0 <e> elements");
			} else {
				System.out.println("[FAIL] Found " + test1.nrEElements() + " <e> elements?!");
			}
		} catch (SAXException e) {
			System.out.println("[FAIL] Got a SAXException: "+ e);
		} catch (IOException e) {
			System.out.println("Usage: RuntimeTest1 path/to/testData");
		} catch (ParserConfigurationException e) {
			System.out.println("[FAIL] Got a ParserConfigurationException: " + e);
		}
	}
}
