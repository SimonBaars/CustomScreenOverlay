# UI Design Documentation

## Main Activity Layout

The app's main screen features a clean, scrollable interface with all controls accessible:

### Status Section (Top)
```
┌─────────────────────────────────────────┐
│  Overlay is stopped                     │  <- Status indicator (red/green)
└─────────────────────────────────────────┘
```

### Message Input
```
┌─────────────────────────────────────────┐
│  Enter your motivational message        │
│                                         │
│                                         │
└─────────────────────────────────────────┘
```
- Multi-line EditText field
- Hint: "Enter your motivational message"
- Default: "You are amazing! Keep going!"

### Text Size Control
```
Text Size                            24sp
├──────────○──────────────────────────┤
10                                    60
```
- Label and current value display
- SeekBar from 10sp to 60sp
- Real-time value updates

### Color Selection
```
Text Color
┌─────────┬─────────┬─────────┬─────────┐
│  White  │  Black  │   Red   │  Blue   │
└─────────┴─────────┴─────────┴─────────┘
```
- Four color buttons in a horizontal layout
- Visual color indicators via button backgrounds
- Contrasting text for readability

### Position Controls
```
Position

X Position                           50%
├──────────────○──────────────────────┤
0%                                  100%

Y Position                           30%
├────────○────────────────────────────┤
0%                                  100%
```
- Two separate position sliders
- Percentage-based positioning
- Real-time value display

### Action Button (Bottom)
```
┌─────────────────────────────────────────┐
│          START OVERLAY                  │
└─────────────────────────────────────────┘
```
- Large, prominent button
- Text changes: "Start Overlay" / "Stop Overlay"
- Material Design styling

## Overlay Appearance

When active, the overlay appears as:

```
┌─────────────────────────────────────────┐
│                                         │
│     You are amazing! Keep going!        │  <- Overlay text
│                                         │
│                                         │
│                                         │
│                                         │
└─────────────────────────────────────────┘
```

### Overlay Characteristics:
- **Font**: Bold, system default
- **Shadow**: Black shadow for visibility (8px blur)
- **Touchable**: No (FLAG_NOT_TOUCHABLE)
- **Focusable**: No (FLAG_NOT_FOCUSABLE)
- **Transparency**: Text only, no background
- **Persistence**: Stays on top of all apps

## Color Scheme

### App Theme (Material Design)
- Primary: Purple (#6200EE)
- Primary Variant: Dark Purple (#3700B3)
- Secondary: Teal (#03DAC5)
- Background: Default Material theme

### Available Overlay Colors
- White (#FFFFFF) - Best for dark backgrounds
- Black (#000000) - Best for light backgrounds
- Red (#FF0000) - High visibility
- Blue (#0000FF) - Professional look

## User Flow

```
┌─────────────────┐
│   Launch App    │
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│  Enter Message  │
│  Set Size       │
│  Choose Color   │
│  Set Position   │
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│ Start Overlay   │
└────────┬────────┘
         │
         ▼
  ┌─────┴──────┐
  │Permission? │
  └─────┬──────┘
        │
    ┌───┴───┐
    │  Yes  │──────────┐
    └───────┘          │
        │              │
    ┌───┴───┐          │
    │   No  │          │
    └───┬───┘          │
        │              │
        ▼              ▼
┌──────────────┐  ┌──────────────┐
│ Request Perm │  │ Show Overlay │
└──────────────┘  └──────────────┘
        │              │
        └──────┬───────┘
               │
               ▼
        ┌─────────────┐
        │   Success   │
        └─────────────┘
```

## Responsive Design

The layout uses:
- **ScrollView**: Handles small screens and landscape orientation
- **ConstraintLayout**: Flexible, adaptive positioning
- **Match parent width**: Full-width controls
- **Wrap content height**: Efficient vertical space usage
- **Padding**: 16dp margins for comfortable spacing

## Accessibility

- High contrast text and backgrounds
- Large touch targets (buttons, sliders)
- Clear labels for all controls
- Status indicators with color and text
- Minimum text size enforced (10sp)

## Future UI Enhancements

Potential improvements:
1. Dark mode support
2. Custom color picker (HSV/RGB)
3. Font family selector
4. Preview mode before activating
5. Multiple overlay presets
6. Animation toggle
7. Transparency slider
8. Background color option
9. Text alignment controls
10. Save/load profiles
