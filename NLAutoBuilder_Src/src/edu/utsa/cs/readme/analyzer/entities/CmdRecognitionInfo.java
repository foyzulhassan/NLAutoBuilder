package edu.utsa.cs.readme.analyzer.entities;

public class CmdRecognitionInfo 
{
	private String beforeCmdtxt;
	private String recognizedCmdtxt;
	private String actualCmdtxt;
	private int lineNumber;
	
	public String getBeforeCmdtxt() {
		return beforeCmdtxt;
	}
	public void setBeforeCmdtxt(String beforeCmdtxt) {
		this.beforeCmdtxt = beforeCmdtxt;
	}
	public String getRecognizedCmdtxt() {
		return recognizedCmdtxt;
	}
	public void setRecognizedCmdtxt(String recognizedCmdtxt) {
		this.recognizedCmdtxt = recognizedCmdtxt;
	}
	public String getActualCmdtxt() {
		return actualCmdtxt;
	}
	public void setActualCmdtxt(String actualCmdtxt) {
		this.actualCmdtxt = actualCmdtxt;
	}
	public int getLineNumber() {
		return lineNumber;
	}
	public void setLineNumber(int lineNumber) {
		this.lineNumber = lineNumber;
	}
}
