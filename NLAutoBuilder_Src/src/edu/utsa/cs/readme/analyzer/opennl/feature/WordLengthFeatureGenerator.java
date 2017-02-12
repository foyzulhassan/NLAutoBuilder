package edu.utsa.cs.readme.analyzer.opennl.feature;

import java.util.List;
import opennlp.tools.util.featuregen.FeatureGeneratorAdapter;


public class WordLengthFeatureGenerator extends FeatureGeneratorAdapter {
	
	////public void createFeatures(List<String> features, String[] tokens, int index, String[] preds) {

	public void createFeatures(List<String> features, String[] tokens, int index, String[] previousOutcomes)
	{
		features.add("w=length=" + tokens[index].length());
	}
}