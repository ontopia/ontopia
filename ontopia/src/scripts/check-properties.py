
"""Checks all properties files in a given directory for consistency
against the master file."""

import sys, glob, os, string

# --- Helpers

def load_properties(file):
    props = {}
    for line in open(file).readlines():
        pos = string.find(line, "#")
        if pos != -1:
            line = line[ : pos]
        line = string.strip(line)

        if not line:
            continue

        pos = string.find(line, "=")
        assert pos != -1

        property = string.strip(line[ : pos])
        value = string.strip(line[pos+1 : ])
        props[property] = value

    return props

# --- Main

msgdir = sys.argv[1]
master = load_properties(msgdir + os.sep + "messages.properties")

for file in glob.glob(msgdir + os.sep + "messages_??.properties"):
    trans = load_properties(file)
    missing = []
    extra = []

    for (prop, val) in trans.items():
        if not master.has_key(prop):
            extra.append(prop)

    for (prop, val) in master.items():
        if not trans.has_key(prop):
            missing.append(prop)

    if missing or extra:
        print file
        print "Missing"
        for prop in missing:
            print "    ", prop
        for prop in extra:
            print "    ", prop
    else:
        print file, "OK"

