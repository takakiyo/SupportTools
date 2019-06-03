/*
 * @(#)HeapWatcher.java 1.30 2004/09/04
 * 
 * Copyright (c) 2000,2004 IBM Japan, Inc. All Rights Reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Japan, Inc. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with IBM.
 * 
 * IBM MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE
 * SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE, OR NON-INFRINGEMENT. IBM SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING
 * THIS SOFTWARE OR ITS DERIVATIVES.
 * 
 * CopyrightVersion 1.0
 * 
 * $Id:$
 */

package com.ibm.jp.support.heap;

import java.text.SimpleDateFormat;
import java.util.Date;

public class HeapWatcher extends Thread {
    public static final String version = "HeapWatcher 1.30";
    
    private static final int BUFFER_SIZE = 120;
    private static long interval = 60000;
    
    private Date date[];
    private long free[], total[];
    private int top, bot;
    private boolean isOutStdout, isOutStderr;
    private SimpleDateFormat formatter;
    private boolean run;
    
    HeapWatcher() {
        date = new Date[BUFFER_SIZE];
        free = new long[BUFFER_SIZE];
        total = new long[BUFFER_SIZE];
        top = bot = 0;
        isOutStdout = false;
        isOutStderr = true;
        formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        run = true;
    }
    
    public void run() {
        String str;
        while (run) {
            synchronized(this) {
                date[top]  = new Date();
                free[top]  = Runtime.getRuntime().freeMemory();
                total[top] = Runtime.getRuntime().totalMemory();
                str = formatter.format(date[top])
                    + " [HeapWatcher] freeMemory()=" + free[top] 
                    + " totalMemory()=" + total[top];
                top++;
                if (top == BUFFER_SIZE) top = 0;
                if (bot == top) bot++;
                if (bot == BUFFER_SIZE) bot = 0;
            }
            if (isOutStdout) System.out.println(str);
            if (isOutStderr) System.err.println(str);
            try {
                Thread.sleep(interval);
            } catch (InterruptedException ex) {
            }
        }
    }
    
    public void stopWatcher() {
    	run = false;
    }
    
    public void init() {
        synchronized(this) {
            top = bot = 0;
        }
    }
    
    private int getPos(int index) {
        int c;
        synchronized(this) {
            c = bot + index;
            if (bot > top) {
                if (c >= top+BUFFER_SIZE) c = -1;
            } else {
                if (c >= top) c = -1;
            }
            if (c >= BUFFER_SIZE) c -= BUFFER_SIZE;
        }
        return c;
    }
    
    public String getStat(int index) {
        int c = getPos(index);
        if (c < 0) return null;
        String str = formatter.format(date[c]) 
            + " free=" + free[c] + " total=" + total[c];
        return str;
    }

    public String getDate(int index) {
        int c = getPos(index);
        if (c < 0) return null;
        String str = formatter.format(date[c]); 
        return str;
    }

    public long getFree(int index) {
        int c = getPos(index);
        if (c < 0) return 0L;
        return free[c];
    }

    public long getTotal(int index) {
        int c = getPos(index);
        if (c < 0) return 0L;
        return total[c];
    }
    
    public void setInterval(long newIntarval) {
        interval = newIntarval;
        this.interrupt();
    }
    
    public long getInterval() {
        return interval;
    }
    
    public void setIsOutStdout(boolean b) {
        isOutStdout = b;
    }
    
    public boolean getIsOutStdout() {
        return isOutStdout;
    }
    
    public void setIsOutStderr(boolean b) {
        isOutStderr = b;
    }
    
    public boolean getIsOutStderr() {
        return isOutStderr;
    }
}
