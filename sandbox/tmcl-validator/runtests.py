
import sys, validator
from glob import glob

BASE = "/Users/larsga/cvs-co/cxtm-tests/trunk/tmcl/level-1/"
INVALID = "invalid/"
VALID = "valid/"
PATTERN = "*.ctm"

total = 0
fails = 0
for file in glob(BASE + INVALID + PATTERN):
    total += 1
    tm = validator.load_tm([file])
    errors = validator.validate(tm)

    if not errors:
        print "No errors in", file
        fails += 1
    elif len(errors) != 1:
        print "More than one error in", file
        validator.report(errors)
        fails += 1

for file in glob(BASE + VALID + PATTERN):
    total += 1
    tm = validator.load_tm([file])
    errors = validator.validate(tm)

    if errors:
        print "Errors in", file
        validator.report(errors)
        fails += 1
        
print "%s tests, %s failures" % (total, fails)
