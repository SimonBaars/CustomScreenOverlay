# Development Guide

## Project Structure

```
CustomScreenOverlay/
├── app/
│   ├── build.gradle                 # App module build configuration
│   ├── src/
│   │   └── main/
│   │       ├── AndroidManifest.xml  # App manifest with permissions
│   │       ├── java/com/simonbaars/customscreenoverlay/
│   │       │   ├── MainActivity.kt   # Main UI activity
│   │       │   └── OverlayService.kt # Overlay service
│   │       └── res/
│   │           ├── layout/
│   │           │   └── activity_main.xml  # Main UI layout
│   │           ├── values/
│   │           │   ├── strings.xml
│   │           │   ├── colors.xml
│   │           │   └── themes.xml
│   │           └── drawable/
│   │               └── ic_launcher_foreground.xml
├── build.gradle                     # Root build configuration
├── settings.gradle                  # Gradle settings
└── gradle.properties                # Gradle properties

```

## Key Components

### MainActivity.kt
- Main UI entry point
- Handles user input and settings
- Manages overlay permissions
- Starts/stops the OverlayService
- Uses SharedPreferences for persistence

### OverlayService.kt
- Foreground service that displays the overlay
- Uses WindowManager to add TextView overlay
- Listens for setting updates
- Runs persistently across all apps

### SharedPreferences Keys
- `message`: String - The text to display
- `textSize`: Int - Text size in sp (10-60)
- `textColor`: Int - Android Color value
- `xPosition`: Int - X position as percentage (0-100)
- `yPosition`: Int - Y position as percentage (0-100)
- `serviceRunning`: Boolean - Service state

## Building

### Prerequisites
- Android Studio Arctic Fox or later
- Android SDK with API 31+ installed
- JDK 8 or higher

### Build Commands

```bash
# Debug build
./gradlew assembleDebug

# Release build (requires signing key)
./gradlew assembleRelease

# Install on connected device
./gradlew installDebug

# Run tests
./gradlew test
```

## Permissions

The app requires several permissions:

1. **SYSTEM_ALERT_WINDOW**: To draw overlay over other apps
   - Must be granted by user through system settings
   - App checks and requests this permission at runtime

2. **FOREGROUND_SERVICE**: To run persistently
   - Declared in manifest
   - Normal permission (granted automatically)

3. **POST_NOTIFICATIONS**: For notification on Android 13+
   - Used for foreground service notification

4. **FOREGROUND_SERVICE_SPECIAL_USE**: Android 14+ requirement
   - Needed for overlay service type

## Testing

### Manual Testing Checklist

1. **Basic Functionality**
   - [ ] App launches successfully
   - [ ] Permission request appears on first use
   - [ ] Overlay appears after granting permission
   - [ ] Overlay displays correct message
   - [ ] Overlay persists across app switches

2. **Settings**
   - [ ] Message updates in real-time
   - [ ] Text size changes work
   - [ ] Color selection works
   - [ ] Position sliders work
   - [ ] Settings persist after app restart

3. **Service Management**
   - [ ] Start overlay button works
   - [ ] Stop overlay button works
   - [ ] Status indicator updates correctly
   - [ ] Service notification appears
   - [ ] Overlay survives screen rotation

4. **Edge Cases**
   - [ ] Empty message handling
   - [ ] Very long messages
   - [ ] Extreme positions (0%, 100%)
   - [ ] Minimum text size (10sp)
   - [ ] Maximum text size (60sp)

## Troubleshooting

### Overlay not appearing
- Check if overlay permission is granted
- Verify service is running (check notification)
- Check device logs for errors

### App crashes on start
- Verify Android version is 12+
- Check manifest permissions are correct
- Review device logs for stack trace

### Settings not persisting
- Verify SharedPreferences is properly initialized
- Check file permissions
- Verify app has storage access

## Future Enhancements

Possible features to add:

1. More color options or color picker
2. Font selection
3. Multiple overlay messages
4. Schedule-based display
5. Different overlay shapes
6. Animation effects
7. Transparency control
8. Background color option
9. Multiple lines with different styles
10. Import/export settings

## Code Style

- Follow Kotlin coding conventions
- Use meaningful variable names
- Add comments for complex logic
- Keep functions small and focused
- Handle null safety properly

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Test thoroughly
5. Submit a pull request

## License

Apache License 2.0 - See LICENSE file
