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

mavenVersion=""
mavenUrl="$(curl -s 'https://repo.maven.apache.org/maven2/io/didomi/sdk/android/')"
while read -r line; do
  version="$(echo "$line" | sed -r 's/^.+href="([^"]+)".+$/\1/' | sed 's/.$//')"
  if [[ $version =~ ^[0-9]+.[0-9]+.[0-9]+$ ]]; then
    mavenVersion="$version"
  fi
done <<<"$mavenUrl"

if [[ ! $mavenVersion =~ ^[0-9]+.[0-9]+.[0-9]+$ ]]; then
  echo "Error while getting local android SDK version ($mavenVersion)"
  exit 1
fi

if [[ $localVersion == "$mavenVersion" ]]; then
  echo "Android SDK last version is $localVersion, no change"
  exit 0
fi

# Confirm update
echo "yes"
