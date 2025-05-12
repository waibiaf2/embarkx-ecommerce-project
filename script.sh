#!/bin/bash
name="Waibi Andrew Franklin"
echo $name

echo $0
echo "First argument: $1"
echo "Second argument: $2"


File=pom.xml

if [ -e ./pom.xml ]; then
	echo "The file $File exists"
fi


for item in file1 file2 file3; do
	echo $item
done
