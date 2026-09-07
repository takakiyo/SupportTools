package com.ibm.jp.support.os;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * OS Information Dumper Servlet.
 * Executes OS commands and displays system information.
 *
 * @version 2.0
 */
public class OsInfoServlet extends HttpServlet {

    private static final long serialVersionUID = -1090393704959856598L;

    private boolean AIX, Sun, Linux, UNIX, Win, MacOS_X;
    private String osName;

    /**
     * @see javax.servlet.GenericServlet#init()
     */
    @Override
    public void init() throws ServletException {
        super.init();
        osName = System.getProperty("os.name");

        AIX    = osName.equals("AIX");
        Sun    = osName.equals("SunOS") || osName.equals("Solaris");
        Linux  = osName.equals("Linux");
        MacOS_X = osName.equals("Mac OS X") || osName.startsWith("Mac OS X ")
                || osName.equals("macOS")   || osName.startsWith("macOS ");
        UNIX   = AIX || Sun || MacOS_X;
        Win    = osName.startsWith("Windows");
    }

    /**
     * @see javax.servlet.http.HttpServlet#doGet(HttpServletRequest, HttpServletResponse)
     */
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("text/html; charset=UTF-8");
        PrintWriter out = resp.getWriter();

        out.print(
            "<!DOCTYPE html>\n" +
            "<html>\n" +
            "<head>\n" +
            "<meta charset=\"UTF-8\">\n" +
            "<title>OS Information Dumper</title>\n" +
            "</head>\n" +
            "<body>\n" +
            "<h1>OS Information Dumper</h1>\n"
        );

        if (!UNIX && !Linux && !Win) {
            out.println(
                "<p><font color=\"#ff0000\"><b>Warning</b> " +
                "SUP0001W: This servlet does not support " + htmlEscape(osName) +
                " platform. Maybe, some error message will appear.</font></p>"
            );
        }

