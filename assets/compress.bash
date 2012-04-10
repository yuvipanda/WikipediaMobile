#!/bin/bash
find . -name '*.js' -exec java -jar yuicompressor.jar {} -o {} \;
