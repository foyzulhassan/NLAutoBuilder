package edu.utsa.cs.repoanalysis;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.FileUtils;

import edu.utsa.cs.buildlog.analysis.BuildErrorLogAnalyzer;
import edu.utsa.cs.buildlog.analysis.BuildLogAnalyzer;
import edu.utsa.cs.filefolder.util.FolderManipulator;
import edu.utsa.cs.filefolder.util.ProjFinder;
import edu.utsa.cs.readme.analyzer.App;
import edu.utsa.cs.readme.analyzer.crawler.SpiderBuildCmdSearcher;
import edu.utsa.cs.readme.analyzer.entities.CmdRank;
import edu.utsa.cs.readme.analyzer.entities.CmdRecognitionInfo;
import edu.utsa.cs.readme.analyzer.entities.CmdToExecute;
import edu.utsa.cs.readme.analyzer.entities.CmdWithPriority;
import edu.utsa.cs.readme.analyzer.entities.GitHubProjects;
import edu.utsa.cs.readme.analyzer.entities.ProjectBuildStatus;
import edu.utsa.cs.readme.analyzer.opennl.OpenNLPNER;
import edu.utsa.cs.readme.analyzer.ranking.CmdGroupMaker;
import edu.utsa.cs.readme.analyzer.ranking.CmdtoExecuteSelector;
import edu.utsa.cs.readme.analyzer.string.FileLinkParser;
import edu.utsa.cs.readme.analyzer.util.FileFinder;
import edu.utsa.cs.readme.analyzer.util.LogPrinter;
import edu.utsa.cs.readme.analyzer.util.ReadConfigFile;
import edu.utsa.cs.readme.executor.CmdExecutor;
import edu.utsa.cs.repoanalysis.typeresolver.build.BuildJavaVersion;
import edu.utsa.cs.repoanalysis.typeresolver.build.BuildResult;
import edu.utsa.cs.repoanalysis.typeresolver.build.BuildType;
import edu.utsa.cs.repoanalysis.typeresolver.build.Builder;
import edu.utsa.cs.repoanalysis.typeresolver.build.JavacBuilder;
import edu.utsa.cs.repoanalysis.typeresolver.librepo.BaseResolver;
import edu.utsa.cs.repoanalysis.typeresolver.librepo.LibResolvingException;
import edu.utsa.cs.repoanalysis.typeresolver.librepo.LoadBasic;
import edu.utsa.cs.util.ConfigTypeChecker;
import edu.utsa.cs.util.FileManager;
import edu.utsa.cs.util.Logger;

