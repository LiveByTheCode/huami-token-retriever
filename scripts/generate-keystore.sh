#!/bin/bash

# Script to generate a keystore for signing Android APKs
# Run this locally, DO NOT commit the generated keystore to git

echo "🔐 Generating Android Keystore for APK Signing"
echo "=============================================="

read -p "Enter keystore filename (default: release.keystore): " KEYSTORE_NAME
KEYSTORE_NAME=${KEYSTORE_NAME:-release.keystore}

read -p "Enter key alias (default: release): " KEY_ALIAS
KEY_ALIAS=${KEY_ALIAS:-release}

read -s -p "Enter keystore password: " KEYSTORE_PASSWORD
echo

read -s -p "Enter key password: " KEY_PASSWORD
echo

read -p "Enter your name: " DNAME_CN
read -p "Enter organization (optional): " DNAME_O
read -p "Enter city: " DNAME_L
read -p "Enter state/province: " DNAME_ST
read -p "Enter country code (2 letters): " DNAME_C

echo
echo "Generating keystore..."

keytool -genkey -v \
    -keystore "$KEYSTORE_NAME" \
    -alias "$KEY_ALIAS" \
    -keyalg RSA \
    -keysize 2048 \
    -validity 10000 \
    -storepass "$KEYSTORE_PASSWORD" \
    -keypass "$KEY_PASSWORD" \
    -dname "CN=$DNAME_CN, O=$DNAME_O, L=$DNAME_L, ST=$DNAME_ST, C=$DNAME_C"

if [ $? -eq 0 ]; then
    echo
    echo "✅ Keystore generated successfully: $KEYSTORE_NAME"
    echo
    echo "📝 To set up GitHub Actions, add these secrets to your repository:"
    echo "   Settings → Secrets and variables → Actions → New repository secret"
    echo
    echo "   SIGNING_KEY: $(base64 -i "$KEYSTORE_NAME" | tr -d '\n')"
    echo "   ALIAS: $KEY_ALIAS"
    echo "   KEY_STORE_PASSWORD: $KEYSTORE_PASSWORD"
    echo "   KEY_PASSWORD: $KEY_PASSWORD"
    echo
    echo "⚠️  IMPORTANT:"
    echo "   - Keep this keystore file secure and backed up"
    echo "   - DO NOT commit it to git (it's in .gitignore)"
    echo "   - You'll need the same keystore to update your app"
else
    echo "❌ Failed to generate keystore"
fi