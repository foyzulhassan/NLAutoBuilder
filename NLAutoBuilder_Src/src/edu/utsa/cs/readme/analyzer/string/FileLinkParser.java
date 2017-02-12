package edu.utsa.cs.readme.analyzer.string;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;

public class FileLinkParser {
	
	public static List<String> pullLinks(String filepath)
	{
		String text=null;
		
		try {
			text= FileUtils.readFileToString(new File(filepath), "utf-8");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		List<String> links = new ArrayList<String>();
		 
		String regex = "\\(?\\b(http|https://|www[.])[-A-Za-z0-9+&amp;@#/%?=~_()|!:,.;]*[-A-Za-z0-9+&amp;@#/%=~_()|]";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(text);
		while(m.find()) 
		{
			String urlStr = m.group();
			
			if (urlStr.startsWith("(") && urlStr.endsWith(")"))
			{
				urlStr = urlStr.substring(1, urlStr.length() - 1);				
			}
			
			else if(urlStr.startsWith("("))
			{
				urlStr = urlStr.substring(1, urlStr.length());
			}
			
			else if (urlStr.endsWith(")"))
			{
				urlStr = urlStr.substring(0, urlStr.length() - 1);				
			}			
			
			//For now we are only considering wiki as link in Readme File
			if(urlStr.contains("wiki")||urlStr.contains("Wiki")||urlStr.contains("WiKi")||urlStr.contains("WIKI"))
				links.add(urlStr);
		}
		
		return links;
	}

}
