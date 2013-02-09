package com.googlecode.axs;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic.Kind;

/**
 * This class collects all the annotated methods found within a single subclass
 * of AbstractAnnotatedHandler, so that they can be combined into a single _AXSData
 * file.
 * @author Ben
 *
 */
class AnnotatedClass {
	// the class that this object is annotating
	private TypeElement mClassElement;
	private Messager mMessager;

	// maps of method names to XPath expressions
	private HashMap<String, String> mXPathMethods = new HashMap<String, String>();
	private HashMap<String, String> mXPathEndMethods = new HashMap<String, String>();
	private HashMap<String, String> mXPathStartMethods = new HashMap<String, String>();
	
	// map of prefixes to Namespace URIs
	private HashMap<String, String> mPrefixMap = new HashMap<String, String>();
	
	public AnnotatedClass(Messager messager, TypeElement clazz) {
		mMessager = messager;
		mClassElement = clazz;
	}

	public void addMethodAnnotation(Element methodElement, TypeElement annotationElement) {
		final String aType = annotationElement.getSimpleName().toString();
		final String method = methodElement.getSimpleName().toString();
		
		if ("XPath".equals(aType)) {
			XPath xp = methodElement.getAnnotation(XPath.class);
			mXPathMethods.put(method, xp.value());
		} else if ("XPathStart".equals(aType)) {
			XPathStart xp = methodElement.getAnnotation(XPathStart.class);
			mXPathStartMethods.put(method, xp.value());
		} else if ("XPathEnd".equals(aType)) {
			XPathEnd xp = methodElement.getAnnotation(XPathEnd.class);
			mXPathEndMethods.put(method, xp.value());
		} else {
			mMessager.printMessage(Kind.ERROR, 
					"Cannot apply annotation " + aType + " to element of type " + methodElement.getKind(), methodElement);
		}
	}
	
	public void addClassAnnotation(TypeElement annotationElement) {
		final Name aType = annotationElement.getSimpleName();
		
		if (!"XPathNamespaces".equals(aType)) {
			mMessager.printMessage(Kind.ERROR, "Cannot apply annotation " + aType, mClassElement);
			return;
		}
		XPathNamespaces ns = mClassElement.getAnnotation(XPathNamespaces.class);
		String[] prefixes = ns.value();
		
		for (String p : prefixes) {
			int eqPos = p.indexOf('=');
			
			if (eqPos < 0) {
				mMessager.printMessage(Kind.ERROR, "Namespaces must be of the form \"prefix=URI\"", mClassElement);
				continue;
			}
			String prefix = p.substring(0, eqPos);
			String uri = p.substring(eqPos+1);
			mPrefixMap.put(prefix, uri);
		}
	}

	public Name className() {
		return mClassElement.getQualifiedName();
	}
	
	public TypeElement classElement() {
		return mClassElement;
	}
	
	public Map<String, String> xPathMethods() {
		return mXPathMethods;
	}
	
	public Map<String, String> xPathEndMethods() {
		return mXPathEndMethods;
	}
	
	public Map<String, String> xPathStartMethods() {
		return mXPathStartMethods;
	}
	
	public Map<String, String> prefixMap() {
		return mPrefixMap;
	}
}