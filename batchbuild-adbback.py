import sys
import commands

def countLevel(pathStr):
    return len(pathStr.split('/')) - 1

def checkTree(seed, fileList):
    prefix = seed[0:seed.rfind('/')]
    for fileItem in fileList:
        if not fileItem.startswith(prefix):
            return False
    return True

def buildConf(path):
    commands.getoutput('find ' + path + ' -name \"*.java\" > workdir/tmp-javalist')
    jars = commands.getoutput('find ' + path + ' -name \"*.jar\"').split('\n')
    outjars = ''
    for jar in jars:
        outjars = outjars + jar  + ':'
    if outjars.endswith(':'):
        outjars = outjars[:-1]
    commands.getoutput('echo '+ '\"' + outjars + '\" > workdir/tmp-jarlist')
    return path + '/tmp-javalist'

def findConf(path, tgt):
    if tgt == 'javac':
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
    elif ctype == 'javac':
        return 'javac -cp @tmp-jarlist @tmp-javalist'
    else:
        return None

def getTgt(ctype):
    if ctype == 'mvn':
        return 'pom.xml'
    elif ctype == 'ant':
        return 'build.xml'
    elif ctype == 'javac':
        return 'javac'
    else:
        return None

def init(projname):
    commands.getoutput('cp -r sample-projs/' + projname + ' workdir')

def clean():
    commands.getoutput('rm -rf workdir/*')

def runConf(confPath, confType, logPath, homeDir):
    subPath = confPath[len(homeDir + '/sample-projs/'):]
    projname = subPath[0:subPath.find('/')]
#    print projname   
    init(projname)
    newPath = homeDir + '/workdir/' + subPath
    parentPath = newPath[0:newPath.rfind('/')]
    if confType=='javac':
        print commands.getoutput('cp ' + homeDir + '/workdir/tmp-javalist ' + parentPath)
        print commands.getoutput('cp ' + homeDir + '/workdir/tmp-jarlist ' + parentPath)


    print 'Running ' + confType + ' on ' + newPath
    commands.getoutput('cd ' + parentPath + ' && ' + getCommand(confType) +' > ' + logPath + '/'  + projname + '-' + confType+ '.log 2>&1')
    commands.getoutput('cd ' + homeDir)
    if confType=='javac':
        commands.getoutput('echo \"tmp-javas\" >>' + logPath + '/' + projname + '-' + confType + '.log')
        commands.getoutput('cat ' + homeDir + '/workdir/tmp-javalist >> ' + logPath + '/' + projname + '-' + confType + '.log')
        commands.getoutput('echo \"tmp-jars\" >>' + logPath + '/' + projname + '-' + confType + '.log')
        commands.getoutput('cat ' + homeDir + '/workdir/tmp-jarlist >> ' + logPath + '/' + projname + '-' + confType + '.log')
    clean()

def tryrunConf(confType, projname, logPath, homeDir):
    print 'Trying to build ' + projname + ' with ' + confType 
    conf = findConf(homeDir + '/sample-projs/' + projname, getTgt(confType))
    if conf == None:
        return
    elif conf.startswith('Warning:'):
        conf = conf[8:]
        print 'Warning'
        commands.getoutput('echo \"Warning: tree violation build in project:' + projname + ';;build-approach:' + confType + ';;filepath:' + conf + '\n\" >> summary.txt')
    else:
        commands.getoutput('echo \"Info: normal build in project:' + projname + ';;build-approach:' + confType + ';;filepath:' + conf + '\n\" >> summary.txt')
    runConf(conf, confType, logPath, homeDir)

def tryBuild(projname, logPath, homeDir):
    print 'Trying to build ' + projname
#    tryrunConf('mvn', projname, logPath, homeDir)
#    tryrunConf('ant', projname, logPath, homeDir)
    tryrunConf('javac', projname, logPath, homeDir)
     

cmd, start = sys.argv
homeDir = '/home/sean/ASE_2014'
logDir = 'logs-javac'
commands.getoutput('mkdir ' + logDir)
filelist = commands.getoutput('ls sample-projs').split('\n');
for filename in filelist:
    if filename > start:
        tryBuild(filename, homeDir + '/' + logDir, homeDir) 



#    print filename
#    print commands.getoutput('cd sample-projs/' + filename + ' && mvn compile > /home/sean/ASE_2014/logs/' + filename + '-mvn.log')
#    print commands.getoutput('ant > /home/sean/ASE_2014/logs/' + filename + '-ant.log && cd ../..')

