#!/bin/bash
out=$(pgrep virtuoso)
if [ -n "$out" ]; then
   kill -9 $out
fi