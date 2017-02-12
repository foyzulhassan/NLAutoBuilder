package edu.utsa.cs.readme.analyzer.entities;

public class CmdWithPriority implements Comparable<CmdWithPriority>{
	private int priority;
	private String cmdString;
	private String cmdType;
	
	public String getCmdType() {
		return cmdType;
	}
	public void setCmdType(String cmdType) {
		this.cmdType = cmdType;
	}
	public int getPriority() {
		return priority;
	}
	public void setPriority(int priority) {
		this.priority = priority;
	}
	public String getCmdString() {
		return cmdString;
	}
	public void setCmdString(String cmdString) {
		this.cmdString = cmdString;
	}
	@Override
	public int compareTo(CmdWithPriority comparereq) {
		long comparereqtime=((CmdWithPriority)comparereq).getPriority();
	    /* For Decending order*/
	    return (int)(comparereqtime-this.getPriority());
	}

}
