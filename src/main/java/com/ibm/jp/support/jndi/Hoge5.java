// Decompiled by Jad v1.5.8d. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   Hoge5.java

package com.ibm.jp.support.jndi;

import java.io.Serializable;

public class Hoge5
    implements Serializable
{

    public Hoge5()
    {
        value = null;
    }

    public Hoge5(String s)
    {
        value = null;
        value = s;
    }

    public boolean equals(Object obj)
    {
        return value.equals(obj);
    }

    public int hashCode()
    {
        return value.hashCode();
    }

    public String toString()
    {
        return "Hoge5[" + value + "]";
    }

    public String getValue()
    {
        return value;
    }

    public void setValue(String value)
    {
        this.value = value;
    }

    private static final long serialVersionUID = 0x127ee607957ae7L;
    private String value;
}