        if (Win) {
            // -----------------------------------------------------------------
            // Windows
            // -----------------------------------------------------------------
            out.println("<h2>OS Version</h2>");
            beginTable(out);
            drawCmdOutput(out, "ver",          new String[]{"cmd.exe", "/c", "ver"});
            drawCmdOutput(out, "systeminfo",   new String[]{"cmd.exe", "/c", "systeminfo"});
            endTable(out);

            out.println("<h2>Account Info</h2>");
            beginTable(out);
            drawCmdOutput(out, "net accounts",    new String[]{"cmd.exe", "/c", "net", "accounts"});
            drawCmdOutput(out, "net user",         new String[]{"cmd.exe", "/c", "net", "user"});
            drawCmdOutput(out, "net localgroup",   new String[]{"cmd.exe", "/c", "net", "localgroup"});
            endTable(out);

            out.println("<h2>Current User Environments</h2>");
            beginTable(out);
            drawCmdOutput(out, "set", new String[]{"cmd.exe", "/c", "set"});
            endTable(out);

            out.println("<h2>Current User Information</h2>");
            beginTable(out);
            drawCmdOutput(out, "net user %USERNAME%", new String[]{"cmd.exe", "/c", "net", "user", "%USERNAME%"});
            endTable(out);

            out.println("<h2>Running Services</h2>");
            beginTable(out);
            drawCmdOutput(out, "net start", new String[]{"cmd.exe", "/c", "net", "start"});
            drawCmdOutput(out, "sc query type= all state= all",
                          new String[]{"cmd.exe", "/c", "sc", "query", "type=", "all", "state=", "all"});
            endTable(out);

            out.println("<h2>Windows Network Config</h2>");
            beginTable(out);
            drawCmdOutput(out, "net config server",      new String[]{"cmd.exe", "/c", "net", "config", "server"});
            drawCmdOutput(out, "net config workstation", new String[]{"cmd.exe", "/c", "net", "config", "workstation"});
            endTable(out);

            out.println("<h2>Process List</h2>");
            beginTable(out);
            drawCmdOutput(out, "tasklist /v", new String[]{"tasklist.exe", "/v"});
            endTable(out);
        } else if (UNIX) {
            // -----------------------------------------------------------------
            // AIX / Solaris / macOS
            // -----------------------------------------------------------------

            out.println("<h2>Operating System Info</h2>");
            beginTable(out);
            if (MacOS_X) {
                drawCmdOutput(out, "uname -a",  new String[]{"uname", "-a"});
                drawCmdOutput(out, "sw_vers",   new String[]{"sw_vers"});
                // sysctl hw/kern: CPU・メモリ・カーネルの基本情報
                // hostinfo は macOS 13 Ventura で廃止されたため sysctl で代替
                drawCmdOutput(out, "sysctl hw",   new String[]{"/bin/sh", "-c", "sysctl hw"});
                drawCmdOutput(out, "sysctl kern", new String[]{"/bin/sh", "-c", "sysctl kern"});
            } else {
                drawCmdOutput(out, "uname -a",  new String[]{"/bin/sh", "-c", "LANG=C uname -a"});
            }
            endTable(out);

            out.println("<h2>User Environments</h2>");
            beginTable(out);
            drawCmdOutput(out, "export", new String[]{"/bin/sh", "-c", "export"});
            endTable(out);

            if (AIX) {
                out.println("<h2>System Environments</h2>");
                beginTable(out);
                drawCmdOutput(out, "/etc/environment", new String[]{"/bin/sh", "-c", "LANG=C cat /etc/environment"});
                endTable(out);
            }

            out.println("<h2>System Uptime</h2>");
            beginTable(out);
            drawCmdOutput(out, "uptime", new String[]{"/bin/sh", "-c", "LANG=C uptime"});
            endTable(out);

            out.println("<h2>Paging Space Info</h2>");
            beginTable(out);
            if (AIX) {
                drawCmdOutput(out, "lsps -a", new String[]{"/bin/sh", "-c", "LANG=C lsps -a"});
            } else if (Sun) {
                drawCmdOutput(out, "swap -l",  new String[]{"/bin/sh", "-c", "LANG=C swap -l"});
                drawCmdOutput(out, "swap -s",  new String[]{"/bin/sh", "-c", "LANG=C swap -s"});
            } else if (MacOS_X) {
                drawCmdOutput(out, "vm_stat",             new String[]{"vm_stat"});
                // /private/var/vm/ は macOS 10.15 Catalina 以降で廃止
                // sysctl vm.swapusage でスワップ使用量を確認
                drawCmdOutput(out, "sysctl vm.swapusage", new String[]{"/bin/sh", "-c", "sysctl vm.swapusage"});
            }
            endTable(out);

            out.println("<h2>Disk Space Info</h2>");
            beginTable(out);
            drawCmdOutput(out, "df -k", new String[]{"/bin/sh", "-c", "LANG=C df -k"});
            if (MacOS_X) {
                // APFS ボリューム・パーティション構成の確認 (macOS 10.13 High Sierra 以降)
                drawCmdOutput(out, "diskutil list", new String[]{"diskutil", "list"});
            }
            endTable(out);

            out.println("<h2>IPC Stat</h2>");
            beginTable(out);
            drawCmdOutput(out, "ipcs -a", new String[]{"/bin/sh", "-c", "LANG=C ipcs -a"});
            endTable(out);

            out.println("<h2>Resource Limits</h2>");
            beginTable(out);
            if (MacOS_X) {
                drawCmdOutput(out, "Software Limits (ulimit -Sa)", new String[]{"/bin/sh", "-c", "ulimit -Sa"});
                drawCmdOutput(out, "Hardware Limits (ulimit -Ha)", new String[]{"/bin/sh", "-c", "ulimit -Ha"});
            } else {
                drawCmdOutput(out, "Software Limits (ulimit -Sa)", new String[]{"/bin/sh", "-c", "LANG=C ulimit -Sa"});
                drawCmdOutput(out, "Hardware Limits (ulimit -Ha)", new String[]{"/bin/sh", "-c", "LANG=C ulimit -Ha"});
            }
            endTable(out);

            if (AIX) {
                out.println("<h2>System Devices</h2>");
                beginTable(out);
                drawCmdOutput(out, "lsdev -C", new String[]{"/bin/sh", "-c", "LANG=C lsdev -C"});
                endTable(out);

                out.println("<h2>System Attribute</h2>");
                beginTable(out);
                drawCmdOutput(out, "lsattr -El sys0", new String[]{"/bin/sh", "-c", "LANG=C lsattr -El sys0"});
                endTable(out);

                out.println("<h2>Processor Attribute</h2>");
                beginTable(out);
                drawCmdOutput(out, "lsattr -El proc*", new String[]{"/bin/sh", "-c",
                    "LANG=C for i in $(lsdev -C | grep proc | awk '{ print $1;}'); do echo $i; lsattr -El $i; echo; done"});
                endTable(out);

                out.println("<h2>Memory Attribute</h2>");
                beginTable(out);
                drawCmdOutput(out, "lsattr -El mem*", new String[]{"/bin/sh", "-c",
                    "LANG=C for i in $(lsdev -C | grep mem | awk '{ print $1;}'); do echo $i; lsattr -El $i; echo; done"});
                endTable(out);

            } else if (Sun) {
                out.println("<h2>OS Booting Messages</h2>");
                beginTable(out);
                drawCmdOutput(out, "dmesg", new String[]{"/bin/sh", "-c", "LANG=C dmesg"});
                endTable(out);

                out.println("<h2>Processor Attribute</h2>");
                beginTable(out);
                drawCmdOutput(out, "psrinfo -v", new String[]{"/bin/sh", "-c", "LANG=C /usr/sbin/psrinfo -v"});
                endTable(out);

                out.println("<h2>Kernel Parameters</h2>");
                beginTable(out);
                drawCmdOutput(out, "sysdef -i", new String[]{"/bin/sh", "-c", "LANG=C /usr/sbin/sysdef -i"});
                endTable(out);

                out.println("<h2>System Configuration</h2>");
                beginTable(out);
                drawCmdOutput(out, "prtconf", new String[]{"/bin/sh", "-c", "LANG=C prtconf"});
                endTable(out);
            }

            if (MacOS_X) {
                // launchctl: macOS のサービス管理 (launchd ベース)
                out.println("<h2>Running Services (launchd)</h2>");
                beginTable(out);
                drawCmdOutput(out, "launchctl list", new String[]{"launchctl", "list"});
                endTable(out);
            }

            out.println("<h2>Process List</h2>");
            beginTable(out);
            if (MacOS_X) {
                drawCmdOutput(out, "ps auxwww", new String[]{"/bin/sh", "-c", "LANG=C ps auxwww"});
            } else {
                drawCmdOutput(out, "ps -efl", new String[]{"/bin/sh", "-c", "LANG=C ps -efl"});
            }
            endTable(out);

            if (MacOS_X) {
                // インストール済みパッケージ情報
                out.println("<h2>Installed Packages</h2>");
                beginTable(out);
                // pkgutil: macOS 標準のパッケージ管理 (Apple/App Store 系)
                drawCmdOutput(out, "pkgutil --pkgs",
                              new String[]{"/bin/sh", "-c", "pkgutil --pkgs"});
                // Homebrew がインストールされていれば一覧を取得
                drawCmdOutput(out, "brew list --versions",
                              new String[]{"/bin/sh", "-c",
                                  "brew list --versions 2>/dev/null || echo 'Homebrew not installed'"});
                endTable(out);
            }

            if (AIX) {
                out.println("<h2>Installed Software Modules</h2>");
                beginTable(out);
                drawCmdOutput(out, "lslpp -l", new String[]{"/bin/sh", "-c", "LANG=C lslpp -l"});
                endTable(out);

                out.println("<h2>Error Report</h2>");
                beginTable(out);
                drawCmdOutput(out, "errpt",   new String[]{"/bin/sh", "-c", "LANG=C errpt"});
                drawCmdOutput(out, "errpt -a (head 1000)", new String[]{"/bin/sh", "-c", "LANG=C errpt -a | head -1000"});
                endTable(out);

            } else if (Sun) {
                out.println("<h2>OS Patches</h2>");
                beginTable(out);
                drawCmdOutput(out, "showrev -p", new String[]{"/bin/sh", "-c", "LANG=C showrev -p"});
                endTable(out);

                out.println("<h2>Installed Softwares</h2>");
                beginTable(out);
                drawCmdOutput(out, "pkginfo -x", new String[]{"/bin/sh", "-c", "LANG=C pkginfo -x"});
                endTable(out);
            }
        } else if (Linux) {
            // -----------------------------------------------------------------
            // Linux
            // -----------------------------------------------------------------

            out.println("<h2>Operating System Info</h2>");
            beginTable(out);
            drawCmdOutput(out, "uname -a",        new String[]{"/bin/sh", "-c", "LANG=C uname -a"});
            // /etc/os-release は主要ディストリビューションで共通
            drawCmdOutput(out, "/etc/os-release",  new String[]{"/bin/sh", "-c", "cat /etc/os-release 2>/dev/null || cat /etc/system-release 2>/dev/null"});
            endTable(out);

            out.println("<h2>User Environments</h2>");
            beginTable(out);
            drawCmdOutput(out, "export", new String[]{"/bin/sh", "-c", "export"});
            endTable(out);

            out.println("<h2>System Uptime</h2>");
            beginTable(out);
            drawCmdOutput(out, "uptime", new String[]{"/bin/sh", "-c", "LANG=C uptime"});
            endTable(out);

            out.println("<h2>Memory and Paging Space Info</h2>");
            beginTable(out);
            drawCmdOutput(out, "free -h", new String[]{"/bin/sh", "-c", "LANG=C free -h"});
            endTable(out);

            out.println("<h2>Disk Space Info</h2>");
            beginTable(out);
            drawCmdOutput(out, "df -k", new String[]{"/bin/sh", "-c", "LANG=C df -k"});
            endTable(out);

            out.println("<h2>IPC Stat</h2>");
            beginTable(out);
            drawCmdOutput(out, "ipcs -a", new String[]{"/bin/sh", "-c", "LANG=C ipcs -a"});
            endTable(out);

            out.println("<h2>Resource Limits</h2>");
            beginTable(out);
            drawCmdOutput(out, "Software Limits (ulimit -Sa)", new String[]{"/bin/sh", "-c", "LANG=C ulimit -Sa"});
            drawCmdOutput(out, "Hardware Limits (ulimit -Ha)", new String[]{"/bin/sh", "-c", "LANG=C ulimit -Ha"});
            endTable(out);

            out.println("<h2>OS Booting Messages (journalctl)</h2>");
            beginTable(out);
            // journalctl が利用可能であれば優先、なければ dmesg にフォールバック
            drawCmdOutput(out, "journalctl -b -k (last boot kernel messages)",
                          new String[]{"/bin/sh", "-c",
                              "LANG=C journalctl -b -k --no-pager 2>/dev/null | head -2000 || LANG=C dmesg | head -2000"});
            endTable(out);

            out.println("<h2>Running Services</h2>");
            beginTable(out);
            // systemd 環境では systemctl、SysVinit 環境では service --status-all
            drawCmdOutput(out, "systemctl list-units --type=service",
                          new String[]{"/bin/sh", "-c",
                              "LANG=C systemctl list-units --type=service --no-pager 2>/dev/null || LANG=C service --status-all 2>&1"});
            endTable(out);

            out.println("<h2>Proc Filesystem Info</h2>");
            beginTable(out);
            drawCmdOutput(out, "kernel version",  new String[]{"/bin/sh", "-c", "cat /proc/version"});
            drawCmdOutput(out, "kernel cmdline",  new String[]{"/bin/sh", "-c", "cat /proc/cmdline"});
            drawCmdOutput(out, "cpuinfo",          new String[]{"/bin/sh", "-c", "cat /proc/cpuinfo"});
            drawCmdOutput(out, "devices",          new String[]{"/bin/sh", "-c", "cat /proc/devices"});
            drawCmdOutput(out, "interrupts",       new String[]{"/bin/sh", "-c", "cat /proc/interrupts"});
            drawCmdOutput(out, "dma",              new String[]{"/bin/sh", "-c", "cat /proc/dma"});
            drawCmdOutput(out, "ioports",          new String[]{"/bin/sh", "-c", "cat /proc/ioports"});
            drawCmdOutput(out, "partitions",       new String[]{"/bin/sh", "-c", "cat /proc/partitions"});
            drawCmdOutput(out, "swaps",            new String[]{"/bin/sh", "-c", "cat /proc/swaps"});
            endTable(out);

            out.println("<h2>Loaded Kernel Modules</h2>");
            beginTable(out);
            drawCmdOutput(out, "lsmod", new String[]{"/bin/sh", "-c", "LANG=C lsmod"});
            endTable(out);

            out.println("<h2>PCI Devices Info</h2>");
            beginTable(out);
            // lspci が利用可能な場合のみ実行
            drawCmdOutput(out, "lspci -vv",
                          new String[]{"/bin/sh", "-c", "LANG=C lspci -vv 2>/dev/null || echo 'lspci not available'"});
            endTable(out);

            out.println("<h2>Process List</h2>");
            beginTable(out);
            drawCmdOutput(out, "ps -efl", new String[]{"/bin/sh", "-c", "LANG=C ps -efl"});
            endTable(out);

            out.println("<h2>Software Packages Info</h2>");
            beginTable(out);
            // RPM 系 / DEB 系 / どちらでもない場合を考慮
            drawCmdOutput(out, "installed packages",
                          new String[]{"/bin/sh", "-c",
                              "LANG=C rpm -qai 2>/dev/null || LANG=C dpkg -l 2>/dev/null || echo 'No supported package manager found'"});
            endTable(out);
        }

