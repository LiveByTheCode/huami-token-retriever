# Setting Up Automated APK Releases

This guide explains how to set up GitHub Actions to automatically build and release APK files.

## Overview

The repository includes two GitHub Actions workflows:
- **build-debug.yml**: Builds debug APKs on every push/PR
- **build-release.yml**: Builds signed release APKs when you create version tags

## Quick Setup

### 1. Generate a Keystore (One-time setup)

```bash
# Run this in your project root directory
./scripts/generate-keystore.sh
```

This will:
- Generate a keystore file for signing your APKs
- Provide the base64-encoded keystore and passwords needed for GitHub

### 2. Add GitHub Secrets

Go to your GitHub repository:
1. **Settings** → **Secrets and variables** → **Actions**
2. Click **New repository secret**
3. Add these four secrets:

| Secret Name | Value |
|-------------|-------|
| `SIGNING_KEY` | Base64-encoded keystore (provided by script) |
| `ALIAS` | Your key alias (default: "release") |
| `KEY_STORE_PASSWORD` | Your keystore password |
| `KEY_PASSWORD` | Your key password |

### 3. Create a Release

To trigger a release build:

```bash
# Tag your release
git tag v1.0.0
git push origin v1.0.0
```

Or create a release through GitHub's web interface:
1. Go to **Releases** → **Create a new release**
2. Create a new tag (e.g., `v1.0.0`)
3. Fill in release details
4. Publish release

## What Happens

### Debug Builds
- Triggered on every push to main branches
- Creates unsigned debug APK
- Available as workflow artifact

### Release Builds  
- Triggered when you create version tags (`v*`)
- Creates signed release APK
- Automatically creates GitHub release
- APK attached to release for download

## Manual Triggers

Both workflows can be triggered manually:
1. Go to **Actions** tab in GitHub
2. Select the workflow
3. Click **Run workflow**

## Troubleshooting

### Build Fails
- Check if your `gradlew` is executable
- Verify your `build.gradle` syntax
- Check Action logs for specific errors

### Signing Fails
- Verify all four secrets are set correctly
- Ensure `SIGNING_KEY` is valid base64
- Check keystore password matches

### No APK in Release
- Ensure you're using version tags (`v1.0.0`, not just `1.0.0`)
- Check if signing step completed successfully
- Verify GitHub token permissions

## Security Notes

🔐 **Keep your keystore secure:**
- Never commit keystore files to git
- Back up your keystore safely
- You need the same keystore to update your app

⚠️ **GitHub Secrets:**
- Secrets are encrypted and only accessible to workflows
- Contributors cannot see secret values
- Regularly rotate passwords if needed

## File Structure

```
.github/
  workflows/
    build-debug.yml     # Debug builds
    build-release.yml   # Release builds
scripts/
  generate-keystore.sh  # Keystore generation
```

## APK Download

Users can download APKs from:
- **Releases page**: Signed release APKs
- **Actions page**: Debug APKs (as artifacts)