# Huami Token Retriever

An Android application to retrieve authentication tokens from Huami/Amazfit servers for use with third-party applications like Gadgetbridge.

## Features

- Simple email/password login interface
- Retrieves authentication tokens from Huami/Amazfit servers
- Copy token to clipboard functionality
- Material Design 3 UI
- Error handling and loading states

## Usage

1. Install the APK on your Android device (minimum API 24)
2. Enter your Amazfit account email and password
3. Tap "Get Token" to authenticate and retrieve your token
4. Copy the displayed token to use with other applications

## Building

This project requires:
- Android Studio Arctic Fox or later
- Android SDK 34
- Kotlin 1.9.0+

1. Clone the repository
2. Open in Android Studio
3. Build and run on device or emulator

## API Endpoints Used

The app communicates with the following Huami/Amazfit API endpoints:

- `https://api-user.huami.com/registrations/{email}/tokens` - Token retrieval
- `https://account.huami.com/v2/client/login` - User authentication

## Known Limitations

- **Region Support**: Currently hardcoded for US region (`us-west-2`). Users in other regions may need modifications.
- **Redirect URI**: Uses the same redirect URI as the original huami-token script. This appears to be a standard Huami OAuth endpoint.

## Security Notes

⚠️ **Important**: This app handles sensitive authentication credentials. The token retrieved is unique to your account and should be kept secure.

- Credentials are not stored on the device
- All network communication uses HTTPS
- Tokens are only displayed temporarily and can be copied to clipboard

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Legal Disclaimers

⚠️ **IMPORTANT DISCLAIMERS:**

1. **No Warranty**: This software is provided "AS IS" without warranty of any kind.
2. **Use at Your Own Risk**: You use this application entirely at your own risk.
3. **Not Affiliated**: This project is not affiliated with, endorsed by, or connected to Huami, Amazfit, or Xiaomi.
4. **Terms of Service**: Users are responsible for complying with Huami/Amazfit's terms of service.
5. **Account Security**: Use of this application may violate terms of service and could potentially result in account suspension.
6. **Reverse Engineering**: This application uses reverse-engineered API calls for educational purposes.
7. **No Liability**: The author accepts no responsibility for any damages, account issues, or legal consequences arising from use of this software.

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## Acknowledgments

- Based on the authentication flow from [huami-token](https://github.com/argrento/huami-token) by argrento
- Inspired by [huafetcher](https://codeberg.org/vanous/huafetcher) by vanous
- Created for use with [Gadgetbridge](https://codeberg.org/Freeyourgadget/Gadgetbridge)