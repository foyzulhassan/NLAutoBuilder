package edu.utsa.cs.readme.analyzer.crawler;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;

import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;

public class SpiderLeg
{
    // We'll use a fake USER_AGENT so the web server thinks the robot is a normal web browser.
    private static final String USER_AGENT =
            "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/13.0.782.112 Safari/535.1";
    private List<String> links = new LinkedList<String>();
    private Document htmlDocument;
    private String url;

    /**
     * This performs all the work. It makes an HTTP request, checks the response, and then gathers
     * up all the links on the page. Perform a searchForWord after the successful crawl
     * 
     * @param url
     *            - The URL to visit
     * @return whether or not the crawl was successful
     */
    public boolean crawl(String url)
    {
        try
        {
            Connection connection = Jsoup.connect(url).userAgent(USER_AGENT);
            Document htmlDocument = connection.get();
            this.htmlDocument = htmlDocument;
            this.url=url;
            if(connection.response().statusCode() == 200) // 200 is the HTTP OK status code
                                                          // indicating that everything is great.
            {
                System.out.println("\n**Visiting** Received web page at " + url);
                
                if(url.equals("https://github.com/nostra13/Android-Universal-Image-Loader/wiki/Quick-Setup/10335114f470e73e36f4be9d71226792d1b3d620#3-application-class"))
                {
                	int text=1;
                	
                }
            }
            if(!connection.response().contentType().contains("text/html"))
            {
                System.out.println("**Failure** Retrieved something other than HTML");
                return false;
            }
            Elements linksOnPage = htmlDocument.select("a[href]");
            //System.out.println("Found (" + linksOnPage.size() + ") links");
            for(Element link : linksOnPage)
            {
                this.links.add(link.absUrl("href"));
            }
            return true;
        }
        catch(IOException ioe)
        {
            // We were not successful in our HTTP request
            return false;
        }
    }


    /**
     * Performs a search on the body of on the HTML document that is retrieved. This method should
     * only be called after a successful crawl.
     * 
     * @param searchWord
     *            - The word or string to look for
     * @return whether or not the word was found
     */
    public int searchForWords(List<String> searchWords)
    {
    	int matchindex=-1;
    	
        // Defensive coding. This method should only be used after a successful crawl.    	
        if(this.htmlDocument == null)
        {
            System.out.println("ERROR! Call crawl() before performing analysis on the document");
            return -1;
        }
        System.out.println("Searching for the word " + searchWords.toString() + "...");
        String bodyText = this.htmlDocument.body().text();
        
        for(int index=0;index<searchWords.size();index++)
        {
        	if(bodyText.toLowerCase().contains(searchWords.get(index).toLowerCase()))
        		matchindex=index;
        }        
        
        return matchindex;
    }
    
    
    public List<String> getSentences()
    {
    	 	
    	// Defensive coding. This method should only be used after a successful crawl.    	
        if(this.htmlDocument == null)
        {
            System.out.println("ERROR! Call crawl() before performing analysis on the document");
            return null;
        }
        
        
        Document document = Jsoup.parse(this.htmlDocument.body().html());
        document.outputSettings(new Document.OutputSettings().prettyPrint(false));//makes html() preserve linebreaks and spacing
        document.select("br").append("\\n");
        document.select("p").prepend("\\n\\n");
        String s = document.html().replaceAll("\\\\n", "\n");
        
        String testing=Jsoup.clean(s, "", Whitelist.none(), new Document.OutputSettings().prettyPrint(false));   

    	
        
        StringTokenizer st2 = new StringTokenizer(testing, "\n");
        
        List<String> sentList=new ArrayList<String>();        
      
        
		while (st2.hasMoreElements())
		{
			String str=st2.nextElement().toString();
			
			String text=str.trim();
			
			if(text.length()>0)
			{
				text.replaceAll("[\\n\\t ]", ""); 
				sentList.add(text);
			}
		}    	
    	
    	return sentList;    	
    	
    }


    public List<String> getLinks()
    {
        return this.links;
    }

}