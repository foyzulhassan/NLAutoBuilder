package edu.utsa.cs.readme.analyzer.string;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.utsa.cs.readme.analyzer.entities.GitHubProjects;
import edu.utsa.cs.readme.analyzer.util.TextFileReaderWriter;

public class BuildCmdParser {
	GitHubProjects project;
	
	public BuildCmdParser()
	{
		project=null;
		
	}
	
	public BuildCmdParser(GitHubProjects proj)
	{
		this.project=proj;
	}
	
	
	public boolean IsFileContainsBuildCmd(int nfoldno)
	{
		boolean flag=false;
		boolean ret=false;
		
		List<String> list = new ArrayList<String>();
		
		list=TextFileReaderWriter.GetFileContentByLine(project.getProjectReadmeFile());
		
		boolean cmdfound=false;
		

		
		Iterator<String> listIterator = list.iterator();
		int[] lineindicator=new int[list.size()];
		
		for(int t=0;t<list.size();t++)
			lineindicator[t]=0;
			
		int line_index=0;
		
		while (listIterator.hasNext())
		{
			String line=listIterator.next();
			
			flag=this.IslineConatainsMavenCmd(line);
			
			if(flag==true)
			{
				String filename="samplecmd_"+Integer.toString(nfoldno)+".txt";
				lineindicator[line_index]=1;
				
				if(cmdfound==false)
				{
					String cmnfile="samplecmd_"+Integer.toString(nfoldno)+".txt";
					TextFileReaderWriter.DumptoCommonLabelFile("/home/foyzulhassan/Research/Data/Label_1000",cmnfile,project.getProjectReadmeFile());
					TextFileReaderWriter.DumptoCommonLabelFile("/home/foyzulhassan/Research/Data/Label_1000",cmnfile,"********************************************");
					cmdfound=true;
					
				}
				
				if(lineindicator[line_index-1]==0)
				{
					TextFileReaderWriter.DumptoCommonLabelFile("/home/foyzulhassan/Research/Data/Label_1000",filename,"$$$$$$$$$$>"+list.get(line_index-1));
					
				}
				
				TextFileReaderWriter.DumptoCommonLabelFile("/home/foyzulhassan/Research/Data/Label_1000",filename,line);
				ret=true;
			}
			
			flag=this.IslineConatainsGradleCmd(line);
			
			if(flag==true)
			{
				lineindicator[line_index]=1;
				if(cmdfound==false)
				{
					String cmnfile="samplecmd_"+Integer.toString(nfoldno)+".txt";
					TextFileReaderWriter.DumptoCommonLabelFile("/home/foyzulhassan/Research/Data/Label_1000",cmnfile,project.getProjectReadmeFile());
					TextFileReaderWriter.DumptoCommonLabelFile("/home/foyzulhassan/Research/Data/Label_1000",cmnfile,"********************************************");
					cmdfound=true;
					
				}		
				
				
				
				String filename="samplecmd_"+Integer.toString(nfoldno)+".txt";
				
				if(lineindicator[line_index-1]==0)
				{
					TextFileReaderWriter.DumptoCommonLabelFile("/home/foyzulhassan/Research/Data/Label_1000",filename,"$$$$$$$$$$>"+list.get(line_index-1));
					
				}
				
				TextFileReaderWriter.DumptoCommonLabelFile("/home/foyzulhassan/Research/Data/Label_1000",filename,line);
				ret=true;
			}
			
				
			flag=this.IslineConatainsAntCmd(line);
			
			if(flag==true)
			{
				lineindicator[line_index]=1;
				if(cmdfound==false)
				{
					String cmnfile="samplecmd_"+Integer.toString(nfoldno)+".txt";
					TextFileReaderWriter.DumptoCommonLabelFile("/home/foyzulhassan/Research/Data/Label_1000",cmnfile,project.getProjectReadmeFile());
					TextFileReaderWriter.DumptoCommonLabelFile("/home/foyzulhassan/Research/Data/Label_1000",cmnfile,"********************************************");
					cmdfound=true;
					
				}			
				
				
				String filename="samplecmd_"+Integer.toString(nfoldno)+".txt";
				
				if(lineindicator[line_index-1]==0)
				{
					TextFileReaderWriter.DumptoCommonLabelFile("/home/foyzulhassan/Research/Data/Label_1000",filename,"$$$$$$$$$$>"+list.get(line_index-1));
					
				}
				
				TextFileReaderWriter.DumptoCommonLabelFile("/home/foyzulhassan/Research/Data/Label_1000",filename,line);
				ret=true;
			}
	
			
			/*flag=this.IslineConatainsMakeCmd(line);
			
			if(flag==true)
			{
				if(cmdfound==false)
				{
					String cmnfile="samplecmd_"+Integer.toString(nfoldno)+".txt";
					TextFileReaderWriter.DumptoCommonLabelFile("/home/foyzulhassan/Research/Data/Label_1000",cmnfile,project.getProjectReadmeFile());
					TextFileReaderWriter.DumptoCommonLabelFile("/home/foyzulhassan/Research/Data/Label_1000",cmnfile,"********************************************");
					cmdfound=true;
					
				}
				
				String filename="samplecmd_"+Integer.toString(nfoldno)+".txt";
				TextFileReaderWriter.DumptoCommonLabelFile("/home/foyzulhassan/Research/Data/Label_1000",filename,line);
				ret=true;
			}*/
			line_index++;
				
		}
		
		return ret;
		
	}
	
	
	public int IsHtmlContainsBuildCmd(List<String> sentenceList,String url,String projfolder,int lineno,String link)
	{
		boolean flag=false;		
		int linecount=lineno;	
		String filename="cmdfromwiki.txt";
		boolean[] prevflag=new boolean[sentenceList.size()];
		String prevText="";
		int index=0;
		
		Iterator<String> listIterator = sentenceList.iterator();
		
		//TextFileReaderWriter.DumptoCommonLabelFile(projfolder,filename,"@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@>>" + link);
		for(int i=0;i<sentenceList.size();i++)
			prevflag[i]=false;
		
		while (listIterator.hasNext())
		{
			String line=listIterator.next();
			
			flag=this.IslineConatainsMavenCmd(line);			
			
			if(flag==true)
			{
				if(prevflag[index-1]==false)
					TextFileReaderWriter.DumptoCommonLabelFile(projfolder,filename,"@s"+" "+prevText);
				
				TextFileReaderWriter.DumptoCommonLabelFile(projfolder,filename,Integer.toString(linecount)+" "+line);
				linecount++;	
				prevflag[index]=true;
			}
			
			flag=this.IslineConatainsGradleCmd(line);
			
			if(flag==true)
			{
				if(prevflag[index-1]==false)
					TextFileReaderWriter.DumptoCommonLabelFile(projfolder,filename,"@s"+" "+prevText);
				
				TextFileReaderWriter.DumptoCommonLabelFile(projfolder,filename,Integer.toString(linecount)+" "+line);
				linecount++;
				prevflag[index]=true;
			}
			
				
			flag=this.IslineConatainsAntCmd(line);
			
			if(flag==true)
			{
				if(prevflag[index-1]==false)
					TextFileReaderWriter.DumptoCommonLabelFile(projfolder,filename,"@s"+" "+prevText);
				
				TextFileReaderWriter.DumptoCommonLabelFile(projfolder,filename,Integer.toString(linecount)+" "+line);
				linecount++;
				prevflag[index]=true;
			}
			
			
			prevText=line;
			index++;
		}
		
		return linecount;
		
	}
	
