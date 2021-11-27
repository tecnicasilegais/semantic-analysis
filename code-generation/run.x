#!/bin/bash

ARQ=`basename $1|sed "s/\.cmm//"`

java Parser test_cases/$ARQ.cmm > output/$ARQ.s
# 32 bits
# as -o $ARQ.o $ARQ.s
#ld -o $ARQ   $ARQ.o

# 64 bits 
as --32 -o output/$ARQ.o output/$ARQ.s
ld -m elf_i386 -s -o output/$ARQ   output/$ARQ.o

# run
echo $'\n'
./output/$ARQ