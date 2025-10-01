# Application Flow

## Component Interaction Diagram

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                              USER INTERACTION                                │
└─────────────────────────────────────────────────────────────────────────────┘
                                      │
                                      ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                               MainActivity                                   │
│  ┌───────────────────────────────────────────────────────────────────────┐  │
│  │  UI Components:                                                        │  │
│  │  • EditText (message input)                                           │  │
│  │  • SeekBar (text size: 10-60sp)                                       │  │
│  │  • Buttons (color selection: W/B/R/B)                                 │  │
│  │  • SeekBar (X position: 0-100%)                                       │  │
│  │  • SeekBar (Y position: 0-100%)                                       │  │
│  │  • Button (Start/Stop overlay)                                        │  │
│  │  • TextView (status indicator)                                        │  │
│  └───────────────────────────────────────────────────────────────────────┘  │
│                                      │                                        │
│                    ┌─────────────────┴─────────────────┐                     │
│                    ▼                                   ▼                     │
│           ┌──────────────────┐              ┌──────────────────┐            │
│           │ saveSettings()   │              │ Permission Check │            │
│           │   ↓              │              │   ↓              │            │
│           │ SharedPrefs      │              │ System Settings  │            │
│           └──────────────────┘              └──────────────────┘            │
│                    │                                   │                     │
│                    └─────────────────┬─────────────────┘                     │
│                                      ▼                                        │
│                          startOverlayService()                               │
└──────────────────────────────────────┬──────────────────────────────────────┘
                                       │
                                       ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                             OverlayService                                   │
│  ┌───────────────────────────────────────────────────────────────────────┐  │
│  │  Service Lifecycle:                                                    │  │
│  │  1. onCreate() → Initialize WindowManager                            │  │
│  │  2. onStartCommand() → Start foreground service                       │  │
│  │  3. showOverlay() → Add TextView to WindowManager                     │  │
│  │  4. updateOverlay() → Update existing overlay                         │  │
│  │  5. onDestroy() → Remove overlay and cleanup                          │  │
│  └───────────────────────────────────────────────────────────────────────┘  │
│                                      │                                        │
│                    ┌─────────────────┴─────────────────┐                     │
│                    ▼                                   ▼                     │
│           ┌──────────────────┐              ┌──────────────────┐            │
│           │ WindowManager    │              │ SharedPreferences│            │
│           │   ↓              │              │   ↓              │            │
│           │ Display Overlay  │              │ Read Settings    │            │
│           └──────────────────┘              └──────────────────┘            │
│                    │                                   │                     │
│                    └─────────────────┬─────────────────┘                     │
│                                      ▼                                        │
│                          Notification (Foreground)                           │
└──────────────────────────────────────┬──────────────────────────────────────┘
                                       │
                                       ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                           SCREEN OVERLAY                                     │
│  ┌───────────────────────────────────────────────────────────────────────┐  │
│  │  TextView Properties:                                                  │  │
│  │  • Text: User's message                                               │  │
│  │  • Size: User's selected size                                         │  │
│  │  • Color: User's selected color                                       │  │
│  │  • Position: User's X/Y coordinates                                   │  │
│  │  • Font: Bold                                                         │  │
│  │  • Shadow: Black, 8px blur                                            │  │
│  │  • Touchable: NO (FLAG_NOT_TOUCHABLE)                                │  │
│  │  • Focusable: NO (FLAG_NOT_FOCUSABLE)                                │  │
│  │  • Layer: TYPE_APPLICATION_OVERLAY (API 26+)                         │  │
│  └───────────────────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────────────────┘
```

## Data Flow

### Settings Persistence

```
User Input → MainActivity → SharedPreferences → OverlayService → Display
     │                              ↓
     │                         (Persistent)
     │                              ↓
     └──────────────────────────────┴────→ App Restart → Load Settings
```

### Real-time Updates

```
User Changes Setting
     │
     ▼
