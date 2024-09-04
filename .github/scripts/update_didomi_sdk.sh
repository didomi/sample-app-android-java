#!/bin/bash

#----------------------------------------------------------
# Update Android SDK version (latest from repos)
#----------------------------------------------------------

buildFile="app/build.gradle"

lastVersion=$(curl -s 'https://repo.maven.apache.org/maven2/io/didomi/sdk/android/maven-metadata.xml' | sed -ne '/release/{s/.*<release>\(.*\)<\/release>.*/\1/p;q;}')
if [[ ! $lastVersion =~ ^[0-9]+.[0-9]+.[0-9]+$ ]]; then
  echo "Error while getting latest android SDK version ($lastVersion)"
  exit 1
fi

sed -i~ -e "s|io.didomi.sdk:android:[0-9]\{1,2\}.[0-9]\{1,2\}.[0-9]\{1,2\}|io.didomi.sdk:android:$lastVersion|g" $buildFile || exit 1

# Cleanup backup files
find . -type f -name '*~' -delete
