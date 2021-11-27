#!/bin/bash

ARQ=`basename $1|sed "s/\.txt//"`

java Parser test_cases/$ARQ.txt
