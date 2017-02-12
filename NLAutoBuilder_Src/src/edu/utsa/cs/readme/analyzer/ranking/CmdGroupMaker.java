package edu.utsa.cs.readme.analyzer.ranking;

import java.util.ArrayList;
import java.util.List;

import edu.utsa.cs.readme.analyzer.entities.CmdRank;
import edu.utsa.cs.readme.analyzer.entities.CmdRecognitionInfo;

public class CmdGroupMaker 
{
	public static List<CmdRank> GetCmdGroup(List<CmdRecognitionInfo> recognizedcmd)
	{
		
		List<CmdRank> cmdranklist=new ArrayList<CmdRank>();
		CmdRecognitionInfo previnfo=null;
		int lastgroupindex=0;
		
		for(int i=0;i<recognizedcmd.size();i++)
		{
			CmdRecognitionInfo info=recognizedcmd.get(i);
			
			
			if(previnfo!=null)
			{
				if(((info.getLineNumber()-1)==previnfo.getLineNumber()) && (IsRecognizedAndActualCmdSame(info)==true) && info.getBeforeCmdtxt().equals(previnfo.getBeforeCmdtxt()))
				{
					cmdranklist.get(lastgroupindex).addToGroupList(info);
				}
				else
				{
					CmdRank cmdobj=new CmdRank();
					cmdobj.addToGroupList(info);
					cmdranklist.add(cmdobj);
					lastgroupindex++;
				}
				
			}
			else
			{
				CmdRank cmdobj=new CmdRank();
				cmdobj.addToGroupList(info);
				cmdranklist.add(cmdobj);
			}
			
			
			previnfo=info;
		}
		
		
		return cmdranklist;
	}
	
	
	public static boolean IsRecognizedAndActualCmdSame(CmdRecognitionInfo info)
	{
		boolean ret=false;
		String actualcmd;
		String prevtext;
		
		if(info.getRecognizedCmdtxt().contains("mvn"))
		{
			actualcmd=info.getActualCmdtxt().substring(info.getActualCmdtxt().indexOf("mvn"));
			prevtext=info.getActualCmdtxt().substring(0,info.getActualCmdtxt().indexOf("mvn"));
			
			boolean atleastOneAlpha = prevtext.matches(".*[a-zA-Z]+.*");
			
			if(actualcmd.equals(info.getRecognizedCmdtxt()) && atleastOneAlpha==false)
				ret=true;
		}
		
		else if(info.getRecognizedCmdtxt().contains("gradle"))
		{
			actualcmd=info.getActualCmdtxt().substring(info.getActualCmdtxt().indexOf("gradle"));
			prevtext=info.getActualCmdtxt().substring(0,info.getActualCmdtxt().indexOf("gradle"));
			
			boolean atleastOneAlpha = prevtext.matches(".*[a-zA-Z]+.*");
			
			if(actualcmd.equals(info.getRecognizedCmdtxt()) && atleastOneAlpha==false)
				ret=true;
		}
		
		else if(info.getRecognizedCmdtxt().contains("ant"))
		{
			actualcmd=info.getActualCmdtxt().substring(info.getActualCmdtxt().indexOf("ant"));
			prevtext=info.getActualCmdtxt().substring(0,info.getActualCmdtxt().indexOf("ant"));
			
			boolean atleastOneAlpha = prevtext.matches(".*[a-zA-Z]+.*");
			
			if(actualcmd.equals(info.getRecognizedCmdtxt()) && atleastOneAlpha==false)
				ret=true;
		}
		
		return ret;
		
	}
	
	public static boolean IsCmdListContainsGroup(List<CmdRank> cmdranklist) {
		boolean ret = false;

		int index = 0;

		while (index < cmdranklist.size()) {
			CmdRank obj = cmdranklist.get(index);

			if (obj.GetCmdRecognitionGrpSize() > 1) {

				ret = true;
				break;
			}

			index++;
		}

		return ret;

	}
	

	public static CmdRank GetTheCmdGroup(List<CmdRank> cmdranklist) {
		CmdRank cmdrank=new CmdRank();

		int index = 0;

		while (index < cmdranklist.size()) {
			CmdRank obj = cmdranklist.get(index);

			if (obj.GetCmdRecognitionGrpSize() > 1) {

				cmdrank=obj;
				break;
			}

			index++;
		}

		return cmdrank;

	}
}
