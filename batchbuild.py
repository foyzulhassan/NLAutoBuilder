import sys
import commands

cmd = sys.argv

commands.getoutput('mkdir logs')
filelist = commands.getoutput('ls sample-projs').split('\n');
for filename in filelist:
    print filename
    print commands.getoutput('cd sample-projs/' + filename + ' && mvn compile > /home/sean/ASE_2014/logs/' + filename + '-mvn.log')
    print commands.getoutput('ant > /home/sean/ASE_2014/logs/' + filename + '-ant.log && cd ../..')

