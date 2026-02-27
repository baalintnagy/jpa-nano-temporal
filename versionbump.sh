#!/bin/bash

# Version bump script for JPA Temporal project
# Usage: ./versionbump.sh v1.0.0

set -e

# Check if version argument is provided
if [ $# -eq 0 ]; then
    echo "Error: Version argument is required"
    echo "Usage: ./versionbump.sh v1.0.0"
    exit 1
fi

VERSION=$1

# Validate version format (starts with 'v')
if [[ ! $VERSION =~ ^v[0-9]+\.[0-9]+\.[0-9]+[a-z]?$ ]]; then
    echo "Error: Version must start with 'v' and follow semantic versioning (e.g., v1.0.0, v1.0.0a)"
    exit 1
fi

# Extract version without 'v' prefix for file updates
VERSION_NO_PREFIX=${VERSION#v}

echo "Bumping version to $VERSION..."

# Update pom.xml version
echo "Updating pom.xml..."
sed -i.bak "s|<version>[^<]*</version>|<version>$VERSION_NO_PREFIX</version>|g" pom.xml
rm pom.xml.bak

# Update README.md Maven dependency version
echo "Updating README.md Maven dependency..."
sed -i.bak "s|<version>[^<]*</version>|<version>$VERSION_NO_PREFIX</version>|g" README.md
rm README.md.bak

# Update README.md Gradle dependency version
echo "Updating README.md Gradle dependency..."
sed -i.bak "s|implementation '[^']*:[^']*:[^']*'|implementation 'io.github.baalintnagy:jpa.nano-temporal:$VERSION_NO_PREFIX'|g" README.md
rm README.md.bak

# Create and push git tag
echo "Creating git tag $VERSION..."
git add pom.xml README.md
git commit -m "Bump version to $VERSION"
git tag $VERSION

echo "Version bump completed successfully!"
echo "Changes committed and tagged as $VERSION"
echo "To push changes: git push origin main --tags"
