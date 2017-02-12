package edu.utsa.cs.readme.analyzer.opennl;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.utsa.cs.readme.analyzer.entities.CmdRecognitionInfo;
import edu.utsa.cs.readme.analyzer.entities.PerformanceEval;
import edu.utsa.cs.readme.analyzer.opennl.feature.NgramTokenFeatureGenerator;
import edu.utsa.cs.readme.analyzer.opennl.feature.Word2VecCluster;
import edu.utsa.cs.readme.analyzer.opennl.feature.Word2VecClusterFeatureGenerator;
import edu.utsa.cs.readme.analyzer.opennl.feature.WordClusterFeatureGeneratorForCmd;
import edu.utsa.cs.readme.analyzer.opennl.feature.WordLengthFeatureGenerator;
import edu.utsa.cs.readme.analyzer.string.BuildCmdParser;
import edu.utsa.cs.readme.analyzer.string.CmdStringUtil;
import edu.utsa.cs.readme.analyzer.util.TextFileReaderWriter;
import edu.utsa.cs.repoanalysis.Config;
import opennlp.tools.cmdline.namefind.NameEvaluationErrorListener;
import opennlp.tools.cmdline.namefind.TokenNameFinderDetailedFMeasureListener;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.NameFinderSequenceValidator;
import opennlp.tools.namefind.NameSample;
import opennlp.tools.namefind.NameSampleDataStream;
import opennlp.tools.namefind.TokenNameFinder;
import opennlp.tools.namefind.TokenNameFinderEvaluationMonitor;
import opennlp.tools.namefind.TokenNameFinderEvaluator;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.tokenize.SimpleTokenizer;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.WhitespaceTokenizer;
import opennlp.tools.util.InvalidFormatException;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import opennlp.tools.util.Span;
import opennlp.tools.util.TrainingParameters;
import opennlp.tools.util.eval.EvaluationMonitor;
import opennlp.tools.util.eval.FMeasure;
import opennlp.tools.util.featuregen.AdaptiveFeatureGenerator;
import opennlp.tools.util.featuregen.AdditionalContextFeatureGenerator;
import opennlp.tools.util.featuregen.AggregatedFeatureGenerator;
import opennlp.tools.util.featuregen.BigramNameFeatureGenerator;
import opennlp.tools.util.featuregen.BrownBigramFeatureGenerator;
import opennlp.tools.util.featuregen.BrownCluster;
import opennlp.tools.util.featuregen.BrownTokenClassFeatureGenerator;
import opennlp.tools.util.featuregen.BrownTokenFeatureGenerator;
import opennlp.tools.util.featuregen.CachedFeatureGenerator;
import opennlp.tools.util.featuregen.CharacterNgramFeatureGenerator;
import opennlp.tools.util.featuregen.FeatureGeneratorResourceProvider;
import opennlp.tools.util.featuregen.InSpanGenerator;
import opennlp.tools.util.featuregen.OutcomePriorFeatureGenerator;
import opennlp.tools.util.featuregen.PrefixFeatureGenerator;
import opennlp.tools.util.featuregen.PreviousMapFeatureGenerator;
import opennlp.tools.util.featuregen.PreviousTwoMapFeatureGenerator;
import opennlp.tools.util.featuregen.SentenceFeatureGenerator;
import opennlp.tools.util.featuregen.StringPattern;
import opennlp.tools.util.featuregen.SuffixFeatureGenerator;
import opennlp.tools.util.featuregen.TokenClassFeatureGenerator;
import opennlp.tools.util.featuregen.TokenFeatureGenerator;
import opennlp.tools.util.featuregen.TokenPatternFeatureGenerator;
import opennlp.tools.util.featuregen.TrigramNameFeatureGenerator;
import opennlp.tools.util.featuregen.WindowFeatureGenerator;
import opennlp.tools.util.featuregen.WordClusterDictionary;
import opennlp.tools.util.featuregen.WordClusterFeatureGenerator;

public class OpenNLPNER {
	
	AdaptiveFeatureGenerator featureGenerator;
	
	private int logcount;
	
	
	public OpenNLPNER (int type, int logcount) 
	{
		this.logcount=logcount;
		if(type==1)
			featureGenerator=DeafultFeatureGenerator();
		
		else if(type==2)
			try {
				featureGenerator = createFeatureGenerator();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}		
		
	}
	
