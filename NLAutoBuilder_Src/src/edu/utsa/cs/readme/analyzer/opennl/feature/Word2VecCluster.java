package edu.utsa.cs.readme.analyzer.opennl.feature;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import opennlp.tools.util.InvalidFormatException;
import opennlp.tools.util.model.ArtifactSerializer;
import opennlp.tools.util.model.SerializableArtifact;



/**
 * 
 * Class to load a Word2Vec cluster document: word\\s+word_class
 * http://code.google.com/p/word2vec/
 * 
 * The file containing the clustering lexicon has to be passed as the 
 * argument of the Word2VecCluster property.
 * 
 * @author ragerri
 * @version 2014/07/29
 * 
 */
public class Word2VecCluster implements SerializableArtifact {

  private static final Pattern spacePattern = Pattern.compile(" ");
  
  public static class Word2VecClusterSerializer implements ArtifactSerializer<Word2VecCluster> {

    public Word2VecCluster create(InputStream in) throws IOException,
        InvalidFormatException {
      return new Word2VecCluster(in);
    }

    public void serialize(Word2VecCluster artifact, OutputStream out)
        throws IOException {
      artifact.serialize(out);
    }
  }
  
  private Map<String, String> tokenToClusterMap = new HashMap<String, String>();
  
  public Word2VecCluster(InputStream in) throws IOException {

    BufferedReader breader = new BufferedReader(new InputStreamReader(in, Charset.forName("UTF-8")));
    String line;
    while ((line = breader.readLine()) != null) {
      
      String[] lineArray = spacePattern.split(line);
      if (lineArray.length == 2) {
        String normalizedToken = ClarkCluster.dotInsideI.matcher(lineArray[0]).replaceAll("i");
        //System.out.println(normalizedToken.toLowerCase()+lineArray[1].intern());
        tokenToClusterMap.put(normalizedToken.toLowerCase(), lineArray[1].intern());
      }
    }
  }
  
  public String lookupToken(String string) {
    return tokenToClusterMap.get(string);
  }
  
  public Map<String, String> getMap() {
    return tokenToClusterMap;
  }

  public void serialize(OutputStream out) throws IOException {
    Writer writer = new BufferedWriter(new OutputStreamWriter(out));

    for (Map.Entry<String, String> entry : tokenToClusterMap.entrySet()) {
      writer.write(entry.getKey() + " " + entry.getValue() + "\n");
    }

    writer.flush();
  }

  public Class<?> getArtifactSerializerClass() {
    return Word2VecClusterSerializer.class;
  }

}