public class Main {
    private static LogPrinter logprint=new LogPrinter();
    
    
    public static void main(String args[]) throws IOException {
	//Config.reconfig(args[0]);
	setupEnv();
	LoadBasic basicLoader = new LoadBasic(Config.jrePath,
		Config.libCacheDir);
	File srcRepo = new File(Config.srcRepoDir);
	
	//////////////////////////////////////////////////////////////////////////////
	
	ReadConfigFile config = new ReadConfigFile();

	List<String> projnames = new ArrayList<String>();
	projnames = config.GetProjectNameList();

	List<String> projfolders = new ArrayList<String>();
	projfolders = config.GetProjectDirList();

	List<GitHubProjects> project = new ArrayList<GitHubProjects>();
	
	LogPrinter logprint=new LogPrinter();
	
//	for (int i = 0; i < projnames.size(); i++) {
//		GitHubProjects proj = new GitHubProjects();
//
//		proj.setProjectName(projnames.get(i));
//		proj.setProjectFolder(projfolders.get(i));
//
//		// Here extracting Readme File Path
//		String readmefile = FileFinder.GetReadmeFilePath(projfolders.get(i));
//		
//		if (readmefile != null)
//			proj.setProjectReadmeFile(readmefile);
//
//		// This is for extracting build commands from readme file thorugh
//		// regular experssion
//		if (readmefile != null) {
//
//			List<String> htmllinks = new ArrayList<String>();
//
//			htmllinks = FileLinkParser.pullLinks(proj.getProjectReadmeFile());
//
//			//SpiderBuildCmdSearcher spiderbuildcmdsearch = new SpiderBuildCmdSearcher();
//
//			// spiderbuildcmdsearch.SearchAndDumpallBuildCmd(htmllinks,"/home/foyzulhassan/Research/Data/testfiles/");
//			// spiderbuildcmdsearch.SearchAndDumpallBuildCmd(htmllinks,proj.getProjectFolder());
//
//			String htmlbuildfile = FileFinder.GetHtmlCmdFilePath(projfolders.get(i));
//
//			if (htmlbuildfile != null)
//				proj.setProjectHtmlFile(htmlbuildfile);
//
//		}
//		project.add(proj);
//
//	}
	
	
	int readmeprojcount=0;
	int autobuilderprojcount=0;
	int hisbuildprojcount=0;
	int javacbuildprojcount=0;
	
	
	List<ProjectBuildStatus> projectsbuildstatuslst = new ArrayList<ProjectBuildStatus>();
	
	//for (int projindex = 0; projindex < project.size(); projindex++) {
	for (int i = 0; i < projnames.size(); i++) {

	    boolean isbuildsuccess = false;
	    
	    
	    GitHubProjects proj = new GitHubProjects();
	    ProjectBuildStatus projstatus=new ProjectBuildStatus();

	    proj.setProjectName(projnames.get(i));
	    proj.setProjectFolder(projfolders.get(i));
	    proj.setProjectWorkDir(Config.workDir + "/" + proj.getProjectName());
	    
	    
	    projstatus.setProjectName(proj.getProjectName());
	    
	    if ((new File(Config.logDir + "/" + proj.getProjectName()))
		    .exists()) {
		continue;
	    }
	    
	    System.out.println("Setup for Project:"+proj.getProjectName());
	    
	    setUpWorkDir(Config.srcRepoDir + "/"
			+ proj.getProjectName());
	    
	    URL location = App.class.getProtectionDomain().getCodeSource()
			.getLocation();
	    
	    CmdExecutor gitupdatecmdexecutor = new CmdExecutor(
			Config.logDir + "/" + proj.getProjectName());
	    
	    CmdExecutor gitcomitcmdexecutor = new CmdExecutor(
			Config.logDir + "/" + proj.getProjectName(),true);
	    
	    
	    
	    gitupdatecmdexecutor.ExecuteCommand(proj.getProjectWorkDir(), "git reset --hard",
		    location.getPath());
	    
	    gitupdatecmdexecutor.ExecuteCommand(proj.getProjectWorkDir(), "git pull origin",
		    location.getPath());
	    
	    String readmefile = FileFinder.GetReadmeFilePath(proj.getProjectWorkDir());
		
		if (readmefile != null)
			proj.setProjectReadmeFile(readmefile);

		// This is for extracting build commands from readme file thorugh
		// regular experssion
		if (readmefile != null) {

			List<String> htmllinks = new ArrayList<String>();

			htmllinks = FileLinkParser.pullLinks(proj.getProjectReadmeFile());

			//SpiderBuildCmdSearcher spiderbuildcmdsearch = new SpiderBuildCmdSearcher();

			// spiderbuildcmdsearch.SearchAndDumpallBuildCmd(htmllinks,"/home/foyzulhassan/Research/Data/testfiles/");
			// spiderbuildcmdsearch.SearchAndDumpallBuildCmd(htmllinks,proj.getProjectFolder());

			String htmlbuildfile = FileFinder.GetHtmlCmdFilePath(proj.getProjectWorkDir());

			if (htmlbuildfile != null)
				proj.setProjectHtmlFile(htmlbuildfile);

		}
	    

	    List<CmdRecognitionInfo> cmdsfromreadme = new ArrayList<CmdRecognitionInfo>();
	    List<CmdRecognitionInfo> cmdsfromhtml = new ArrayList<CmdRecognitionInfo>();

	    ///GitHubProjects proj = project.get(projindex);
	    
	 
	   
	 
	    
	  
	 
//	    if ((new File(Config.logDir + "/" + proj.getProjectName()))
//		    .exists()) {
//		continue;
//	    }
	    
	    

	    cmdsfromreadme = getBuildInstructionFromReadme(
		    proj.getProjectReadmeFile());
	    cmdsfromhtml = getBuildInstructionFromReadme(
		    proj.getProjectHtmlFile());

	    if (cmdsfromreadme.size() > 0 || cmdsfromhtml.size() > 0) {
		
		File logdir=new File(Config.logDir + "/" + proj.getProjectName());
		
		if (!logdir.exists())
		{
		    logdir.mkdirs();
		}

		System.out.println("Trying to build "
			+ proj.getProjectName() + "... With NLP");
		
		  
		
		 gitupdatecmdexecutor.ExecuteCommand(proj.getProjectWorkDir(), "ant clean",
			    location.getPath());
		 
		 gitupdatecmdexecutor.ExecuteCommand(proj.getProjectWorkDir(), "mvn clean",
			    location.getPath());
		 
		 gitupdatecmdexecutor.ExecuteCommand(proj.getProjectWorkDir(), "gradle clean",
			    location.getPath());
		 
		 gitcomitcmdexecutor.ExecuteGitCommand(proj.getProjectWorkDir(), "git log --pretty=format:%h -n 1",
			    location.getPath());
		 
		 

		executeBuildInstruction(proj, cmdsfromreadme, cmdsfromhtml);

		isbuildsuccess = BuildLogAnalyzer
			.isBuildSuccessful(Config.logDir + "/"
				+ proj.getProjectName() + "/build-readme.log");
		
		if(isbuildsuccess==true)
		{
		    readmeprojcount++;
		    
		    projstatus.setBuildSuccessfulType("NLP Build");
		    projstatus.setBuildStatus("SUCCESSFUL");
		}

	    }
	    
	    Logger logger;

	    if (isbuildsuccess == false) {

		// if(!proj.equals("Stanford-CS106A-master")){ continue; }
//		if ((new File(Config.logDir + "/" + proj.getProjectName()))
//			.exists()) {
//		    continue;
//		}
		
		gitupdatecmdexecutor.ExecuteCommand(proj.getProjectWorkDir(), "git pull origin",
			    location.getPath());

		List<String> folderlist = new ArrayList<String>();

		folderlist = ProjFinder.getProjectList(proj.getProjectWorkDir());

		for (String folderitem : folderlist) {
		    
		   Path pathAbsolute = Paths.get(proj.getProjectWorkDir());
		   Path pathBase = Paths.get(folderitem);
		   Path pathRelative = pathAbsolute.relativize(pathBase);
		   System.out.println(pathRelative);
		   
		    String relpathinprj=pathRelative.toString();
		    
		    if(relpathinprj.length()>0)
			relpathinprj="/"+relpathinprj;

		    try {
			String sumStr = "START...";
			System.out.println("Trying to build "
				+ proj.getProjectName() + "...");
//			setUpWorkDir(Config.srcRepoDir + "/"
//				+ proj.getProjectName());
			sumStr += "|SETUP:Success";
			logger= new Logger(proj.getProjectName()+relpathinprj,
				Logger.LEVEL_INFO);

			// try ant, maven, and javac
			int buildtype = 0;

			// buildtype=ConfigTypeChecker.getBuildConfigFileExits(Config.srcRepoDir+"/"+proj);
			buildtype = ConfigTypeChecker.getBuildConfigFileExits(
				proj.getProjectWorkDir()+relpathinprj);

			if (buildtype == 1) {
			    gitupdatecmdexecutor.ExecuteCommand(proj.getProjectWorkDir()+relpathinprj, "ant clean",
				    location.getPath());
			    logger.logTimeStart("Ant");
			    sumStr = sumStr + "|Ant:"
				    + tryBuild(proj.getProjectName()+relpathinprj, logger,
					    BuildType.Type_Ant,
					    BuildJavaVersion.JAVA_18,"0",false);
			    logger.logTimeEnd("Ant");
			    projstatus.setBuildConfType("Ant");
			}

			else if (buildtype == 2) {
			    gitupdatecmdexecutor.ExecuteCommand(proj.getProjectWorkDir()+relpathinprj, "mvn clean",
				    location.getPath());
			    logger.logTimeStart("Maven");
			    sumStr = sumStr + "|Maven:"
				    + tryBuild(proj.getProjectName()+relpathinprj, logger,
					    BuildType.Type_Maven,
					    BuildJavaVersion.JAVA_18,"0",false);
			    logger.logTimeEnd("Maven");
			    projstatus.setBuildConfType("Maven");
			}

			else if (buildtype == 3) {
			    logger.logTimeStart("Gradle");

			    boolean checkgradlew = false;

			    checkgradlew = ConfigTypeChecker
				    .isGradleBuildGradlewExists(
					    proj.getProjectWorkDir()+relpathinprj);

			    if (checkgradlew == false) {
				gitupdatecmdexecutor.ExecuteCommand(proj.getProjectWorkDir()+relpathinprj, "gradle clean",
					    location.getPath());
				sumStr = sumStr + "|Gradle:"
					+ tryBuild(proj.getProjectName()+relpathinprj,
						logger, BuildType.Type_Gradle,
						BuildJavaVersion.JAVA_18,"0",false);
			    } else {
				gitupdatecmdexecutor.ExecuteCommand(proj.getProjectWorkDir()+relpathinprj, "chmod 777 gradlew",
					    location.getPath());
				gitupdatecmdexecutor.ExecuteCommand(proj.getProjectWorkDir()+relpathinprj, "./gradlew clean",
					    location.getPath());
				sumStr = sumStr + "|Gradle:"
					+ tryBuild(proj.getProjectName()+relpathinprj,
						logger, BuildType.Type_Gradlew,
						BuildJavaVersion.JAVA_18,"0",false);
			    }

			    logger.logTimeEnd("Gradle");
			    projstatus.setBuildConfType("Gradle");
			}

			else {
			    logger.logTimeStart("Javac");
			    sumStr = sumStr + "|Javac:"
				    + tryBuild(proj.getProjectName()+relpathinprj, logger,
					    BuildType.Type_Javac,
					    BuildJavaVersion.JAVA_18,"0",false);
			    logger.logTimeEnd("Javac");
			    projstatus.setBuildConfType("Javac");
			}

			String flagPart = sumStr.substring(20);
			
			if (flagPart.indexOf("Success") != -1) {
			    // logger.logSummary(sumStr);
			    autobuilderprojcount++;
			    projstatus.setBuildSuccessfulType("Deafult AutoBuilder");
			    projstatus.setBuildStatus("SUCCESSFUL");			    
			    continue;
			}
			
			// resolve dependencies
			
			//jdk version issue begin
//			String buildlogpath;
//			
//			if (buildtype == 1)
//			    buildlogpath=logger.getBuildLogPath(BuildType.Type_Ant);
//			
//			else if (buildtype == 2)
//			    buildlogpath=logger.getBuildLogPath(BuildType.Type_Maven);
//			
//			else if (buildtype == 3)
//			    buildlogpath=logger.getBuildLogPath(BuildType.Type_Gradle);
//			
//			else
//			    buildlogpath=logger.getBuildLogPath(BuildType.Type_Javac);
//			
//			BuildErrorLogAnalyzer builderror=new BuildErrorLogAnalyzer(buildlogpath);
//			
//			boolean iscomperror=builderror.isBuuildLogContainsCompileError();
//			
//			if(iscomperror)
//			{
//			    	sumStr = "START...";
//			    	sumStr += "|SETUP:Success";
//				System.out.println("Trying to build with Java 1.7 "
//					+ proj.getProjectName() + "...");
//				logger = new Logger(proj.getProjectName()+relpathinprj,
//					Logger.LEVEL_INFO);
//
//				// try ant, maven, and javac
//				
//
//				// buildtype=ConfigTypeChecker.getBuildConfigFileExits(Config.srcRepoDir+"/"+proj);
//				buildtype = ConfigTypeChecker.getBuildConfigFileExits(
//					proj.getProjectFolder()+relpathinprj);
//
//				if (buildtype == 1) {
//
//				    logger.logTimeStart("Ant");
//				    sumStr = sumStr + "|Ant:"
//					    + tryBuild(proj.getProjectName()+relpathinprj, logger,
//						    BuildType.Type_Ant,
//						    BuildJavaVersion.JAVA_17,"0",false);
//				    logger.logTimeEnd("Ant");
//				}
//
//				else if (buildtype == 2) {
//				    logger.logTimeStart("Maven");
//				    sumStr = sumStr + "|Maven:"
//					    + tryBuild(proj.getProjectName()+relpathinprj, logger,
//						    BuildType.Type_Maven,
//						    BuildJavaVersion.JAVA_17,"0",false);
//				    logger.logTimeEnd("Maven");
//				}
//
//				else if (buildtype == 3) {
//				    logger.logTimeStart("Gradle");
//
//				    boolean checkgradlew = false;
//
//				    checkgradlew = ConfigTypeChecker
//					    .isGradleBuildGradlewExists(
//						    proj.getProjectFolder()+relpathinprj);
//
//				    if (checkgradlew == false) {
//					sumStr = sumStr + "|Gradle:"
//						+ tryBuild(proj.getProjectName()+relpathinprj,
//							logger, BuildType.Type_Gradle,
//							BuildJavaVersion.JAVA_17,"0",false);
//				    } else {
//					sumStr = sumStr + "|Gradle:"
//						+ tryBuild(proj.getProjectName()+relpathinprj,
//							logger, BuildType.Type_Gradlew,
//							BuildJavaVersion.JAVA_17,"0",false);
//				    }
//
//				    logger.logTimeEnd("Gradle");
//				}
//
//				else {
//				    logger.logTimeStart("Javac");
//				    sumStr = sumStr + "|Javac:"
//					    + tryBuild(proj.getProjectName()+relpathinprj, logger,
//						    BuildType.Type_Javac,
//						    BuildJavaVersion.JAVA_17,"0",false);
//				    logger.logTimeEnd("Javac");
//				}		    
//			    
//			}
			
			///jdk version issue end
			
			
			////////historical build start
			boolean buildfail=true;
			
			flagPart = sumStr.substring(20);
			if (flagPart.indexOf("Success") != -1) {
			    // logger.logSummary(sumStr);
			    continue;
			}
			
			if(buildfail)
			{
			    	sumStr = "START...";
			    	sumStr += "|SETUP:Success";
				System.out.println("Trying to build with Historical Build "
					+ proj.getProjectName() + "...");
				//logger = new Logger(proj.getProjectName()+relpathinprj,
				//	Logger.LEVEL_INFO);

				// try ant, maven, and javac
				

				// buildtype=ConfigTypeChecker.getBuildConfigFileExits(Config.srcRepoDir+"/"+proj);
				buildtype = ConfigTypeChecker.getBuildConfigFileExits(
					proj.getProjectWorkDir()+relpathinprj);

				if (buildtype == 1) {
				    gitupdatecmdexecutor.ExecuteCommand(proj.getProjectWorkDir()+relpathinprj, "ant clean",
					    location.getPath());

				    logger.logTimeStart("Ant");
				    sumStr = sumStr + "|Ant:"
					    + tryBuild(proj.getProjectName()+relpathinprj, logger,
						    BuildType.Type_His_Ant,
						    BuildJavaVersion.JAVA_00,"1",false);
				    logger.logTimeEnd("Ant");
				}

				else if (buildtype == 2) {
				    gitupdatecmdexecutor.ExecuteCommand(proj.getProjectWorkDir()+relpathinprj, "mvn clean",
					    location.getPath());
				    logger.logTimeStart("Maven");
				    sumStr = sumStr + "|Maven:"
					    + tryBuild(proj.getProjectName()+relpathinprj, logger,
						    BuildType.Type_His_Maven,
						    BuildJavaVersion.JAVA_00,"1",false);
				    logger.logTimeEnd("Maven");
				}

				else if (buildtype == 3) {
				    logger.logTimeStart("Gradle");

				    boolean checkgradlew = false;

				    checkgradlew = ConfigTypeChecker
					    .isGradleBuildGradlewExists(
						    proj.getProjectWorkDir()+relpathinprj);

				    if (checkgradlew == false) {
					gitupdatecmdexecutor.ExecuteCommand(proj.getProjectWorkDir()+relpathinprj, "gradle clean",
						    location.getPath());
					sumStr = sumStr + "|Gradle:"
						+ tryBuild(proj.getProjectName()+relpathinprj,
							logger, BuildType.Type_His_Gradle,
							BuildJavaVersion.JAVA_00,"1",false);
				    } else {
					gitupdatecmdexecutor.ExecuteCommand(proj.getProjectWorkDir()+relpathinprj, "chmod 777 gradlew",
												       
						    location.getPath());
					gitupdatecmdexecutor.ExecuteCommand(proj.getProjectWorkDir()+relpathinprj, "./gradlew clean",
						    location.getPath());
					sumStr = sumStr + "|Gradle:"
						+ tryBuild(proj.getProjectName()+relpathinprj,
							logger, BuildType.Type_His_Gradlew,
							BuildJavaVersion.JAVA_00,"1",false);
				    }

				    logger.logTimeEnd("Gradle");
				}

				else {
				    logger.logTimeStart("Javac");
				    sumStr = sumStr + "|Javac:"
					    + tryBuild(proj.getProjectName()+relpathinprj, logger,
						    BuildType.Type_His_Javac,
						    BuildJavaVersion.JAVA_00,"1",false);
				    logger.logTimeEnd("Javac");
				}		    
			    
			}
			
			
			///////historical build end 			
			
			flagPart = sumStr.substring(20);
			if (flagPart.indexOf("Success") != -1) {
			    // logger.logSummary(sumStr);
			    hisbuildprojcount++;
			    projstatus.setBuildSuccessfulType("Historical AutoBuilder");
			    projstatus.setBuildStatus("SUCCESSFUL");		
			    continue;
			}
			
//			setUpWorkDir(Config.srcRepoDir + "/"
//				+ proj.getProjectName());
			BaseResolver resolver = new BaseResolver(
				Config.workDir + "/" + proj.getProjectName(),
				logger);
			try {
			    resolver.resolve(basicLoader);
			    sumStr += "|RESOLVE:Success";
			} catch (LibResolvingException e) {
			    logger.log(e.getMessage(), Logger.LEVEL_IMPORTANT);
			    sumStr += "|RESOLVE:Failure";
			    e.printStackTrace();
			    cleanAndCopy(proj.getProjectName(), false);
			}

			// retry build
			Builder jb = new JavacBuilder();
			BuildResult result = jb.build(proj.getProjectName()+relpathinprj,
				logger, BuildJavaVersion.JAVA_18.getText(),"0");
			if (result.success()) {
			    sumStr += "|BUILD:Success";
			    projstatus.setBuildSuccessfulType("Javac Resolve and Build");
			    projstatus.setBuildStatus("SUCCESSFUL");	
			    
			    javacbuildprojcount++;
			} else {
			    sumStr += "|BUILD:Failure";
			}
			// sumStr += "|" + result.getMajorError();
			// sumStr += result.getSummary();
			cleanAndCopy(proj.getProjectName(), false);
			// logger.logSummary(sumStr);
		    } catch (IOException e) {
			e.printStackTrace();
		    } catch (InterruptedException e) {
			e.printStackTrace();
		    }

		} // *
	    }
	    
	    projectsbuildstatuslst.add(projstatus);
	    buildReporting(projectsbuildstatuslst);
	}

	buildReporting(projectsbuildstatuslst);
	
	System.out.println("Build Process Completed");
	System.out.println("Readme Build Successful Count::" + readmeprojcount
		+ " AutoBuilder Successful Count:" + autobuilderprojcount+" Historical Build Successful Count:"+ hisbuildprojcount+" Javac Build Successful Count:"+javacbuildprojcount);

    }
    
    
    private static void buildReporting(List<ProjectBuildStatus> projectsbuildstatuslst)
    {
	String fileName = Config.summaryLog;

        try {
            // Assume default encoding.
            FileWriter fileWriter =
                new FileWriter(fileName);

            // Always wrap FileWriter in BufferedWriter.
            BufferedWriter bufferedWriter =
                new BufferedWriter(fileWriter);
            
            
            
            for(int index=0;index<projectsbuildstatuslst.size();index++)
            {
        	ProjectBuildStatus projectstatus=projectsbuildstatuslst.get(index);
        	
        	bufferedWriter.write(projectstatus.getProjectName()+"----"+projectstatus.getBuildConfType()+"----"+projectstatus.getBuildStatus()+"----"+projectstatus.getBuildSuccessfulType()+"\n");
            }           

            // Always close files.
            bufferedWriter.close();
        }
        catch(IOException ex) {
            System.out.println(
                "Error writing to file '"
                + fileName + "'");
            // Or we could just do this:
            // ex.printStackTrace();
        }	
	
    }

