#!/bin/bash
git ls-files | xargs file | grep text | cut -d":" -f1 | xargs -Ifile tab2space -lf file file
