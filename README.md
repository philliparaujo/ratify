# Ratify
## Screenshots
Click here to see pictures of the app in action: https://imgur.com/a/le6irzS
## Description
Ratify is an Android app that connects to your Spotify account to **help you rate songs** and **explore your music listening habits**. While the app is running, it automatically tracks songs played on Spotify and lets you quickly rate them—either during playback or afterward. 

Full functionality requires the Spotify app to be installed.
## Installation
To install Ratify on your Android device, go to `Releases` and click on `APK`. Download the `app-debug.apk` on your device, and it should automatically download if permissions are granted.
## Project Structure
Most of the code I wrote is located in `app/src/main/java/com/example/ratify`:

- `core`
  - `helper`: Utility functions used throughout the app
  - `model`: Core data models (custom types)
  - `state`: UI-related state containers
- `database`: [Room](https://developer.android.com/training/data-storage/room) database setup, schema migrations, db import/export functionality
- `di`: [Koin](https://insert-koin.io/) configuration wiring together app dependencies
- `mocks`: Mock data and preview helpers used for UI testing
- `repository`: Exposes data sources to UI
- `services`: Foreground service for managing a notification-based rating system 
- `spotify`: Handles [Spotify](https://developer.spotify.com/documentation/android) authentication, permissions, and playback control
- `ui`
  - `components`: Reusable UI widgets
  - `navigation`: Defines routes and targets between different screens in the app
  - `screens`: Full screen layouts
  - `theme`: App-wide visual theming (colors, light/dark mode)
- `MainActivity.kt`: The app entry point where everything is initialized

## Development Setup
This section explains how to set up a development environment for Ratify. After completing these steps, you'll be able to build, test, and modify the app locally. 

For more detailed steps, see the [official Spotify Android SDK quickstart guide](https://developer.spotify.com/documentation/android/tutorials/getting-started).

1. Install [Android Studio](https://developer.android.com/studio) ([API 25+](https://developer.android.com/tools/releases/platforms))
2. Clone or download the repository onto your local machine
```
git clone https://github.com/philliparaujo/ratify.git
cd ratify
```
3. Open the `ratify/` directory in Android Studio, which will automatically sync dependencies and configure Gradle
4. Create required local configuration files
- `local.properties`
```
# Local SDK path – update this to match your system
sdk.dir=<path\\to\\folder>\\Android\\Sdk
```

- `gradle.properties`
```
# Gradle settings
org.gradle.jvmargs=-Xmx2048m -Dfile.encoding=UTF-8
android.useAndroidX=true
kotlin.code.style=official
android.nonTransitiveRClass=true
org.gradle.configuration-cache=true

# Spotify API keys – fill these out in step 7
spotifyClientId=TODO
spotifyRedirectUri=TODO
```

5. Create a [Spotify developer](https://developer.spotify.com/) account
6. On the Spotify developer [dashboard](https://developer.spotify.com/dashboard), create an app for this project.
- Set a redirect URI
- Set Android packages (i.e. com.example.ratify)
- Set APIs used (Android, Web API)
7. Copy the client ID and redirect URI from your Spotify app dashboard into your `gradle.properties` file.
8. Build and run the app, either via an Android emulator or using USB debugging on a physical device