    private static void setupEnv() {
	if (!(new File(Config.logDir)).exists()) {
	    (new File(Config.logDir)).mkdirs();
	}
	if (!(new File(Config.outDir)).exists()) {
	    (new File(Config.outDir)).mkdirs();
	}
    }

    private static String tryBuild(String proj, Logger logger, BuildType type, BuildJavaVersion javaversion,String hisbuild, boolean trycleanbuild)
	    throws IOException, InterruptedException {
	
	if(trycleanbuild==true)
	    setUpWorkDir(Config.srcRepoDir + "/" + proj);

	Builder b = Builder.createBuilder(type);
	BuildResult result = b.build(proj, logger, javaversion.getText(),hisbuild);
	if (result.success()) {
	    return "Success";
	} else {
	    return "Failure";
	}
    }

    private static void cleanAndCopy(String projName, boolean copy)
	    throws IOException {
	if (copy) {
	    FileUtils.copyDirectoryToDirectory(
		    new File(Config.workDir + '/' + projName),
		    new File(Config.outDir));
	}
	FileManager.recursiveDeleteDir(Config.workDir + '/' + projName);
    }

    private static void setUpWorkDir(String projPath) throws IOException {
	File workDirFile = new File(Config.workDir);
	if (!workDirFile.exists()) {
	    workDirFile.mkdirs();
	} else {
	    FileManager.recursiveDeleteDir(Config.workDir);
	    workDirFile.mkdirs();
	}
	
	try{
	    FileUtils.copyDirectoryToDirectory(new File(projPath),
			new File(Config.workDir)); 
	}catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	
    }
    
    

