# Testing and Verification Guide

Complete guide for testing all features of the Food Scanner application.

## Pre-Testing Checklist

- [ ] Android Studio latest version installed
- [ ] Android SDK API 24+ installed
- [ ] Emulator running or physical device connected
- [ ] Internet connection available on device
- [ ] Firebase project created and configured
- [ ] google-services.json placed in app/
- [ ] Local.properties configured with SDK path
- [ ] Gradle sync completed successfully
- [ ] Project builds without errors

## Test Barcodes

Use these real barcodes from Open Food Facts to test:

| Barcode | Product | Nutri-Score |
|---------|---------|-------------|
| 4006381333931 | Nutella | E |
| 5000159511357 | Cadbury Dairy Milk | D |
| 3017620425035 | Ferrero Rocher | D |
| 20674202 | Coca Cola | D |
| 737628064502 | Sprite | D |
| 8718206051220 | Heinz Tomato Sauce | C |

Invalid/Unknown barcodes to test error handling:
- 1234567890123
- 9999999999999
- 0000000000000

## Test Plan

### Phase 1: Firebase Authentication

**Test 1.1: Application Launch**
- [ ] Install and launch app
- [ ] Should display LoginActivity
- [ ] Email and password fields visible
- [ ] Register link visible
- [ ] Login button enabled

**Test 1.2: Registration - Happy Path**
- [ ] Click "Register" link
- [ ] Navigate to RegisterActivity
- [ ] Enter:
  - Name: Test User
  - Email: testuser@gmail.com
  - Password: TestPass123
- [ ] Click Register
- [ ] Wait for Firebase call (may take 2-3 seconds)
- [ ] Should navigate to HomeActivity
- [ ] Welcome message shows email

**Test 1.3: Registration - Validation Errors**
- [ ] Go back and click Register link again
- [ ] Leave name empty, enter email/password → Click Register
  - [ ] Error: "Please enter your name" appears
- [ ] Enter name, leave email empty → Click Register
  - [ ] Error: "Please enter email" appears
- [ ] Enter name, invalid email (missing @), enter password → Click Register
  - [ ] Error: "Invalid email address" appears
- [ ] Enter name, valid email, empty password → Click Register
  - [ ] Error: "Please enter password" appears
- [ ] Enter name, email, password < 6 chars → Click Register
  - [ ] Error: "Password must be at least 6 characters" appears

**Test 1.4: Registration - Duplicate Email**
- [ ] Try registering with email from Test 1.2
- [ ] Should get error: "Email already in use"

**Test 1.5: Login - Happy Path**
- [ ] From login screen
- [ ] Enter:
  - Email: testuser@gmail.com
  - Password: TestPass123
- [ ] Click Login
- [ ] Should navigate to HomeActivity
- [ ] Welcome message shows logged-in user's email

**Test 1.6: Login - Invalid Credentials**
- [ ] Enter valid email, wrong password
- [ ] Should show error message
- [ ] Login button re-enabled for retry
- [ ] Enter wrong email
- [ ] Should show error message

**Test 1.7: Session Persistence**
- [ ] Close app (swipe from recent apps)
- [ ] Reopen app
- [ ] Should go directly to HomeActivity (not LoginActivity)
- [ ] User still logged in
- [ ] No need to re-enter credentials

**Test 1.8: Logout**
- [ ] On HomeActivity, click Logout button
- [ ] Should navigate to LoginActivity
- [ ] Session cleared
- [ ] Can login with different user

### Phase 2: Home Screen UI

**Test 2.1: Home Screen Layout**
- [ ] LoggedIn user sees HomeActivity
- [ ] Header shows:
  - [ ] "Food Scanner" title
  - [ ] Welcome message with user email/name
  - [ ] Logout button
- [ ] "Scan Product" button prominent
- [ ] "Recent Scans" section visible
- [ ] "View All History" button visible (or hidden if no scans)
- [ ] "No scans" message if first time

**Test 2.2: First-Time User**
- [ ] Fresh user (no scans)
- [ ] "No scans yet" message displays
- [ ] RecyclerView hidden or empty
- [ ] "View All History" button hidden

### Phase 3: Barcode Scanner Integration

**Test 3.1: Scanner Launch**
- [ ] Click "Scan Product" button
- [ ] Google Code Scanner UI appears
- [ ] Scanner preview shows camera feed
- [ ] Scanner controls visible (torch, etc.)

**Test 3.2: Successful Scan**
- [ ] Point at barcode: 4006381333931 (Nutella)
- [ ] Scanner recognizes barcode
- [ ] Returns barcode value
- [ ] Auto-navigates to ProductDetailsActivity
- [ ] Shows loading indicator initially
- [ ] Then shows product details

**Test 3.3: Scanner Cancellation**
- [ ] Click "Scan Product" button
- [ ] Scanner opens
- [ ] Press back button or close scanner
- [ ] Gracefully returns to HomeActivity
- [ ] No crashes or errors

