import sys
import commands

global android_path
global aapt_path

def countLevel(pathStr):
    return len(pathStr.split('/')) - 1

def checkTree(seed, fileList):
    prefix = seed[0:seed.rfind('/')]
    for fileItem in fileList:
        if not fileItem.startswith(prefix):
            return False
    return True

def checkAndroid(path):
    rfiles = commands.getoutput('grep -r \"import.*\\.R;\" --include *.java ' + path).split('\n')
    if len(rfiles) == 0 or len(rfiles[0]) == 0:
        return (None, None, None)
    rfilepath = path + '/summary_R_files'
    f = open(rfilepath, 'w')
    for line in rfiles:
        f.write(line + '\n')
    f.close()
    
    festfile = commands.getoutput('find ' + path + ' -name \"AndroidManifest*\"').split('\n')
    if len(festfile) == 0 or len(festfile[0]) == 0:
        return (None, None, None)
    festpath = festfile[0]
    
    parentpath = festpath[0][:festpath[0].rfind('/')]

    if 'res' in commands.getoutput('ls ' + parentpath).split('\n'):
        respath = parentpath + '/res'
    else: 
        resfiles = commands.getoutput('find ' + path + ' -name \"res\"').split('\n')
        if len(resfiles) == 0 or len(resfiles[0]) == 0:
            return (None, None, None)
        else:
            respath = resfiles[0]
    return (rfilepath, festpath, respath)
    

def handleAndroid(path):
    (rfiles, festpath, respath) = checkAndroid(path)
    if rfiles == None or festpath == None or respath == None:
        return
    else:
        command = aapt_path + '/aapt package -f -M ' + festpath + ' -F a.out -I ' + android_path + '/android.jar' + ' -S ' + respath + ' -m -J ' + path + '/src'
        print command
        print commands.getoutput(command) 
    

def buildConf(path):
    commands.getoutput('find ' + path + ' -name \"*.java\" > ' + path + '/tmp-javalist')
    jars = commands.getoutput('find ' + path + ' -name \"*.jar\"').split('\n')
    outjars = ''
    for jar in jars:
        outjars = outjars + jar  + ':'
    if outjars.endswith(':'):
        outjars = outjars[:-1]
    commands.getoutput('echo '+ '\"' + outjars + '\" > ' + path + '/tmp-jarlist')
    return path + '/tmp-javalist'

def findConf(path, tgt):
    if tgt == 'javac':
        handleAndroid(path)
        return buildConf(path)
    confs = commands.getoutput('find ' + path + ' -name \''+tgt + '\'').split('\n')
#    print confs
    minlevel = 1000
    minconf = ''
    for conf in confs:
        level = countLevel(conf)
        if level < minlevel:
            minlevel = level
            minconf = conf
    if minconf == '':
        return None
    else:
        if(checkTree(minconf, confs)):
            return minconf
        else:
            return 'Warning:' + minconf

def getCommand(ctype):
    if ctype=='mvn':
        return 'mvn compile'
    elif ctype == 'ant':
        return 'ant'
    elif ctype == 'gradle':
	return 'gradle assemble'
    elif ctype == 'javac':
        return 'javac -cp @tmp-jarlist @tmp-javalist'
    else:
        return None

def getTgt(ctype):
    if ctype == 'mvn':
        return 'pom.xml'
    elif ctype == 'ant':
        return 'build.xml'
    elif ctype == 'gradle':
	return 'build.gradle'
    elif ctype == 'javac':
        return 'javac'
    else:
        return None

def init(projname, homeDir):
    commands.getoutput('cp -r ' + homeDir + '/sample-projs/' + projname + ' ' + homeDir + '/workdir')

def clean():
    commands.getoutput('rm -rf workdir/*')

def runConf(confType, logPath, workDir, projname):
    projpath = workDir + '/' + projname
    print 'Trying to build ' + projname + ' with ' + confType 
    conf = findConf(projpath, getTgt(confType))
    if conf == None:
        print "Cannot find " + confType + " configuration file"
        return
    elif conf.startswith('Warning:'):
        conf = conf[8:]
        print 'Warning'
        commands.getoutput('echo \"Warning: tree violation build in project:' + projname + ';;build-approach:' + confType + ';;filepath:' + conf + '\n\" >> summary.txt')
    else:
        commands.getoutput('echo \"Info: normal build in project:' + projname + ';;build-approach:' + confType + ';;filepath:' + conf + '\n\" >> summary.txt')

#    subPath = conf[len(homeDir + '/sample-projs/'):]
#    projname = subPath[0:subPath.find('/')]
#    print projname   
#    init(projname)
#    newPath = homeDir + '/workdir/' + subPath
    parentPath = conf[0:conf.rfind('/')]
#    if confType=='javac':
#        print commands.getoutput('cp ' + workDir + '/tmp-javalist ' + parentPath)
#        print commands.getoutput('cp ' + workDir + '/tmp-jarlist ' + parentPath)


    print 'Running ' + confType + ' on ' + conf
    commands.getoutput('cd ' + parentPath + ' && ' + getCommand(confType) +' > ' + logPath + ' 2>&1')
    commands.getoutput('cd ' + workDir)
    if confType=='javac':
        commands.getoutput('echo \"tmp-javas\" >>' + logPath)
        commands.getoutput('cat ' + projpath + '/tmp-javalist >> ' + logPath)
        commands.getoutput('echo \"tmp-jars\" >>' + logPath)
        commands.getoutput('cat ' + projpath + '/tmp-jarlist >> ' + logPath)

cmd, confType, logPath, workDir, projname = sys.argv
android_path = '/home/sean/projects/ASE_2014/android-sdk-linux/platforms/android-19'
aapt_path = '/home/sean/projects/ASE_2014/android-sdk-linux/build-tools/19.1.0'
runConf(confType, logPath, workDir, projname)


