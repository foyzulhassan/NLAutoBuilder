package edu.utsa.cs.readme.analyzer.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ReadConfigFile {
	public ReadConfigFile()
	{
		
	}
	
	public List<String> GetProjectDirList()
	{
		List<String> list = new ArrayList<String>();
		//Get file from resources folder
		ClassLoader classLoader = getClass().getClassLoader();
		//File file = new File(classLoader.getResource(fileName).getFile());
		
		
		try {
			//File file =  new File(classLoader.getResource("filelist.txt").getFile());
		    	File file =  new File("/home/foyzulhassan/Research/Research_Code/autobuilder/Code/BatchTypeResolver/resource/filelist.txt");
			FileReader fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			//StringBuffer stringBuffer = new StringBuffer();
			String line;
			while ((line = bufferedReader.readLine()) != null) 
			{
				list.add(line);
			}
			fileReader.close();
		  } 
		  catch (IOException e)
		  {
			e.printStackTrace();
		  }
		
		return list;
	}
	
	public List<String> GetProjectNameList()
	{
		List<String> list = new ArrayList<String>();
		//Get file from resources folder
		ClassLoader classLoader = getClass().getClassLoader();
		//File file = new File(classLoader.getResource(fileName).getFile());
		
		
		try {
			//File file =  new File(classLoader.getResource("pathprojlist.txt").getFile());
		        File file =  new File("/home/foyzulhassan/Research/Research_Code/autobuilder/Code/BatchTypeResolver/resource/projlist.txt");
			FileReader fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			//StringBuffer stringBuffer = new StringBuffer();
			String line;
			int projcounter=1;
			
			while ((line = bufferedReader.readLine()) != null) 
			{
			        line=line.replace('/','_');
			        
			        line=Integer.toString(projcounter)+"-"+line;
			        
				list.add(line);
				
				projcounter++;
			}
			fileReader.close();
		  } 
		  catch (IOException e)
		  {
			e.printStackTrace();
		  }
		
		return list;
	}	
	
	public List<String> GetProjectCmdPriorityList()
	{
		List<String> list = new ArrayList<String>();
		//Get file from resources folder
		ClassLoader classLoader = getClass().getClassLoader();
		//File file = new File(classLoader.getResource(fileName).getFile());
		
		
		try {
			//File file =  new File(classLoader.getResource("cmdrank.txt").getFile());
		       File file =  new File("/home/foyzulhassan/Research/Research_Code/autobuilder/Code/BatchTypeResolver/resource/cmdrank.txt");
			FileReader fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			//StringBuffer stringBuffer = new StringBuffer();
			String line;
			while ((line = bufferedReader.readLine()) != null) 
			{
				list.add(line);
			}
			fileReader.close();
		  } 
		  catch (IOException e)
		  {
			e.printStackTrace();
		  }
		
		return list;
	}
	
	public List<String> GetProjectSubCmdPriorityList()
	{
		List<String> list = new ArrayList<String>();
		//Get file from resources folder
		ClassLoader classLoader = getClass().getClassLoader();
		//File file = new File(classLoader.getResource(fileName).getFile());
		
		
		try {
			File file =  new File(classLoader.getResource("subcmdrank.txt").getFile());
			FileReader fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			//StringBuffer stringBuffer = new StringBuffer();
			String line;
			while ((line = bufferedReader.readLine()) != null) 
			{
				list.add(line);
			}
			fileReader.close();
		  } 
		  catch (IOException e)
		  {
			e.printStackTrace();
		  }
		
		return list;
	}
	

}
