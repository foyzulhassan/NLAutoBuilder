package edu.utsa.cs.repoanalysis.typeresolver.librepo;

public class LibResolvingException extends Exception {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private String className;
    private Exception nestException;

    public LibResolvingException(String className) {
	this.className = className;
	this.nestException = null;
    }

    public LibResolvingException(String className, Exception e) {
	this.className = className;
	this.nestException = e;
    }

    public String getMessage() {
	if (hasNest()) {
	    return LibDownloader.noLibFound + ":" + className + "\nCaused by:"
		    + this.nestException.toString();
	} else {
	    return LibDownloader.noLibFound + ":" + className;
	}
    }

    public boolean hasNest() {
	return this.nestException != null;
    }

    public void printStackTrace() {
	super.printStackTrace();
	if (this.nestException != null) {
	    this.nestException.printStackTrace();
	}
    }
}
