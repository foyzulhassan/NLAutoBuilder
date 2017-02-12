import sys
import commands

def tryrunConf(confType, projname, logDir, homeDir):
    init(projname, homeDir)
    print commands.getoutput('python ' + homeDir + '/build.py ' + confType + ' ' + logDir + '/' + projname + '-' + confType + '.log' + ' ' + homeDir + '/workdir')
    clean()

def tryBuild(projname, logDir, homeDir):
    print 'Trying to build ' + projname
#    tryrunConf('mvn', projname, logDir, homeDir)
#    tryrunConf('ant', projname, logDir, homeDir)
    tryrunConf('javac', projname, logDir, homeDir)
     

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