**Test 3.4: Invalid Barcode**
- [ ] Click "Scan Product" button
- [ ] Point at invalid barcode or number
- [ ] If recognized: Navigate to ProductDetails
- [ ] Should show error (product not found)
- [ ] "Try Again" button available

### Phase 4: Product Details Screen

**Test 4.1: Valid Product Display**
- [ ] Scan barcode: 4006381333931 (Nutella)
- [ ] ProductDetailsActivity loads
- [ ] Loading indicator shows briefly
- [ ] Product image displays
- [ ] Product name: "Nutella" or similar
- [ ] Brand: "Ferrero" or similar
- [ ] Barcode: 4006381333931 displayed
- [ ] Quantity displayed if available
- [ ] Nutri-Score: "E" displayed with red background

**Test 4.2: Nutri-Score Color Coding**
- [ ] Test various products:
  - [ ] Scan product with Score A → Green background
  - [ ] Scan product with Score B → Light green
  - [ ] Scan product with Score C → Orange
  - [ ] Scan product with Score D → Dark orange
  - [ ] Scan product with Score E → Red
  - [ ] Scan product with no score → Hidden or gray

**Test 4.3: Image Loading**
- [ ] Products with images → Image loads
- [ ] Large images → Load with reasonable delay
- [ ] Missing images → Gray placeholder shown
- [ ] No crashes if image URL invalid

**Test 4.4: Product Not Found**
- [ ] Scan invalid barcode: 1234567890123
- [ ] Loading indicator shows
- [ ] After 2-3 seconds: Error message displays
- [ ] Error text: "Product not found" or similar
- [ ] "Try Again" button visible
- [ ] Back button works

**Test 4.5: Network Error Handling**
- [ ] Turn off device internet (airplane mode)
- [ ] Click "Scan Product" or "Try Again"
- [ ] Wait for timeout
- [ ] Error message: "Network error" or similar
- [ ] App doesn't crash
- [ ] "Try Again" button available
- [ ] Turn internet back on → "Try Again" works

**Test 4.6: Partial Data Display**
- [ ] Scan product with missing fields
- [ ] Missing fields not displayed (no empty sections)
- [ ] App gracefully handles missing:
  - [ ] Product name
  - [ ] Brand
  - [ ] Image
  - [ ] Nutri-Score
  - [ ] Quantity

**Test 4.7: Back Navigation**
- [ ] From ProductDetailsActivity
- [ ] Click back button → Navigate back to HomeActivity
- [ ] Recent scans updated if scan was successful

### Phase 5: Scan History

**Test 5.1: History After Scan**
- [ ] Scan valid product (Nutella)
- [ ] After product displays, click back
- [ ] HomeActivity shows:
  - [ ] Recent scan appears in list
  - [ ] Product image, name, brand visible
  - [ ] Scan date and time shown
  - [ ] Nutri-Score badge displayed
  - [ ] Product is newest in list

**Test 5.2: Multiple Scans**
- [ ] Scan 5 different products
- [ ] HomeActivity shows recent scans
- [ ] Newest scan at top
- [ ] Each scan displays correctly
- [ ] Images load properly

**Test 5.3: View All History**
- [ ] From HomeActivity
- [ ] Click "View All History" button
- [ ] Navigate to ScanHistoryActivity
- [ ] All scans (more than 5) displayed
- [ ] Newest first
- [ ] RecyclerView scrolls properly

**Test 5.4: History Item Details**
- [ ] Each history item shows:
  - [ ] Product image (thumbnail)
  - [ ] Product name
  - [ ] Brand
  - [ ] Barcode
  - [ ] Scan date and time
  - [ ] Nutri-Score with color

**Test 5.5: Click History Item**
- [ ] From history list
- [ ] Click any scan item
- [ ] Navigate to ProductDetailsActivity
- [ ] Shows same product details as original scan
- [ ] Image, name, brand, barcode, score all correct
- [ ] Back button navigates back to history

**Test 5.6: No Scans Yet**
- [ ] Fresh user without scans
- [ ] HomeActivity shows "No scans yet" message
- [ ] RecyclerView hidden
- [ ] "View All History" button hidden
- [ ] After first scan, message disappears

**Test 5.7: Database Persistence**
- [ ] Scan 3 products
- [ ] Close app completely
- [ ] Reopen app
- [ ] Login with same user
- [ ] All 3 scans still in history
- [ ] History persists after app restart

**Test 5.8: Failed Scans Not Saved**
- [ ] Scan invalid barcode → Show error
- [ ] Click back
- [ ] History should NOT include failed scan
- [ ] Only successful product scans saved
- [ ] Go to invalid product screen, click "Try Again"
- [ ] Try Again doesn't save failed attempt

**Test 5.9: User Isolation**
- [ ] Scan 3 products as User A
- [ ] Logout
- [ ] Login as User B
- [ ] User B's history empty (no User A scans)
- [ ] Logout
- [ ] Login as User A
- [ ] All 3 original scans still there

### Phase 6: Navigation Flow

