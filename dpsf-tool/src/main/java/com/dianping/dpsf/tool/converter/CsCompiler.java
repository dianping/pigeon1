package com.dianping.dpsf.tool.converter;

import java.io.IOException;

/** 
 * csc /target:library /out:D:/tmp/ElectSystem.dll /recurse:"*.cs"
 */
public class CsCompiler {
	private String partA = "csc /target:library /out:";
	private String partB = " /recurse:";
	private String partC = "*.cs";
	
	public void complie(String cscPath, String dllName, String srcPath) throws IOException {
		cscPath = cscPath.replaceAll("\\\\", "/");
		if(!cscPath.endsWith("/")) {
			cscPath = cscPath+"/";
		}
		if(!srcPath.endsWith("\\")) {
			srcPath = srcPath+"\\";
		}
		if(!dllName.endsWith("\\.dll")) {
			dllName = dllName+".dll";
		}
		String dllPackageCMD = cscPath+partA+srcPath+dllName+partB+srcPath+partC;
		String[] cmds = new String[]{"cmd.exe","/C",
				dllPackageCMD};
		Runtime.getRuntime().exec(cmds);
		System.out.println(srcPath+dllName+" generated.");
	}
}