    private static List<CmdRecognitionInfo> getBuildInstructionFromReadme(
	    String projfile) {
	List<CmdRecognitionInfo> cmds = new ArrayList<CmdRecognitionInfo>();
	OpenNLPNER opennlpobj = new OpenNLPNER(2, 1);

	if (projfile != null)
	    try {
		cmds = opennlpobj.recognize(projfile,
			"/home/foyzulhassan/Research/Data/Label_1000/NFold_Final_Label/NFoldV4/models/en-ner-cmd-model5.bin");
	    } catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }

	return cmds;
    }

    private static void executeBuildInstruction(GitHubProjects proj,
	    List<CmdRecognitionInfo> cmdsfromreadme,
	    List<CmdRecognitionInfo> cmdsfromhtml) {
	boolean rdgrp = false;
	boolean htgrp = false;
	boolean rdcmd = false;
	boolean htcmd = false;

	CmdRank rdgrpcmdrank = new CmdRank();
	CmdRank htgrpcmdrank = new CmdRank();
	CmdToExecute rdcmdtoexecute = new CmdToExecute();
	CmdToExecute htcmdtoexecute = new CmdToExecute();

	URL location = App.class.getProtectionDomain().getCodeSource()
		.getLocation();
	//System.out.println(location.getPath());

	FolderManipulator.createFolderIfNotExists(
		Config.logDir + "/" + proj.getProjectName());

	CmdExecutor cmdexecutor = new CmdExecutor(
		Config.logDir + "/" + proj.getProjectName());

	logprint.println("@@@Project Folder:" + proj.getProjectWorkDir());

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

	    rdcmdtoexecute = cmdexecutselector.GetCommandToExecute(cmdranklist,
		    proj.getProjectWorkDir());

	}

	if (cmdsfromhtml.size() >= 1) {

	    CmdtoExecuteSelector cmdexecutselector = new CmdtoExecuteSelector();
	    List<CmdRank> cmdranklist = new ArrayList<CmdRank>();
	    cmdranklist = CmdGroupMaker.GetCmdGroup(cmdsfromhtml);

	    htcmdtoexecute = cmdexecutselector.GetCommandToExecute(cmdranklist,
		    proj.getProjectWorkDir());

	}

	/***************************************
	 * Code Block To Select Which Cmd Should Execute
	 ***************************************************/
	if (rdcmdtoexecute.GetPreCmdListSize() >= 1
		|| rdcmdtoexecute.GetCmdListSize() >= 1
		|| rdcmdtoexecute.GetPostCmdListSize() >= 1) {
	    List<CmdWithPriority> cmdprilist = new ArrayList<CmdWithPriority>();

	    if (rdcmdtoexecute.GetPreCmdListSize() >= 1) {
		CmdWithPriority obj = new CmdWithPriority();
		obj.setCmdString(
			rdcmdtoexecute.GetPreCmdListItem(0).getCmdText());
		obj.setPriority(
			rdcmdtoexecute.GetPreCmdListItem(0).getPriorityScore());
		obj.setCmdType("rd");
		cmdprilist.add(obj);

	    }

	    if (rdcmdtoexecute.GetCmdListSize() >= 1) {

		CmdWithPriority obj = new CmdWithPriority();

		obj.setCmdString(rdcmdtoexecute.GetCmdListItem(0).getCmdText());
		obj.setPriority(
			rdcmdtoexecute.GetCmdListItem(0).getPriorityScore());
		obj.setCmdType("rd");
		cmdprilist.add(obj);

	    }

	    if (rdcmdtoexecute.GetPostCmdListSize() >= 1) {

		CmdWithPriority obj = new CmdWithPriority();

		obj.setCmdString(
			rdcmdtoexecute.GetPostCmdListItem(0).getCmdText());
		obj.setPriority(rdcmdtoexecute.GetPostCmdListItem(0)
			.getPriorityScore());
		obj.setCmdType("rd");
		cmdprilist.add(obj);
	    }

	    if (htcmdtoexecute.GetPreCmdListSize() >= 1) {
		CmdWithPriority obj = new CmdWithPriority();
		obj.setCmdString(
			htcmdtoexecute.GetPreCmdListItem(0).getCmdText());
		obj.setPriority(
			htcmdtoexecute.GetPreCmdListItem(0).getPriorityScore());
		obj.setCmdType("ht");
		cmdprilist.add(obj);

	    }

	    if (htcmdtoexecute.GetCmdListSize() >= 1) {

		CmdWithPriority obj = new CmdWithPriority();

		obj.setCmdString(htcmdtoexecute.GetCmdListItem(0).getCmdText());
		obj.setPriority(
			htcmdtoexecute.GetCmdListItem(0).getPriorityScore());
		obj.setCmdType("ht");
		cmdprilist.add(obj);

	    }

	    if (htcmdtoexecute.GetPostCmdListSize() >= 1) {

		CmdWithPriority obj = new CmdWithPriority();

		obj.setCmdString(
			htcmdtoexecute.GetPostCmdListItem(0).getCmdText());
		obj.setPriority(htcmdtoexecute.GetPostCmdListItem(0)
			.getPriorityScore());
		obj.setCmdType("ht");
		cmdprilist.add(obj);
	    }

	    Collections.sort(cmdprilist);

	    if (cmdprilist.size() >= 1) {
		String highestcmd = cmdprilist.get(0).getCmdString();

		if (cmdprilist.get(0).getCmdType().equals("ht")) {
		    htcmd = true;
		}

		if (rdgrpcmdrank != null) {
		    for (int index = 0; index < rdgrpcmdrank
			    .GetCmdRecognitionGrpSize(); index++) {

			if (rdgrpcmdrank.GetCmdRecognitionInfo(index)
				.getRecognizedCmdtxt().equals(highestcmd)) {
			    rdgrp = true;
			}
		    }

		    if (rdgrp == false && htcmd == false) {
			rdcmd = true;
		    }
		}
	    }

	}

	//// Fot handling HTML cmds
	if (rdgrp == false && rdcmd == false) {
	    if (htcmdtoexecute.GetPreCmdListSize() >= 1
		    || htcmdtoexecute.GetCmdListSize() >= 1
		    || htcmdtoexecute.GetPostCmdListSize() >= 1) {
		List<CmdWithPriority> cmdprilist = new ArrayList<CmdWithPriority>();

		if (htcmdtoexecute.GetPreCmdListSize() >= 1) {
		    CmdWithPriority obj = new CmdWithPriority();
		    obj.setCmdString(
			    htcmdtoexecute.GetPreCmdListItem(0).getCmdText());
		    obj.setPriority(htcmdtoexecute.GetPreCmdListItem(0)
			    .getPriorityScore());
		    cmdprilist.add(obj);

		}

		if (htcmdtoexecute.GetCmdListSize() >= 1) {

		    CmdWithPriority obj = new CmdWithPriority();

		    obj.setCmdString(
			    htcmdtoexecute.GetCmdListItem(0).getCmdText());
		    obj.setPriority(htcmdtoexecute.GetCmdListItem(0)
			    .getPriorityScore());
		    cmdprilist.add(obj);

		}

		if (htcmdtoexecute.GetPostCmdListSize() >= 1) {

		    CmdWithPriority obj = new CmdWithPriority();

		    obj.setCmdString(
			    htcmdtoexecute.GetPostCmdListItem(0).getCmdText());
		    obj.setPriority(htcmdtoexecute.GetPostCmdListItem(0)
			    .getPriorityScore());
		    cmdprilist.add(obj);
		}

		Collections.sort(cmdprilist);

		if (cmdprilist.size() >= 1) {
		    String highestcmd = cmdprilist.get(0).getCmdString();

		    if (htgrpcmdrank != null) {
			for (int index = 0; index < htgrpcmdrank
				.GetCmdRecognitionGrpSize(); index++) {

			    if (htgrpcmdrank.GetCmdRecognitionInfo(index)
				    .getRecognizedCmdtxt().equals(highestcmd)) {
				htgrp = true;
			    }
			}

			if (htgrp == false) {
			    htcmd = true;
			}
		    }
		}

	    }

	}

	if (rdgrp == true) {

	    logprint.println(
		    "##############From Readme Group Test####################");

	    if (rdgrpcmdrank != null) {
		for (int index = 0; index < rdgrpcmdrank
			.GetCmdRecognitionGrpSize(); index++) {

		    logprint.println(rdgrpcmdrank.GetCmdRecognitionInfo(index)
			    .getRecognizedCmdtxt());

		    cmdexecutor
			    .ExecuteCommand(proj.getProjectWorkDir(),
				    rdgrpcmdrank.GetCmdRecognitionInfo(index)
					    .getRecognizedCmdtxt(),
				    location.getPath());
		}

	    }

	} else if (rdcmd == true) {
	    logprint.println(
		    "##############From Readme Rank Test####################");
	    if (rdcmdtoexecute.GetPreCmdListSize() >= 1
		    || rdcmdtoexecute.GetCmdListSize() >= 1
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
		    cmdexecutor.ExecuteCommand(proj.getProjectWorkDir(), precmd,
			    location.getPath());
		if (cmd.length() > 0)
		    cmdexecutor.ExecuteCommand(proj.getProjectWorkDir(), cmd,
			    location.getPath());
		if (postcmd.length() > 0)
		    cmdexecutor.ExecuteCommand(proj.getProjectWorkDir(), postcmd,
			    location.getPath());

	    }

	} else if (htgrp == true) {
	    logprint.println(
		    "##############From HTMLFILE Group Test####################");
	    if (htgrpcmdrank != null) {
		for (int index = 0; index < htgrpcmdrank
			.GetCmdRecognitionGrpSize(); index++) {
		    logprint.println(htgrpcmdrank.GetCmdRecognitionInfo(index)
			    .getRecognizedCmdtxt());
		    cmdexecutor
			    .ExecuteCommand(proj.getProjectWorkDir(),
				    htgrpcmdrank.GetCmdRecognitionInfo(index)
					    .getRecognizedCmdtxt(),
				    location.getPath());
		}

	    }

	} else if (htcmd == true) {
	    logprint.println(
		    "##############From HTML Rank Test####################");
	    if (htcmdtoexecute.GetPreCmdListSize() >= 1
		    || htcmdtoexecute.GetCmdListSize() >= 1
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
		    cmdexecutor.ExecuteCommand(proj.getProjectWorkDir(), precmd,
			    location.getPath());
		if (cmd.length() > 0)
		    cmdexecutor.ExecuteCommand(proj.getProjectWorkDir(), cmd,
			    location.getPath());
		if (postcmd.length() > 0)
		    cmdexecutor.ExecuteCommand(proj.getProjectWorkDir(), postcmd,
			    location.getPath());
	    }

	}

    }

}


