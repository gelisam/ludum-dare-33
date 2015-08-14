#!/bin/bash
set -e

NAME=bouncing_ball
rm -rf "$NAME" "${NAME}.zip"

sed -i '' -e 's/^os.type = [a-z]*$/os.type = windows/g' build.properties
mvn package -Dmaven.test.skip=true

sed -i '' -e 's/^os.type = [a-z]*$/os.type = linux/g' build.properties
mvn package -Dmaven.test.skip=true

cp target/*-windows.dir/run.bat target/*-linux.dir/

mv target/*-linux.dir "$NAME"
cp -r media "$NAME"
zip -r "${NAME}.zip" "$NAME"
