/*
 * ClassInfoDumper.java
 * 
 * Created by: T.Takakiyo
 * $Id: ClassInfoDumper.java,v 1.2 2005/02/16 04:45:57 takakiyo Exp $
 */
package com.ibm.jp.support.classinfo;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ibm.jp.util.ClassCache;
import com.ibm.jp.util.Html;

/**
 * @author T.Takakiyo
 */
public class ClassInfoDumper extends HttpServlet {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 8399602067925688359L;

	public void service(HttpServletRequest req, HttpServletResponse resp)
        throws IOException, ServletException
    {
    	req.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html; charset=UTF-8");
        PrintWriter out = resp.getWriter();

        String param = req.getParameter("class");
        if (param == null) param = "";

        out.println("<html>");
        out.println("<head>");
        out.println("<title>Class Infomation Dumper</title>");
        out.println("<META http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />");
        out.println(
			"<style type=\"text/css\">\n" +
			"<!--\n" +
			"a {\n" +
//			"\tcursor: pointer;\n" +
//			"\tfont-style: normal;\n" +
			"\ttext-decoration: none\n" +
			"}\n" +
			"-->\n" +
			"</style>\n" +
			"</head>\n" +
            "<body text=\"#000000\" link=\"#0000ff\" vlink=\"#0000ff\">\n" +
            "<h1>Class Infomation Dumper</h1>\n" +
            "<form method=\"GET\" action=\"" + req.getRequestURI() + "\">\n" +
            "Class Name <input type=\"text\" name=\"class\" size=24 value=\"" +
            Html.escapeChar(param) +
            "\">\n" +
            "</form>");
        
        if (param.equals("")) {
            out.println("</body></html>");
            return;
        }
        
        Class<?> targetClass;
        try {
            targetClass = Class.forName(param);
        } catch (ClassNotFoundException ex) {
        	targetClass = ClassCache.get(param);
        	if (targetClass == null) {
        		out.println("Class not found: " + Html.escapeChar(param) + "\n" +
        				"</body></html>");
        		return;
        	}
        } catch (Exception ex) {
        	out.print("<pre>");
			out.print(Html.escapeStackTrace(ex));
			out.println("</pre></body></html>");
        	return;
        }
        
        printClassInfo(out, targetClass, req.getRequestURI());
        out.println("</body></html>");
    }
    
    public static void main(String[] args) throws Exception {
        Class<?> targetClass = Class.forName(args[0]);
        PrintWriter out = new PrintWriter(System.out);
		out.println("<html>\n" +
			"<head>\n<title>Class Infomation Dumper</title>\n" +
			"<style type=\"text/css\">\n" +
			"<!--\n" +
			"a {\n" +
			"\ttext-decoration: none\n" +
			"}\n" +
			"-->\n" +
			"</style>\n" +
			"</head>\n" +
			"<body text=\"#000000\" link=\"#0000ff\" vlink=\"#0000ff\">\n" +
			"<h1>Class Infomation Dumper</h1>\n");
        printClassInfo(out, targetClass, null);
        out.print("</body></html>");
    }
    
    private static Comparator<Class<?>> classComparator = new Comparator<Class<?>>() {
		public int compare(Class<?> c1, Class<?> c2) {
			return c1.getName().compareTo(c2.getName());
		}
    };
    
    private static Comparator<Method> methodComparator = new Comparator<Method>() {
		public int compare(Method m1, Method m2) {
			if (m1.equals(m2)) return 0;
			int ret;
			ret = m1.getName().compareTo(m2.getName());
			if (ret != 0) return ret;
			Class<?>[] params1 = m1.getParameterTypes();
			Class<?>[] params2 = m2.getParameterTypes();
			for (int i = 0; ; i++) {
				if (params1.length <= i) return -1;
				if (params2.length <= i) return 1;
				ret = params1[i].getName().compareTo(params2[i].getName());
				if (ret != 0) return ret;
			}
		}
    };
    
    private static Comparator<Field> fieldComparator = new Comparator<Field>() {
		public int compare(Field f1, Field f2) {
			if (f1.equals(f2)) return 0;
			return f1.getName().compareTo(f2.getName());
		}
    };
    
