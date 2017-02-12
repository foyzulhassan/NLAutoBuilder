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
 * Class to load a Clark cluster document: word\\s+word_class\\s+prob
 * https://github.com/ninjin/clark_pos_induction
 * 
 * The file containing the clustering lexicon has to be passed as the 
 * argument of the DistSim property.
 * 
 * @author ragerri
 * @version 2014/07/29
 * 
 */
public class ClarkCluster implements SerializableArtifact {

  private static final Pattern spacePattern = Pattern.compile(" ");
  /**
   * Turkish capital letter I with dot.
   */
  public static final Pattern dotInsideI = Pattern.compile("\u0130", Pattern.UNICODE_CHARACTER_CLASS);
  
  public static class ClarkClusterSerializer implements ArtifactSerializer<ClarkCluster> {

    public ClarkCluster create(InputStream in) throws IOException,
        InvalidFormatException {
      return new ClarkCluster(in);
    }

    public void serialize(ClarkCluster artifact, OutputStream out)
        throws IOException {
      artifact.serialize(out);
    }
  }
  
  private Map<String, String> tokenToClusterMap = new HashMap<String, String>();

  public ClarkCluster(InputStream in) throws IOException {

    BufferedReader breader = new BufferedReader(new InputStreamReader(in, Charset.forName("UTF-8")));
    String line;
    while ((line = breader.readLine()) != null) {
      String[] lineArray = spacePattern.split(line);
      if (lineArray.length == 3) {
        String normalizedToken = dotInsideI.matcher(lineArray[0]).replaceAll("i");
        tokenToClusterMap.put(normalizedToken.toLowerCase(), lineArray[1].intern());
      }
      else if (lineArray.length == 2) {
        String normalizedToken = dotInsideI.matcher(lineArray[0]).replaceAll("i");
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
    return ClarkClusterSerializer.class;
  }
}

