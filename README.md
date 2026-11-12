# Food Barcode Scanner & Scan History App

A complete Android application for scanning food product barcodes and viewing nutritional information from Open Food Facts API.

## Features Implemented

### 1. Firebase Authentication
- User registration with name, email, and password
- Email validation and password strength requirements (minimum 6 characters)
- User login with persistent session management
- Logout functionality
- Protected access to main application screens

### 2. Barcode Scanner
- Integration with Google Code Scanner (GMS)
- No camera permission required (Google Code Scanner handles it)
- Click "Scan Product" button to open scanner
- Automatic navigation to product details on successful scan
- Graceful error and cancellation handling

### 3. Open Food Facts API Integration
- Retrofit-based HTTP client
- Fetches product details using barcode
- Retrieves:
  - Product name
  - Brand
  - Product image URL
  - Nutri-Score grade (A-E)
  - Quantity/size
- Handles network errors and missing products gracefully
- No hardcoded product data

### 4. Product Details Screen
- Displays product image with Glide image loading
- Shows product name and brand
- Displays barcode value
- Shows quantity information
- Color-coded Nutri-Score badge
- Loading indicator while fetching
- Error messages for unavailable products
- Try Again button for failed requests

### 5. Scan History
- Local storage using Room database
- RecyclerView with product cards
- Displays:
  - Product image (thumbnail)
  - Product name
  - Brand
  - Barcode
  - Scan date and time
  - Nutri-Score grade with color coding
- Newest scans displayed first
- Automatic refresh when new product is scanned
- Associated with currently logged-in user
- Prevents saving failed API responses

### 6. Home Screen
- Welcome message with user's email/name
- Prominent "Scan Product" button
- Recent scans section (5 most recent)
- "View All History" button
- Logout option in header
- Automatic refresh on activity resume

## Technical Stack

- **Language**: Kotlin
- **UI Framework**: Android Framework (XML layouts, no Jetpack Compose)
- **Database**: Room (SQLite)
- **Networking**: Retrofit 2 + Gson
- **Image Loading**: Glide
- **Authentication**: Firebase Authentication
- **Barcode Scanning**: Google Code Scanner (GMS)
- **Async Operations**: Coroutines + Flow
- **RecyclerView**: For scan history list
- **Material Design**: Material Components

## Project Structure

```
food barcode/
├── app/
│   ├── src/
│   │   └── main/
│   │       ├── java/com/example/foodscanner/
│   │       │   ├── data/
│   │       │   │   ├── api/
│   │       │   │   │   ├── OpenFoodFactsService.kt
│   │       │   │   │   ├── RetrofitClient.kt
│   │       │   │   │   └── model/
│   │       │   │   │       └── ProductResponse.kt
│   │       │   │   ├── auth/
│   │       │   │   │   └── FirebaseAuthManager.kt
│   │       │   │   ├── dao/
│   │       │   │   │   └── ScanHistoryDao.kt
│   │       │   │   ├── database/
│   │       │   │   │   └── FoodScannerDatabase.kt
│   │       │   │   ├── entity/
│   │       │   │   │   └── ScanHistory.kt
│   │       │   │   └── repository/
│   │       │   │       ├── ProductRepository.kt
│   │       │   │       └── ScanHistoryRepository.kt
│   │       │   └── ui/
│   │       │       ├── adapter/
│   │       │       │   └── ScanHistoryAdapter.kt
│   │       │       ├── auth/
│   │       │       │   ├── LoginActivity.kt
│   │       │       │   └── RegisterActivity.kt
│   │       │       ├── home/
│   │       │       │   └── HomeActivity.kt
│   │       │       ├── history/
│   │       │       │   └── ScanHistoryActivity.kt
│   │       │       └── product/
│   │       │           └── ProductDetailsActivity.kt
│   │       ├── res/
│   │       │   ├── layout/
│   │       │   │   ├── activity_login.xml
│   │       │   │   ├── activity_register.xml
│   │       │   │   ├── activity_home.xml
│   │       │   │   ├── activity_product_details.xml
│   │       │   │   ├── activity_scan_history.xml
│   │       │   │   └── item_scan_history.xml
│   │       │   ├── values/
│   │       │   │   ├── colors.xml
│   │       │   │   ├── strings.xml
│   │       │   │   └── themes.xml
│   │       │   └── xml/
│   │       │       ├── backup_rules.xml
│   │       │       └── data_extraction_rules.xml
│   │       └── AndroidManifest.xml
│   ├── build.gradle
│   ├── proguard-rules.pro
│   └── google-services.json
├── build.gradle
├── settings.gradle
├── gradle.properties
└── gradlew.bat
```

## Setup Instructions

### Prerequisites
- Android Studio latest version
- Java Development Kit (JDK) 8 or higher
- Android SDK with API level 24 or higher
- Firebase project configured

### Step-by-Step Setup

1. **Clone/Open the Project**
   - Open the project in Android Studio
   - Let Android Studio sync Gradle files

2. **Configure Firebase**
   - Go to Firebase Console (console.firebase.google.com)
   - Create a new project or use existing one
   - Enable Firebase Authentication (Email/Password method)
   - Download the google-services.json file
   - Replace the placeholder file at `app/google-services.json` with your actual Firebase config

3. **Configure SDK Path**
   - Copy `local.properties.example` to `local.properties`
   - Update `sdk.dir` with your Android SDK path
   - On Windows: typically `C:\Users\YourUsername\AppData\Local\Android\Sdk`

4. **Sync Gradle**
   - Click "Sync Now" in Android Studio
   - Wait for all dependencies to download and sync