    private static Comparator<Constructor<?>> constructorComparator = new Comparator<Constructor<?>>() {
    	public int compare(Constructor<?> c1, Constructor<?> c2) {
			if (c1.equals(c2)) return 0;
			Class<?>[] params1 = c1.getParameterTypes();
			Class<?>[] params2 = c2.getParameterTypes();
			for (int i = 0; ; i++) {
				if (params1.length <= i) return -1;
				if (params2.length <= i) return 1;
				int ret = params1[i].getName().compareTo(params2[i].getName());
				if (ret != 0) return ret;
			}
    	}
    };
    
    private static void printClassInfo(PrintWriter out, Class<?> target, String url) {
    	StringBuilder sb = new StringBuilder();
    	
        sb.append("<hr>");

        // General Info

    	sb.append("<h2><font size=\"-1\">");
    	sb.append(getPackageName(target));
     	sb.append("</font><br>\n");
     	if (target.isEnum()) {
     		sb.append("Enum ");
     	} else if (target.isAnnotation()) {
     		sb.append("Annotation Type ");
     	} else if (target.isInterface()) {
     		sb.append("Interface ");
     	} else {
         	sb.append("Class ");
     	}
     	sb.append(getShortName(target));
     	sb.append("</h2>\n");

        List<Class<?>> supers = new ArrayList<Class<?>>();
        for (Class<?> c = target; c != null; c = c.getSuperclass()) {
        	supers.add(c);
        }
//        for (Class<?> c = target; c != null; ) {
//        	supers.add(c);
//        	Type t = c.getGenericSuperclass();
//        	System.out.println(t + ": (" + t.getClass().getName() + ")");
//        	if (t != null && t instanceof Class) { 
//        		c = (Class<?>)t;
//        	} else {
//        		c = null;
//        	}
//        }
        
        sb.append("<pre>");
        for (int i = 0; i < supers.size(); i++) {
        	Class<?> c = supers.get(supers.size() - 1 - i);
        	if (i > 0) {
        		for (int j = i*2-1; j > 0; j--) {
        			sb.append("  ");
        		}
        		sb.append("|\n");
        		for (int j = i*2-1; j > 0; j--) {
        			sb.append("  ");
        		}
        		sb.append("+--");
        	}
        	if (i + 1 == supers.size()) {
        		sb.append("<b>");
        		sb.append(c.getName());
        		sb.append("</b>");
        	} else {
	        	sb.append(link(url, c, supers.get(supers.size() - i - 2).getGenericSuperclass()));
        	}
        	sb.append("\n");
        }
        sb.append("</pre>");
        
        Set<Class<?>> allInts = new HashSet<Class<?>>();
        getInterfaces(target, allInts);

        if (allInts.size() > 0) {
        	List<Class<?>> intlist = new ArrayList<Class<?>>(allInts.size());
        	intlist.addAll(allInts);
        	Collections.sort(intlist, classComparator);
	        sb.append("<dl>\n");
	        sb.append("<dt><b>All Implemented Interfaces:</b></dt>\n");
	        sb.append("<dd>");
	        for (int i = 0; i <intlist.size(); i++) {
	        	if (i > 0) sb.append(", ");
	        	sb.append(link(url, intlist.get(i)));
	        }
        	sb.append("</dd>\n</dl>\n");
        }
        
        if (target.isArray()) {
        	Class<?> c = target.getComponentType();
        	while (c.isArray()) {
        		c = c.getComponentType();
        	}
	        sb.append("<dl>\n");
	        sb.append("<dt><b>Component Type of an array:</b></dt>\n");
	        sb.append("<dd>");
	        sb.append(link(url, c));
        	sb.append("</dd>\n</dl>\n");
        }

        sb.append("<hr>\n");
        
        // Definition
        
   		sb.append("<dl>\n<dt>");

   		Annotation[] annos = target.getAnnotations();
        if (annos.length > 0) {
        	for (Annotation an : annos) {
        		sb.append(an.toString());
    	        sb.append("<br>\n");
        	}
        }
   	
   		sb.append("<font color=\"#7f0055\">");
    	if (target.isInterface()) {
	        sb.append(Modifier.toString((~Modifier.ABSTRACT)&target.getModifiers()));
	        sb.append("</font> ");
    	} else if (target.isEnum()) {
	        sb.append(Modifier.toString(target.getModifiers()));
	        sb.append(" enum</font> ");
    	} else {
	        sb.append(Modifier.toString((~Modifier.SYNCHRONIZED)&target.getModifiers()));
	        sb.append(" class</font> ");
    	}
        sb.append("<b>");
        sb.append(getShortName(target));
        sb.append("</b></dt>\n");
        
        if (target.getSuperclass() != null) {
	        sb.append("<dd><font color=\"#7f0055\">extends</font> ");
	        sb.append(link(url, target.getSuperclass(), target.getGenericSuperclass()));
	        sb.append("</dd>\n");
        }
        
        Class<?>[] ints = target.getInterfaces();
        if (ints.length > 0) {
        	Arrays.sort(ints, classComparator);
        	sb.append("<dd><font color=\"#7f0055\">implements</font> ");
        	for (int i = 0; i < ints.length; i++) {
        		if (i > 0) sb.append(", ");
        		sb.append(link(url, ints[i]));
        	}
        	sb.append("</dd>\n");
        }
        sb.append("</dl>\n");
        
		sb.append("<hr>\n");
        
		// ClassLoader info
   	
        ClassLoader loader = target.getClassLoader();
        sb.append("Loaded by \"");
        if (loader == null) {
        	sb.append("Bootstrap ClassLoader\"<br>\n");
        } else {
	        sb.append(loader.getClass().getName());
	        sb.append("\" (id = 0x");
	        sb.append(Integer.toHexString(System.identityHashCode(loader)));
	        sb.append(")<br>\n");
	        Vector<ClassLoader> loaders = new Vector<ClassLoader>();
	        for (ClassLoader cl = loader; cl != null; cl = cl.getParent()) {
	        	loaders.add(cl);
	        }
	        sb.append("<pre>\n\"Bootstrap ClassLoader\"\n");
	        for (int i = 0; i < loaders.size(); i++) {
	        	ClassLoader l = (ClassLoader)loaders.elementAt(loaders.size() - 1 - i);
	        	Class<?> c = l.getClass();
        		for (int j = i*2+1; j > 0; j--) {
        			sb.append("  ");
        		}
        		sb.append("|\n");
        		for (int j = i*2+1; j > 0; j--) {
        			sb.append("  ");
        		}
        		sb.append("+--");
        		sb.append(c.getName());
				sb.append(" (id = ");
				sb.append(Integer.toHexString(System.identityHashCode(l)));
        		sb.append(")<!--\n");
				try {
					Method m = c.getMethod("getClassPath", (Class<?>)null);
					String s = m.invoke(l, (Class<?>)null).toString();
					sb.append("Class Path = ").append(s).append("\n");
				} catch (Exception ex) {
				}
				sb.append(" -->\n");
	        }
	        sb.append("</pre>\n");
	    }
        
        
        sb.append("<hr>\n");

    	sb.append("&nbsp;\n");

        Field[] fields = target.getDeclaredFields();
        if (fields.length > 0) {
            Arrays.sort(fields, fieldComparator);
        	sb.append("<table border=\"1\" cellpadding=\"\" cellspacing=\"\" width=\"100%\">\n");
        	sb.append("<tr><td colspan=\"2\" bgcolor=\"#CCCCFF\"><font size=\"+2\">"
        		+ "<b>Field Summary</b></font></td></tr>");
        	for (int i = 0; i < fields.length; i++) {
	        	sb.append("<tr><td><font color=\"#7f0055\">");
	        	sb.append(Modifier.toString(fields[i].getModifiers()));
        		sb.append("</font> ");
	        	sb.append(link(url, fields[i].getType()));
	        	sb.append(" <b>");
	        	sb.append(fields[i].getName());
	        	sb.append("</b></td></tr>\n");
        	}
        	sb.append("</table>\n&nbsp;\n");
        }
        
        for (int i = 1; i < supers.size(); i++) {
        	Class<?> c = supers.get(i);
        	fields = c.getDeclaredFields();
        	Vector<String> publicAndProtected = new Vector<String> ();
        	for (int j = 0; j < fields.length; j++) {
        		if ((fields[j].getModifiers()&(Modifier.PUBLIC|Modifier.PROTECTED)) != 0) {
        			publicAndProtected.add(fields[j].getName());
        		}
        	}
        	if (publicAndProtected.size() > 0) {
        		Collections.sort(publicAndProtected);
	        	sb.append("<table border=\"1\" cellpadding=\"\" cellspacing=\"\" width=\"100%\">\n");
	        	sb.append("<tr><td bgcolor=\"#EEEEFF\"><font size=\"+1\">"
	        		+ "<b>Fields inherited from class ");
	        	sb.append(link(url, c));
	        	sb.append("</b></font></td></tr>\n");
	        	sb.append("<tr><td>");
	        	for (int j = 0; j < publicAndProtected.size(); j++) {
	        		if (j > 0) sb.append(", ");
	        		sb.append(publicAndProtected.elementAt(j));
	        	}
	        	sb.append("</td></tr>\n</table>\n&nbsp;\n");
        	}
        }

		Iterator<Class<?>> it = allInts.iterator();
		while (it.hasNext()) {
        	Class<?> c = it.next();
        	fields = c.getDeclaredFields();
        	if (fields.length > 0) {
	        	sb.append("<table border=\"1\" cellpadding=\"\" cellspacing=\"\" width=\"100%\">\n");
	        	sb.append("<tr><td bgcolor=\"#EEEEFF\"><font size=\"+1\">"
	        		+ "<b>Fields inherited from interface ");
	        	sb.append(link(url, c));
	        	sb.append("</b></font></td></tr>\n");
	        	sb.append("<tr><td>");
	        	for (int j = 0; j < fields.length; j++) {
	        		if (j > 0) sb.append(", ");
	        		sb.append(fields[j].getName());
	        	}
	        	sb.append("</td></tr>\n</table>\n&nbsp;\n");
        	}
        }
        
        Constructor<?>[] consts = target.getDeclaredConstructors();
        Arrays.sort(consts, constructorComparator);
        if (consts.length > 0) {
	    	sb.append("<table border=\"1\" cellpadding=\"\" cellspacing=\"\" width=\"100%\">\n");
	    	sb.append("<tr><td bgcolor=\"#CCCCFF\"><font size=\"+2\">"
	    		+ "<b>Constructor Summary</b></font></td></tr>");
	    	for (int i = 0; i < consts.length; i++) {
	    		sb.append("<tr><td><font color=\"#7f0055\">");
	    		sb.append(Modifier.toString(consts[i].getModifiers()));
	    		sb.append("</font> <b>");
	    		sb.append(getShortName(target));
	    		sb.append("</b>(");
	    		Class<?>[] args = consts[i].getParameterTypes();
	    		for (int j = 0; j < args.length; j++) {
	    			if (j > 0) sb.append(", ");
	    			sb.append(link(url, args[j]));
	    		}
	     		sb.append(")</td></tr>\n");
	    	}
		    sb.append("</table>\n&nbsp;\n");
        }
        
        Method methods[] = target.getDeclaredMethods();
        Arrays.sort(methods, methodComparator);
        if (methods.length > 0) {
	    	sb.append("<table border=\"1\" cellpadding=\"\" cellspacing=\"\" width=\"100%\">\n");
	    	sb.append("<tr><td colspan=\"2\" bgcolor=\"#CCCCFF\"><font size=\"+2\">"
	    		+ "<b>Method Summary</b></font></td></tr>");
	    	for (int i = 0; i < methods.length; i++) {
	    		sb.append("<tr><td><font color=\"#7f0055\">");
	    		sb.append(Modifier.toString(methods[i].getModifiers()));
	    		sb.append("</font> ");
	    		sb.append(link(url, methods[i].getReturnType()));
	    		sb.append(" <b>");
	    		sb.append(methods[i].getName());
	    		sb.append("</b>(");
	    		Class<?>[] args = methods[i].getParameterTypes();
	    		for (int j = 0; j < args.length; j++) {
	    			if (j > 0) sb.append(", ");
	    			sb.append(link(url, args[j]));
	    		}
	     		sb.append(")");
	     		Class<?>[] throwz = methods[i].getExceptionTypes();
	     		if (throwz.length > 0) {
	     			Arrays.sort(throwz, classComparator);
	     			sb.append("<br>&nbsp;&nbsp;&nbsp;&nbsp;<font size=\"-1\"><font color=\"#7f0055\">throws</font> ");
	     			for (int k = 0; k < throwz.length; k++) {
	     				if (k > 0) sb.append(", ");
	     				sb.append(link(url, throwz[k]));
	     			}
	     			sb.append("</font>");
	     		}
				sb.append("</td></tr>\n");
	    	}
		    sb.append("</table>\n&nbsp;\n");
        }
        
        methods = target.getMethods();
        Arrays.sort(methods, methodComparator);
        for (int i = 1; i < supers.size(); i++) {
        	Vector<Method> v = new Vector<Method>();
        	Class<?> s = supers.get(i);
        	for (int j = 0; j < methods.length; j++) {
        		if (methods[j].getDeclaringClass().equals(s)) v.add(methods[j]);
        	}
        	if (v.size() > 0) {
	        	sb.append("<table border=\"1\" cellpadding=\"\" cellspacing=\"\" width=\"100%\">\n");
	        	sb.append("<tr><td colspan=\"2\" bgcolor=\"#EEEEFF\"><font size=\"+1\">"
	        		+ "<b>Methods inherited from class ");
	        	sb.append(link(url, s));
	        	sb.append("</b></font></td></tr>\n");
	        	for (int j = 0; j < v.size(); j++) {
	        		Method method = (Method)v.elementAt(j);
		        	sb.append("<tr><td><font color=\"#7f0055\">");
		    		sb.append(Modifier.toString(method.getModifiers()));
		    		sb.append("</font> ");
		    		sb.append(link(url, method.getReturnType()));
		    		sb.append(" <b>");
		    		sb.append(method.getName());
		    		sb.append("</b>(");
		    		Class<?>[] args = method.getParameterTypes();
		    		for (int k = 0; k < args.length; k++) {
		    			if (k > 0) sb.append(", ");
		    			sb.append(link(url, args[k]));
		    		}
		     		sb.append(")</td></tr>\n");
	        	}
		        sb.append("</table>\n&nbsp;\n");
        	}
        }
       

        out.print(sb.toString());
    }
    
