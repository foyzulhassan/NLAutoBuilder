package edu.utsa.cs.readme.analyzer.opennl.feature;


import java.util.List;

import opennlp.tools.util.StringUtil;
import opennlp.tools.util.featuregen.FeatureGeneratorAdapter;
import opennlp.tools.util.featuregen.WordClusterDictionary;

public class WordClusterFeatureGeneratorForCmd extends FeatureGeneratorAdapter {

  private WordClusterDictionary tokenDictionary;
  private String resourceName;
  private boolean lowerCaseDictionary;

  public WordClusterFeatureGeneratorForCmd(WordClusterDictionary dict, String dictResourceKey, boolean lowerCaseDictionary) {
      tokenDictionary = dict;
      resourceName = dictResourceKey;
      this.lowerCaseDictionary = lowerCaseDictionary;
  }

  public void createFeatures(List<String> features, String[] tokens, int index,
      String[] previousOutcomes) {

    String clusterId;
    if (lowerCaseDictionary) {
      clusterId = tokenDictionary.lookupToken(StringUtil.toLowerCase(tokens[index]));
    } else {
      clusterId = tokenDictionary.lookupToken(tokens[index]);
    }
    
    if(clusterId==null && tokens[index].startsWith("-D"))
    	clusterId="cmd";
    	
    if (clusterId != null) {
      features.add(resourceName + clusterId);
    }  
    //System.out.println(clusterId+"==>"+tokens[index]);
  
  }
}