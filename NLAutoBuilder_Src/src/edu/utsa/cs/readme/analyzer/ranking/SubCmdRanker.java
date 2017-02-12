package edu.utsa.cs.readme.analyzer.ranking;

import java.util.ArrayList;
import java.util.List;

import edu.utsa.cs.readme.analyzer.entities.CmdRank;
import edu.utsa.cs.readme.analyzer.util.FileFinder;
import edu.utsa.cs.readme.analyzer.util.ReadConfigFile;

public class SubCmdRanker {
	public SubCmdRanker()
	{
		
		
	}
	
	public static List<CmdRank> GetSubCmdsRankedByCmd(List<String> cmdtxt,String projpath)
	{
		List<CmdRank> rankedcmds=new ArrayList<CmdRank>();
		
		for (int i = 0; i < cmdtxt.size(); i++) 
		{			
			rankedcmds.add(new CmdRank(cmdtxt.get(i)));
		}		
		
		
		List<String> cmdprioritylist=new ArrayList<String>();
		ReadConfigFile config=new ReadConfigFile();
		cmdprioritylist=config.GetProjectSubCmdPriorityList();
		
		int[] cmdflag=new int[cmdprioritylist.size()];
		
		for(int i=0;i<cmdprioritylist.size();i++)
			cmdflag[i]=0;
		
		int priority=0;
		
		for(int i=0;i<cmdprioritylist.size();i++)
		{
			for(int j=0;j<rankedcmds.size();j++)
			{
				if((rankedcmds.get(j).getCmd().contains(cmdprioritylist.get(i))) && (FileFinder.IsConfigExists(projpath, cmdprioritylist.get(i))==true))
				{
					if(cmdflag[i]==0)
					{
						priority++;					
					}
					if(rankedcmds.get(j).getSubrank()==0)
						rankedcmds.get(j).setSubrank(priority);
				}
				
			}
		}
		
		
		return rankedcmds;
	}

}
