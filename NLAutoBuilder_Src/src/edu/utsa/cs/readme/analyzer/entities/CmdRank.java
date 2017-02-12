package edu.utsa.cs.readme.analyzer.entities;

import java.util.ArrayList;
import java.util.List;

public class CmdRank {
	private String cmd;
	private int rank;
	private int subrank;	
	private int txtrank;
	private List<CmdRecognitionInfo> cmdrecognitionlist;
	
	
	public CmdRank()
	{
		cmdrecognitionlist=new ArrayList<CmdRecognitionInfo>();
	}
	
	public int getSubrank() {
		return subrank;
	}

	public void setSubrank(int subrank) {
		this.subrank = subrank;
	}

	public int getTxtrank() {
		return txtrank;
	}

	public void setTxtrank(int txtrank) {
		this.txtrank = txtrank;
	}

	
	
	
	public String getCmd() {
		return cmd;
	}

	public void setCmd(String cmd) {
		this.cmd = cmd;
	}

	public int getRank() {
		return rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}	
	
	public CmdRank(String c)
	{
		this.cmd=c;
		//Lower means high priority, so make the priority higher intially
		this.rank=99999;
		this.subrank=99999;
		this.txtrank=99999;
	}
	
	public void addToGroupList(CmdRecognitionInfo cmdrecognitionobj)
	{
		cmdrecognitionlist.add(cmdrecognitionobj);
	}
	
	public List<CmdRecognitionInfo> getCmdGroupList()
	{
		return cmdrecognitionlist;
	}
	
	public int GetCmdRecognitionGrpSize()
	{
		return cmdrecognitionlist.size();
	}
	
	public CmdRecognitionInfo GetCmdRecognitionInfo(int index)
	{
		if(index<=cmdrecognitionlist.size())
			return cmdrecognitionlist.get(index);
		else
			return null;
	} 	

}
