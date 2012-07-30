package com.dianping.dpsf.tool.converter;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Java2csInterfaceConverter {
	/**
	 * The interface refer containers.
	 */
	private Map<String, List<String>> referMap  = new ConcurrentHashMap<String, List<String>>();
	
	/**
	 * The converted reference classes.
	 */
	private Set<String> set = new HashSet<String>();
	
	/**
	 * 
	 */
	private Map<String, Class<?>> clsMap = new HashMap<String, Class<?>>();

	/**
	 * Convert the java classes in the clsList to the c# file and write into filePath
	 * @param clsList
	 * @param filePath
	 * @throws Exception
	 */
	
	/**
	 * Store the parameter name according to MethodSignature.
	 */
	private Map<MethodSignature, String[]> paraNames = new HashMap<MethodSignature, String[]>();
	
	private Map<String, StringBuffer> sourceFiles = new HashMap<String, StringBuffer>();	
	/**
	 * record the warning message
	 */
	private StringBuffer warningMessage = new StringBuffer();
	
	public void csFileConvert(List<Class<?>> clsList, String directory, String sourceFile) throws IOException, SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, InstantiationException {
		loadParaName(sourceFile, clsList);
		//convert the interfaces
		for(Class<?> cls : clsList) {
			StringBuffer sBody = new StringBuffer();
			if(cls.isInterface()) {
				//write class

				//String interfaceName = cls.getName().substring(cls.getName().lastIndexOf(".")+1, cls.getName().length());
				StringBuffer superSB = new StringBuffer();
				if(cls.getInterfaces() != null && cls.getInterfaces().length > 0) {
					superSB.append(" : ");
					for(int m = 0; m < cls.getInterfaces().length; m++) {
						superSB.append(cls.getInterfaces()[m].getSimpleName());
						if(m != cls.getInterfaces().length -1) {
							superSB.append(", ");
						}
					}
				}
				sBody.append("\nnamespace "+cls.getPackage().getName()+"\n{\n\tpublic interface "+cls.getSimpleName()+superSB.toString()+"\n\t{\n");
				//write method declaration
				Method[] methods = cls.getDeclaredMethods();
				for(Method method : methods) {
					Class<?> returnType = method.getReturnType();
					//With C#, no method access modifiers, public by default
					sBody.append("\t\t"+convertJavaCSType(returnType)+" "+method.getName().substring(0,1).toUpperCase() + method.getName().substring(1)+"(");
					if(!isPrimitiveType(returnType) && !isSystemCollection(returnType)) {
						refreshRefer(returnType.getName(), cls, referMap);
					}
					Class<?>[] pClasses = method.getParameterTypes();
					String[] paraName = readParaName(cls, method);
					for(int i = 0; i < pClasses.length; i++) {
						sBody.append(convertJavaCSType(pClasses[i]));
						sBody.append(" ");
						sBody.append(paraName[i]);
						if(i != pClasses.length - 1) {
							sBody.append(", ");
						}
						if(!isPrimitiveType(pClasses[i]) && !isSystemCollection(pClasses[i])) {
							refreshRefer(pClasses[i].getName(), cls, referMap);
						}
					}
					sBody.append(");\n");
				}
				sBody.append("\t}\n}");

			} else if(cls.isEnum()) {
				sBody.append("\nnamespace "+cls.getPackage().getName()+"\n{\n\tpublic enum "+cls.getSimpleName()+"\n\t{\n");
				Object[] enums = cls.getEnumConstants();
				for(int i = 0; i < enums.length; i++) {
					Method method = enums[i].getClass().getMethod("name");
					String name = (String)method.invoke(enums[i], null);
					sBody.append("\t\t");
					sBody.append(name);
					if(i != enums.length - 1) {
						sBody.append(",");
					}
					sBody.append("\n");
				}
				sBody.append("\t}\n}");
			} else {
				clsMap.put(cls.getName(), cls);
				continue;
			}
			StringBuffer sHead = new StringBuffer();
			sHead.append("using System;\nusing System.Collections;\n");
			List<String> rList = referMap.get(cls.getName());
			if(rList != null) {
				Set<String> referSet = new HashSet<String>();
				for(int j = 0; j < rList.size(); j++) {
					referSet.add(rList.get(j).substring(0, rList.get(j).lastIndexOf(".")));
				}
				Iterator<String> ite = referSet.iterator();
				while(ite.hasNext()) {
					sHead.append("using "+ite.next().toString()+";\n");
				}
			}
			String folder = createPackageDirectory(directory, cls.getName());
			BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(folder+cls.getSimpleName()+".cs"));
			bos.write(sHead.toString().getBytes());
			bos.write(sBody.toString().getBytes());
			bos.close();
			//System.out.println(folder+cls.getSimpleName()+".cs generated.");
		}
		//convert the referred entities
		convertReferring(referMap, directory);
	}
	
	private void refreshRefer(String referName, Class<?> orginCls, Map<String, List<String>> referMap) {
		//write the reference if not the primitive
		List<String> rList = null;
		if(referMap.get(orginCls.getName()) == null) {
			rList = new ArrayList<String>();
			referMap.put(orginCls.getName(), rList);
		} else {
			rList = referMap.get(orginCls.getName());
		}
		String rName = referName.replaceAll("\\[+L", "");
		rName = rName.replace(";", "");
		rList.add(rName);
	}
	
	private String createPackageDirectory(String directory, String fullClsName) {
		String dir = directory.replaceAll("\\\\", "/");
		StringBuffer packagePath = new StringBuffer(dir);
		if(fullClsName != null) {
			String[] files = fullClsName.split("\\.");
			for(int i = 0; i < files.length - 1; i++) {
				if(!(dir.endsWith("/") || dir.endsWith("\\"))) {
					packagePath.append("/");
				}
				packagePath.append(files[i]);
				packagePath.append("/");
			}
			File f = new File(packagePath.toString());
			if(!f.exists()) {
				f.mkdirs();
			}
		}
		return packagePath.toString();
	}
	
	private String convertJavaCSType(Class<?> nodeType) {
		String csType = null;
		if(nodeType == null) {
			return null;
		}
		String type = nodeType.getSimpleName();
		if(Map.class.isAssignableFrom(nodeType)) {
			csType = "IDictionary";
		}else if(Collection.class.isAssignableFrom(nodeType)) {
			csType = "IList";
		}else if(type.startsWith("boolean")) {
			csType = type.replaceAll("boolean", "bool");
		}else if(type.startsWith("Boolean")) {
			csType = type.replaceAll("Boolean", "bool?");
		}else if(type.startsWith("String")) {
			csType = type.replaceAll("String", "string");
		}else if(type.startsWith("Integer")) {
			csType = type.replaceAll("Integer", "int?");
		}else if(type.startsWith("Float")) {
			csType = type.replaceAll("Float", "float?");
		}else if(type.startsWith("Double")) {
			csType = type.replaceAll("Double", "double?");
		}else if(type.startsWith("Long")) {
			csType = type.replaceAll("Long", "long?");
		}else if(type.startsWith("Short")) {
			csType = type.replaceAll("Short", "short?");
		}else if(type.startsWith("byte")) {
			csType = type.replaceAll("byte", "sbyte");
		}else if(type.startsWith("Byte")) {
			csType = type.replaceAll("Byte", "sbyte?");
		}else if(type.startsWith("Character")) {
			csType = type.replaceAll("Character", "char?");
		}else if(type.startsWith("BigDecimal")) {
			csType = type.replaceAll("BigDecimal", "decimal?");
		}else if(type.startsWith("Object")) {
			csType = type.replaceAll("Object", "object");
		}else if(type.startsWith("Date")) {
			csType = type.replaceAll("Date", "DateTime");
		}else {
			csType = type;
		}
		return csType;
	}
	
	private boolean isPrimitiveType(Class<?> nodeClass) {
		boolean result = false;
		if(nodeClass != null) {
			String lctype = nodeClass.getName().toLowerCase().replaceAll("\\[+l", "");
			lctype = lctype.replace("java.lang.", "");
			lctype = lctype.replace("java.math.", "");
			lctype = lctype.replace("java.util.", "");
			lctype = lctype.replace("java.sql.", "");
			lctype = lctype.replace(";", "");
			if("boolean".equals(lctype) || "string".equals(lctype) ||"int".equals(lctype)
					||"long".equals(lctype) || "double".equals(lctype) || "float".equals(lctype) 
					|| "integer".equals(lctype) || "bigdecimal".equals(lctype) || "char".equals(lctype)
					|| "character".equals(lctype) || "short".equals(lctype) || "object".equals(lctype)
					|| "date".equals(lctype) || "byte".equals(lctype)) {
				result = true;
			}
		}
		return result;
	}
	
	private boolean isSystemCollection(Class<?> nodeType) {
		boolean result = false;
		if(nodeType != null) {
			if(Map.class.isAssignableFrom(nodeType) || Set.class.isAssignableFrom(nodeType) || List.class.isAssignableFrom(nodeType)) {
				result = true;
			}
		}
		return result;
	}
	
	private String hasDefault(Object obj, Field f) throws IllegalArgumentException, IllegalAccessException {
		String hasDef = null;
		Class<?> t = f.getType();
		f.setAccessible(true);
		Object v = f.get(obj);
		 if(t == boolean.class && !Boolean.FALSE.equals(v)) {
			 hasDef = "true";
		 } else if(t.isPrimitive() && ((Number) v).doubleValue() != 0) {
			 hasDef = v+"";
		 } else if(!t.isPrimitive() && v != null) {
			 if(java.lang.String.class.isAssignableFrom(t)) {
				 hasDef = "\""+v+"\"";
			 } else {
				 hasDef = "new "+v.getClass().getSimpleName()+"()";
			 }
		 }
		return hasDef;
	}
	
	private void convertReferring(Map<String, List<String>> referMap, String directory) throws InstantiationException, IllegalAccessException, IOException {
		if(referMap.size() == 0 ) {
			return;
		}
		Map<String, List<String>> lowerReferMap  = new ConcurrentHashMap<String, List<String>>();
		for(Entry<String, List<String>> entry : referMap.entrySet()) {
			if(entry.getValue() != null) {
				for(String entityName : entry.getValue()) {
					Class<?> entityCls = clsMap.get(entityName);
					if(entityCls == null) {
						warningMessage.append(entry.getKey()+" -> "+entityName +" doesn't exist.\n");
					}
					String folder = createPackageDirectory(directory, entityCls.getName());
					if(set.contains(entityName)) {
						continue;
					}
					StringBuffer sBody = new StringBuffer();
					sBody.append("namespace "+entityCls.getPackage().getName()+"\n{\n\tpublic class "+entityCls.getSimpleName()+"\n\t{\n");
					Field[] fields = entityCls.getDeclaredFields();
					Object entityInstance = entityCls.newInstance(); //to check the default value
					for(int i = 0; i < fields.length; i++) {
						sBody.append("\t\t");
						sBody.append(Modifier.toString(fields[i].getModifiers())+" ");
						sBody.append(convertJavaCSType(fields[i].getType())+" ");
						sBody.append(fields[i].getName());
						String defaultValue = hasDefault(entityInstance, fields[i]);
						if(defaultValue != null) {
							sBody.append(" = "+defaultValue);
						}
						sBody.append(";\n");
						if(!isPrimitiveType(fields[i].getType()) && !isSystemCollection(fields[i].getType())) {
							refreshRefer(fields[i].getType().getName(), entityCls, lowerReferMap);
						}
					}
					for(int i = 0; i < fields.length; i++) {
						sBody.append("\n\t\t");
						sBody.append("public ");
						sBody.append(convertJavaCSType(fields[i].getType())+" ");
						sBody.append(fields[i].getName().substring(0,1).toUpperCase() + fields[i].getName().substring(1)+"\n");
						sBody.append("\t\t{");
						sBody.append("\n\t\t\tget { return "+fields[i].getName()+"; }");
						sBody.append("\n\t\t\tset { "+fields[i].getName()+" = value; }\n\t\t}\n");
					}
					sBody.append("\t}\n}");
					
					StringBuffer sHead = new StringBuffer();
					sHead.append("using System;\nusing System.Collections;\n");
					List<String> rList = lowerReferMap.get(entityName);
					if(rList != null) {
						Set<String> referSet = new HashSet<String>();
						for(int j = 0; j < rList.size(); j++) {
							referSet.add(rList.get(j).substring(0, rList.get(j).lastIndexOf(".")));
						}
						Iterator<String> ite = referSet.iterator();
						while(ite.hasNext()) {
							sHead.append("using "+ite.next().toString()+";\n");
						}
					}
					
					BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(folder+entityCls.getSimpleName()+".cs"));
					bos.write(sHead.toString().getBytes());
					bos.write(sBody.toString().getBytes());
					bos.close();
					set.add(entityName);
					System.out.println(folder+entityCls.getSimpleName()+".cs generated.");
				}
			}
		}
		convertReferring(lowerReferMap, directory);
	}
	
	protected String[] readParaName(Class<?> cls, Method method) {
		String[] methodParaNames = null;
		StringBuffer fileStringBuffer = sourceFiles.get(cls.getName());
		MethodSignature ms = new MethodSignature();
		ms.setMethodName(method.getName());
		ms.setParaTypes(method.getParameterTypes());
		methodParaNames = paraNames.get(ms);
		return methodParaNames;
	}
	
	protected void loadParaName(String sourceFile, List<Class<?>> clsList) throws IOException {
		//load the source files
		JarFile jarFile = new JarFile(sourceFile);
		Enumeration<JarEntry> entries = jarFile.entries();
		while (entries.hasMoreElements()) {
			JarEntry jarEntry = entries.nextElement();
			String entryName = jarEntry.getName();
			if (jarEntry.isDirectory()) {
				//pass directory
			} else if (entryName.endsWith(".java")) {
				String javaFileName = entryName.replaceAll("/", ".").substring(0,
						entryName.length() - 5);
				try {
					StringBuffer sourceFileBuffer = new StringBuffer();
					BufferedReader reader = new BufferedReader(new InputStreamReader(jarFile.getInputStream(jarEntry)));
					String line = reader.readLine();
			        while (line != null) {
			        	sourceFileBuffer.append(line);
			        	sourceFileBuffer.append("\n");
			            line = reader.readLine();
			        }
			        sourceFiles.put(javaFileName, sourceFileBuffer);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		for(Class<?> cls : clsList) {
			if(cls.isInterface()) {
				StringBuffer sourceFileStringBuffer = sourceFiles.get(cls.getName());
				for(Method method : cls.getMethods()) {
					MethodSignature ms = new MethodSignature();
					ms.setMethodName(method.getName());
					ms.setParaTypes(method.getParameterTypes());
					String[] paraNamesArray = new String[method.getParameterTypes().length];
					StringBuffer patternSB = new StringBuffer(method.getName()+"\\s*\\(\\s*");
					for(int i = 0; i < method.getParameterTypes().length; i++) {
						patternSB.append(method.getParameterTypes()[i].getSimpleName()).append("\\s+(\\w+)\\s*");
						if(i != method.getParameterTypes().length - 1) {
							patternSB.append(",\\s*");
						} else {
							patternSB.append("\\)");
						}
					}
					Pattern pattern = Pattern.compile(patternSB.toString());
					//get the para name
					Matcher matcher = pattern.matcher(sourceFileStringBuffer);
					if (matcher.find()) {
						for(int j = 0; j < method.getParameterTypes().length; j++) {
							paraNamesArray[j] = matcher.group(j+1);
						}
					}
					paraNames.put(ms, paraNamesArray);
				}
			}
		}
		
	}
}
