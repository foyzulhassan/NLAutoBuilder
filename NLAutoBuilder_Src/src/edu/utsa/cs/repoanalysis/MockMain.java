package edu.utsa.cs.repoanalysis;

import java.io.IOException;

import edu.utsa.cs.readme.executor.CmdExecutor;

public class MockMain {
    public static void main(String args[]) throws IOException {
	CmdExecutor gitcomitcmdexecutor = new CmdExecutor(
		"/home/foyzulhassan/Research/Data/autobuilder_testing/logs-lib/152-neo4j_neo4j",true);
	
	 gitcomitcmdexecutor.ExecuteGitCommand("/home/foyzulhassan/Research/Data/autobuilder_testing/workdir/152-neo4j_neo4j", "git log --pretty=format:%h -n 1",
		   "test");
	
    }

}
