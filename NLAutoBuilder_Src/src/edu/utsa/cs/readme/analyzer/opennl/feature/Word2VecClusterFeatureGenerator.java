package edu.utsa.cs.readme.analyzer.opennl.feature;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import opennlp.tools.util.InvalidFormatException;
import opennlp.tools.util.featuregen.ArtifactToSerializerMapper;
import opennlp.tools.util.featuregen.CustomFeatureGenerator;
import opennlp.tools.util.featuregen.FeatureGeneratorResourceProvider;
import opennlp.tools.util.model.ArtifactSerializer;


public class Word2VecClusterFeatureGenerator extends CustomFeatureGenerator implements ArtifactToSerializerMapper {
  
  private Word2VecCluster word2vecCluster;
  private static String unknownClass = "noWord2Vec";
  private Map<String, String> attributes;
  
  
  public Word2VecClusterFeatureGenerator() {
	  ///System.out.println("testing");
  }
  
  public void createFeatures(List<String> features, String[] tokens, int index,
      String[] previousOutcomes) {
    //System.out.println("testing");
    String wordClass = getWordClass(tokens[index].toLowerCase());
    if(tokens[index].startsWith("-D") && wordClass.equals(unknownClass))
    	wordClass="cmd";
    //System.out.println(attributes.get("dict") + "=" + wordClass);
    features.add(attributes.get("dict") + "=" + wordClass);
  }
  
  private String getWordClass(String token) {
    String wordClass = word2vecCluster.lookupToken(token);
    if (wordClass == null) {
      wordClass = unknownClass;
    }
    return wordClass;
  }

 
  public void updateAdaptiveData(String[] tokens, String[] outcomes) {
    
  }

 
  public void clearAdaptiveData() {
    
  }

  @Override
  public void init(Map<String, String> properties,
      FeatureGeneratorResourceProvider resourceProvider)
      throws InvalidFormatException {
    Object dictResource = resourceProvider.getResource(properties.get("dict"));
    if (!(dictResource instanceof Word2VecCluster)) {
      throw new InvalidFormatException("Not a Word2VecCluster resource for key: " + properties.get("dict"));
    }
    this.word2vecCluster = (Word2VecCluster) dictResource;
    this.attributes = properties;
  }
  
  
  public void setParam(Word2VecCluster obj,Map<String, String> attributes)
  {
	  this.word2vecCluster=obj;
	  this.attributes = attributes;
  }
  
  public Map<String, ArtifactSerializer<?>> getArtifactSerializerMapping() {
    Map<String, ArtifactSerializer<?>> mapping = new HashMap<>();
    mapping.put("word2vecserializer", new Word2VecCluster.Word2VecClusterSerializer());    
    
    return Collections.unmodifiableMap(mapping);
  }
}