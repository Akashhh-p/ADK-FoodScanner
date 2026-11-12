# Quick Start Guide - 5 Minute Setup

## 1. Open Project

- File → Open
- Select project folder
- Click OK
- Wait for Gradle sync to complete

**Time: 1 minute**

## 2. Create Firebase Project

Go to: https://console.firebase.google.com

1. Click "Create a project"
2. Enter: "Food Scanner"
3. Accept defaults, click "Continue"
4. Click "Create project"
5. Wait for initialization

**Time: 2 minutes**

## 3. Add Android App to Firebase

1. In Firebase Console, click Android icon
2. Package name: `com.example.foodscanner`
3. App nickname: `Food Scanner` (optional)
4. Click "Register app"
5. **Download google-services.json**
6. Copy file to: `app/google-services.json`

**Time: 1 minute**

## 4. Enable Email/Password Auth

1. Firebase Console → "Build" → "Authentication"
2. Click "Get started"
3. Select "Email/Password"
4. Toggle enable
5. Click "Save"

**Time: 1 minute**

## 5. Sync & Build

1. Android Studio → Build → Sync Now
2. Wait for sync to complete
3. Build → Rebuild Project
4. Wait for build to complete

**Total setup time: ~5 minutes**

## 6. Run App

1. Click Run (green play icon) or Shift+F10
2. Select emulator or connected device
3. App launches

## Test It

### First Test: Registration
1. Launch app → See Login screen
2. Click "Register"
3. Enter:
   - Name: Test User
   - Email: test@example.com
   - Password: Test@123
4. Click Register
5. Should go to Home screen

### Second Test: Barcode Scan
1. On Home screen, click "Scan Product"
2. Google Code Scanner opens
3. Point at barcode: **4006381333931** (Nutella)
4. Should show product details
5. Product saved to history

### Third Test: View History
1. Click "View All History"
2. Should see scanned product
3. Click product to view details

## Troubleshooting Quick Fixes

### "gradle sync failed"
```
Solution:
- File → Invalidate Caches
- Restart Android Studio
- Build → Sync Now
```

### "Missing google-services.json"
```
Solution:
- Download from Firebase Console
- Place at: app/google-services.json
- Sync Gradle
```

### "Cannot resolve symbol"
```
Solution:
- Build → Clean Project
- Build → Rebuild Project
- File → Invalidate Caches → Restart
```

### "Authentication failed"
```
Solutions:
- Check internet connection
- Verify Firebase project created
- Check Email/Password enabled
- Check google-services.json is valid
```

### "Scanner doesn't work"
```
Solutions:
- Check Google Play Services installed
- Ensure device has camera
- Check internet connection
- Try on physical device vs emulator
```

## What's Working Now

✅ Firebase Authentication (Register/Login/Logout)
✅ Google Code Scanner integration
✅ Product fetching from Open Food Facts API
✅ Scan history storage in Room database
✅ Image loading with Glide
✅ Material Design UI
✅ User-isolated data

## Next Steps

1. **Full Setup:** See SETUP.md for detailed Firebase configuration
2. **Complete Testing:** See TESTING.md for comprehensive test plan
3. **Understand Architecture:** See ARCHITECTURE.md
4. **Full Feature List:** See README.md

## Test Barcodes

Copy-paste these to test the app:

| Product | Barcode | Expected Score |
|---------|---------|-----------------|
| Nutella | 4006381333931 | E |
| Coca Cola | 20674202 | D |
| Heinz Sauce | 8718206051220 | C |
| Invalid | 1234567890123 | Error |

## File Locations

- **Project Root:** `C:\Users\akash\OneDrive\Desktop\Documents\food barcode\`
- **Source Code:** `app\src\main\java\com\example\foodscanner\`
- **Layouts:** `app\src\main\res\layout\`
- **Strings:** `app\src\main\res\values\strings.xml`

## Common Commands (Terminal)

```bash
# Clean build
./gradlew clean

# Full build
./gradlew build

# Run tests
./gradlew test

# Check for errors
./gradlew check

# Install on device
./gradlew installDebug
```

## App Screenshots (After Running)

1. **Login Screen** - Email/password input, register link
2. **Home Screen** - Scan button, recent scans list
3. **Product Screen** - Product image, name, barcode, nutri-score
4. **History Screen** - All scanned products list

## Features Demo

1. **Register** → New account with email
2. **Login** → Authenticate with credentials
3. **Scan** → Point at real barcode
4. **View** → See product details from API
5. **History** → All scans saved locally
6. **Logout** → Sign out and back to login

## Success Indicators

- ✅ App launches without crashing
- ✅ Can register and login
- ✅ Scanner opens when button clicked
- ✅ Products load from API
- ✅ Scans appear in history
- ✅ Images load properly
- ✅ Logout clears session

---

**That's it!** You now have a fully functional Food Scanner app.

For more details:
- Setup issues: SETUP.md
- Testing guide: TESTING.md
- Code architecture: ARCHITECTURE.md
- Feature list: README.md
