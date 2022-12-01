#!/bin/bash

#----------------------------------------------------------
# Update Android SDK version (latest from repos)
#----------------------------------------------------------

buildFile="app/build.gradle"

mavenUrl="https://repo.maven.apache.org/maven2/io/didomi/sdk/android/"
mavenVersion=""

while read -r line; do
  version="$(echo "$line" | sed -r 's/^.+href="([^"]+)".+$/\1/' | sed 's/.$//')"
  if [[ $version =~ ^[0-9]+.[0-9]+.[0-9]+$ ]]; then
    mavenVersion="$version"
  fi
done <<< "$(curl -s "$mavenUrl")"

if [[ ! $mavenVersion =~ ^[0-9]+.[0-9]+.[0-9]+$ ]]; then
  echo "Error while getting local android SDK version ($mavenVersion)"
  exit 1
fi

sed -i~ -e "s|io.didomi.sdk:android:[0-9]\{1,2\}.[0-9]\{1,2\}.[0-9]\{1,2\}|io.didomi.sdk:android:$mavenVersion|g" $buildFile || exit 1

# Cleanup backup files
find . -type f -name '*~' -delete
