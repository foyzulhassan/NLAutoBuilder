package edu.utsa.cs.readme.analyzer.crawler;

import java.util.ArrayList;
import java.util.List;

public class SpiderBuildCmdSearcher 
{
	private List<String> searchlist=new ArrayList<String>();
	private Spider spider;
	
	public SpiderBuildCmdSearcher()
	{
		searchlist.add("mvn");
		searchlist.add("gradle");
		searchlist.add("gradlew");
		searchlist.add("ant");
		searchlist.add("make");	
		spider= new Spider(0);		
	}
	
	public boolean SearchAndDumpallBuildCmd(List<String> htmllinks,String projectfolder)
	{
		boolean flag=false;		
		
		
		  
		for(int index=0;index<htmllinks.size();index++)
		{
			String link=htmllinks.get(index);			
	
			spider.search(link,searchlist,projectfolder);
		}
		
		
		return flag;
	}
}