**Test 6.1: Complete User Journey**
- [ ] Login with credentials
- [ ] HomeActivity displays
- [ ] Click "Scan Product"
- [ ] Scan Nutella (4006381333931)
- [ ] ProductDetailsActivity shows details
- [ ] Click back → HomeActivity
- [ ] Recent scan appears
- [ ] Click "View All History"
- [ ] ScanHistoryActivity shows scan
- [ ] Click on scan → ProductDetailsActivity
- [ ] Click back → ScanHistoryActivity
- [ ] Click back → HomeActivity
- [ ] Click Logout → LoginActivity

**Test 6.2: Back Button Behavior**
- [ ] All back buttons navigate correctly
- [ ] No state loss
- [ ] Lists maintain scroll position (optional)

**Test 6.3: Rapid Navigation**
- [ ] Rapidly scan multiple products
- [ ] Switch between screens quickly
- [ ] App doesn't crash
- [ ] No duplicate records
- [ ] UI remains responsive

### Phase 7: Edge Cases & Error Scenarios

**Test 7.1: Network Latency**
- [ ] On slow connection, scan product
- [ ] Loading indicator shows
- [ ] Eventually loads or times out gracefully

**Test 7.2: Concurrent Operations**
- [ ] While product loading, click back
- [ ] API request cancels gracefully
- [ ] No crashes

**Test 7.3: Empty Responses**
- [ ] Scan product with no image URL
- [ ] App doesn't crash
- [ ] Placeholder shown

**Test 7.4: Large Image URLs**
- [ ] Products with large images load
- [ ] Memory not excessively used
- [ ] No OutOfMemory crashes

**Test 7.5: Special Characters**
- [ ] Products with special characters in names
- [ ] Display correctly (é, ñ, etc.)
- [ ] No encoding issues

## Performance Testing

**Test P1: App Launch Time**
- [ ] Cold start (force stop then launch)
- [ ] Should launch within 3 seconds
- [ ] Warm start (from recent) < 1 second

**Test P2: Memory Usage**
- [ ] Monitor memory during:
  - [ ] Scanning products
  - [ ] Loading history
  - [ ] Image loading
  - [ ] Database queries
- [ ] No memory leaks (check with Android Profiler)

**Test P3: Battery Usage**
- [ ] Network operations don't drain excessive battery
- [ ] Coroutines handle efficiently

## Automation/Regression Testing

After manual tests pass, consider:
- Unit tests for repositories
- Instrumented tests for Activities
- API integration tests

## Devices to Test On

- [ ] Android 7 (API 24) - minimum support
- [ ] Android 10 (API 29)
- [ ] Android 13 (API 33)
- [ ] Android 14 (API 34)
- [ ] Physical device if possible

## Sign-Off Checklist

After all tests pass:

- [ ] All authentication tests passed
- [ ] UI displays correctly
- [ ] Barcode scanner works
- [ ] Products display correctly
- [ ] History saves and displays
- [ ] Navigation works smoothly
- [ ] No crashes or unhandled exceptions
- [ ] Network errors handled gracefully
- [ ] Database persistence confirmed
- [ ] Performance acceptable
- [ ] Firebase integration working
- [ ] No sensitive data exposed

## Known Limitations

1. **Rate Limiting**: Open Food Facts API may rate limit after many requests
2. **Incomplete Data**: Some products may have missing information
3. **Image Loading**: Large images may take time depending on connection
4. **Offline Mode**: Currently requires internet connection
5. **No Search**: Current implementation requires barcode scanning only

## Troubleshooting During Testing

### Issue: App Crashes on Launch
- Check Firebase is configured correctly
- Check internet connection
- Check API 24+ on device
- Review logcat for exceptions

### Issue: Scanner Not Opening
- Ensure Google Play Services installed
- Check device has camera hardware
- Check Play Services version updated

### Issue: Products Not Loading
- Verify internet connection
- Check barcode is valid
- Try known barcode (4006381333931)
- Check API endpoint: https://world.openfoodfacts.org/api/v2/product/{barcode}.json

### Issue: History Not Saving
- Check Room database created
- Verify user ID captured correctly
- Check logcat for database errors
- Ensure app has storage permissions

### Issue: Firebase Errors
- Verify google-services.json in correct location
- Check Firebase project ID matches
- Verify Email/Password provider enabled
- Check internet connectivity

## Test Result Documentation

Create a test report:
```
App Version: 1.0
Test Date: [Date]
Tester: [Name]
Device: [Model, Android Version]

Test Results:
- Authentication: PASS/FAIL
- UI Display: PASS/FAIL
- Scanner: PASS/FAIL
- Product Display: PASS/FAIL
- History: PASS/FAIL
- Navigation: PASS/FAIL

Issues Found:
1. [Issue description]
2. [Issue description]

Sign-Off: [Signature/Approval]
```

## Final Steps

1. All tests complete and passing
2. Build release APK (optional): `./gradlew assembleRelease`
3. App ready for submission to Google Play Store
4. Maintain test records for compliance

---

For detailed setup, see SETUP.md
For feature overview, see README.md
