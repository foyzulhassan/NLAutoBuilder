package edu.utsa.cs.readme.analyzer.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;


/*
 * Class to load properties value from config file
 */
public class PropertyLoader {
	
	/*
	 * Loads Property Value according to key
	 */
	public String GetPropertyValue(String key,String propFileName)
	{
		Properties prop = new Properties();
    	InputStream input = null;
    	String propValue=null;
    	String path="/home/foyzulhassan/Research/Research_Code/autobuilder/Code/BatchTypeResolver/resource/"+propFileName;
    	//input = this.getClass().getClassLoader().getResourceAsStream(propFileName);
    	try {
	    input = new FileInputStream(path);
	} catch (FileNotFoundException e1) {
	    // TODO Auto-generated catch block
	    e1.printStackTrace();
	}


    	try {
            		
    		
    		//input = new FileInputStream(path);
    		
    		
    		
    		if(input==null)
    		{
    	            System.out.println("Sorry, unable to find " + input.toString());
    		    return null;
    		}

    		//load a properties file from class path, inside static method
    		prop.load(input);
 
            //get the property value and print it out
    		propValue=prop.getProperty(key);
    	       
 
    	} catch (IOException ex) {
    		ex.printStackTrace();
        } finally{
        	if(input!=null){
        		try {
				input.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
        	}
        }
    	
    	return propValue;			
	}

}