//public class Main {
//    public static void main(String args[]) throws IOException {
//	//Config.reconfig(args[0]);
//	setupEnv();
//	LoadBasic basicLoader = new LoadBasic(Config.jrePath,
//		Config.libCacheDir);
//	File srcRepo = new File(Config.srcRepoDir);
//	
//	for (String proj : srcRepo.list()) {
//	    
////	    if(!proj.equals("Stanford-CS106A-master")){ continue; }
//	    if((new File(Config.logDir + "/" + proj)).exists()){continue;}
//	     
//	    try {
//		String sumStr = "START...";
//		System.out.println("Trying to build " + proj + "...");
//		setUpWorkDir(Config.srcRepoDir + "/" + proj);
//		sumStr += "|SETUP:Success";
//		Logger logger = new Logger(proj, Logger.LEVEL_INFO);
//
//		//try ant, maven, and javac
//		int buildtype=0;
//		
//		buildtype=ConfigTypeChecker.getBuildConfigFileExits(Config.srcRepoDir+"/"+proj);
//		
//		if (buildtype==1) {
//
//		    logger.logTimeStart("Ant");
//		    sumStr = sumStr + "|Ant:"
//			    + tryBuild(proj, logger, BuildType.Type_Ant);
//		    logger.logTimeEnd("Ant");
//		}
//		
//		else if(buildtype==2)
//		{
//		    logger.logTimeStart("Maven");
//		    sumStr = sumStr + "|Maven:" + tryBuild(proj, logger, BuildType.Type_Maven);
//		    logger.logTimeEnd("Maven");
//		}
//		
//		else if(buildtype==3)
//		{
//		    logger.logTimeStart("Gradle");
//		    sumStr = sumStr + "|Gradle:" + tryBuild(proj, logger, BuildType.Type_Gradle);
//		    logger.logTimeEnd("Gradle");
//		}
//		
//		else
//		{
//		    logger.logTimeStart("Javac");
//		    sumStr = sumStr + "|Javac:" + tryBuild(proj, logger, BuildType.Type_Javac);
//		    logger.logTimeEnd("Javac");
//		}
//		
//		String flagPart = sumStr.substring(20);
//		if(flagPart.indexOf("Success")!=-1){
//		//    logger.logSummary(sumStr);
//		    continue;
//		}
//		//resolve dependencies
//		setUpWorkDir(Config.srcRepoDir + "/" + proj);
//		BaseResolver resolver = new BaseResolver(Config.srcRepoDir
//			+ "/" + proj, logger);
//		try {
//		    resolver.resolve(basicLoader);
//		    sumStr += "|RESOLVE:Success";
//		} catch (LibResolvingException e) {
//		    logger.log(e.getMessage(), Logger.LEVEL_IMPORTANT);
//		    sumStr += "|RESOLVE:Failure";
//		    e.printStackTrace();
//		}
//		
//		//retry build
//		Builder jb = new JavacBuilder();
//		BuildResult result = jb.build(proj, logger);
//		if(result.success()){
//		    sumStr += "|BUILD:Success";
//		}else{
//		    sumStr += "|BUILD:Failure";
//		}
////		sumStr += "|" + result.getMajorError();
////		sumStr += result.getSummary();
//		cleanAndCopy(proj, true);
//	//	logger.logSummary(sumStr);
//	    } catch (IOException e) {
//		e.printStackTrace();
//	    } catch (InterruptedException e) {
//		e.printStackTrace();
//	    }
//	}
//    }
//
//    private static void setupEnv() {
//	if(!(new File(Config.logDir)).exists()){
//	    (new File(Config.logDir)).mkdirs();
//	}
//	if(!(new File(Config.outDir)).exists()){
//	    (new File(Config.outDir)).mkdirs();
//	}
//    }
//
//    private static String tryBuild(String proj, Logger logger, BuildType type)
//	    throws IOException, InterruptedException {
//	setUpWorkDir(Config.srcRepoDir + "/" + proj);
//
//	Builder b = Builder.createBuilder(type);
//	BuildResult result = b.build(proj, logger);
//	if(result.success()){
//	    return "Success";
//	}else{
//	    return "Failure";
//	}
//    }
//
//    private static void cleanAndCopy(String projName, boolean copy) throws IOException {
//	if (copy){
//	    FileUtils.copyDirectoryToDirectory(new File(Config.workDir + '/'
//		+ projName), new File(Config.outDir));
//	}
//	FileManager.recursiveDeleteDir(Config.workDir + '/' + projName);
//    }
//
//    private static void setUpWorkDir(String projPath) throws IOException {
//	File workDirFile = new File(Config.workDir);
//	if (!workDirFile.exists()) {
//	    workDirFile.mkdirs();
//	} else {
//	    FileManager.recursiveDeleteDir(Config.workDir);
//	    workDirFile.mkdirs();
//	}
//	FileUtils.copyDirectoryToDirectory(new File(projPath), new File(
//		Config.workDir));
//    }
//
//}


