package edu.utsa.cs.readme.analyzer.string;

public class CmdStringUtil {
	
	public static String RemoveLeadingTrailingSpecialChar(String str)
	{
		int lindex=0;
		int rindex=str.length() - 1;
		int index;
		
		String tstring="";
		
		
		index=str.length() - 1; 
		
		while(index>=0)
		{
			char c = str.charAt(index);
	        if (Character.isLetter(c) || Character.isDigit(c))
	        {
	        	rindex=index;
	        	break;
	        }
	        
	        index--;
		}
		
		index=0;
		
		while(index<=(str.length() - 1))
		{
			char c = str.charAt(index);
	        if (Character.isLetter(c) || Character.isDigit(c))
	        {
	        	lindex=index;
	        	break;
	        }
	        
	        index++;			
		}	   
		
		tstring=str.substring(lindex,rindex+1);
		
		return tstring;
		
	}
	
	public static String GetCoatedCmdString(String str)
	{
		String retstr=null;
		int lindex=0;
		int rindex=0;
		boolean flag=false;
		
		if(str.startsWith("`"))
		{
			rindex=str.length()-1;
			
			while(rindex>=0)
			{
				if(str.charAt(rindex)=='`')
				{
					flag=true;
					break;
				}
				rindex--;
			}
			
		}
		
		if(flag==true)
		{
			retstr=str.substring(1,rindex);
		}
		else
		{
			retstr=str;
		}
		
		return retstr;
	}
	
	public static String GetDifferentTypeCmd(String origcmdtype,String newcmdtype,String cmdstr)
	{
		String retcmdStr=null;
		
		if(cmdstr.startsWith(origcmdtype))
		{
			retcmdStr=cmdstr.replace(origcmdtype, newcmdtype);
		}
		else
		{
			retcmdStr=cmdstr;
		}
		
		
		return retcmdStr;
	}
	
	public static boolean ConatainsAntCmd(String txt)
	{
		boolean ret=false;
		
		if(txt.contains("ant"))
		{
			int index=txt.indexOf("ant");
			int rindex=index+3;
			
			int i=0;
			ret=true;
			
			while(i<index)
			{
				if(Character.isLetter(txt.charAt(i)) || Character.isDigit(txt.charAt(i)))
				{
					ret=false;
					break;
				}
					
				i++;
			}
			
			if(ret==true)
			{
				i=rindex;
				
				while(i<txt.length())
				{
					if(Character.isLetter(txt.charAt(i)) || Character.isDigit(txt.charAt(i)))
					{
						ret=false;
						break;
					}
						
					i++;
				}
			}	
			
		}
		
		return ret;
	}
	
	public static boolean ConatainsCmd(String cmd, String token)
	{
		boolean ret=false;
		
		if(token.contains(cmd))
		{
			int tokenlength=token.length();
			int cmdlength=cmd.length();
			
			int index=token.indexOf(cmd);
			
			if((index+cmdlength)>=tokenlength)
			{
				ret=true;
			}
			
			else
			{
				String cmdwithspc=cmd+" ";
				
				if(token.contains(cmdwithspc))
				{
					ret=true;
				}
			}			
			
		}
		
		return ret;
	}

}
