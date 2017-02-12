package edu.utsa.cs.readme.analyzer.util;

import java.io.File;
import java.io.FilenameFilter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileFinder {
	
	public static String GetReadmeFilePath(String dirname)
	{
        File directory = new File(dirname);
        String readmefile=null;
        //get all the files from a directory

        File[] fList = directory.listFiles();

        for (File file : fList){

            if (file.isFile()){

               String name=file.getName().toLowerCase();
               
               if(name.contains("read"))
               {
            	   readmefile=dirname+"/"+file.getName();
               }
               
            }

        }
        
        return readmefile;
		
	}
	
	public static String GetHtmlCmdFilePath(String dirname)
	{
        File directory = new File(dirname);
        String htmlcmdfile=null;
        //get all the files from a directory

        File[] fList = directory.listFiles();

        for (File file : fList){

            if (file.isFile()){

               String name=file.getName().toLowerCase();
               
               if(name.contains("cmdfromwiki"))
               {
            	   htmlcmdfile=dirname+"/"+file.getName();
               }
               
            }

        }
        
        return htmlcmdfile;
		
	}
	
	public static boolean IsConfigExists(String dirname,String type)
	{
        File directory = new File(dirname); 
        String filename=null;
        
        if(type.equals("mvn"))
        	filename="pom.xml";
        else if(type.equals("gralde"))
        	filename="build.gradle";
        else if(type.equals("ant"))
        	filename="build.xml";
        	
        
        boolean ret=false;
        //get all the files from a directory

        File[] fList = directory.listFiles();

        for (File file : fList){

            if (file.isFile()){

               String name=file.getName().toLowerCase();
               
               if(name.equalsIgnoreCase(filename))
               {
            	   ret=true;
               }
               
            }

        }
        
        return ret;
		
	}
	

}
