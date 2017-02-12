package edu.utsa.cs.repoanalysis.typeresolver.librepo;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import edu.utsa.cs.util.FileManager;
import edu.utsa.cs.util.Logger;

public class LibDownloader {
    public static String noLibFound = "Cannot find Jar file for the class";
    private String cacheRepo;
    private String localJarPath;
    private LoadBasic loader;

    public LibDownloader(LoadBasic loader, String localJarPath,
	    String libCacheDir) {
	this.loader = loader;
	this.localJarPath = localJarPath;
	this.cacheRepo = libCacheDir;
    }

    public void resolveClassesFromMavenRepo(List<String> classes, Logger logger)
	    throws LibResolvingException {
	HashSet<String> resolvedClasses = new HashSet<String>();
	Hashtable<String, Set<String>> jarClassTable = new Hashtable<String, Set<String>>();
	Random r = new Random(1234567);
	for (String className : classes) {
	    if (resolvedClasses.contains(className)) {

	    } else {
		try {
		    System.out.println(">>>Finding lib for " + className);
		    String jarName = fetchBestJar(className, r, logger);

		    logger.log("resolve", className, Logger.LEVEL_INFO);
		    logger.log("download", jarName, Logger.LEVEL_INFO);
		    Set<String> allClasses = FileManager
			    .fetchClassesFromJar(jarName);
		    logger.log("download", "jarName:" + allClasses, Logger.LEVEL_DEBUG);
		    if (allClasses == null) {
			logger.log("resolve", className
				+ " can not be resolved", Logger.LEVEL_IMPORTANT);
		    } else {
			allClasses.retainAll(classes);
			logger.log("resolved with " + jarName,
				allClasses.toString(), Logger.LEVEL_VERBOSE);
			resolvedClasses.addAll(allClasses);
			logger.log("summary",
				"resolved:" + resolvedClasses.size() + ";all:"
					+ classes.size(), Logger.LEVEL_IMPORTANT);

			jarClassTable.put(jarName, allClasses);
		    }
		} catch (IOException e) {
		    throw new LibResolvingException(className, e);
		} catch (JSONException e) {
		    throw new LibResolvingException(className, e);
		}
	    }
	}

	for (String jarI : jarClassTable.keySet()) {
	    for (String jarJ : jarClassTable.keySet()) {
		if (!jarI.equals(jarJ)
			&& jarClassTable.get(jarI).containsAll(
				jarClassTable.get(jarJ))) {
		    (new File(this.localJarPath + "/" + jarJ)).delete();
		}
	    }
	}
    }

    public List<MavenItem> fetchAllJars(String className, Logger logger) throws JSONException,
	    IOException {
	String query = "fc:" + className;
	URL url_fc = new URL("https://search.maven.org/solrsearch/select?q="
		+ query + "&rows=20&wt=json");
	BufferedReader in = new BufferedReader(new InputStreamReader(url_fc
		.openConnection().getInputStream()));
	String line = in.readLine();

	int numFound = getNumOfItems(line);
	in.close();
	if (numFound > 20) {
	    if (numFound > 100 && numFound < 200) {
		numFound = 100;
	    }
	    else if (numFound > 200 && numFound < 300) {
		numFound = 200;
	    }
	    else if (numFound > 400 && numFound < 500) {
		numFound = 400;
	    }
	    else if (numFound > 500) {
		numFound = 500;
	    }
	    URL url_full = new URL(
		    "https://search.maven.org/solrsearch/select?q=" + query
			    + "&rows=" + numFound + "&wt=json");
	    in = new BufferedReader(new InputStreamReader(url_full
		    .openConnection().getInputStream()));
	    line = in.readLine();
	    logger.log("Json Content", url_full.toString(), Logger.LEVEL_DEBUG);
	    logger.log("Json Content", line, Logger.LEVEL_DEBUG);
	    List<MavenItem> items = getAllItemsFromLine(line, query);
	    return items;
	} else if (numFound > 0) {
	    List<MavenItem> items = getAllItemsFromLine(line, query);
	    return items;
	}
	return new ArrayList<MavenItem>();
    }

    public String fetchBestJar(String className, Random r, Logger logger) throws IOException,
	    JSONException, LibResolvingException {
	List<String> repoJars = loader.getRepoMap().get(className);
	if (!(repoJars == null)) {
	    logger.logTimeStart("libcache");
	    String jarPath = repoJars.get(0);
	    String jarName = jarPath.substring(jarPath.lastIndexOf('/') + 1);
	    FileUtils.copyFile(new File(jarPath), new File(this.localJarPath
		    + "/" + jarName));
	    logger.logTimeEnd("libcache");
	    return this.localJarPath + "/" + jarName;
	} else {
	    logger.logTimeStart("download");
	    List<MavenItem> items = fetchAllJars(className, logger);
	    try {
		int next = r.nextInt() % 10;
		Thread.sleep(10000 + next * 1000);
	    } catch (InterruptedException e) {
		e.printStackTrace();
	    }

	    if (items.size() == 0) {
		throw new LibResolvingException(className);
	    }
	    for (MavenItem item : items) {
		item.calculateScore();
	    }
	    Collections.sort(items);
	    String downURL = items.get(0).getDownloadURL();
	    //downURL=downURL.replaceFirst("http", "https");
	    String fileName = downURL.substring(downURL.lastIndexOf('/') + 1);
	    File target = new File(this.cacheRepo + "/" + fileName);
	    if (!target.exists()) {
		FileUtils.copyURLToFile(new URL(downURL), target);
	    }
	    FileUtils.copyFile(target, new File(this.localJarPath + "/"
		    + fileName));
	    logger.logTimeEnd("download");
	    return this.localJarPath + "/" + fileName;
	}
    }

