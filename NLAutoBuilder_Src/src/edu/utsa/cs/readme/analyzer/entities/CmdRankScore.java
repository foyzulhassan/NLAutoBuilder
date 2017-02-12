package edu.utsa.cs.readme.analyzer.entities;

public class CmdRankScore implements Comparable<CmdRankScore>{
	private String cmdText;
	private int priorityScore;
	
	public CmdRankScore()
	{
		this.cmdText="";
		this.priorityScore=0;
	}
	
	public String getCmdText() {
		return cmdText;
	}
	public void setCmdText(String cmdText) {
		this.cmdText = cmdText;
	}
	public int getPriorityScore() {
		return priorityScore;
	}
	public void setPriorityScore(int priorityScore) {
		this.priorityScore = priorityScore;
	}

	@Override
	public int compareTo(CmdRankScore comparereq) {
		long comparereqtime=((CmdRankScore)comparereq).getPriorityScore();
	    /* For Decending order*/
	    return (int)(comparereqtime-this.getPriorityScore());
	}

}
