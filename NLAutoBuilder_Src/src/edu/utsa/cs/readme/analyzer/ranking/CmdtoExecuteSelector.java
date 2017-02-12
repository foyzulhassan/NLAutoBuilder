package edu.utsa.cs.readme.analyzer.ranking;

import java.util.Arrays;
import java.util.List;

import edu.utsa.cs.readme.analyzer.entities.CmdRank;
import edu.utsa.cs.readme.analyzer.entities.CmdRankScore;
import edu.utsa.cs.readme.analyzer.entities.CmdRecognitionInfo;
import edu.utsa.cs.readme.analyzer.entities.CmdToExecute;
import edu.utsa.cs.readme.analyzer.util.FileFinder;
import edu.utsa.cs.readme.analyzer.util.PropertyLoader;

public class CmdtoExecuteSelector
{
		
	public CmdToExecute GetCommandToExecute(List<CmdRank> cmdranklist,String projfolder) {
		CmdToExecute cmdtoexecute = new CmdToExecute();

		for (int i = 0; i < cmdranklist.size(); i++) {
			CmdRank cmdrank = cmdranklist.get(i);

			if (cmdrank.GetCmdRecognitionGrpSize() <= 1) {

				String precmdtxt = GetPreCmdtoBuild(cmdrank);
				String cmdtxt = GetCmdtoBuild(cmdrank);
				String postcmdtxt = GetPostCmdtoBuild(cmdrank);
				boolean precmdtxtflag = IsBeforeCmdTextTellsToBuild(cmdrank);

				if (precmdtxt != null) {
					if (cmdtoexecute.IsCmdAvailableInAnyCmdList(precmdtxt) == false) {
						CmdRankScore rankscore = new CmdRankScore();
						rankscore.setCmdText(precmdtxt);

						if (precmdtxtflag)
							rankscore.setPriorityScore(rankscore.getPriorityScore() + 50);

						cmdtoexecute.AddPreCmdListItem(rankscore);
					}

				}

				else if (cmdtxt != null) {
					if (cmdtoexecute.IsCmdAvailableInAnyCmdList(cmdtxt) == false) {
						CmdRankScore rankscore = new CmdRankScore();
						rankscore.setCmdText(cmdtxt);

						if (precmdtxtflag)
							rankscore.setPriorityScore(rankscore.getPriorityScore() + 50);

						cmdtoexecute.AddCmdListItem(rankscore);
					}

				}

				else if (postcmdtxt != null) {
					if (cmdtoexecute.IsCmdAvailableInAnyCmdList(postcmdtxt) == false) {
						CmdRankScore rankscore = new CmdRankScore();
						rankscore.setCmdText(postcmdtxt);

						if (precmdtxtflag)
							rankscore.setPriorityScore(rankscore.getPriorityScore() + 50);

						cmdtoexecute.AddPostCmdListItem(rankscore);
					}

				}
			} 
			else {
				for (int j = 0; j < cmdrank.GetCmdRecognitionGrpSize(); j++) {
					CmdRecognitionInfo recognitioninfo = cmdrank.GetCmdRecognitionInfo(j);

					CmdRank grpcmdrank = new CmdRank();
					grpcmdrank.addToGroupList(recognitioninfo);

					String precmdtxt = GetPreCmdtoBuild(grpcmdrank);
					String cmdtxt = GetCmdtoBuild(grpcmdrank);
					String postcmdtxt = GetPostCmdtoBuild(grpcmdrank);
					boolean precmdtxtflag = IsBeforeCmdTextTellsToBuild(grpcmdrank);

					if (precmdtxt != null) {
						if (cmdtoexecute.IsCmdAvailableInAnyCmdList(precmdtxt) == false) {
							CmdRankScore rankscore = new CmdRankScore();
							rankscore.setCmdText(precmdtxt);

							if (precmdtxtflag)
								rankscore.setPriorityScore(rankscore.getPriorityScore() + 50);

							cmdtoexecute.AddPreCmdListItem(rankscore);
						}

					}

					else if (cmdtxt != null) {
						if (cmdtoexecute.IsCmdAvailableInAnyCmdList(cmdtxt) == false) {
							CmdRankScore rankscore = new CmdRankScore();
							rankscore.setCmdText(cmdtxt);

							if (precmdtxtflag)
								rankscore.setPriorityScore(rankscore.getPriorityScore() + 50);

							cmdtoexecute.AddCmdListItem(rankscore);
						}

					}

					else if (postcmdtxt != null) {
						if (cmdtoexecute.IsCmdAvailableInAnyCmdList(postcmdtxt) == false) {
							CmdRankScore rankscore = new CmdRankScore();
							rankscore.setCmdText(postcmdtxt);

							if (precmdtxtflag)
								rankscore.setPriorityScore(rankscore.getPriorityScore() + 50);

							cmdtoexecute.AddPostCmdListItem(rankscore);
						}

					}

				}

			}

		}
		
		
		/*////////////////////for command priority such as mvn, gradle ant//////////////////////////*/
		PropertyLoader proploader=new PropertyLoader();
		
		String cmdwords=proploader.GetPropertyValue("CMDS","cmdrank.properties");
		
		List<String> cmditems = Arrays.asList(cmdwords.split("\\s*,\\s*"));
		
		
		for(int i=0;i<cmditems.size();i++)
		{
			String cmdtxt=cmditems.get(i);
			String pvalue=proploader.GetPropertyValue(cmdtxt,"cmdrank.properties");
			
			for(int j=0;j<cmdtoexecute.GetPreCmdListSize();j++)
			{
				if(cmdtoexecute.GetPreCmdListItem(j).getCmdText().contains(cmdtxt) && (FileFinder.IsConfigExists(projfolder, cmdtxt)==true))
				{
					
					cmdtoexecute.GetPreCmdListItem(j).setPriorityScore(cmdtoexecute.GetPreCmdListItem(j).getPriorityScore()+Integer.parseInt(pvalue));
				}
			}
			
			for(int j=0;j<cmdtoexecute.GetCmdListSize();j++)
			{
				if(cmdtoexecute.GetCmdListItem(j).getCmdText().contains(cmdtxt) && (FileFinder.IsConfigExists(projfolder, cmdtxt)==true))
				{
					
					cmdtoexecute.GetCmdListItem(j).setPriorityScore(cmdtoexecute.GetCmdListItem(j).getPriorityScore()+Integer.parseInt(pvalue));
				}
			}
			
			for(int j=0;j<cmdtoexecute.GetPostCmdListSize();j++)
			{
				if(cmdtoexecute.GetPostCmdListItem(j).getCmdText().contains(cmdtxt) && (FileFinder.IsConfigExists(projfolder, cmdtxt)==true))
				{
					
					cmdtoexecute.GetPostCmdListItem(j).setPriorityScore(cmdtoexecute.GetPostCmdListItem(j).getPriorityScore()+Integer.parseInt(pvalue));
				}
			}
			
		}		
		/*///////////////////////////////////////////////////////////////////*/
		
		/*///////////////////Pre Cmd//////////////////////////////////////////////*/
		String precmdwords=proploader.GetPropertyValue("PRECMD","subcmdrank.properties");
		
		List<String> precmditems = Arrays.asList(precmdwords.split("\\s*,\\s*"));
		
		
		for(int i=0;i<precmditems.size();i++)
		{
			String cmdtxt=precmditems.get(i);
			String pvalue=proploader.GetPropertyValue(cmdtxt,"subcmdrank.properties");
			
			for(int j=0;j<cmdtoexecute.GetPreCmdListSize();j++)
			{
				if(cmdtoexecute.GetPreCmdListItem(j).getCmdText().contains(cmdtxt))
				{					
					cmdtoexecute.GetPreCmdListItem(j).setPriorityScore(cmdtoexecute.GetPreCmdListItem(j).getPriorityScore()+Integer.parseInt(pvalue));
				}
			}
			
			for(int j=0;j<cmdtoexecute.GetCmdListSize();j++)
			{
				if(cmdtoexecute.GetCmdListItem(j).getCmdText().contains(cmdtxt))
				{
					
					cmdtoexecute.GetCmdListItem(j).setPriorityScore(cmdtoexecute.GetCmdListItem(j).getPriorityScore()+Integer.parseInt(pvalue));
				}
			}
			
			for(int j=0;j<cmdtoexecute.GetPostCmdListSize();j++)
			{
				if(cmdtoexecute.GetPostCmdListItem(j).getCmdText().contains(cmdtxt))
				{
					cmdtoexecute.GetPostCmdListItem(j).setPriorityScore(cmdtoexecute.GetPostCmdListItem(j).getPriorityScore()+Integer.parseInt(pvalue));
				}
			}
			
		}	
		
		/*//////////////////////////////////////////////////////////////////////////*/
		
		/*///////////////////Cmd//////////////////////////////////////////////*/
		String cmdoptionwords=proploader.GetPropertyValue("CMD","subcmdrank.properties");
		
		List<String> cmdoptionitems = Arrays.asList(cmdoptionwords.split("\\s*,\\s*"));
		
		
		for(int i=0;i<cmdoptionitems.size();i++)
		{
			String cmdtxt=cmdoptionitems.get(i);
			String pvalue=proploader.GetPropertyValue(cmdtxt,"subcmdrank.properties");
			
			for(int j=0;j<cmdtoexecute.GetPreCmdListSize();j++)
			{
				if(cmdtoexecute.GetPreCmdListItem(j).getCmdText().contains(cmdtxt))
				{					
					cmdtoexecute.GetPreCmdListItem(j).setPriorityScore(cmdtoexecute.GetPreCmdListItem(j).getPriorityScore()+Integer.parseInt(pvalue));
				}
			}
			
			for(int j=0;j<cmdtoexecute.GetCmdListSize();j++)
			{
				if(cmdtoexecute.GetCmdListItem(j).getCmdText().contains(cmdtxt))
				{
					
					cmdtoexecute.GetCmdListItem(j).setPriorityScore(cmdtoexecute.GetCmdListItem(j).getPriorityScore()+Integer.parseInt(pvalue));
				}
			}
			
			for(int j=0;j<cmdtoexecute.GetPostCmdListSize();j++)
			{
				if(cmdtoexecute.GetPostCmdListItem(j).getCmdText().contains(cmdtxt))
				{
					cmdtoexecute.GetPostCmdListItem(j).setPriorityScore(cmdtoexecute.GetPostCmdListItem(j).getPriorityScore()+Integer.parseInt(pvalue));
				}
			}
			
		}	
		
		/*/////////////////////////////////////////////////////////////////*/
		
		/*///////////////////post Cmd//////////////////////////////////////////////*/
		
		String postcmdwords=proploader.GetPropertyValue("CMD","subcmdrank.properties");
		
		List<String> postcmditems = Arrays.asList(postcmdwords.split("\\s*,\\s*"));
		
		
		for(int i=0;i<postcmditems.size();i++)
		{
			String cmdtxt=postcmditems.get(i);
			String pvalue=proploader.GetPropertyValue(cmdtxt,"subcmdrank.properties");
			
			for(int j=0;j<cmdtoexecute.GetPreCmdListSize();j++)
			{
				if(cmdtoexecute.GetPreCmdListItem(j).getCmdText().contains(cmdtxt))
				{					
					cmdtoexecute.GetPreCmdListItem(j).setPriorityScore(cmdtoexecute.GetPreCmdListItem(j).getPriorityScore()+Integer.parseInt(pvalue));
				}
			}
			
			for(int j=0;j<cmdtoexecute.GetCmdListSize();j++)
			{
				if(cmdtoexecute.GetCmdListItem(j).getCmdText().contains(cmdtxt))
				{
					
					cmdtoexecute.GetCmdListItem(j).setPriorityScore(cmdtoexecute.GetCmdListItem(j).getPriorityScore()+Integer.parseInt(pvalue));
				}
			}
			
			for(int j=0;j<cmdtoexecute.GetPostCmdListSize();j++)
			{
				if(cmdtoexecute.GetPostCmdListItem(j).getCmdText().contains(cmdtxt))
				{
					cmdtoexecute.GetPostCmdListItem(j).setPriorityScore(cmdtoexecute.GetPostCmdListItem(j).getPriorityScore()+Integer.parseInt(pvalue));
				}
			}
			
		}
		
		/*///////////////////End post Cmd//////////////////////////////////////////////*/
		
		/*********************************IF No Commands Added to The List**********************************************/
		int onecmdVal = 10;
		int grpcmdval = 3;

		if (cmdtoexecute.GetPreCmdListSize() <= 0 && cmdtoexecute.GetCmdListSize() <= 0
				&& cmdtoexecute.GetPostCmdListSize() <= 0) {
			if (cmdranklist.size() >= 1) {
				for (int i = 0; i < cmdranklist.size(); i++) {
					CmdRank cmdrank = cmdranklist.get(i);

					if (cmdrank.GetCmdRecognitionGrpSize() <= 1) {
						CmdRankScore rankscore = new CmdRankScore();
						rankscore.setCmdText(cmdrank.getCmdGroupList().get(0).getRecognizedCmdtxt());

						if (cmdranklist.size() == 1 && cmdrank.GetCmdRecognitionGrpSize() <= 1) {
							rankscore.setPriorityScore(onecmdVal);
							cmdtoexecute.AddPreCmdListItem(rankscore);
						} else {
							cmdtoexecute.AddPreCmdListItem(rankscore);
						}

					} else {
						for (int j = 0; j < cmdrank.GetCmdRecognitionGrpSize(); j++) {
							CmdRecognitionInfo recognitioninfo = cmdrank.GetCmdRecognitionInfo(j);

							CmdRank grpcmdrank = new CmdRank();
							grpcmdrank.addToGroupList(recognitioninfo);

							CmdRankScore rankscore = new CmdRankScore();
							rankscore.setCmdText(grpcmdrank.getCmdGroupList().get(0).getRecognizedCmdtxt());

							if (cmdranklist.size() == 1 && cmdrank.GetCmdRecognitionGrpSize() <= 1) {
								rankscore.setPriorityScore(grpcmdval);
								cmdtoexecute.AddPreCmdListItem(rankscore);
							} else {
								cmdtoexecute.AddPreCmdListItem(rankscore);
							}

						}

					}

				}
			}

		}		
		
		//********************************Sort Items***********************************/
		cmdtoexecute.SortAllTheListItems();
		

		return cmdtoexecute;
	}
	