    private static String getShortName(Class<?> c) {
    	String fullName = c.getName();
    	if (c.isArray()) return fullName;
    	StringBuilder ret;
    	int pos = fullName.lastIndexOf('.');
    	if (pos < 0) {
    		ret = new StringBuilder(fullName);
    	} else {
    		ret = new StringBuilder(fullName.substring(pos + 1));
    	}
    	ret.append(getGenTypes(c));
		return ret.toString();
    }
    
    private static String getGenTypes(Class<?> c) {
    	TypeVariable<?>[] genTypes = c.getTypeParameters();
    	if (genTypes.length > 0) {
        	StringBuilder ret = new StringBuilder("&lt;");
    		for (int i = 0; i < genTypes.length; i++) {
    			if (i > 0) ret.append(",");
    			ret.append(genTypes[i].toString());
    		}
    		ret.append("&gt;");
    		return ret.toString();
    	} else {
    		return "";
    	}
    }
    
    private static String getPackageName(Class<?> c) {
    	if (c.isArray()) return "";
    	return c.getPackage().getName();
    }
    
    private static String link(String url, Class<?> cls, Type t) {
    	if (t == null) return link(url, cls);
    	ClassCache.put(cls);
    	StringBuilder sb = new StringBuilder();
    	sb.append("<a href=\"");
    	sb.append(url);
    	sb.append("?class=");
    	sb.append(cls.getName());
    	sb.append("\">");
    	sb.append(Html.escapeChar(t.toString()));
    	sb.append("</a>");
    	return sb.toString();
    }

