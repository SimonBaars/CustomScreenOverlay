# CustomScreenOverlay
Android app which shows a custom message as a persistent screen overlay

## Features

- Display custom motivational messages overlaying any app
- Fully customizable:
  - Message text (multi-line support)
  - Text size (10-60sp)
  - Text color (White, Black, Red, Blue)
  - Position on screen (X and Y coordinates as percentages)
- Persistent across all apps and screen changes
- Sensible defaults: "You are amazing! Keep going!" in white, 24sp, centered
- Works with Android 12 (API 31) and above

## Requirements

- Android 12 (API 31) or higher
- Overlay permission (requested at first use)

## Building

To build the project:

```bash
./gradlew assembleDebug
```

The APK will be generated at `app/build/outputs/apk/debug/app-debug.apk`

## Installation

1. Build the APK or download from releases
2. Install on your Android device
3. Grant overlay permission when prompted
4. Set your message and preferences
5. Click "Start Overlay" to activate

## Usage

### Setting up the overlay

1. **Message**: Enter your motivational quote or message in the text field
2. **Text Size**: Adjust the slider to change text size (10-60sp)
3. **Text Color**: Choose from White, Black, Red, or Blue
4. **Position**: Use X and Y position sliders to place the text on screen
   - 0% = left/top edge
   - 50% = center
   - 100% = right/bottom edge

### Starting the overlay

1. Click "Start Overlay" button
2. If prompted, grant overlay permission in system settings
3. Return to the app and click "Start Overlay" again
4. The overlay will now be visible across all apps

### Stopping the overlay

1. Return to the Custom Screen Overlay app
2. Click "Stop Overlay" button

## Permissions

- **SYSTEM_ALERT_WINDOW**: Required to draw overlay over other apps
- **FOREGROUND_SERVICE**: Keeps the overlay running persistently
- **POST_NOTIFICATIONS**: For service notification (Android 13+)
- **FOREGROUND_SERVICE_SPECIAL_USE**: Android 14+ requirement for overlay service

## Technical Details

- Uses WindowManager to display overlay
- Foreground service ensures persistent operation
- SharedPreferences for settings persistence
- Real-time updates when settings change

## License

Apache License 2.0 - See LICENSE file for details
