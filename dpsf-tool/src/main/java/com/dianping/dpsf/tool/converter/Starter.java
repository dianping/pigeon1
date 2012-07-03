package com.dianping.dpsf.tool.converter;

import java.util.List;

public class Starter {
	/**
	 * <ul>
	 * <li>args[0] jar file path, such as D:\dianping\logger2\JInterfaces.jar</li>
	 * <li>args[1] work home, such as D:\dianping\logger2\</li>
	 * <li>args[2] dll name, such as ElectSystem.dll</li>
	 * <li>args[3] csc.exe path, such as C:\\Windows\\Microsoft.NET\\Framework\\v4.0.30319\\</li>
	 * <ul>
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		if(args.length < 5 || args[0]==null || args[1]==null || args[2]==null || args[3]==null) {
			System.out.println("用法: java -jar converter.jar <arg0> <arg1> <arg2> <arg3> <arg4>\n");
			System.out.println("参数说明:");
			System.out.println("\targ0\tjar file path, such as D:\\dianping\\logger2\\JInterfaces.jar");
			System.out.println("\targ1\tsource code jar file path, such as D:\\dianping\\logger2\\JInterfacescode.jar, for extracting the para-names,");
			System.out.println("\targ2\twork home, such as D:\\dianping\\logger2\\");
			System.out.println("\targ3\tsuch as ElectSystem.dll");
			System.out.println("\targ4\tcsc.exe path, such as C:\\Windows\\Microsoft.NET\\Framework\\v4.0.30319\\\n");
			System.exit(1);
		}
		AppClassFinder acf = new AppClassFinder();
		Java2csInterfaceConverter jcs = new Java2csInterfaceConverter();
		CsCompiler cc = new CsCompiler();
		
		List<Class<?>> clsList = acf.loadClass(args[0]);
		jcs.csFileConvert(clsList, args[2], args[1]);
		cc.complie(args[4], args[3], args[2]);
	}

}