	public OpenNLPNER (AdaptiveFeatureGenerator featureGenerator)
	{
		this.featureGenerator = featureGenerator;
	}
	
	
	
	
	public AdaptiveFeatureGenerator DeafultFeatureGenerator()
	{
		AdaptiveFeatureGenerator featureGenerator = new CachedFeatureGenerator(
		         new AdaptiveFeatureGenerator[]{
		           new WindowFeatureGenerator(new TokenFeatureGenerator(), 2, 2),
		           new WindowFeatureGenerator(new TokenClassFeatureGenerator(true), 2, 2),
		           new OutcomePriorFeatureGenerator(),
		           new PreviousMapFeatureGenerator(),
		           new BigramNameFeatureGenerator(),
		           new SentenceFeatureGenerator(true, false)
		           });
		
		return featureGenerator; 
		
	}
	
	public AdaptiveFeatureGenerator createFeatureGenerator() throws IOException 
	{	
		
		/*AdaptiveFeatureGenerator featureGenerator = new CachedFeatureGenerator(new AdaptiveFeatureGenerator[] {

			new WindowFeatureGenerator(new TokenClassFeatureGenerator(true), 2, 2),
			new WindowFeatureGenerator(new TokenFeatureGenerator(true), 2, 2),
			new WindowFeatureGenerator(new NgramTokenFeatureGenerator(true, 2, 2), 2, 2),
			new WindowFeatureGenerator(new NgramTokenFeatureGenerator(true, 3, 3), 2, 2),
			//new WindowFeatureGenerator(new JeniaFeatureGenerator(), 2, 2),
			new PrefixFeatureGenerator(),
			new SuffixFeatureGenerator(),
			new WordLengthFeatureGenerator(),
			new CharacterNgramFeatureGenerator(2, 5),
			new BigramNameFeatureGenerator(),
			new OutcomePriorFeatureGenerator(),
			new PreviousMapFeatureGenerator(),
			new SentenceFeatureGenerator(true, false) 
		});*/    
		
	        String dictfile=Config.dictFilePath+"/Dict.txt";
		File initialFile = new File(dictfile);
		InputStream targetStream = new FileInputStream(initialFile);
	    
		String brdictfile=Config.dictFilePath+"/BrDict.txt";
		File initialFile2 = new File(brdictfile);
		InputStream targetStream2 = new FileInputStream(initialFile2);
		
		WordClusterDictionary dict=new WordClusterDictionary(targetStream);		
		BrownCluster brdict=new BrownCluster(targetStream2);
		
		String w2vdictfile=Config.dictFilePath+"/w2v.txt";
		File initialFile3 = new File(w2vdictfile);
		InputStream targetStream3 = new FileInputStream(initialFile);
		
		Word2VecCluster wordveccluster=new Word2VecCluster(targetStream3);
		
		Word2VecClusterFeatureGenerator obj=new Word2VecClusterFeatureGenerator();
		
		Map<String, String> prop = new HashMap<String, String>();
		prop.put("dict", "cmd");
		obj.setParam(wordveccluster, prop);
		
		AdaptiveFeatureGenerator featureGenerator = new CachedFeatureGenerator(
		         new AdaptiveFeatureGenerator[]{
		           new WindowFeatureGenerator(new TokenFeatureGenerator(), 2, 2),
		           new WindowFeatureGenerator(new TokenClassFeatureGenerator(true), 2, 2),			          
		           new OutcomePriorFeatureGenerator(),
		           new PreviousMapFeatureGenerator(),
		           new BigramNameFeatureGenerator(),		       
		           new SentenceFeatureGenerator(true, false),
		           new PrefixFeatureGenerator(),
		           new AdditionalContextFeatureGenerator(),
		           new PreviousTwoMapFeatureGenerator(),
		           new TokenPatternFeatureGenerator(),
		           new TrigramNameFeatureGenerator(),
		           new WordClusterFeatureGeneratorForCmd(dict,"wcf",true),
		           new BrownBigramFeatureGenerator(brdict),
		           new BrownTokenFeatureGenerator(brdict),
		           obj
		          
		           });
		
		
		return featureGenerator;
	}	
	
	
	@SuppressWarnings("deprecation")
	public void trainNER(String trainingPath, String modelFilePath, int iterator, int cutoff) throws Exception 
	{
		Charset charset = Charset.forName("UTF-8");
		ObjectStream<String> lineStream = new PlainTextByLineStream(new FileInputStream(trainingPath), charset);
	
		
	
		
		ObjectStream<NameSample> sampleStream = new NameSampleDataStream(lineStream);
		
		TrainingParameters mlParams = new TrainingParameters();
	    mlParams.put(TrainingParameters.ALGORITHM_PARAM, "MAXENT");
	    //mlParams.put(TrainingParameters.ALGORITHM_PARAM, "PERCEPTRON");
		mlParams.put(TrainingParameters.ITERATIONS_PARAM, Integer.toString(5000));
	    mlParams.put(TrainingParameters.CUTOFF_PARAM, Integer.toString(5));
	    		
	
	    
		TokenNameFinderModel model;		
	
		try {
			model = NameFinderME.train("en", "ner-cmd-model", sampleStream, mlParams,featureGenerator, Collections.<String, Object> emptyMap());
				
		} finally {
			sampleStream.close();
		}
		
		
		BufferedOutputStream modelOut = null;
		
		try {
				modelOut = new BufferedOutputStream(new FileOutputStream(modelFilePath));
				model.serialize(modelOut);
			} finally
				{
					if (modelOut != null) {
							modelOut.close();
					}
		}
	}
	
	
	public PerformanceEval evaluatebyExactMatching(String testPath, String modelPath) throws Exception 
	{
		InputStream modelIn = new FileInputStream(modelPath);
		
		List<EvaluationMonitor<NameSample>> listeners = getListeners(true);
		
		TokenNameFinderModel NEModel = new TokenNameFinderModel(modelIn);
		
		NameFinderME finder=new NameFinderME(NEModel,featureGenerator,NameFinderME.DEFAULT_BEAM_SIZE);	
		
		TokenNameFinderEvaluator evaluator = new TokenNameFinderEvaluator(finder,listeners.toArray(new TokenNameFinderEvaluationMonitor[listeners.size()]));
		
		Charset charset = Charset.forName("UTF-8");
		ObjectStream<String> lineStream = new PlainTextByLineStream(new FileInputStream(testPath), charset);
			
		/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		String sample;
		//testStream.
		 
		// StringTokenizer st = new StringTokenizer(msg, ". ");
		File f = new File(testPath);
		//System.out.println(f.getName());
		
		 while ((sample = lineStream.read()) != null) {
			 //Span[] spans = ((TokenNameFinder) NEModel).find(sample.getSentence());
			   // System.out.println(sample);
			    if(!sample.contains(". "))
			    {
			    	int i=0;
			    	TextFileReaderWriter.DumptoCommonLabelFile(Config.prunePath, f.getName(), sample);
			    }
			    else
			    {
			    	/*System.out.println(sample);
			    	StringTokenizer st = new StringTokenizer(sample, ". ");
			        while (st.hasMoreTokens()) {
			            System.out.println(st.nextToken());    
			            }*/
			    	///System.out.println("main:"+sample);
			    	Pattern re = Pattern.compile("[^.!?\\s][^.!?]*(?:[.!?](?!['\"]?\\s|$)[^.!?]*)*[.!?]?['\"]?(?=\\s|$)", Pattern.MULTILINE | Pattern.COMMENTS);
			        Matcher reMatcher = re.matcher(sample);
			        
			        while (reMatcher.find()) {
			        	
			           // System.out.println("token:"+reMatcher.group());
			            String line=reMatcher.group();
			            
			            if(line.length()>8)
			            {
			            	int i=0;
			            	while(i<line.length())
			            	{
			            		if(line.charAt(i)=='<' & (line.length()-i)>6)
			            		{
			            			if(line.charAt(i+1)=='S' && line.charAt(i+2)=='T' && line.charAt(i+3)=='A' && line.charAt(i+4)=='R' && line.charAt(i+5)=='T')
			            			{
			            				line=line.substring(i);
			            				TextFileReaderWriter.DumptoCommonLabelFile(Config.prunePath, f.getName(), line);
			            				break;
			            			}
			            		}
			            		
			            		i++;
			            	}
			            	
			            }
			            
			            //System.out.println("token:"+line);
			            
			        }
			
			        	
			    	
			    }
			 
			 }
		
		///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		//ObjectStream<NameSample> iterator = new ObjectStream();
		ObjectStream<String> lineStreamnew = new PlainTextByLineStream(new FileInputStream(Config.prunePath+f.getName()), charset);
		ObjectStream<NameSample> testStream = new NameSampleDataStream(lineStreamnew);
		//ObjectStream<NameSample> testStream2 = new NameSampleDataStream();
		
		////////////////////////////////////////////////////////////////////////////
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		/////////////////////////////////////////////////////////////////////////////
		
		
		
		
		evaluator.evaluate(testStream);
		
		FMeasure result = evaluator.getFMeasure();		
		
		System.out.println(result.toString());
		
		PerformanceEval perfeval=new PerformanceEval();
		
		perfeval.setPrecision(evaluator.getFMeasure().getPrecisionScore());
		perfeval.setRecall(evaluator.getFMeasure().getRecallScore());
		perfeval.setFMeasure(evaluator.getFMeasure().getFMeasure());
		
		for (EvaluationMonitor<NameSample> listener : listeners) 
		{
			if (listener instanceof NameEvaluationErrorListener)
			{
				//System.out.println(listener.toString());
				//listener.
				//System.out.println(listener.);
				//listener.
				//listener.g
				//NameSample reference = null;
				//NameSample prediction = null;
				//((TokenNameFinderDetailedFMeasureListener) listener).missclassified(reference, prediction);
				
				//System.out.println("Reference->:"+reference+"Prediction->:"+prediction);
				//listener.
			}
		}
		
		return perfeval;
	}
	