	public boolean IslineConatainsMavenCmd(String line)
	{
		boolean flag=false;
		
		Pattern p = Pattern.compile("(\\s|^|/)(mvn)(\\s+)");
		Matcher m = p.matcher(line);
	
		while (m.find()) {
			flag=true;
		}
		
		return flag;
	}
	
	public boolean IslineConatainsGradleCmd(String line)
	{
		boolean flag=false;
		
		Pattern p = Pattern.compile("(\\s|^|/)(gradle|gradlew)(\\s+)");
		Matcher m = p.matcher(line);

		while (m.find()) {
			flag=true;
			}
		
		return flag;
	}
	
	public boolean IslineConatainsAntCmd(String line)
	{
		boolean flag=false;
		
		Pattern p = Pattern.compile("(\\s|^|/)(ant)(\\s+)");
		Matcher m = p.matcher(line);
		
		while (m.find()) {
			flag=true;
			}
		
		return flag;
	}
	
	public boolean IslineConatainsMakeCmd(String line)
	{
		boolean flag=false;
		
		//Pattern p = Pattern.compile("(\\s|^)(make)(\\s+)");
		Pattern p = Pattern.compile("(\\W\\s+|^|/)(make)(\\s+)");
		Pattern q = Pattern.compile("([^,;):]\\s+|^|/)(make)(\\s+)");
		Matcher m = p.matcher(line);
		Matcher n = q.matcher(line);

		while (m.find()&& n.find()) {
			flag=true;
		}
		
		return flag;
	}	

}