	private boolean IsBeforeCmdTextTellsToBuild(CmdRank cmdrank)
	{
		boolean ret=false;
		int index=0;
		
		PropertyLoader proploader=new PropertyLoader();
		
		String beforecmdwords=proploader.GetPropertyValue("BEFORECMDWORDS","beforecmdtext.properties");
		
		List<String> beforecmditems = Arrays.asList(beforecmdwords.split("\\s*,\\s*"));
		
		while(index<beforecmditems.size())
		{
			String str=beforecmditems.get(index);

		    if(cmdrank.getCmdGroupList().size()<1)
		    {
		    	ret=false;
		    	break;
		    }
		    
		    else{
		    	if(cmdrank.getCmdGroupList().get(0).getBeforeCmdtxt()!=null && cmdrank.getCmdGroupList().get(0).getBeforeCmdtxt().contains(str))
		    	{
		    		ret=true;
		    		break;
		    	}		    	
		    }	
		    index++;
		}
		
		
		return ret;
	}
	
	private String GetPreCmdtoBuild(CmdRank cmdrank)
	{
		String precmdtxt=null;
		int index=0;
		int innerindex=0;
		
		PropertyLoader proploader=new PropertyLoader();
		
		String presubcmdlist=proploader.GetPropertyValue("PRECMD","subcmdrank.properties");
		List<String> presubcmditems = Arrays.asList(presubcmdlist.split("\\s*,\\s*"));
		
		
		while(index<presubcmditems.size())
		{
			String str=presubcmditems.get(index);

		    if(cmdrank.getCmdGroupList().size()<1)
		    {
		    	
		    	break;
		    }
		    
		    else{
		    	if(cmdrank.getCmdGroupList().get(0).getRecognizedCmdtxt().contains(str))
		    	{
		    		precmdtxt=cmdrank.getCmdGroupList().get(0).getRecognizedCmdtxt();
		    		break;
		    	}		    	
		    }
		    index++;
		}
		
		return precmdtxt;		
		
	}
	
