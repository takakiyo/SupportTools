package com.ibm.jp.util;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

public class ClassCache {
	private static Map<String, WeakReference<Class<?>>> cache = new HashMap<String, WeakReference<Class<?>>>();
	
	public static void put(Class<?> clazz) {
		WeakReference<Class<?>> c = cache.get(clazz.getName());
		if (c != null && c.get() != null) return;
		cache.put(clazz.getName(), new WeakReference<Class<?>>(clazz));
	}
	public static Class<?> get(String name) {
		WeakReference<Class<?>> value = cache.get(name);
		if (value == null) return null;
		return value.get();
	}
}