	private List<EvaluationMonitor<NameSample>> getListeners(boolean detailed)
	{
		
		
		List<EvaluationMonitor<NameSample>> listeners = new LinkedList<EvaluationMonitor<NameSample>>();
		if (detailed) {
		
		String OUTPUT_FILE = "/home/foyzulhassan/Research/Data/Label_1000/NFold_Final_Label/NFoldV4/"+Integer.toString(logcount)+"/error.log";
		OutputStream out=null;
		try {
			out = new FileOutputStream(OUTPUT_FILE);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		listeners.add(new NameEvaluationErrorListener(out));
		listeners.add(new TokenNameFinderDetailedFMeasureListener());
		}
		
		
		return listeners;
  }
	
	/*public void evaluatebyApproximateMatching(String testPath, String modelPath) throws Exception {
		InputStream modelIn = new FileInputStream(modelPath);
		
		TokenNameFinderModel NEModel = new TokenNameFinderModel(modelIn);
		
		TokenNameFinderApproximateEvaluator evaluator = new TokenNameFinderApproximateEvaluator(new NameFinderME(NEModel));
		Charset charset = Charset.forName("UTF-8");
		
		ObjectStream<String> lineStream = new PlainTextByLineStream(new FileInputStream(testPath), charset);
		ObjectStream<NameSample> testStream = new NameSampleDataStream(lineStream);
		evaluator.evaluate(testStream);
		ApproximateFMeasure result = evaluator.getFMeasure();
		System.out.println(result.toString());
	}*/
	
	 private void displayNames(Span[] names, String[] tokens) {
		 for (int si = 0; si < names.length; si++) { //<co id="co.opennlp.name.eachname"/>
		 StringBuilder cb = new StringBuilder();
		 for (int ti = names[si].getStart(); ti < names[si].getEnd(); ti++) {
		 cb.append(tokens[ti]).append(" "); //<co id="co.opennlp.name.eachtoken"/>
		 }
		 System.out.println(cb.substring(0, cb.length() - 1)); //<co id="co.opennlp.name.extra"/>
		 System.out.println("\ttype: " + names[si].getType());
		 }
	}
	
	
	public List<CmdRecognitionInfo> recognize(String testPath, String modelPath) throws Exception {
		boolean iscmdfound = false;
		String origcmdtype = "";
		InputStream modelIn = new FileInputStream(modelPath);
		TokenNameFinderModel NEModel = new TokenNameFinderModel(modelIn);
		Charset charset = Charset.forName("UTF-8");

		NameFinderME finder = new NameFinderME(NEModel, featureGenerator, NameFinderME.DEFAULT_BEAM_SIZE);
		Tokenizer tokenizer = WhitespaceTokenizer.INSTANCE;/// SimpleTokenizer.INSTANCE;
															/// //<co
															/// id="co.opennlp.name.inittokenizer2"/>

		List<String> list = new ArrayList<String>();

		list = TextFileReaderWriter.GetFileContentByLine(testPath);

		FileWriter fileWriter = null;
		try {
			fileWriter = new FileWriter(
					Config.classifyOutput, true);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

		int lineindex = 0;
		boolean[] cmdflag = new boolean[list.size()];

		for (int i = 0; i < list.size(); i++) {
			cmdflag[i] = false;
		}

		List<CmdRecognitionInfo> cmdrecognitioninfo = new ArrayList<CmdRecognitionInfo>();
		String prevLine = "";

		ObjectStream<String> lineStream = new PlainTextByLineStream(new FileInputStream(testPath), charset);

		/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		String sample;
		// testStream.

		// StringTokenizer st = new StringTokenizer(msg, ". ");
		File f = new File(testPath);
		// System.out.println(f.getName());

		while ((sample = lineStream.read()) != null) {
			iscmdfound = false;

			if (sample.startsWith("'") && sample.endsWith("'")) {
				sample = sample.substring(1, sample.length() - 1);
			}

			if (sample.startsWith("`") && sample.endsWith("`")) {
				sample = sample.substring(1, sample.length() - 1);
			}

			if (sample.startsWith("\"") && sample.endsWith("\"")) {
				sample = sample.substring(1, sample.length() - 1);
			}

			String sentence = sample;

			// Split the sentence into tokens
			String[] tokens = tokenizer.tokenize(sentence);
			List<String> mylistStr = new ArrayList<String>();

			// String[] cmdtokens;
			int wflag = 0;
			boolean once = false;

			for (String token : tokens) {
				if (CmdStringUtil.ConatainsCmd("mvn",token) || CmdStringUtil.ConatainsAntCmd(token) || CmdStringUtil.ConatainsCmd("gradlew",token)
						|| CmdStringUtil.ConatainsCmd("gradle",token))
				{
					String cmdtxt = "";
					String prvtxt = "";				
					
					
					boolean prevflag = false;
					if (token.contains("mvn")) {
						cmdtxt = token.substring(token.indexOf("mvn"));

						if (token.indexOf("mvn") >= 1) {
							prvtxt = token.substring(0, token.indexOf("mvn"));
							origcmdtype = "mvn";
							prevflag = true;
						}

					}

					else if (token.contains("ant")) {
						cmdtxt = token.substring(token.indexOf("ant"));

						if (token.indexOf("ant") >= 1) {
							prvtxt = token.substring(0, token.indexOf("ant"));
							origcmdtype = "ant";
							prevflag = true;
						}
					}

					else if (token.contains("gradlew")) {
						cmdtxt = token.substring(token.indexOf("gradlew"));

						if (token.indexOf("gradlew") >= 1) {
							prvtxt = token.substring(0, token.indexOf("gradlew"));
							origcmdtype = "./gradlew";
							prevflag = true;
						}
					}

					else if (token.contains("gradle")) {
						cmdtxt = token.substring(token.indexOf("gradle"));

						if (token.indexOf("gradle") >= 1) {
							prvtxt = token.substring(0, token.indexOf("gradle"));
							origcmdtype = "gradle";
							prevflag = true;
						}
					}

					if (prevflag)
						mylistStr.add(prvtxt);

					mylistStr.add(cmdtxt);
					wflag = 1;

				} else if (wflag == 1) {
					mylistStr.add(token);

				}

			}

			String[] bldtokens = mylistStr.toArray(new String[mylistStr.size()]);
			String testsentence = "";

			for (int test = 0; test < mylistStr.size(); test++) {
				testsentence += (mylistStr.get(test));

				if (!mylistStr.get(test).endsWith("./"))
					testsentence += " ";
			}

			if (testsentence.length() > 0) {
				testsentence = CmdStringUtil.GetCoatedCmdString(testsentence);
				bldtokens = tokenizer.tokenize(testsentence);
			}
			Span[] cmdSpans = finder.find(bldtokens);
			// Span[] testcmdSpans=finder.find(testsentence);
			// Span[] cmdSpans = finder.

			String[] entities = Span.spansToStrings(cmdSpans, bldtokens);

			for (String entity : entities) {

				CmdRecognitionInfo obj = new CmdRecognitionInfo();

				iscmdfound = true;
				String cmdstring = CmdStringUtil.RemoveLeadingTrailingSpecialChar(entity);

				// String
				// testingcmd=CmdStringUtil.RemoveLeadingTrailingSpecialChar("'mvn
				// clean abc -build'");
				// System.out.println(testingcmd + "||| Was in setnence==> "
				// +
				// sentence + "|||File=" + testPath);

				//System.out.println(cmdstring + "||| Was in setnence==> " + sentence + "|||File=" + testPath);
				obj.setRecognizedCmdtxt(cmdstring);
				obj.setActualCmdtxt(sentence);
				obj.setLineNumber(lineindex);

				if ((lineindex - 1) >= 0) {
					if (cmdflag[lineindex - 1] == false) {
						obj.setBeforeCmdtxt(prevLine);
					} else {
						obj.setBeforeCmdtxt("");
					}
				}

				cmdrecognitioninfo.add(obj);

				try {
					bufferedWriter.write(entity + " Was in setnence==> " + sentence);
					bufferedWriter.newLine();

				} catch (IOException ex) {
					System.out.println("Error writing to file '");
				}
			}

			if (iscmdfound == false && !origcmdtype.equals("mvn")) {
				String testsentencemvn = "";
				if (testsentence.length() > 0) {
					testsentencemvn = CmdStringUtil.GetDifferentTypeCmd(origcmdtype, "mvn", testsentence);
					bldtokens = tokenizer.tokenize(testsentencemvn);
				}
				cmdSpans = finder.find(bldtokens);
				// Span[] testcmdSpans=finder.find(testsentence);
				// Span[] cmdSpans = finder.

				entities = Span.spansToStrings(cmdSpans, bldtokens);

				for (String entity : entities) {

					CmdRecognitionInfo obj = new CmdRecognitionInfo();

					iscmdfound = true;
					String cmdstring = CmdStringUtil.RemoveLeadingTrailingSpecialChar(entity);
					cmdstring = CmdStringUtil.GetDifferentTypeCmd("mvn", origcmdtype, cmdstring);
					// String
					// testingcmd=CmdStringUtil.RemoveLeadingTrailingSpecialChar("'mvn
					// clean abc -build'");
					// System.out.println(testingcmd + "||| Was in
					// setnence==> " +
					// sentence + "|||File=" + testPath);

					//System.out.println(cmdstring + "||| Was in setnence==> " + sentence + "|||File=" + testPath);
					obj.setRecognizedCmdtxt(cmdstring);
					obj.setActualCmdtxt(sentence);
					obj.setLineNumber(lineindex);

					if ((lineindex - 1) >= 0) {
						if (cmdflag[lineindex - 1] == false) {
							obj.setBeforeCmdtxt(prevLine);
						} else {
							obj.setBeforeCmdtxt("");
						}
					}

					cmdrecognitioninfo.add(obj);

					try {
						bufferedWriter.write(entity + " Was in setnence==> " + sentence);
						bufferedWriter.newLine();

					} catch (IOException ex) {
						System.out.println("Error writing to file '");
					}
				}
			}

			if (iscmdfound == false && !origcmdtype.equals("ant")) {
				String testsentenceant = "";
				if (testsentence.length() > 0) {
					testsentenceant = CmdStringUtil.GetDifferentTypeCmd(origcmdtype, "ant", testsentence);
					bldtokens = tokenizer.tokenize(testsentenceant);
				}
				cmdSpans = finder.find(bldtokens);
				// Span[] testcmdSpans=finder.find(testsentence);
				// Span[] cmdSpans = finder.

				entities = Span.spansToStrings(cmdSpans, bldtokens);

				for (String entity : entities) {

					CmdRecognitionInfo obj = new CmdRecognitionInfo();

					iscmdfound = true;
					String cmdstring = CmdStringUtil.RemoveLeadingTrailingSpecialChar(entity);
					cmdstring = CmdStringUtil.GetDifferentTypeCmd("ant", origcmdtype, cmdstring);
					// String
					// testingcmd=CmdStringUtil.RemoveLeadingTrailingSpecialChar("'mvn
					// clean abc -build'");
					// System.out.println(testingcmd + "||| Was in
					// setnence==> " +
					// sentence + "|||File=" + testPath);

					//System.out.println(cmdstring + "||| Was in setnence==> " + sentence + "|||File=" + testPath);
					obj.setRecognizedCmdtxt(cmdstring);
					obj.setActualCmdtxt(sentence);
					obj.setLineNumber(lineindex);

					if ((lineindex - 1) >= 0) {
						if (cmdflag[lineindex - 1] == false) {
							obj.setBeforeCmdtxt(prevLine);
						} else {
							obj.setBeforeCmdtxt("");
						}
					}

					cmdrecognitioninfo.add(obj);

					try {
						bufferedWriter.write(entity + " Was in setnence==> " + sentence);
						bufferedWriter.newLine();

					} catch (IOException ex) {
						System.out.println("Error writing to file '");
					}
				}
			}

			if (iscmdfound == false && !origcmdtype.equals("gradle")) {
				String testsentencegradle = "";
				if (testsentence.length() > 0) {
					testsentencegradle = CmdStringUtil.GetDifferentTypeCmd(origcmdtype, "gradle", testsentence);
					bldtokens = tokenizer.tokenize(testsentencegradle);
				}
				cmdSpans = finder.find(bldtokens);
				// Span[] testcmdSpans=finder.find(testsentence);
				// Span[] cmdSpans = finder.

				entities = Span.spansToStrings(cmdSpans, bldtokens);

				for (String entity : entities) {

					CmdRecognitionInfo obj = new CmdRecognitionInfo();

					iscmdfound = true;
					String cmdstring = CmdStringUtil.RemoveLeadingTrailingSpecialChar(entity);
					cmdstring = CmdStringUtil.GetDifferentTypeCmd("gradle", origcmdtype, cmdstring);
					// String
					// testingcmd=CmdStringUtil.RemoveLeadingTrailingSpecialChar("'mvn
					// clean abc -build'");
					// System.out.println(testingcmd + "||| Was in
					// setnence==> " +
					// sentence + "|||File=" + testPath);

					//System.out.println(cmdstring + "||| Was in setnence==> " + sentence + "|||File=" + testPath);
					obj.setRecognizedCmdtxt(cmdstring);
					obj.setActualCmdtxt(sentence);
					obj.setLineNumber(lineindex);

					if ((lineindex - 1) >= 0) {
						if (cmdflag[lineindex - 1] == false) {
							obj.setBeforeCmdtxt(prevLine);
						} else {
							obj.setBeforeCmdtxt("");
						}
					}

					cmdrecognitioninfo.add(obj);

					try {
						bufferedWriter.write(entity + " Was in setnence==> " + sentence);
						bufferedWriter.newLine();

					} catch (IOException ex) {
						System.out.println("Error writing to file '");
					}
				}
			}

			if (iscmdfound == false && !origcmdtype.equals("./gradlew")) {
				String testsentencegradlew = "";
				if (testsentence.length() > 0) {
					//System.out.println("####" + testsentence);
					testsentencegradlew = CmdStringUtil.GetDifferentTypeCmd(origcmdtype, "./gradlew", testsentence);
					bldtokens = tokenizer.tokenize(testsentencegradlew);
				}

				/// System.out.println(testsentence);
				cmdSpans = finder.find(bldtokens);
				// Span[] testcmdSpans=finder.find(testsentence);
				// Span[] cmdSpans = finder.

				entities = Span.spansToStrings(cmdSpans, bldtokens);

				for (String entity : entities) {

					CmdRecognitionInfo obj = new CmdRecognitionInfo();

					iscmdfound = true;
					String cmdstring = CmdStringUtil.RemoveLeadingTrailingSpecialChar(entity);
					cmdstring = CmdStringUtil.GetDifferentTypeCmd("gradlew", origcmdtype, cmdstring);
					// String
					// testingcmd=CmdStringUtil.RemoveLeadingTrailingSpecialChar("'mvn
					// clean abc -build'");
					// System.out.println(testingcmd + "||| Was in
					// setnence==> " +
					// sentence + "|||File=" + testPath);

					//System.out.println(cmdstring + "||| Was in setnence==> " + sentence + "|||File=" + testPath);
					obj.setRecognizedCmdtxt(cmdstring);
					obj.setActualCmdtxt(sentence);
					obj.setLineNumber(lineindex);

					if ((lineindex - 1) >= 0) {
						if (cmdflag[lineindex - 1] == false) {
							obj.setBeforeCmdtxt(prevLine);
						} else {
							obj.setBeforeCmdtxt("");
						}
					}

					cmdrecognitioninfo.add(obj);

					try {
						bufferedWriter.write(entity + " Was in setnence==> " + sentence);
						bufferedWriter.newLine();

					} catch (IOException ex) {
						System.out.println("Error writing to file '");
					}
				}
			}

			finder.clearAdaptiveData();
			lineindex++;
			if (sentence.matches(".*[a-zA-Z]+.*"))
				prevLine = sentence;

		}

		try {
			bufferedWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return cmdrecognitioninfo;

	}
	
	public List<CmdRecognitionInfo> recognizehtmlcontent(String testPath, String modelPath) throws Exception {

		InputStream modelIn = new FileInputStream(modelPath);
		TokenNameFinderModel NEModel = new TokenNameFinderModel(modelIn);
		Charset charset = Charset.forName("UTF-8");

		NameFinderME finder = new NameFinderME(NEModel, featureGenerator, NameFinderME.DEFAULT_BEAM_SIZE);
		Tokenizer tokenizer = WhitespaceTokenizer.INSTANCE;/// SimpleTokenizer.INSTANCE;
															/// //<co
															/// id="co.opennlp.name.inittokenizer2"/>

		List<String> list = new ArrayList<String>();

		list = TextFileReaderWriter.GetFileContentByLine(testPath);

		Iterator<String> listIterator = list.iterator();

		int lineindexfromfile = 0;
		int lineindex = 0;

		boolean[] cmdflag = new boolean[list.size()];

		for (int i = 0; i < list.size(); i++) {
			cmdflag[i] = false;
		}

		List<CmdRecognitionInfo> cmdrecognitioninfo = new ArrayList<CmdRecognitionInfo>();
		String prevLine = "";

		while (listIterator.hasNext()) {

			String linestr = listIterator.next();

			if (linestr.startsWith("@s")) {
				prevLine = linestr.substring(3);

			}

			else {

				String sentence;
				String sample;
				String strlinenumber = linestr.replaceFirst(".*?(\\d+).*", "$1");
				lineindexfromfile = Integer.parseInt(strlinenumber);
				sample = linestr.substring(linestr.indexOf(' ') + 1);
				
				if(sample.startsWith("'") && sample.endsWith("'")) 
				{
					sample=sample.substring(1, sample.length()-1);
				}
				
				if(sample.startsWith("`") && sample.endsWith("`")) 
				{
					sample=sample.substring(1, sample.length()-1);
				}
				
				if(sample.startsWith("\"") && sample.endsWith("\"")) 
				{
					sample=sample.substring(1, sample.length()-1);
				}
				 
				 
		        sentence=sample ;
				

				String[] tokens = tokenizer.tokenize(sentence);
				List<String> mylistStr = new ArrayList<String>();

				// String[] cmdtokens;       
		
		        //String[] cmdtokens;
		        int wflag=0; 
		        boolean once=false;
		     
		        for (String token : tokens)
		        {
		        	if(token.contains("mvn") || CmdStringUtil.ConatainsAntCmd(token) || token.contains("gradlew") || token.contains("gradle"))
		        	{
		        		String cmdtxt="";
		        		String prvtxt="";
		        		boolean prevflag=false;
		        		if(token.contains("mvn"))
		        		{
		        			cmdtxt=token.substring(token.indexOf("mvn"));
		        			
		        			if(token.indexOf("mvn")>=1)
		        			{
		        				prvtxt=token.substring(0,token.indexOf("mvn"));
		        				prevflag=true;
		        			}
		        			
		        		}
		        		
		        		else if(token.contains("ant"))
		        		{
		        			cmdtxt=token.substring(token.indexOf("ant"));
		        			

		        			if(token.indexOf("ant")>=1)
		        			{
		        				prvtxt=token.substring(0,token.indexOf("ant"));
		        				prevflag=true;
		        			}
		        		}
		        		
		        		else if(token.contains("gradle"))
		        		{
		        			cmdtxt=token.substring(token.indexOf("gradle"));
		        			
		        			if(token.indexOf("gradle")>=1)
		        			{
		        				prvtxt=token.substring(0,token.indexOf("gradle"));
		        				prevflag=true;
		        			}
		        		}
		        		
		        		else if(token.contains("gradlew"))
		        		{
		        			cmdtxt=token.substring(token.indexOf("gradlew"));
		        			
		        			if(token.indexOf("gradlew")>=1)
		        			{
		        				prvtxt=token.substring(0,token.indexOf("gradlew"));
		        				prevflag=true;
		        			}
		        		}
		        		
		        		if(prevflag)
		        			mylistStr.add(prvtxt);
		        		
		        		mylistStr.add(cmdtxt);
		        		wflag=1;
		        		
		        	}
		        	else if(wflag==1)
		        	{
		        		mylistStr.add(token);
		        		
		        	}   	
		        	
		        }
				
				
				String[] bldtokens = mylistStr.toArray(new String[mylistStr.size()]);
				// Find the names in the tokens and return Span objects
				Span[] cmdSpans = finder.find(bldtokens);
				// Span[] cmdSpans = finder.

				String[] entities = Span.spansToStrings(cmdSpans, bldtokens);

				for (String entity : entities)
				{
					CmdRecognitionInfo obj = new CmdRecognitionInfo();
					String cmdstring=CmdStringUtil.RemoveLeadingTrailingSpecialChar(entity);				
					
					
					//System.out.println(cmdstring + "||| Was in setnence==> " + sentence + "|||File=" + testPath);					
					
					
		            obj.setRecognizedCmdtxt(cmdstring);
					obj.setActualCmdtxt(sentence);
					obj.setLineNumber(lineindexfromfile);

					if ((lineindex - 1) >= 0) {
						if (cmdflag[lineindex] == false) {
							obj.setBeforeCmdtxt(prevLine);
						} else {
							obj.setBeforeCmdtxt("");
						}
					}

					cmdflag[lineindex] = true;

					cmdrecognitioninfo.add(obj);
				}
	
				finder.clearAdaptiveData();

			}

			lineindex++;
		}

		return cmdrecognitioninfo;

	}
	
}