	private String GetCmdtoBuild(CmdRank cmdrank)
	{
		String cmdtxt=null;
		int index=0;
		int innerindex=0;
		
		PropertyLoader proploader=new PropertyLoader();
		
		String cmdlist=proploader.GetPropertyValue("CMD","subcmdrank.properties");
		List<String> cmditems = Arrays.asList(cmdlist.split("\\s*,\\s*"));
		
		
		while(index<cmditems.size())
		{
			String str=cmditems.get(index);

		    if(cmdrank.getCmdGroupList().size()<1)
		    {
		    	
		    	break;
		    }
		    
		    else{
		    	if(cmdrank.getCmdGroupList().get(0).getRecognizedCmdtxt().contains(str))
		    	{
		    		cmdtxt=cmdrank.getCmdGroupList().get(0).getRecognizedCmdtxt();
		    		break;
		    	}		    	
		    }	
		    index++;
		}
		
		return cmdtxt;		
		
	}
	
	private String GetPostCmdtoBuild(CmdRank cmdrank)
	{
		String cmdtxt=null;
		int index=0;		
		
		PropertyLoader proploader=new PropertyLoader();
		
		String postcmdlist=proploader.GetPropertyValue("POSTCMD","subcmdrank.properties");		
		List<String> postsubcmditems = Arrays.asList(postcmdlist.split("\\s*,\\s*"));
		
		
		while(index<postsubcmditems.size())
		{
			String str=postsubcmditems.get(index);

		    if(cmdrank.getCmdGroupList().size()<1)
		    {
		    	
		    	break;
		    }
		    
		    else{
		    	if(cmdrank.getCmdGroupList().get(0).getRecognizedCmdtxt().contains(str))
		    	{
		    		cmdtxt=cmdrank.getCmdGroupList().get(0).getRecognizedCmdtxt();
		    		break;
		    	}		    	
		    }	
		    index++;
		}
		
		return cmdtxt;		
		
	}
	

}
