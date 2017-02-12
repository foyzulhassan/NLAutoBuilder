package edu.utsa.cs.readme.executor;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import edu.utsa.cs.readme.analyzer.util.LogPrinter;

public class ProcessHandler extends Thread {

	InputStream inpStr;
	String strType;
	LogPrinter logprint;

	public ProcessHandler(InputStream inpStr, String strType) {
		this.inpStr = inpStr;
		this.strType = strType;
		this.logprint=new LogPrinter();
	}
	
	public ProcessHandler(InputStream inpStr, String strType,LogPrinter logprint) {
		this.inpStr = inpStr;
		this.strType = strType;
		this.logprint=logprint;
	}

	public void run() 
	{
		try {
			InputStreamReader inpStrd = new InputStreamReader(inpStr);
			BufferedReader buffRd = new BufferedReader(inpStrd);
			String line = null;
			while((line = buffRd.readLine()) != null) {
				//logprint.println(strType + "-->" + line);	
				logprint.println(line);	
			}
			buffRd.close();				
		} catch(Exception e) {
			System.out.println(e);
		}

	}
}