    private static String link(String url, Class<?> cls) {
    	if (url == null || cls.isPrimitive()) return cls.getName();
    	ClassCache.put(cls);
    	StringBuilder sb = new StringBuilder();
    	sb.append("<a href=\"");
    	sb.append(url);
    	sb.append("?class=");
    	sb.append(cls.getName());
    	sb.append("\">");
    	sb.append(getNameArrayExpand(cls));
    	sb.append("</a>");
    	return sb.toString();
    }
    
//	private static String link(String url, String name) {
//		try {
//			return link(url, Class.forName(name));
//		} catch (ClassNotFoundException ex) {
//			return name;
//		}
//	}
    
    private static String getNameArrayExpand(Class<?> c) {
    	if (c == null) return "null";
    	if (!c.isArray()) return c.getName() + getGenTypes(c);
    	String s = "";
    	while (c.isArray()) {
    		s = s + "[]";
    		c = c.getComponentType();
    	}
    	return c.getName() + s;
    }
    
    private static void getInterfaces(Class<?> cls, Set<Class<?>> set) {
    	for (Class<?> c = cls; c != null; c = c.getSuperclass()) {
    		Class<?>[] ints = c.getInterfaces();
    		for (Class<?> i : ints) {
    			if (!set.contains(i)) {
        			set.add(i);
        			getInterfaces(i, set);
    			}
    		}
    	}
    }
    
}

