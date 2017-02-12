package edu.utsa.cs.filefolder.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ProjFinder {

	public ProjFinder() {

	}

	public static List<String> getProjectList(String path) {

		List<String> projfolderlist = new ArrayList<String>();

		if (isConfigFileExistsinPath(path)) {
			projfolderlist.add(path);
			return projfolderlist;
		} else {

			File file = new File(path);
			String[] names = file.list();

			for (String name : names) {
				String childpath = path + "/" + name;
				if (new File(childpath).isDirectory()) {

					projfolderlist.addAll(getProjectList(childpath));

				}
			}

			return projfolderlist;
		}

	}

	private static boolean isConfigFileExistsinPath(String path) {
		boolean ret = false;

		String antconfig = path + "//" + "build.xml";
		String mvnconfig = path + "//" + "pom.xml";
		String gradleconfig = path + "//" + "build.gradle";

		File fileant = new File(antconfig);
		File filemvn = new File(mvnconfig);
		File filegradle = new File(gradleconfig);

		if ((fileant.exists() && !fileant.isDirectory())
				|| (filemvn.exists() && !filemvn.isDirectory() || (filegradle.exists() && !filegradle.isDirectory()))) {
			ret = true;
		}

		return ret;

	}

}