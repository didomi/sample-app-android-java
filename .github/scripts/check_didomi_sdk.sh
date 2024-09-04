#!/bin/bash

#----------------------------------------------------------
# Check Android SDK version (latest from repos)
#----------------------------------------------------------

buildFile="app/build.gradle"

localVersion=$(sed -n 's|.*io.didomi.sdk:android:\([^"]*\)".*|\1|p' <$buildFile)
if [[ ! $localVersion =~ ^[0-9]+.[0-9]+.[0-9]+$ ]]; then
  echo "Error while getting local android SDK version ($localVersion)"
  exit 1
fi

lastVersion=$(curl -s 'https://repo.maven.apache.org/maven2/io/didomi/sdk/android/maven-metadata.xml' | sed -ne '/release/{s/.*<release>\(.*\)<\/release>.*/\1/p;q;}')
if [[ ! $lastVersion =~ ^[0-9]+.[0-9]+.[0-9]+$ ]]; then
  echo "Error while getting latest android SDK version ($lastVersion)"
  exit 1
fi

if [[ $localVersion == "$lastVersion" ]]; then
  echo "Android SDK last version is $localVersion, no change"
  exit 0
fi

# Confirm update
echo "yes"