MainActivity.saveSettings()
     │
     ├──→ Save to SharedPreferences
     │
     └──→ If service running:
           │
           └──→ Intent(UPDATE_OVERLAY)
                 │
                 └──→ OverlayService.updateOverlay()
                       │
                       └──→ WindowManager.updateViewLayout()
                             │
                             └──→ Overlay Updates Immediately
```

## State Management

### Service States

```
┌──────────┐    Start Button    ┌──────────┐
│ STOPPED  │ ─────────────────→ │ STARTING │
└──────────┘                     └──────────┘
     ▲                                │
     │                                │ Permission granted
     │                                ▼
     │                           ┌──────────┐
     │      Stop Button          │ RUNNING  │
     └─────────────────────────  └──────────┘
                                      │
                                      │ Settings changed
                                      ↓
                                 ┌──────────┐
                                 │ UPDATING │
                                 └──────────┘
                                      │
                                      │ Update complete
                                      ▼
                                 ┌──────────┐
                                 │ RUNNING  │
                                 └──────────┘
```

### Permission Flow

```
User Clicks Start
     │
     ▼
Permission Check
     │
     ├─→ Granted ──→ Start Service Immediately
     │
     └─→ Not Granted
          │
          └─→ Open System Settings
               │
               └─→ User Grants Permission
                    │
                    └─→ Return to App
                         │
                         └─→ User Clicks Start Again
                              │
                              └─→ Service Starts
```

## Threading Model

```
Main Thread (UI Thread)
    │
    ├─→ MainActivity
    │   ├─→ UI Events (button clicks, slider changes)
    │   ├─→ SharedPreferences (async mode)
    │   └─→ Service start/stop commands
    │
    └─→ OverlayService
        ├─→ onCreate() → Initialize components
        ├─→ onStartCommand() → Start foreground
        ├─→ showOverlay() → Add view to WindowManager
        ├─→ updateOverlay() → Update view properties
        └─→ onDestroy() → Cleanup
```

## Memory Management

### Service Lifecycle

```
Application Start
     │
     └─→ No Service Running
          │
          ├─→ User Starts Service
          │    │
          │    └─→ Service Created
          │         │
          │         ├─→ TextView Created
          │         │
          │         └─→ WindowManager Reference Held
          │
          ├─→ App Killed by System
          │    │
          │    └─→ Service Continues (Foreground)
          │         │
          │         └─→ Overlay Remains Visible
          │
          └─→ User Stops Service
               │
               └─→ Service Destroyed
                    │
                    ├─→ TextView Removed
                    │
                    └─→ References Released
```

## Error Handling

### Common Scenarios

```
Overlay Display Failure
     │
     ├─→ No Permission
     │    └─→ Show Permission Request
     │
     ├─→ Invalid Position
     │    └─→ Clamp to Valid Range (0-100%)
     │
     ├─→ Invalid Size
     │    └─→ Clamp to Valid Range (10-60sp)
     │
     └─→ Service Crash
          └─→ Restart Service (START_STICKY)
```

### Recovery Mechanisms

```
SharedPreferences Corruption
     │
     └─→ Use Default Values
          │
          └─→ Log Error
               │
               └─→ Continue Execution

Service Killed by System
     │
     └─→ START_STICKY flag
          │
          └─→ System Restarts Service
               │
               └─→ Reload Settings
                    │
                    └─→ Recreate Overlay
```

## Performance Considerations

### Optimization Points

1. **Lazy Initialization**: Components initialized only when needed
2. **Efficient Updates**: Only changed properties are updated
3. **Minimal Memory**: Single TextView overlay (< 1KB)
4. **No Polling**: Event-driven updates only
5. **Battery Friendly**: No background processing, pure display layer

### Resource Usage

```
Memory: ~2-5 MB (app + service)
CPU: < 1% (idle after setup)
Battery: Negligible (no computation)
Storage: ~50 KB (settings only)
```