        // -----------------------------------------------------------------
        // Network Information (全 OS 共通)
        // -----------------------------------------------------------------
        out.println("<h2>Network Information</h2>");
        beginTable(out);
        if (UNIX) {
            drawCmdOutput(out, "netstat -in",  new String[]{"/bin/sh", "-c", "LANG=C netstat -in"});
            if (!MacOS_X) {
                drawCmdOutput(out, "netstat -v", new String[]{"/bin/sh", "-c", "LANG=C netstat -v"});
            }
            drawCmdOutput(out, "netstat -m",   new String[]{"/bin/sh", "-c", "LANG=C netstat -m"});
            drawCmdOutput(out, "netstat -rn",  new String[]{"/bin/sh", "-c", "LANG=C netstat -rn"});
            drawCmdOutput(out, "netstat -s",   new String[]{"/bin/sh", "-c", "LANG=C netstat -s"});
            drawCmdOutput(out, "netstat -an",  new String[]{"/bin/sh", "-c", "LANG=C netstat -an"});
            if (AIX) {
                drawCmdOutput(out, "netstat -D", new String[]{"/bin/sh", "-c", "LANG=C netstat -D"});
            }
            drawCmdOutput(out, "arp -a",       new String[]{"/bin/sh", "-c", "LANG=C arp -a"});
            drawCmdOutput(out, "ifconfig -a",  new String[]{"/bin/sh", "-c", "LANG=C ifconfig -a"});
            if (MacOS_X) {
                // scutil: macOS のネットワーク設定・DNS・プロキシ情報
                drawCmdOutput(out, "scutil --nwi",        new String[]{"scutil", "--nwi"});
                drawCmdOutput(out, "scutil --dns",        new String[]{"scutil", "--dns"});
                drawCmdOutput(out, "scutil --proxy",      new String[]{"scutil", "--proxy"});
                drawCmdOutput(out, "networksetup -listallhardwareports",
                              new String[]{"networksetup", "-listallhardwareports"});
            }
            if (AIX) {
                drawCmdOutput(out, "no -a", new String[]{"/bin/sh", "-c", "LANG=C no -a"});
            }
        } else if (Linux) {
            // ss は iproute2 パッケージに含まれ現代の Linux の標準ツール
            // netstat (net-tools) にフォールバック
            drawCmdOutput(out, "ss -anp",
                          new String[]{"/bin/sh", "-c", "LANG=C ss -anp 2>/dev/null || LANG=C netstat -an"});
            drawCmdOutput(out, "ss -s (statistics)",
                          new String[]{"/bin/sh", "-c", "LANG=C ss -s 2>/dev/null || LANG=C netstat -s"});
            drawCmdOutput(out, "ip route",
                          new String[]{"/bin/sh", "-c", "LANG=C ip route 2>/dev/null || LANG=C netstat -rn"});
            drawCmdOutput(out, "arp -a",
                          new String[]{"/bin/sh", "-c", "LANG=C ip neigh 2>/dev/null || LANG=C arp -a"});
            // ip addr は ifconfig の後継
            drawCmdOutput(out, "ip addr",
                          new String[]{"/bin/sh", "-c", "LANG=C ip addr 2>/dev/null || LANG=C ifconfig -a"});
        } else if (Win) {
            drawCmdOutput(out, "ipconfig /all",
                          new String[]{"cmd.exe", "/c", "ipconfig", "/all"});
            drawCmdOutput(out, "netstat -an",
                          new String[]{"cmd.exe", "/c", "netstat", "-an"});
            drawCmdOutput(out, "netstat -e",
                          new String[]{"cmd.exe", "/c", "netstat", "-e"});
            drawCmdOutput(out, "netstat -r",
                          new String[]{"cmd.exe", "/c", "netstat", "-r"});
            drawCmdOutput(out, "netstat -s",
                          new String[]{"cmd.exe", "/c", "netstat", "-s"});
            drawCmdOutput(out, "arp -a",
                          new String[]{"cmd.exe", "/c", "arp", "-a"});
        }
        endTable(out);

