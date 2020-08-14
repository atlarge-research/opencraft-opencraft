#!/bin/sh
# Run script within the directory
BINDIR=$(dirname "$(readlink -fn "$0")")
cd "$BINDIR"

# Build Glowstone
(cd .. && mvn -Dcheckstyle.skip -T 1C -B package)