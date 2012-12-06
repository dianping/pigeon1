package com.dianping.dpsf.tool.converter;

import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class AppClassFinder {
	/**
	 * Load all the classes in the app class loader.
	 * @throws NoSuchFieldException 
	 * @throws SecurityException 
	 */
	@SuppressWarnings("unchecked")
	public List<Class<?>> loadClass(String prefix, String suffix) throws Exception{
		List<Class<?>> clsList = new ArrayList<Class<?>>();
		ClassLoader cl = Java2csInterfaceConverter.class.getClassLoader();
		Field clsField = getField("classes", cl.getClass());
		clsField.setAccessible(true);
		for(Object obj : (Vector)clsField.get(cl)) {
			Class<?> cls = (Class<?>)obj;
			if(cls.getName().startsWith(prefix)) {
				clsList.add(cls);
			}
		}
		return clsList;
	}
	
	/**
	 * Get the field named "classes" contained all the app classes, 
	 * traversing the class represented and all the super-class recursively.
	 * @param fieldName
	 * @param originClass
	 * @return
	 * @throws NoSuchFieldException 
	 * @throws  
	 */
	protected Field getField(String fieldName, Class<?> originClass) {
		Field f = null;
		try {
			f = originClass.getDeclaredField("classes");
		} catch (Exception e) {
		}
		if(f==null) {
			f = getField(fieldName, originClass.getSuperclass());
		}
		return f;
	}
	
	public List<Class<?>> loadClass(String jarFilePath) throws Exception {
		List<Class<?>> clsList = new ArrayList<Class<?>>();
		URLClassLoader urlLoader = new URLClassLoader(new URL[]{new URL("file:"+jarFilePath)});
		JarFile jarFile = new JarFile(jarFilePath);
		Enumeration<JarEntry> entries = jarFile.entries();
		while (entries.hasMoreElements()) {
			JarEntry jarEntry = entries.nextElement();
			String entryName = jarEntry.getName();
			if (jarEntry.isDirectory()) {
				//pass directory
			} else if (entryName.endsWith(".class")) {
				String className = entryName.replaceAll("/", ".").substring(0,
						entryName.length() - 6);
				try {
					Class<?> clazz = urlLoader.loadClass(className);
					System.out.println(clazz.getName() + " loaded.");
					clsList.add(clazz);
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
		return clsList;
	}
}
