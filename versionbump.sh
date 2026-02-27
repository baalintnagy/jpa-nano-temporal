#!/bin/bash
set -euo pipefail

# Usage: ./versionbump.sh v1.0.0
if [ $# -eq 0 ]; then
  echo "Error: Version argument is required"
  echo "Usage: ./versionbump.sh v1.0.0"
  exit 1
fi

VERSION="$1"

if [[ ! $VERSION =~ ^v[0-9]+\.[0-9]+\.[0-9]+([a-z0-9.-]+)?$ ]]; then
  echo "Error: Version must start with 'v' and follow semver-ish format (e.g., v1.0.0, v1.0.0-alpha.1)"
  exit 1
fi

VERSION_NO_PREFIX="${VERSION#v}"

echo "Bumping project version to $VERSION ($VERSION_NO_PREFIX)..."

echo "Updating pom.xml project version via Maven Versions Plugin..."
mvn -q versions:set -DnewVersion="$VERSION_NO_PREFIX" -DgenerateBackupPoms=false

echo "Updating README dependency snippets (optional)..."
# Maven snippet (replace only the line that mentions YOUR artifact coordinates)
sed -i.bak "s|<version>[^<]*</version>|<version>$VERSION_NO_PREFIX</version>|g" README.md
rm README.md.bak

# Gradle snippet (only your module)
sed -i.bak -E \
  "s|(implementation[[:space:]]+'io\.github\.baalintnagy:jpa\.nano-temporal:)[^']*(')|\1$VERSION_NO_PREFIX\2|g" \
  README.md && rm -f README.md.bak

echo "Committing + tagging..."
git add pom.xml README.md
git commit -m "Bump version to $VERSION"
git tag "$VERSION"

echo "Done."
echo "Push with: git push origin main --tags"