    public HashSet<String> fetchPackages(String jarFile) throws IOException,
	    InterruptedException {
	HashSet<String> packs = new HashSet<String>();
	Set<String> classes = FileManager.fetchClassesFromJar(jarFile);
	for (String className : classes) {
	    packs.add(className.substring(0, className.lastIndexOf('.')));
	}
	return packs;
    }

    /*
     * public HashSet<String> fetchClasses(String jarFile) throws IOException,
     * InterruptedException { HashSet<String> packs = new HashSet<String>();
     * Process process = Runtime.getRuntime().exec("jar tf " +
     * jarFile.replace('/', '\\')); BufferedReader stdInput = new
     * BufferedReader(new InputStreamReader(process.getInputStream())); String
     * outline = ""; while((outline = stdInput.readLine())!=null){
     * if(outline.endsWith(".class") && outline.lastIndexOf('/')!=-1){ String
     * packName = outline.substring(0, outline.lastIndexOf('.')).replace('/',
     * '.'); packs.add(packName); } } stdInput.close(); return packs; }
     */

    private static int getNumOfItems(String line) throws JSONException {
	JSONTokener js = new JSONTokener(line);
	JSONObject root = new JSONObject(js);
	JSONObject response = root.getJSONObject("response");
	int numFound = response.getInt("numFound");
	return numFound;
    }

    private List<MavenItem> getAllItemsFromLine(String line, String top)
	    throws JSONException {
	List<MavenItem> mItems = new ArrayList<MavenItem>();
	JSONTokener js = new JSONTokener(line);
	JSONObject root = new JSONObject(js);
	JSONObject response = root.getJSONObject("response");
	JSONObject highlightMap = root.getJSONObject("highlighting");
	int numFound = response.getInt("numFound");
	if (numFound != 0) {
	    JSONArray results = response.getJSONArray("docs");
	    for (int i = 0; i < results.length(); i++) {
		JSONObject item = (JSONObject) results.get(i);
		MavenItem mItem = new MavenItem(top);
		String id = item.getString("id");
		JSONArray highlights = highlightMap.getJSONObject(id)
			.optJSONArray("fch");
		if (highlights != null && highlights.length() != 0) {
		    mItem.highlight = reformat(highlights.get(0).toString());
		}
		mItem.timestamp = item.getLong("timestamp");
		mItem.groupID = item.getString("g");
		mItem.artifactID = item.getString("a");
		mItem.rank = i;
		if (mItem.isFc) {
		    mItem.version = item.getString("v");
		} else {
		    mItem.version = item.getString("latestVersion");
		}
		JSONArray files = item.getJSONArray("ec");

		for (int j = 0; j < files.length(); j++) {
		    mItem.files.add(files.getString(j));
		}
		if (mItem.isValid()) {
		    mItems.add(mItem);
		}
	    }
	}
	return mItems;
    }

    private String reformat(String input) {
	String ret = input;
	ret = ret.replaceAll(" ", "");
	ret = ret.replaceAll("<em>", "");
	ret = ret.replaceAll("<\\/em>", "");
	return ret.trim();
    }

    private static class MavenItem implements Comparable<MavenItem> {
	public String highlight;
	String version;
	String groupID;
	String artifactID;
	long timestamp;
	int rank;
	List<String> files;
	long score;
	String query;
	boolean isFc;

	public MavenItem(String query) {
	    this.query = query;
	    if (this.query.startsWith("fc:")) {
		this.isFc = true;
		this.query = this.query.substring(3);
	    }
	    this.files = new ArrayList<String>();
	    this.score = 0;
	}

	public String toString() {
	    return this.groupID + ":" + this.artifactID + ":" + this.score;
	}

	public boolean isValid() {
	    return this.files.contains(".jar") && this.highlight != null
		    && this.highlight.startsWith(this.query);
	}

	public void calculateScore() {
	    if (files.contains("-jar-with-dependencies.jar")) {
		this.score = -1;
	    } else {
		if (isSimilar(this.groupID, query)) {
		    score += 100;
		}
		if (isSimilar(this.artifactID, query)) {
		    score += 100;
		}
		if (this.isFc) {
		    score = score + (20 - rank);
		}
		long start = 1031487092000L;
		long year = 1000L * 86400L * 365L;
		score = score + 4 * (this.timestamp - start) / year;
	    }
	}

	private boolean isSimilar(String str1, String str2) {
	    String core1 = getCore(str1);
	    String core2 = getCore(str2);
	    return core1.indexOf(core2) != -1 || core2.indexOf(core1) != -1;
	}

	private String getCore(String str) {
	    String ret = "";
	    for (int i = 0; i < str.length(); i++) {
		if (Character.isLetter(str.charAt(i))) {
		    ret = ret + str.charAt(i);
		}
	    }
	    return ret;
	}

	@Override
	public int compareTo(MavenItem other) {
	    if (this.score > other.score) {
		return -1;
	    } else if (this.score < other.score) {
		return 1;
	    } else {
		return 0;
	    }
	}

	public String getDownloadURL() {
	    return "https://search.maven.org/remotecontent?filepath="
		    + this.groupID.replace('.', '/') + '/' + this.artifactID
		    + '/' + this.version + '/' + this.artifactID + '-'
		    + this.version + ".jar";
	}
    }
}
