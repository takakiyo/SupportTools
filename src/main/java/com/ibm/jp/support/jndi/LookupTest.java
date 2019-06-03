package com.ibm.jp.support.jndi;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// import com.ibm.websphere.naming.DumpNameSpace;

/**
 * @version 	$Id: LookupTest.java,v 1.1 2004/10/25 06:37:55 takakiyo Exp $
 * @author T.Takakiyo
 */
public class LookupTest extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1320411080556563087L;

	/**
	 * @see javax.servlet.http.HttpServlet#void (javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
		throws ServletException, IOException
	{
		LookupTestResultBean result = new LookupTestResultBean();
		req.setAttribute("result", result);
		ServletContext context = getServletContext();
		RequestDispatcher dispatcher = context.getRequestDispatcher("/WEB-INF/LookupTest.jsp");
		dispatcher.forward(req, resp);
	}

	/**
	 * @see javax.servlet.http.HttpServlet#void (javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
		throws ServletException, IOException
	{
		LookupTestResultBean result = new LookupTestResultBean();
		req.setAttribute("result", result);
		
		Properties prop = new Properties();
		String param, param2;
		
		param = req.getParameter("provider");
		if (param == null || param.equals("")) {
			result.setProviderUrl("");
		} else {
			result.setProviderUrl(param);
			prop.put(javax.naming.Context.PROVIDER_URL, param);
		}
		
		param = req.getParameter("factory");
		if (param == null || param.equals("")) {
			result.setInitialContextFactory("");
		} else {
			result.setInitialContextFactory(param);
			prop.put(javax.naming.Context.INITIAL_CONTEXT_FACTORY, param);
		}
		
		param = req.getParameter("key1");
		if (param != null && !param.equals("")) {
			param2 = req.getParameter("value1");
			result.setAltKey1(param);
			result.setAltValue1(param2);
			prop.put(param, param2);
		}
		
		param = req.getParameter("key2");
		if (param != null && !param.equals("")) {
			param2 = req.getParameter("value2");
			result.setAltKey2(param);
			result.setAltValue2(param2);
			prop.put(param, param2);
		}
		
		param = req.getParameter("name");
		if (param != null && !param.equals("")) {
			result.setJndiName(param);
		}
		
		boolean listNames = false;
		param = req.getParameter("list");
		if (param != null && !param.equals("")) {
			listNames = true;
		}
		
		String root = req.getParameter("root");
		if (root != null && !root.equals("")) {
			result.setRoot(root);
		} else {
			root = null;
		}
				
				
		StringBuffer sb = new StringBuffer();
		
		try {
			Context initContext = new InitialContext(prop);
			sb.append("created InitialContext successfully.\n");
			sb.append("\tInitialContext Class = ");
			sb.append(initContext.getClass().getName());
			sb.append("\n\tInitialContext Instance = ");
			sb.append(initContext.toString());
//			sb.append("\n\tName in Namespace = ");
//			sb.append(initContext.getNameInNamespace());
			sb.append("\n\n");
			
			if (listNames) {
				HashSet<Object> done = new HashSet<Object>();
				if (root == null) {
					listBindings(sb, initContext, "", 0, done);
//					sb.append(dumpNameSpace(initContext));
				} else {
					try {
						Context context = (Context)initContext.lookup(root);
						sb.append("Lookup Context successfully.\n");
						sb.append("\tContext Class = ");
						sb.append(context.getClass().getName());
						sb.append("\n\tContext Instance = ");
						sb.append(context.toString());
						sb.append("\n\tName in Namespace = ");
						sb.append(context.getNameInNamespace());
						sb.append("\n\n");
						listBindings(sb, context, context.getNameInNamespace(), 0, done);
					} catch (ClassCastException e) {
						sb.append(root + " is not Naming Context.");
					}
//					listBindings(sb, initContext, root, 0, done);
				}
			} else {
				Object obj = initContext.lookup(result.getJndiName());
				sb.append("lookup object succeeded.\n");
				sb.append("\tObject Class = ");
				sb.append(obj.getClass().getName());
				sb.append("\n\tObject Instance = ");
				sb.append(obj.toString());
				sb.append("\n\n");
			}
		} catch (NamingException ex) {
			sb.append(exceptionStachTrace(ex));
		}
		
		result.setResult(sb.toString());
		
		ServletContext context = getServletContext();
		RequestDispatcher dispatcher = context.getRequestDispatcher("/WEB-INF/LookupTest.jsp");
		dispatcher.forward(req, resp);
	}
	
	private int listBindings(StringBuffer sb, Context cx, String base, int seq, HashSet<Object> done) {
		NamingEnumeration<NameClassPair> nameEnum;
		try {
			String nameinns = cx.getNameInNamespace();
			if (done.contains(nameinns)) return seq;
			done.add(nameinns);
			//
			nameEnum = cx.list("");
		} catch (NamingException e) {
			sb.append("<!-- debug:1\n");
			sb.append(exceptionStachTrace(e));
			sb.append("\n-->");
			return seq;
		}
		while (nameEnum.hasMoreElements()) {
			NameClassPair pair = nameEnum.nextElement();
			String name = pair.getName();
			System.out.println(base + "/" + name + name.length());
			sb.append(seq++);
			sb.append(": ");
			sb.append(base);
			if (!base.equals("")) sb.append("/");
			sb.append(name.equals("")? "[no name]" : name);
			sb.append("\nClass :");
			sb.append(pair.getClassName());
			if (!name.equals("")) try {
				Object o = cx.lookup(name);
				sb.append("\nValue :");
				sb.append(o.toString());
				if (o instanceof Context) {
					if (!done.contains(o)) {
						sb.append("\n\n");
						String newBase = base.equals("")? name : base + "/" + name;
						done.add(o);
						seq = listBindings(sb, (Context)o, newBase, seq, done);
					}
					continue;
				}
			} catch (NamingException e) {
				sb.append("<!-- debug:2\n");
				sb.append(exceptionStachTrace(e));
				sb.append("\n-->");
			}
			sb.append("\n\n");
		}
		return seq;
	}
	
//	private String dumpNameSpace(Context con) {
//		ByteArrayOutputStream out = new ByteArrayOutputStream();
//		try {
//			Class dnsClass = Class.forName("com.ibm.websphere.naming.DumpNameSpace");
//			Constructor dnsConstructor = dnsClass.getConstructor(PrintStream.class, Integer.TYPE);
//			Object dns = dnsConstructor.newInstance(new PrintStream(out), 2);
//			Method generateDump = dnsClass.getMethod("generateDump", Context.class);
//			generateDump.invoke(dns, con);
//			out.close();
//		} catch (Exception ex) {
//			return ex.getMessage();
//		}
//		return out.toString();
//	}

	private String exceptionStachTrace(Throwable t) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			t.printStackTrace(new PrintStream(out));
			out.close();
		} catch (IOException ex) {}
		return out.toString();
	}

	/**
	 * @see javax.servlet.GenericServlet#void ()
	 */
	public void init() throws ServletException {

		super.init();

	}

}
