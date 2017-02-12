import sys
import commands

cmd, size = sys.argv
size_int = int(size)
count = 0
items = list()
f = open('out.txt')
for item in f.readlines():
    items.append(item[:-1])
while count < size_int:
    print commands.getoutput('cp projs/' + items[count] + ' sample-projs/')
    count = count + 1