        out.println("</body></html>");
    }

    // -------------------------------------------------------------------------
    // Helper methods
    // -------------------------------------------------------------------------

    private void beginTable(PrintWriter out) {
        out.print("<table border=\"1\" width=\"100%\">\n");
    }

    private void endTable(PrintWriter out) {
        out.print("</table>\n");
    }

    private void drawCmdOutput(PrintWriter out, String name, String[] cmdArray) {
        out.println("<tr><th>" + htmlEscape(name) + "</th></tr>");
        out.print("<tr><td><pre>");
        execCmd(out, cmdArray);
        out.println("</pre></td></tr>");
    }

    /**
     * コマンドを ProcessBuilder 経由で実行し、標準出力・標準エラー出力を
     * PrintWriter に書き出す。stderr は別スレッドで並列読み取りしてデッドロックを回避する。
     */
    private void execCmd(PrintWriter out, String[] cmdArray) {
        // stdout/stderr の読み取り文字コードを決定する
        // Windows は CP932(MS932)、その他は UTF-8 を基本とし、
        // システムプロパティで上書き可能にする
        Charset charset;
        String fileEncoding = System.getProperty("file.encoding");
        try {
            charset = (fileEncoding != null) ? Charset.forName(fileEncoding) : StandardCharsets.UTF_8;
        } catch (Exception e) {
            charset = StandardCharsets.UTF_8;
        }

        try {
            ProcessBuilder pb = new ProcessBuilder(cmdArray);
            pb.redirectErrorStream(false); // stdout と stderr を分離して読み取る
            Process child = pb.start();

            // stderr を別スレッドで読み取ることで、stdout の読み取りブロック中に
            // stderr バッファが溢れてデッドロックする問題を回避する
            final Charset cs = charset;
            final StringBuilder errBuf = new StringBuilder();
            ExecutorService executor = Executors.newSingleThreadExecutor();
            Future<?> stderrFuture = executor.submit(() -> {
                try (BufferedReader errReader = new BufferedReader(
                        new InputStreamReader(child.getErrorStream(), cs))) {
                    String line;
                    while ((line = errReader.readLine()) != null) {
                        errBuf.append(line).append('\n');
                    }
                } catch (IOException ex) {
                    errBuf.append("[stderr read error: ").append(ex.getMessage()).append("]\n");
                }
            });
            executor.shutdown();

            try (BufferedReader stdoutReader = new BufferedReader(
                    new InputStreamReader(child.getInputStream(), cs))) {
                String line;
                while ((line = stdoutReader.readLine()) != null) {
                    out.println(htmlEscape(line));
                }
            }

            // stderr スレッドの完了を待つ
            try {
                stderrFuture.get();
            } catch (Exception e) {
                // 無視
            }

            if (errBuf.length() > 0) {
                out.print("<font color=\"#FF0000\">");
                out.print(htmlEscape(errBuf.toString()));
                out.print("</font>");
            }

            child.waitFor();

        } catch (IOException e) {
            out.print("<font color=\"#FF0000\">");
            out.print(htmlEscape(e.toString()));
            out.print("</font>");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            out.print("<font color=\"#FF0000\">[interrupted]</font>");
        }
    }

    /**
     * HTML 特殊文字をエスケープする。
     */
    private static String htmlEscape(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }
}
