package edu.utsa.cs.readme.executor;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

import edu.utsa.cs.readme.analyzer.util.LogPrinter;

public class CmdExecutor
{
	private LogPrinter logprinter;
	
	public CmdExecutor(String path)
	{
		logprinter=new LogPrinter(path);
	}
	
	public CmdExecutor(String path, boolean infocmd)
	{
		logprinter=new LogPrinter(path,infocmd);
	}
	
	
	
	public CmdExecutor()
	{
		logprinter=new LogPrinter();
	}
	
	
	public boolean ExecuteCommand(String executionpath, String cmd,String restorepath)
	{
		boolean ret = false;
		String cmdtoexecute = "";
		StringBuffer output = new StringBuffer();

		if (cmd.contains("gradlew ")) {
			if(!cmd.startsWith("./"))
				cmdtoexecute = "./" + cmd;
			else
				cmdtoexecute = cmd;
		} else {
			cmdtoexecute = cmd;
		}

		String[] cmds = cmdtoexecute.split(" ");

		File exefolder = new File(executionpath);
		
		
		Process p;

		try {
			p = Runtime.getRuntime().exec(cmds, null, exefolder);

			ProcessHandler inputStream = new ProcessHandler(p.getInputStream(), "INPUT",this.logprinter);
			ProcessHandler errorStream = new ProcessHandler(p.getErrorStream(), "ERROR",this.logprinter);

			/* start the stream threads */
			inputStream.start();
			errorStream.start();

			inputStream.join(2000000);
			errorStream.join(2000000);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return ret;
	}
	
	public boolean ExecuteGitCommand(String executionpath, String cmd,String restorepath)
	{
		boolean ret = false;
		String cmdtoexecute = "";
		StringBuffer output = new StringBuffer();

		if (cmd.contains("gradlew ")) {
			if(!cmd.startsWith("./"))
				cmdtoexecute = "./" + cmd;
			else
				cmdtoexecute = cmd;
		} else {
			cmdtoexecute = cmd;
		}

		String[] cmds = cmdtoexecute.split(" ");

		File exefolder = new File(executionpath);
		
		
		Process p;

		try {
			p = Runtime.getRuntime().exec(cmdtoexecute, null, exefolder);

			ProcessHandler inputStream = new ProcessHandler(p.getInputStream(), "INPUT",this.logprinter);
			ProcessHandler errorStream = new ProcessHandler(p.getErrorStream(), "ERROR",this.logprinter);

			/* start the stream threads */
			inputStream.start();
			errorStream.start();

			inputStream.join(2000000);
			errorStream.join(2000000);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return ret;
	}
	
		
	
	public boolean ExecuteCommand(String executionpath, String cmd,String restorepath, boolean infocmd)
	{
		boolean ret = false;
		String cmdtoexecute = "";
		StringBuffer output = new StringBuffer();

		if (cmd.contains("gradlew ")) {
			if(!cmd.startsWith("./"))
				cmdtoexecute = "./" + cmd;
			else
				cmdtoexecute = cmd;
		} else {
			cmdtoexecute = cmd;
		}

		String[] cmds = cmdtoexecute.split(" ");

		File exefolder = new File(executionpath);
		
		
		Process p;

		try {
			p = Runtime.getRuntime().exec(cmds, null, exefolder);

			ProcessHandler inputStream = new ProcessHandler(p.getInputStream(), "INPUT",this.logprinter);
			ProcessHandler errorStream = new ProcessHandler(p.getErrorStream(), "ERROR",this.logprinter);

			/* start the stream threads */
			inputStream.start();
			errorStream.start();

			inputStream.join(2000000);
			errorStream.join(2000000);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return ret;
	}
	
	
	public static String GetCurrentPath() {
		String pathto = "";

		Process p;
		try {
			p = Runtime.getRuntime().exec("sh -c pwd");
			p.waitFor();
			BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

			String line = "";

			while ((line = reader.readLine()) != null) {
				pathto = line;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return pathto;

	}
	
}
