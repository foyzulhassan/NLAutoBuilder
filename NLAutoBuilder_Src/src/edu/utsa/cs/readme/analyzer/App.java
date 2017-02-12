package edu.utsa.cs.readme.analyzer;

import java.net.URL;
import java.util.*;

import edu.utsa.cs.readme.analyzer.crawler.Spider;
import edu.utsa.cs.readme.analyzer.crawler.SpiderBuildCmdSearcher;
import edu.utsa.cs.readme.analyzer.entities.CmdRank;
import edu.utsa.cs.readme.analyzer.entities.CmdRankScore;
import edu.utsa.cs.readme.analyzer.entities.CmdRecognitionInfo;
import edu.utsa.cs.readme.analyzer.entities.CmdToExecute;
import edu.utsa.cs.readme.analyzer.entities.CmdWithPriority;
import edu.utsa.cs.readme.analyzer.entities.GitHubProjects;
import edu.utsa.cs.readme.analyzer.opennl.OpenNLPNER;
import edu.utsa.cs.readme.analyzer.ranking.CmdGroupMaker;
import edu.utsa.cs.readme.analyzer.ranking.CmdtoExecuteSelector;
import edu.utsa.cs.readme.analyzer.string.BuildCmdParser;
import edu.utsa.cs.readme.analyzer.string.FileLinkParser;
import edu.utsa.cs.readme.analyzer.util.FileFinder;
import edu.utsa.cs.readme.analyzer.util.LogPrinter;
import edu.utsa.cs.readme.analyzer.util.ReadConfigFile;
import edu.utsa.cs.readme.executor.CmdExecutor;
import edu.utsa.cs.repoanalysis.Config;

/**
 * Hello world!
 *
 *
 */
