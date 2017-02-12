package edu.utsa.cs.repoanalysis.typeresolver.build;

import java.io.IOException;

import edu.utsa.cs.repoanalysis.Config;
import edu.utsa.cs.util.CommandRunner;
import edu.utsa.cs.util.CommandRunner.CommandResult;
import edu.utsa.cs.util.Logger;

public class BaseBuilder extends Builder{

    public BuildResult build(String proj, Logger logger, String javaversion, String hisbuild) throws IOException,
	    InterruptedException {
	        CommandResult cr = CommandRunner.runCommand("python " + Config.script
	        	+ " " + this.type.getText() + " " + logger.getBuildLogPath(this.type) + " "
	        	+ Config.workDir + " " + proj+ " " + javaversion + " "+ hisbuild);
	        logger.log("build", cr.getStdOut(), Logger.LEVEL_INFO);
	        logger.log("buildError", cr.getErrOut(), Logger.LEVEL_IMPORTANT);
	        
	        return checkBuild(logger.getBuildLogPath(this.type), logger);
	    }

}
