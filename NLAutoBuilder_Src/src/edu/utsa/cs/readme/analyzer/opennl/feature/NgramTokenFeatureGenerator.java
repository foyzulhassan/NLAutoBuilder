package edu.utsa.cs.readme.analyzer.opennl.feature;

import java.util.List;

import opennlp.tools.util.featuregen.FeatureGeneratorAdapter;

public class NgramTokenFeatureGenerator extends FeatureGeneratorAdapter {
	private static final String NTOKEN_PREFIX = "nt";
	private int nLeft;
	private int nRight;
	private boolean lowercase;

	public NgramTokenFeatureGenerator(boolean lowercase, int ngramLeft, int ngramRight) {
		this.nLeft = ngramLeft;
		this.nRight = ngramRight;
		this.lowercase = lowercase;
	}

	public void createFeatures(List<String> features, String[] tokens, int index, String[] preds) {
		int left = index - nLeft + 1;
		left = Math.max(left, 0);
		int right = index + nRight - 1;
		right = Math.min(right, tokens.length - 1);
		StringBuilder strTmp = new StringBuilder();
		strTmp.append(NTOKEN_PREFIX + "=");
		for (int i = left; i <= right; i++) {
			if (lowercase) {
				strTmp.append(tokens[i].toLowerCase() + "_");
			} else {
				strTmp.append(tokens[i] + "_");
			}
		}
		features.add(strTmp.toString());
	}
}