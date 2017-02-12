package edu.utsa.cs.readme.analyzer.util;

import java.io.File;

public class LogPrinter {
	
	private String logword=null;
	private String pathtype=null;
	private String path=null;
	private String logfilename=null;
	private String infologfilename=null;
	private boolean isInfoCmd=false;
	
	public LogPrinter()
	{
		PropertyLoader proploader=new PropertyLoader();
		
		this.logword=proploader.GetPropertyValue("LOGTYPE","logtype.properties");
		this.pathtype=proploader.GetPropertyValue("PATHTYPE","logtype.properties");
		this.logfilename=proploader.GetPropertyValue("LOGFILENAME","logtype.properties");
		this.infologfilename=proploader.GetPropertyValue("LOGINFOFILENAME","logtype.properties");
		this.isInfoCmd=false;
	}
	
	public LogPrinter(String path)
	{
		PropertyLoader proploader=new PropertyLoader();
		
		this.logword=proploader.GetPropertyValue("LOGTYPE","logtype.properties");		
		this.pathtype=proploader.GetPropertyValue("PATHTYPE","logtype.properties");
		this.logfilename=proploader.GetPropertyValue("LOGFILENAME","logtype.properties");
		this.infologfilename=proploader.GetPropertyValue("LOGINFOFILENAME","logtype.properties");
		this.isInfoCmd=false;
		
		
		this.path=path;
		
		 try{
		      
		        String tempFile = this.path+"//"+this.logfilename;
		        //Delete if tempFile exists
		        File fileTemp = new File(tempFile);
		          if (fileTemp.exists()){
		             fileTemp.delete();
		          }   
		      }catch(Exception e){
		         // if any error occurs
		         e.printStackTrace();
		      }

		
	}
	
    public LogPrinter(String path, boolean infocmd) {
	PropertyLoader proploader = new PropertyLoader();

	this.logword = proploader.GetPropertyValue("LOGTYPE",
		"logtype.properties");
	this.pathtype = proploader.GetPropertyValue("PATHTYPE",
		"logtype.properties");
	this.logfilename = proploader.GetPropertyValue("LOGFILENAME",
		"logtype.properties");
	this.infologfilename = proploader.GetPropertyValue("LOGINFOFILENAME",
		"logtype.properties");
	this.isInfoCmd = infocmd;

	this.path = path;
	String tempFile = "";

	try {

	    if (infocmd == true) {
		tempFile = this.path + "//" + this.infologfilename;
		File fileTemp = new File(tempFile);
		if (fileTemp.exists()) {
		    fileTemp.delete();
		}
	    } else {
		tempFile = this.path + "//" + this.logfilename;
		File fileTemp = new File(tempFile);
		if (fileTemp.exists()) {
		    fileTemp.delete();
		}
	    }
	    // Delete if tempFile exists

	} catch (Exception e) {
	    // if any error occurs
	    e.printStackTrace();
	}

    }
	
	
	
	public void println(String txt)
	{
		if(this.logword.equals("println"))
			System.out.println(txt);
		else
		{
			if(this.pathtype.equals("folder") && this.path!=null)	
			{
			    if(this.isInfoCmd==false)
				TextFileReaderWriter.logprintln(this.path,this.logfilename,txt);	
			    else
				TextFileReaderWriter.logprintln(this.path,this.infologfilename ,txt);
			}
			else
			{
				TextFileReaderWriter.logprintln("/home/foyzulhassan/Research/Data/testfiles/For_Testing/",this.logfilename,txt);	
			}
			
		}
	}

}
