
# A script to automatically set the BUILD_DATE and increment the
# BUILD_NUMBER to make sure that these are correctly set in builds.
# $Id: newbuild.py,v 1.13 2002/06/04 14:17:37 grove Exp $

import time, sys, string

def update(dict1, dict2):
    dict1.update(dict2)
    return dict1

if len(sys.argv) < 3:
    print "Usage: newbuild.py VARIABLES_DICT FILENAME"
    sys.exit(1)

for filename in sys.argv[2:]:

    print "Updating product class '%s'." % (filename)
    build_no = int(open(filename + ".no").readline()) + 1
    open(filename + ".no", "w").write(str(build_no))

    # Common variables: MAJOR_VERSION, MINOR_VERSION, MICRO_VERSION, BETA_VERSION
    now = time.localtime(time.time())
    vars = {"BUILD_DATE":  "%d, %d, %d, %d, %d" % (now[0], now[1] - 1, now[2], now[3], now[4]),
            "BUILD_NUMBER" : str(build_no) }

    # Populate dictionary with values from command line
    vars.update(eval(sys.argv[1]))
    print vars
    
    contents = open(filename + ".in").read() % vars
    
    open(filename, "w").write(contents)