public class App {
	public static void main(String[] args) throws Exception {
		ReadConfigFile config = new ReadConfigFile();

		List<String> projnames = new ArrayList<String>();
		projnames = config.GetProjectNameList();

		List<String> projfolders = new ArrayList<String>();
		projfolders = config.GetProjectDirList();

		List<GitHubProjects> project = new ArrayList<GitHubProjects>();
		
		LogPrinter logprint=new LogPrinter();

		for (int i = 0; i < projnames.size(); i++) {
			GitHubProjects proj = new GitHubProjects();

			proj.setProjectName(projnames.get(i));
			proj.setProjectFolder(projfolders.get(i));

			// Here extracting Readme File Path
			String readmefile = FileFinder.GetReadmeFilePath(projfolders.get(i));
			if (readmefile != null)
				proj.setProjectReadmeFile(readmefile);

			// This is for extracting build commands from readme file thorugh
			// regular experssion
			if (readmefile != null) {

				List<String> htmllinks = new ArrayList<String>();

				htmllinks = FileLinkParser.pullLinks(proj.getProjectReadmeFile());

				SpiderBuildCmdSearcher spiderbuildcmdsearch = new SpiderBuildCmdSearcher();

				// spiderbuildcmdsearch.SearchAndDumpallBuildCmd(htmllinks,"/home/foyzulhassan/Research/Data/testfiles/");
				 spiderbuildcmdsearch.SearchAndDumpallBuildCmd(htmllinks,proj.getProjectFolder());

				String htmlbuildfile = FileFinder.GetHtmlCmdFilePath(projfolders.get(i));

				if (htmlbuildfile != null)
					proj.setProjectHtmlFile(htmlbuildfile);

			}
			project.add(proj);

		}

		
		URL location = App.class.getProtectionDomain().getCodeSource().getLocation();		
		//System.out.println(location.getPath());
		
		for (int i = 0; i < project.size(); i++) {
			boolean rdgrp = false;
			boolean htgrp = false;
			boolean rdcmd = false;
			boolean htcmd = false;

			GitHubProjects proj = project.get(i);

			OpenNLPNER opennlpobj = new OpenNLPNER(2, 1);

			List<CmdRecognitionInfo> cmdsfromreadme = new ArrayList<CmdRecognitionInfo>();
			List<CmdRecognitionInfo> cmdsfromhtml = new ArrayList<CmdRecognitionInfo>();

			if (proj.getProjectReadmeFile() != null)
				cmdsfromreadme = opennlpobj.recognize(proj.getProjectReadmeFile(),
						Config.openNLPModelFile);

			if (proj.getProjectHtmlFile() != null)
				cmdsfromhtml = opennlpobj.recognizehtmlcontent(proj.getProjectHtmlFile(),Config.openNLPModelFile);
			
			CmdRank rdgrpcmdrank=new CmdRank();
			CmdRank htgrpcmdrank=new CmdRank();
			CmdToExecute rdcmdtoexecute = new CmdToExecute();			
			CmdToExecute htcmdtoexecute = new CmdToExecute();
			
			
			CmdExecutor cmdexecutor=new CmdExecutor(proj.getProjectFolder());
			
			logprint.println("@@@Project Folder:"+proj.getProjectFolder());

			if (cmdsfromreadme != null) {				
				
				List<CmdRank> cmdranklist = new ArrayList<CmdRank>();
				cmdranklist = CmdGroupMaker.GetCmdGroup(cmdsfromreadme);

				if (CmdGroupMaker.IsCmdListContainsGroup(cmdranklist)) {
					rdgrpcmdrank = CmdGroupMaker.GetTheCmdGroup(cmdranklist);		
				}

			}

			if (cmdsfromhtml != null) {
				
				List<CmdRank> cmdranklist = new ArrayList<CmdRank>();
				cmdranklist = CmdGroupMaker.GetCmdGroup(cmdsfromhtml);

				if (CmdGroupMaker.IsCmdListContainsGroup(cmdranklist)) {
					htgrpcmdrank = CmdGroupMaker.GetTheCmdGroup(cmdranklist);
					
				}		

			}

			if (cmdsfromreadme != null) {
				
				CmdtoExecuteSelector cmdexecutselector = new CmdtoExecuteSelector();
				List<CmdRank> cmdranklist = new ArrayList<CmdRank>();
				cmdranklist = CmdGroupMaker.GetCmdGroup(cmdsfromreadme);
				
				rdcmdtoexecute = cmdexecutselector.GetCommandToExecute(cmdranklist, proj.getProjectFolder());			

			}

			if (cmdsfromhtml.size()>=1) {
				
				CmdtoExecuteSelector cmdexecutselector = new CmdtoExecuteSelector();
				List<CmdRank> cmdranklist = new ArrayList<CmdRank>();
				cmdranklist = CmdGroupMaker.GetCmdGroup(cmdsfromhtml);

				
				htcmdtoexecute = cmdexecutselector.GetCommandToExecute(cmdranklist, proj.getProjectFolder());
			

			}
			
			/***************************************Code Block To Select Which Cmd Should Execute***************************************************/
			if (rdcmdtoexecute.GetPreCmdListSize() >= 1 || rdcmdtoexecute.GetCmdListSize()>=1 || rdcmdtoexecute.GetPostCmdListSize()>=1)
			{
				List<CmdWithPriority> cmdprilist=new ArrayList<CmdWithPriority>();
				
				if(rdcmdtoexecute.GetPreCmdListSize() >= 1)
				{
					CmdWithPriority obj=new CmdWithPriority();
					obj.setCmdString(rdcmdtoexecute.GetPreCmdListItem(0).getCmdText());
					obj.setPriority(rdcmdtoexecute.GetPreCmdListItem(0).getPriorityScore());
					obj.setCmdType("rd");
					cmdprilist.add(obj);
					
				}
					
				
				if(rdcmdtoexecute.GetCmdListSize()>=1)
				{
										
					CmdWithPriority obj=new CmdWithPriority();
					
					obj.setCmdString(rdcmdtoexecute.GetCmdListItem(0).getCmdText());
					obj.setPriority(rdcmdtoexecute.GetCmdListItem(0).getPriorityScore());
					obj.setCmdType("rd");
					cmdprilist.add(obj);
					
				}
				
				if(rdcmdtoexecute.GetPostCmdListSize()>=1)
				{			
					
					CmdWithPriority obj=new CmdWithPriority();
					
					obj.setCmdString(rdcmdtoexecute.GetPostCmdListItem(0).getCmdText());
					obj.setPriority(rdcmdtoexecute.GetPostCmdListItem(0).getPriorityScore());
					obj.setCmdType("rd");
					cmdprilist.add(obj);
				}
				
				if(htcmdtoexecute.GetPreCmdListSize() >= 1)
				{
					CmdWithPriority obj=new CmdWithPriority();
					obj.setCmdString(htcmdtoexecute.GetPreCmdListItem(0).getCmdText());
					obj.setPriority(htcmdtoexecute.GetPreCmdListItem(0).getPriorityScore());
					obj.setCmdType("ht");
					cmdprilist.add(obj);
					
				}
					
				
				if(htcmdtoexecute.GetCmdListSize()>=1)
				{
										
					CmdWithPriority obj=new CmdWithPriority();
					
					obj.setCmdString(htcmdtoexecute.GetCmdListItem(0).getCmdText());
					obj.setPriority(htcmdtoexecute.GetCmdListItem(0).getPriorityScore());
					obj.setCmdType("ht");
					cmdprilist.add(obj);
					
				}
				
				if(htcmdtoexecute.GetPostCmdListSize()>=1)
				{			
					
					CmdWithPriority obj=new CmdWithPriority();
					
					obj.setCmdString(htcmdtoexecute.GetPostCmdListItem(0).getCmdText());
					obj.setPriority(htcmdtoexecute.GetPostCmdListItem(0).getPriorityScore());
					obj.setCmdType("ht");
					cmdprilist.add(obj);
				}
				
				Collections.sort(cmdprilist);
				
				if(cmdprilist.size()>=1)
				{
					String highestcmd=cmdprilist.get(0).getCmdString();
					
					if(cmdprilist.get(0).getCmdType().equals("ht"))
					{
						htcmd=true;
					}
					
					if(rdgrpcmdrank!=null)
					{
						for(int index=0;index<rdgrpcmdrank.GetCmdRecognitionGrpSize();index++)	
						{					
						
						   if(rdgrpcmdrank.GetCmdRecognitionInfo(index).getRecognizedCmdtxt().equals(highestcmd))
						   {
							   rdgrp=true;
						   }
						}
						
						if(rdgrp==false && htcmd==false)
						{
							rdcmd=true;
						}
					}					
				}
				
			}
			
			////Fot handling HTML cmds
			if(rdgrp==false && rdcmd==false)
			{
				if (htcmdtoexecute.GetPreCmdListSize() >= 1 || htcmdtoexecute.GetCmdListSize()>=1 || htcmdtoexecute.GetPostCmdListSize()>=1)
				{
					List<CmdWithPriority> cmdprilist=new ArrayList<CmdWithPriority>();
					
					if(htcmdtoexecute.GetPreCmdListSize() >= 1)
					{
						CmdWithPriority obj=new CmdWithPriority();
						obj.setCmdString(htcmdtoexecute.GetPreCmdListItem(0).getCmdText());
						obj.setPriority(htcmdtoexecute.GetPreCmdListItem(0).getPriorityScore());
						cmdprilist.add(obj);
						
					}
						
					
					if(htcmdtoexecute.GetCmdListSize()>=1)
					{
											
						CmdWithPriority obj=new CmdWithPriority();
						
						obj.setCmdString(htcmdtoexecute.GetCmdListItem(0).getCmdText());
						obj.setPriority(htcmdtoexecute.GetCmdListItem(0).getPriorityScore());
						cmdprilist.add(obj);
						
					}
					
					if(htcmdtoexecute.GetPostCmdListSize()>=1)
					{			
						
						CmdWithPriority obj=new CmdWithPriority();
						
						obj.setCmdString(htcmdtoexecute.GetPostCmdListItem(0).getCmdText());
						obj.setPriority(htcmdtoexecute.GetPostCmdListItem(0).getPriorityScore());
						cmdprilist.add(obj);
					}
					
					Collections.sort(cmdprilist);
					
					if(cmdprilist.size()>=1)
					{
						String highestcmd=cmdprilist.get(0).getCmdString();
						
						if(htgrpcmdrank!=null)
						{
							for(int index=0;index<htgrpcmdrank.GetCmdRecognitionGrpSize();index++)	
							{					
							
							   if(htgrpcmdrank.GetCmdRecognitionInfo(index).getRecognizedCmdtxt().equals(highestcmd))
							   {
								   htgrp=true;
							   }
							}
							
							if(htgrp==false)
							{
								htcmd=true;
							}
						}					
					}
					
				}
				
			}			
			
			
			if (rdgrp == true) {
				
				logprint.println("##############From Readme Group Test####################");
				
				if (rdgrpcmdrank != null) {
					for (int index = 0; index < rdgrpcmdrank.GetCmdRecognitionGrpSize(); index++) {

						logprint.println(rdgrpcmdrank.GetCmdRecognitionInfo(index).getRecognizedCmdtxt());

						cmdexecutor.ExecuteCommand(proj.getProjectFolder(),
								rdgrpcmdrank.GetCmdRecognitionInfo(index).getRecognizedCmdtxt(), location.getPath());
					}

				}

			} else if (rdcmd == true) {
				logprint.println("##############From Readme Rank Test####################");
				if (rdcmdtoexecute.GetPreCmdListSize() >= 1 || rdcmdtoexecute.GetCmdListSize() >= 1
						|| rdcmdtoexecute.GetPostCmdListSize() >= 1) {

					String precmd = "";
					String cmd = "";
					String postcmd = "";
					if (rdcmdtoexecute.GetPreCmdListSize() >= 1)
						precmd = rdcmdtoexecute.GetPreCmdListItem(0).getCmdText();

					if (rdcmdtoexecute.GetCmdListSize() >= 1)
						cmd = rdcmdtoexecute.GetCmdListItem(0).getCmdText();

					if (rdcmdtoexecute.GetPostCmdListSize() >= 1)
						postcmd = rdcmdtoexecute.GetPostCmdListItem(0).getCmdText();

					logprint.println(precmd);
					logprint.println(cmd);
					logprint.println(postcmd);

					if (precmd.length() > 0)
						cmdexecutor.ExecuteCommand(proj.getProjectFolder(), precmd, location.getPath());
					if (cmd.length() > 0)
						cmdexecutor.ExecuteCommand(proj.getProjectFolder(), cmd, location.getPath());
					if (postcmd.length() > 0)
						cmdexecutor.ExecuteCommand(proj.getProjectFolder(), postcmd, location.getPath());

				}

			} else if (htgrp == true) {
				logprint.println("##############From HTMLFILE Group Test####################");
				if (htgrpcmdrank != null) {
					for (int index = 0; index < htgrpcmdrank.GetCmdRecognitionGrpSize(); index++) {
						logprint.println(htgrpcmdrank.GetCmdRecognitionInfo(index).getRecognizedCmdtxt());
						cmdexecutor.ExecuteCommand(proj.getProjectFolder(),
								htgrpcmdrank.GetCmdRecognitionInfo(index).getRecognizedCmdtxt(), location.getPath());
					}

				}

			} else if (htcmd == true) {
				logprint.println("##############From HTML Rank Test####################");
				if (htcmdtoexecute.GetPreCmdListSize() >= 1 || htcmdtoexecute.GetCmdListSize() >= 1
						|| htcmdtoexecute.GetPostCmdListSize() >= 1) {

					String precmd = "";
					String cmd = "";
					String postcmd = "";
					if (htcmdtoexecute.GetPreCmdListSize() >= 1)
						precmd = htcmdtoexecute.GetPreCmdListItem(0).getCmdText();

					if (htcmdtoexecute.GetCmdListSize() >= 1)
						cmd = htcmdtoexecute.GetCmdListItem(0).getCmdText();

					if (htcmdtoexecute.GetPostCmdListSize() >= 1)
						postcmd = htcmdtoexecute.GetPostCmdListItem(0).getCmdText();

					logprint.println(precmd);
					logprint.println(cmd);
					logprint.println(postcmd);

					if (precmd.length() > 0)
						cmdexecutor.ExecuteCommand(proj.getProjectFolder(), precmd, location.getPath());
					if (cmd.length() > 0)
						cmdexecutor.ExecuteCommand(proj.getProjectFolder(), cmd, location.getPath());
					if (postcmd.length() > 0)
						cmdexecutor.ExecuteCommand(proj.getProjectFolder(), postcmd, location.getPath());
				}

			}			
			
			/*************************************************************End Block******************************************************************/		
			

		}
		
		System.out.println("Execution Completed!!!!");

	}
}
