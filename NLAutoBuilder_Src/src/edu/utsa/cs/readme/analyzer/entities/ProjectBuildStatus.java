package edu.utsa.cs.readme.analyzer.entities;

public class ProjectBuildStatus {
    private String projectName;
    private String buildConfType;
    private String buildStatus;
    private String buildSuccessfulType;
    
    
    public ProjectBuildStatus()
    {
	this.projectName=" ";
	this.buildConfType=" ";
	this.buildStatus="FAILED";
	this.buildSuccessfulType=" ";
    }
    
    
    public String getProjectName() {
        return projectName;
    }
    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }
    public String getBuildConfType() {
        return buildConfType;
    }
    public void setBuildConfType(String buildConfType) {
        this.buildConfType = buildConfType;
    }
    public String getBuildStatus() {
        return buildStatus;
    }
    public void setBuildStatus(String buildStatus) {
        this.buildStatus = buildStatus;
    }
    public String getBuildSuccessfulType() {
        return buildSuccessfulType;
    }
    public void setBuildSuccessfulType(String buildSuccessfulType) {
        this.buildSuccessfulType = buildSuccessfulType;
    }
    
    

}
