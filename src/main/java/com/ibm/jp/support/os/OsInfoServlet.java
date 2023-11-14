package com.ibm.jp.support.os;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @version 	1.0
 * @author
 */
public class OsInfoServlet extends HttpServlet implements Servlet {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1090393704959856598L;
	private boolean AIX, Sun, Linux, UNIX, Win, Win2003_XP, Win2008, MacOS_X;
	private String osName;

	/**
	 * @see javax.servlet.GenericServlet#void ()
	 */
	public void init() throws ServletException {
		super.init();
		osName = System.getProperty("os.name");
        
		AIX = (osName.equals("AIX"));
		Sun = (osName.equals("SunOS") || osName.equals("Solaris"));
		Linux = (osName.equals("Linux"));
		MacOS_X = (osName.equals("Mac OS X"));
		UNIX = (AIX || Sun || MacOS_X);
		if (osName.startsWith("Windows")) {
			Win = true;
			Win2003_XP = (osName.startsWith("Windows 2003") || osName.startsWith("Windows XP"));
			Win2008 = osName.startsWith("Windows 2008") || osName.startsWith("Windows 7");
		}
	}

	/**
	 * @see javax.servlet.http.HttpServlet#void (javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
		throws ServletException, IOException {
			resp.setContentType("text/html; charset=UTF-8");
			PrintWriter out = resp.getWriter();

			out.print(
				"<HTML>\n" +
				"<HEAD>\n" +
				"<TITLE>OS Infomation Dumper</TITLE>\n" +
				"</HEAD>\n" +
				"<BODY>\n" +
				"<H1>OS Infomation Dumper</H1>\n"
			);
        
			if (!UNIX && !Linux && !Win) {
				out.println(
					"<FONT color=\"#ff0000\"><b>Warnig</b> " +
					"SUP0001W: This servlet does not support " + osName +
					" platform. Maybe, some error message will appear.</FONT>"
				);
			}

			// OS Informations

			if (Win) {
				out.println("<H2>OS Version</H2>");
				beginTable(out);
				drawCmdOutput(out, "ver", "cmd.exe /c ver");
				endTable(out);
				out.println("<H2>Account Info</H2>");
				beginTable(out);
				drawCmdOutput(out, "net accounts", "cmd.exe /c net accounts");
				drawCmdOutput(out, "net user", "cmd.exe /c net user");
				drawCmdOutput(out, "net localgroup", "cmd.exe /c net localgroup");
				endTable(out);
				out.println("<H2>Current User Environments</H2>");
				beginTable(out);
				drawCmdOutput(out, "set", "cmd.exe /c set");
				endTable(out);
				out.println("<H2>Current User Information</H2>");
				beginTable(out);
				drawCmdOutput(out, "net user %USERNAME%", "cmd.exe /c net user %USERNAME%");
				endTable(out);
				out.println("<H2>Running Services</H2>");
				beginTable(out);
				drawCmdOutput(out, "net start", "net.exe start");
				endTable(out);
				out.println("<H2>Windows Network Config</H2>");
				beginTable(out);
				drawCmdOutput(out, "net config server", "net.exe config server");
				drawCmdOutput(out, "net config workstation", "net.exe config workstation");
				endTable(out);
				
				if (Win2003_XP || Win2008) {
					out.println("<H2>Process List</H2>");
					beginTable(out);
					drawCmdOutput(out, "tasklist /v", "tasklist.exe /v");
					endTable(out);
				}
			} else if (UNIX) {
				out.println("<H2>Operating System Info</H2>");
				beginTable(out);
				if (MacOS_X) {
					drawCmdOutput(out, "uname -a", "uname -a");
					drawCmdOutput(out, "sw_vers", "sw_vers");
					drawCmdOutput(out, "hostinfo", "hostinfo");
				} else {
					drawCmdOutput(out, "uname -a", "/bin/ksh uname -a");
				}
				endTable(out);
				
				out.println("<H2>User Environments</H2>");
				beginTable(out);
				drawCmdOutput(out, "ksh export", "/bin/ksh export");
				endTable(out);

				if (AIX) {
					out.println("<H2>System Environments</H2>");
					beginTable(out);
					drawCmdOutput(out, "/etc/environment", "/bin/ksh LANG=C cat /etc/environment");
					endTable(out);
				}

				out.println("<H2>System Uptime</H2>");
				beginTable(out);
				drawCmdOutput(out, "uptime", "/bin/ksh LANG=C uptime");
				endTable(out);

				out.println("<H2>Paging Space Info</H2>");
				beginTable(out);
				if (AIX) {
					drawCmdOutput(out, "lsps -a", "/bin/ksh LANG=C lsps -a");
				}
				if (Sun) {
					drawCmdOutput(out, "swap -l", "/bin/ksh LANG=C swap -l");
					drawCmdOutput(out, "swap -s", "/bin/ksh LANG=C swap -s");
				}
				if (MacOS_X) {
					drawCmdOutput(out, "vm_stat", "vm_stat");
					drawCmdOutput(out, "/var/vm", "ls -l /private/var/vm/");
				}
				endTable(out);

				out.println("<H2>Disk Space Info</H2>");
				beginTable(out);
				drawCmdOutput(out, "df -k", "/bin/ksh LANG=C df -k");
				endTable(out);

				out.println("<H2>IPC Stat</H2>");
				beginTable(out);
				drawCmdOutput(out, "ipcs -a", "/bin/ksh LANG=C ipcs -a");
				endTable(out);

				out.println("<H2>Resource Limits</H2>");
				beginTable(out);
				if (MacOS_X) {
					drawCmdOutput(out, "Software Limits (ulimit -Sa)", "ulimit -Sa");
					drawCmdOutput(out, "Hardware Limits (ulimit -Ha)", "ulimit -Ha");
				} else {
					drawCmdOutput(out, "Software Limits (ulimit -Sa)", "/bin/ksh LANG=C ksh ulimit -Sa");
					drawCmdOutput(out, "Hardware Limits (ulimit -Ha)", "/bin/ksh LANG=C ksh ulimit -Ha");
				}
				endTable(out);

				if (AIX) {
					out.println("<H2>System Devices</H2>");
					beginTable(out);
					drawCmdOutput(out, "lsdev -C", "/bin/ksh LANG=C lsdev -C");
					endTable(out);

					out.println("<H2>System Attribute</H2>");
					beginTable(out);
					drawCmdOutput(out, "lsattr -El sys0", "/bin/ksh LANG=C lsattr -El sys0");
					endTable(out);

					out.println("<H2>Processor Attribute</H2>");
					beginTable(out);
					drawCmdOutput(out, "lsattr -El proc*", "/bin/ksh LANG=C eval for i in `lsdev -C | grep proc | awk '{ print $1;}'`; do echo $i; lsattr -El $i; echo; done");
					endTable(out);

					out.println("<H2>Memory Attribute</H2>");
					beginTable(out);
					drawCmdOutput(out, "lsattr -El mem*", "/bin/ksh LANG=C eval for i in `lsdev -C | grep mem | awk '{ print $1;}'`; do echo $i; lsattr -El $i; echo; done");
					endTable(out);

				} else if (Sun) {
					out.println("<H2>OS Booting Messages</H2>");
					beginTable(out);
					drawCmdOutput(out, "dmesg", "/bin/ksh LANG=C dmesg");
					endTable(out);

					out.println("<H2>Processor Attribute</H2>");
					beginTable(out);
					drawCmdOutput(out, "psrinfo -v", "/bin/ksh LANG=C /usr/sbin/psrinfo -v");
					endTable(out);

					out.println("<H2>Kernel Paramaters</H2>");
					beginTable(out);
					drawCmdOutput(out, "sysdef -i", "/bin/ksh LANG=C /usr/sbin/sysdef -i");
					endTable(out);

					out.println("<H2>System Configuration</H2>");
					beginTable(out);
					drawCmdOutput(out, "prtconf", "/bin/ksh LANG=C prtconf");
					endTable(out);
				}

				out.println("<H2>Process List</H2>");
				beginTable(out);
				if (MacOS_X) {
					drawCmdOutput(out, "ps auxwww", "/bin/ksh LANG=C ps auxwww");
				} else {
					drawCmdOutput(out, "ps -efl", "/bin/ksh LANG=C ps -efl");
				}
				endTable(out);

				if (AIX) {
					out.println("<H2>Installed Software Modules</H2>");
					beginTable(out);
					drawCmdOutput(out, "lslpp -l","/bin/ksh LANG=C lslpp -l" );
					endTable(out);

					out.println("<H2>Error Report</H2>");
					beginTable(out);
					drawCmdOutput(out, "errpt", "/bin/ksh LANG=C errpt");
					drawCmdOutput(out, "errpt -a", "/bin/ksh LANG=C eval errpt -a | head -1000");
					endTable(out);
				} else if (Sun) {
					out.println("<H2>OS Patches</H2>");
					beginTable(out);
					drawCmdOutput(out, "showrev -p", "/bin/ksh LANG=C showrev -p");
					endTable(out);

					out.println("<H2>Installed Softwares</H2>");
					beginTable(out);
					drawCmdOutput(out, "pkginfo -x", "/bin/ksh LANG=C pkginfo -x");
					endTable(out);
				}
			} else if (Linux) {
				out.println("<H2>User Environments</H2>");
				beginTable(out);
				drawCmdOutput(out, "bash export", "export");
				endTable(out);

				out.println("<H2>System Uptime</H2>");
				beginTable(out);
				drawCmdOutput(out, "uptime", "LANG=C uptime");
				endTable(out);

				out.println("<H2>Memory and Paging Space Info</H2>");
				beginTable(out);
				drawCmdOutput(out, "free", "LANG=C free");
				endTable(out);

				out.println("<H2>Disk Space Info</H2>");
				beginTable(out);
				drawCmdOutput(out, "df -k", "LANG=C df -k");
				endTable(out);

				out.println("<H2>IPC Stat</H2>");
				beginTable(out);
				drawCmdOutput(out, "ipcs -a", "LANG=C ipcs -a");
				endTable(out);

				out.println("<H2>Resource Limits</H2>");
				beginTable(out);
				drawCmdOutput(out, "Software Limits (ulimit -Sa)", "ulimit -Sa");
				drawCmdOutput(out, "Hardware Limits (ulimit -Ha)", "ulimit -Ha");
				endTable(out);

				out.println("<H2>OS Booting Messages</H2>");
				beginTable(out);
				drawCmdOutput(out, "dmesg", "LANG=C dmesg");
				endTable(out);

				out.println("<H2>Proc Filesystem Info.</H2>");
				beginTable(out);
				drawCmdOutput(out, "kernel version", "cat /proc/version");
				drawCmdOutput(out, "kernel cmdline", "cat /proc/cmdline");
				drawCmdOutput(out, "cpuinfo",        "cat /proc/cpuinfo");
				drawCmdOutput(out, "devices",        "cat /proc/devices");
				drawCmdOutput(out, "pci",            "cat /proc/pci");
				drawCmdOutput(out, "interrupts",     "cat /proc/interrupts");
				drawCmdOutput(out, "dma",            "cat /proc/dma");
				drawCmdOutput(out, "ioports",        "cat /proc/ioports");
				drawCmdOutput(out, "partitions",     "cat /proc/partitions");
				drawCmdOutput(out, "swaps",          "cat /proc/swaps");
				endTable(out);

				out.println("<H2>Loaded Kernel Modules</H2>");
				beginTable(out);
				drawCmdOutput(out, "lsmod", "LANG=C lsmod");
				endTable(out);

				out.println("<H2>PCI Devices Info.</H2>");
				beginTable(out);
				drawCmdOutput(out, "lspci -vv", "LANG=C lspci -vv");
				endTable(out);

				out.println("<H2>Process List</H2>");
				beginTable(out);
				drawCmdOutput(out, "ps -efl", "LANG=C ps -efl");
				endTable(out);

				out.println("<H2>Software  Packages Info.</H2>");
				beginTable(out);
				drawCmdOutput(out, "rpm -qai", "LANG=C rpm -qai");
				endTable(out);
				
				out.println("</body></html>");
			}

			out.println("<H2>Network Infomations</H2>");
			beginTable(out);
			if (UNIX) {
				drawCmdOutput(out, "netstat -in", "/bin/ksh LANG=C netstat -in");
				if (!MacOS_X) {
					drawCmdOutput(out, "netstat -v", "/bin/ksh LANG=C netstat -v");
				}
				drawCmdOutput(out, "netstat -m", "/bin/ksh LANG=C netstat -m");
				drawCmdOutput(out, "netstat -rn", "/bin/ksh LANG=C netstat -rn");
				drawCmdOutput(out, "netstat -s", "/bin/ksh LANG=C netstat -s");
				drawCmdOutput(out, "netstat -an", "/bin/ksh LANG=C netstat -an");
				if (AIX) {
					drawCmdOutput(out, "netstat -D", "/bin/ksh LANG=C netstat -D");
				}
				drawCmdOutput(out, "arp -a", "/bin/ksh LANG=C arp -a");
				drawCmdOutput(out, "ifconfig -a", "/bin/ksh LANG=C ifconfig -a");
				if (AIX) {
					drawCmdOutput(out, "no -a", "/bin/ksh LANG=C no -a");
				}
			} else if (Linux) {
				drawCmdOutput(out, "netstat -an", "LANG=C netstat -an");
				drawCmdOutput(out, "netstat -s", "LANG=C netstat -s");
				drawCmdOutput(out, "netstat -rn", "LANG=C netstat -rn");
				drawCmdOutput(out, "arp -a", "LANG=C arp -a");
				drawCmdOutput(out, "ifconfig -a", "LANG=C ifconfig -a");
			} else if (Win) {
				drawCmdOutput(out, "ipconfig /all", "ipconfig /all");
				drawCmdOutput(out, "netstat -an", "netstat -an");
				drawCmdOutput(out, "netstat -e", "netstat -e");
				drawCmdOutput(out, "netstat -r", "netstat -r");
				drawCmdOutput(out, "netstat -s", "netstat -s");
				drawCmdOutput(out, "arp -a", "arp -a");
			}
			endTable(out);
			
			if (MacOS_X) {
				out.println("<!-- <H2>System Infomations</H2>");
				beginTable(out);
				drawCmdOutput(out, "system_profiler", "system_profiler");
				endTable(out);
				out.println("-->");
			}
			
			out.println("</BODY></HTML>");
	}

	private void beginTable(PrintWriter out)
	{
		out.print(
			"<TABLE Border=1 WIDTH=\"100%\">\n"
		);
	}
	private void endTable(PrintWriter out)
	{
		out.print(
			"</TABLE>\n"
		);
	}
//	private void drawCmdOutput(PrintWriter out, String cmd)
//	{
//		out.print("<TR><TD><PRE>");
//		execCmd(out, cmd);
//		out.println("</PRE></TD></TR>");
//	}
	
	private void drawCmdOutput(PrintWriter out, String name, String cmd)
	{
		out.println("<TR><TH>" + name + "</TH></TR>");
		out.print("<TR><TD><PRE>");
		execCmd(out, cmd);
		out.println("</PRE></TD></TR>");
	}

	private void execCmd(PrintWriter out, String cmd)
	{
		String fileEncoding = System.getProperty("file.encoding");
		try {
			Process child;
			if (Linux) {
				child = Runtime.getRuntime().exec("/bin/bash -s");
				OutputStream child_out = child.getOutputStream();
				child_out.write(cmd.getBytes());
				child_out.flush();
				child_out.close();
			} else {
				child = Runtime.getRuntime().exec(cmd);
			}
			InputStreamReader child_in, child_err;
			child_in  = new InputStreamReader(child.getInputStream(), fileEncoding);
			child_err = new InputStreamReader(child.getErrorStream(), fileEncoding);
			int c;
			while ((c = child_in.read()) != -1) {
				putChar(out, c);
			}
			boolean b_err = false;
			while ((c = child_err.read()) != -1) {
				if (!b_err) {
					out.print("<FONT COLOR=\"#FF0000\">");
					b_err = true;
				}
				putChar(out, c);
			}
			if (b_err) {
				out.print("</FONT>");
			}
			child_in.close();
			child_err.close();
		} catch (IOException e) {
			out.println("<FONT COLOR=\"#FF0000\">");
			out.println(e);
			out.println("</FONT>");
		}
	}

	private void putChar(PrintWriter out, int c)
	{
		if (c == '<') {
			out.print("&lt;");
		} else if (c == '>') {
			out.print("&gt;");
		} else if (c == '&') {
			out.print("&amp;");
		} else {
			out.print((char)c);
		}
	}
}