5. **Build the Application**
   - Go to Build menu → Clean Project
   - Go to Build menu → Rebuild Project
   - Or use command line: `./gradlew clean build`

6. **Run on Emulator/Device**
   - Connect an Android device or start an emulator
   - Click the Run button (green play icon)
   - Select target device

## Key Implementation Details

### Firebase Authentication Flow
1. User enters credentials on Login/Register screen
2. FirebaseAuthManager handles auth operations asynchronously
3. Session is automatically maintained
4. Unauthenticated users are redirected to LoginActivity
5. User email/name displayed on HomeActivity

### Barcode Scanning Flow
1. User clicks "Scan Product" button
2. GmsBarcodeScanning.getClient() opens scanner UI
3. User scans barcode (no manual camera permission needed)
4. Raw barcode value extracted and sent to ProductDetailsActivity
5. ProductDetailsActivity fetches product data from API

### Product Fetch & Display
1. ProductDetailsActivity receives barcode value
2. Shows loading indicator
3. ProductRepository.getProductDetails() makes API call
4. ProductResponse parsed with Gson
5. Product details displayed if found
6. ScanHistory saved to Room database on success
7. Error message shown if product not found

### Scan History Management
1. Each successful scan saved to Room database
2. Associated with current user's ID
3. ScanHistoryDao queries all scans for user, ordered by date DESC
4. RecyclerView adapter displays scans with proper formatting
5. Clicking a history item opens ProductDetailsActivity in view-only mode (from_history=true)

### Database Schema

**ScanHistory Table**
```
id: Long (Primary Key, Auto-increment)
userId: String (Associated user)
barcode: String (Product barcode)
productName: String (Product name from API)
brand: String (Brand from API)
imageUrl: String (Product image URL from API)
nutriScore: String (Nutri-Score grade A-E)
quantity: String (Product quantity from API)
scanDate: Long (Timestamp in milliseconds)
```

## Testing Checklist

### Authentication
- [ ] Register new user with valid credentials
- [ ] Register rejects invalid email format
- [ ] Register rejects weak password (<6 chars)
- [ ] Login with registered credentials succeeds
- [ ] Login with wrong credentials fails with error
- [ ] Logged-in user cannot access login screen on restart
- [ ] Logout returns to login screen
- [ ] User email displayed on home screen

### Barcode Scanning
- [ ] Scan Product button visible on home screen
- [ ] Clicking Scan opens Google Code Scanner
- [ ] Scanner can be cancelled without error
- [ ] Valid barcode number (e.g., 4006381333931 - Nutella) shows product
- [ ] Invalid barcode shows error message
- [ ] Try Again button appears on error

### Product Display
- [ ] Product image loads from URL
- [ ] Product name, brand displayed correctly
- [ ] Barcode value shown correctly
- [ ] Quantity displayed when available
- [ ] Nutri-Score shows with correct color:
  - A = Green
  - B = Light Green
  - C = Orange
  - D = Dark Orange
  - E = Red
- [ ] Missing fields don't crash app (graceful handling)

### Scan History
- [ ] Successful scan saved to history
- [ ] Failed API responses NOT saved to history
- [ ] Recent scans show on home screen (up to 5)
- [ ] "View All History" shows all scans
- [ ] Newest scans appear at top
- [ ] Clicking history item shows product details
- [ ] Scan date/time displayed correctly
- [ ] History persists after app restart
- [ ] History cleared when user logs out (optional)

### Navigation
- [ ] Login → Register screen works
- [ ] Register → Login screen works
- [ ] Home → Scan → Product Details → Home works
- [ ] Home → View History → History Details works
- [ ] Back buttons navigate correctly
- [ ] Logout navigates to Login screen

## Debugging Tips

### Build Issues
- Run `./gradlew clean build` to force rebuild
- Check Gradle sync status in Android Studio
- Ensure API level is 24 or higher
- Verify all dependencies in build.gradle

### Runtime Issues
- Check logcat for exceptions (Logcat window in Android Studio)
- Firebase errors: check google-services.json is correctly placed
- API errors: verify internet connection and API endpoint
- Database errors: check Room migrations and schema

### Network/API Issues
- Test API endpoint manually: https://world.openfoodfacts.org/api/v2/product/{barcode}.json
- Use known barcodes for testing (e.g., 4006381333931)
- Check network permissions in AndroidManifest.xml

## Important Notes

1. **Firebase Configuration**: The google-services.json file needs to be configured with actual Firebase project credentials for authentication to work.

2. **Camera Permission**: Google Code Scanner does NOT require manual camera permission declaration - it's handled internally by the GMS library.

3. **Internet Permission**: INTERNET and ACCESS_NETWORK_STATE permissions are declared in AndroidManifest.xml.

4. **API Limitations**: Open Food Facts API may have rate limiting. For production use, consider implementing caching.

5. **Image Loading**: Glide handles image caching automatically. Large product images may take time to load.

6. **Database Migration**: The current implementation uses version 1. For future schema changes, increment version and add migration.

## Additional Features to Consider

- Search history by product name or barcode
- Share scan results
- Add products to wishlist/favorites
- Offline mode with cached products
- Barcode history export
- Nutritional comparison
- Product reviews/ratings
- Alternative product suggestions

## Building APK for Distribution

```bash
./gradlew assembleRelease
```

The APK will be generated at: `app/build/outputs/apk/release/app-release.apk`

For production, configure signing in build.gradle before generating release APK.

## License

This project is created for educational purposes.

## Support

For issues or questions, refer to:
- Android Documentation: https://developer.android.com
- Firebase Documentation: https://firebase.google.com/docs
- Open Food Facts API: https://wiki.openfoodfacts.org/API/
- Room Database: https://developer.android.com/training/data-storage/room
