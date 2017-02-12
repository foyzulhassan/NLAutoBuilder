package edu.utsa.cs.readme.analyzer.entities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CmdToExecute {
	private List<CmdRankScore> preCmdtoExecuteList;
	private List<CmdRankScore> cmdtoExecuteList;
	private List<CmdRankScore> postCmdtoExecuteList;
	
	
	public CmdToExecute()
	{
		preCmdtoExecuteList=new ArrayList<CmdRankScore>();
		cmdtoExecuteList=new ArrayList<CmdRankScore>();
		postCmdtoExecuteList=new ArrayList<CmdRankScore>();		
	}
	
	public int GetPreCmdListSize()
	{
		return preCmdtoExecuteList.size();
	}
	
	public CmdRankScore GetPreCmdListItem(int index)
	{
		if(index<preCmdtoExecuteList.size())
			return preCmdtoExecuteList.get(index);
		else
			return null;
	}
	
	public void AddPreCmdListItem(CmdRankScore rankscore)
	{
		preCmdtoExecuteList.add(rankscore);
	}
	
	public boolean IsCmdAvailableInPreCmdList(String cmdtxt)
	{
		int index=0;
		boolean ret=false;
		
		while(index<preCmdtoExecuteList.size())
		{
			CmdRankScore rankscore=preCmdtoExecuteList.get(index);
			
			if(rankscore.getCmdText().equalsIgnoreCase(cmdtxt))
			{
				ret=true;
				break;
			}
			
			index++;
		}
		
		return ret;
	}
	
	/*For Cmd*/
	
	public int GetCmdListSize()
	{
		return cmdtoExecuteList.size();
	}
	
	public CmdRankScore GetCmdListItem(int index)
	{
		if(index<cmdtoExecuteList.size())
			return cmdtoExecuteList.get(index);
		else
			return null;
	}
	
	public void AddCmdListItem(CmdRankScore rankscore)
	{
		cmdtoExecuteList.add(rankscore);
	}
	
	public boolean IsCmdAvailableInCmdList(String cmdtxt)
	{
		int index=0;
		boolean ret=false;
		
		while(index<cmdtoExecuteList.size())
		{
			CmdRankScore rankscore=cmdtoExecuteList.get(index);
			
			if(rankscore.getCmdText().equalsIgnoreCase(cmdtxt))
			{
				ret=true;
				break;
			}
			
		 index++;
		}
		
		return ret;
	}
	
	/*For PostCmd*/
	public int GetPostCmdListSize()
	{
		return postCmdtoExecuteList.size();
	}
	
	public CmdRankScore GetPostCmdListItem(int index)
	{
		if(index<postCmdtoExecuteList.size())
			return postCmdtoExecuteList.get(index);
		else
			return null;
	}
	
	public void AddPostCmdListItem(CmdRankScore rankscore)
	{
		postCmdtoExecuteList.add(rankscore);
	}
	
	public boolean IsCmdAvailableInPostCmdList(String cmdtxt)
	{
		int index=0;
		boolean ret=false;
		
		while(index<postCmdtoExecuteList.size())
		{
			CmdRankScore rankscore=postCmdtoExecuteList.get(index);
			
			if(rankscore.getCmdText().equalsIgnoreCase(cmdtxt))
			{
				ret=true;
				break;
			}
			index++;
		}
		
		return ret;
	}
	
	public boolean IsCmdAvailableInAnyCmdList(String cmdtxt)
	{
		
		boolean ret=false;
		
		if(IsCmdAvailableInPreCmdList(cmdtxt) || IsCmdAvailableInCmdList(cmdtxt) || IsCmdAvailableInPostCmdList(cmdtxt))
		{
			ret=true;
		}
		
		return ret;
	}
	
	
	public void SortAllTheListItems()
	{
		Collections.sort(preCmdtoExecuteList);
		Collections.sort(cmdtoExecuteList);
		Collections.sort(postCmdtoExecuteList);	
		
	}
	
}
