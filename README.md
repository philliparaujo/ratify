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
TODO
