package edu.utsa.cs.readme.analyzer.crawler;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import edu.utsa.cs.readme.analyzer.string.BuildCmdParser;

public class Spider
{
  private static final int MAX_PAGES_TO_SEARCH = 100;
  private Set<String> pagesVisited = new HashSet<String>();
  private List<String> pagesToVisit = new LinkedList<String>();
  private String baseurl=null;
  private int projecthtmlline=0;
  
  
  
  public Spider(int projecthtmlline)
  {
	  this.projecthtmlline=projecthtmlline;
  }


  /**
   * Our main launching point for the Spider's functionality. Internally it creates spider legs
   * that make an HTTP request and parse the response (the web page).
   * 
   * @param url
   *            - The starting point of the spider
   * @param searchWord
   *            - The word or string that you are searching for
   */
  public boolean search(String url, List<String> searchWords,String projectfolder)
  {
      boolean flag=false;
      boolean flaginit=false;
	  
	  while(this.pagesVisited.size() < MAX_PAGES_TO_SEARCH)
      {
          String currentUrl=null;
          SpiderLeg leg = new SpiderLeg();
          if(flaginit==false)
          {
              currentUrl = url;
              this.pagesVisited.add(url);
              
              baseurl=url;
              
              flaginit=true;
          }
          else
          {
        	  while(true)
        	  {
        		  String nexturl=this.nextUrl();
        		  
        		  if(nexturl==null)
        		  {
        			  currentUrl=null;
        			  break;
        		  }      			 
        		  
        		  
        		  if(IsPageWithintheUrl(baseurl,nexturl))
        		  {
        			  currentUrl = nexturl;
        			  break;
        		  }
        	  }
          }
          
          if(currentUrl==null)
        	  break;
          
          leg.crawl(currentUrl); // Lots of stuff happening here. Look at the crawl method in
                                 // SpiderLeg
          int successindex = leg.searchForWords(searchWords);
          if(successindex>=0)
          {
              System.out.println(String.format("**Success** Word %s found at %s", searchWords.toString(), currentUrl));
              //flag=true;
              
              List<String> sentenceList=leg.getSentences();
              //sentenceList.add(currentUrl);
              BuildCmdParser cmdparser=new BuildCmdParser();
              
              int lastlinecount=cmdparser.IsHtmlContainsBuildCmd(sentenceList,currentUrl,projectfolder,projecthtmlline,currentUrl);
              
              if(projecthtmlline<lastlinecount)
              {
            	  flag=true;
            	  projecthtmlline=lastlinecount;
            	  break;
              }
            	 
          }
          
          List<String> links=new ArrayList<String>();          
          links=leg.getLinks();          
          for(int i=0;i<links.size();i++)
          {
        	  if(!this.pagesToVisit.contains(links.get(i)))
        	  {
        		  
        		  this.pagesToVisit.add(links.get(i));
        		  
        	  }
        	  
          }
      }
      System.out.println("\n**Done** Visited " + this.pagesVisited.size() + " web page(s)");
      
      return flag;
  }

  
  public boolean IsPageWithintheUrl(String current,String next)
  {
	  boolean flag=false;
	  
	  if(next.startsWith(current))
		  flag=true;
	  
	  if(next.contains(current))
		  flag=true;
	  
	  return flag;
  }

  /**
   * Returns the next URL to visit (in the order that they were found). We also do a check to make
   * sure this method doesn't return a URL that has already been visited.
   * 
   * @return
   */
  private String nextUrl()
  {
      String nextUrl=null;
      do
      {
    	  nextUrl=null;
    	  if(!this.pagesToVisit.isEmpty())
    	  {
    		  nextUrl = this.pagesToVisit.remove(0);
    		  //System.out.println(this.pagesToVisit.size());
    	  }
      } while(this.pagesVisited.contains(nextUrl) && this.pagesToVisit.size()>0);
      
      if(this.pagesVisited.contains(nextUrl))
    	  nextUrl=null;
      
      if(nextUrl!=null)
    	  this.pagesVisited.add(nextUrl);
      
      return nextUrl;
  }
}