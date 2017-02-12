package edu.utsa.cs.readme.analyzer.entities;

public class GitHubProjects {
	private String ProjectName;
	
	public String getProjectName() {
		return ProjectName;
	}


	public void setProjectName(String projectName) {
		ProjectName = projectName;
	}


	private String ProjectFolder;
	public String getProjectFolder() {
		return ProjectFolder;
	}


	public void setProjectFolder(String projectFolder) {
		ProjectFolder = projectFolder;
	}


	private String ProjectReadmeFile;
	public String getProjectReadmeFile() {
		return ProjectReadmeFile;
	}


	public void setProjectReadmeFile(String projectRedmineFile) {
		ProjectReadmeFile = projectRedmineFile;
	}


	private String ProjectHtmlFile;
	public String getProjectHtmlFile() {
		return ProjectHtmlFile;
	}


	public void setProjectHtmlFile(String projectHtmlFile) {
		ProjectHtmlFile = projectHtmlFile;
	}


	private String ProjectWikiFile;
	
	
	public String getProjectWikiFile() {
		return ProjectWikiFile;
	}


	public void setProjectWikiFile(String projectWikiFile) {
		ProjectWikiFile = projectWikiFile;
	}
	
	private boolean BuildCmdStatus; 


	public boolean isBuildCmdStatus() {
		return BuildCmdStatus;
	}


	public void setBuildCmdStatus(boolean buildCmdStatus) {
		BuildCmdStatus = buildCmdStatus;
	}
	
	private String ProjectWorkDir;	


	public String getProjectWorkDir() {
	    return ProjectWorkDir;
	}


	public void setProjectWorkDir(String projectWorkDir) {
	    ProjectWorkDir = projectWorkDir;
	}


	public GitHubProjects()
	{
		ProjectName=null;
		ProjectFolder=null;
		ProjectReadmeFile=null;
		ProjectHtmlFile=null;
		ProjectWikiFile=null;	
		ProjectWorkDir=null;
		BuildCmdStatus=false;		
	}

}
