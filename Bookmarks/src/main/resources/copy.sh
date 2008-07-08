#!/bin/sh
for file in `ls`
do
	mv $file retiman/$file.rdf
done
mv retiman/retiman.sh.rdf retiman.sh
mv retiman/copy.sh.rdf copy.sh
