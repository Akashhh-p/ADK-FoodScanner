# Architecture Overview

This document describes the architecture and design patterns used in the Food Scanner application.

## Architecture Pattern: MVVM + Repository

The application uses a layered architecture with clear separation of concerns:

```
┌─────────────────────────────────────┐
│         UI Layer (Activities)        │
│  (LoginActivity, HomeActivity, etc)  │
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│    ViewModel / Business Logic        │
│  (Currently in Activities, can move  │
│   to ViewModel for scalability)      │
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│      Repository Layer                │
│  (ScanHistoryRepository,             │
│   ProductRepository)                 │
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│      Data Layer                      │
│  ┌──────────────────────────────┐   │
│  │ Local: Room Database         │   │
│  │ (ScanHistory)                │   │
│  ├──────────────────────────────┤   │
│  │ Remote: Retrofit API Client  │   │
│  │ (Open Food Facts)            │   │
│  ├──────────────────────────────┤   │
│  │ Auth: Firebase Authentication│   │
│  │ (Email/Password)             │   │
│  └──────────────────────────────┘   │
└─────────────────────────────────────┘
```

## Detailed Layer Breakdown

### 1. UI Layer (Activities)

**Activities:**
- `LoginActivity` - User login screen
- `RegisterActivity` - User registration
- `HomeActivity` - Main screen with scanner and recent history
- `ProductDetailsActivity` - Product information display
- `ScanHistoryActivity` - All scans history

**Responsibilities:**
- Display UI elements
- Handle user interactions
- Navigate between screens
- Show loading/error states
- Bind data to views

**Technology:**
- Android AppCompat Activities
- View Binding for type-safe view access
- Material Components for UI

### 2. Adapter/Utility Layer

**Components:**
- `ScanHistoryAdapter` - RecyclerView adapter for history list
- `FirebaseAuthManager` - Authentication operations

**Responsibilities:**
- Format data for display
- Handle user interactions with list items
- Centralize business logic

### 3. Repository Layer

**Repositories:**
- `ScanHistoryRepository` - Local database operations
- `ProductRepository` - Remote API operations

**Pattern:** Single Responsibility - Each repository handles one data source

```kotlin
// ScanHistoryRepository
insertScan()        // Insert into local DB
getUserScans()      // Query user's scans
getRecentScans()    // Get top N scans
deleteScan()        // Remove from DB

// ProductRepository
getProductDetails() // Fetch from API
```

**Benefits:**
- Decouples data sources from UI
- Easy to mock for testing
- Centralized error handling
- Reusable across activities

### 4. Data Layer

#### Local Storage: Room Database

```
ScanHistory Table
├── id (PK, Auto-increment)
├── userId (Foreign key to user)
├── barcode (Product barcode)
├── productName
├── brand
├── imageUrl
├── nutriScore
├── quantity
└── scanDate (Timestamp)
```

**Components:**
- `ScanHistory` - Entity
- `ScanHistoryDao` - Data Access Object
- `FoodScannerDatabase` - Room Database

**Pattern:** DAO (Data Access Object)
- Provides type-safe access to database
- Query methods return Flow for reactive updates
- Coroutine support for async operations

#### Remote API: Retrofit

```
OpenFoodFactsService (Interface)
└── getProduct(barcode: String)
    └── Returns: ProductResponse
        └── Contains: Product object
            ├── productName
            ├── brands
            ├── imageUrl
            ├── nutriscoreGrade
            └── quantity
```

**Architecture:**
```
RetrofitClient (Singleton)
├── Retrofit Builder
│   ├── Base URL: https://world.openfoodfacts.org/
│   ├── Converter Factory: Gson
│   └── HTTP Client: OkHttp
└── OpenFoodFactsService
```

**Pattern:** Retrofit + Gson
- Automatic JSON deserialization
- Type-safe HTTP requests
- Error handling via Result<T>

#### Authentication: Firebase

```
FirebaseAuthManager
├── register(name, email, password)
│   └── Creates user account
│       └── Sets display name
├── login(email, password)
│   └── Authenticates user
└── logout()
    └── Signs out user
```

**Pattern:** Manager/Helper pattern
- Wraps Firebase SDK
- Provides suspend functions (Coroutines)
- Consistent error handling

## Data Flow

### Scanning Flow

```
User clicks Scan
    │
    ▼
GoogleCodeScanner opens
    │
    ▼
User scans barcode
    │
    ▼
Barcode value extracted
    │
    ▼
Navigate to ProductDetailsActivity with barcode
    │
    ▼
ProductRepository.getProductDetails(barcode)
    │
    ▼
RetrofitClient makes HTTP GET request
    │
    ▼
API Response received & parsed
    │
    ▼
Display product details
    │
    ▼
Save to ScanHistory (Room DB)
    │
    ▼
Navigate back to HomeActivity
```

### History Display Flow

```
HomeActivity loads
    │
    ▼
Get current user ID from Firebase
    │
    ▼
ScanHistoryRepository.getRecentScans(userId, 5)
    │
    ▼
Query Room Database
    │
    ▼
Return List<ScanHistory> ordered by scanDate DESC
    │
    ▼
ScanHistoryAdapter.submitList()
    │
    ▼
RecyclerView displays items with Glide images
    │
    ▼
User clicks item
    │
    ▼
Navigate to ProductDetailsActivity
```

## Design Patterns Used

### 1. Repository Pattern
```kotlin
interface Repository {
    suspend fun fetchData(): Result<Data>
}

class ConcreteRepository : Repository {
    override suspend fun fetchData(): Result<Data> {
        return try {
            val data = remoteService.getData()
            Result.success(data)
        } catch(e: Exception) {
            Result.failure(e)
        }
    }
}
```

**Benefits:**
- Abstraction of data sources
- Easy to swap implementations
- Testability

### 2. Adapter Pattern (RecyclerView)
```kotlin
class ScanHistoryAdapter(onItemClick: (ScanHistory) -> Unit) :
    RecyclerView.Adapter<ScanHistoryAdapter.ViewHolder>() {
    // Adapts List<ScanHistory> for RecyclerView display
}
```

### 3. Singleton Pattern (RetrofitClient, Database)
```kotlin
object RetrofitClient {
    private val retrofit = Retrofit.Builder().build()
    val service = retrofit.create(Service::class.java)
}

class FoodScannerDatabase {
    companion object {
        @Volatile
        private var INSTANCE: FoodScannerDatabase? = null
        
        fun getDatabase(context: Context): FoodScannerDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(...).build()
            }
        }
    }
}
```

### 4. Result Wrapper Pattern
```kotlin
sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error<T>(val exception: Exception) : Result<T>()
}

// Usage
result.onSuccess { data -> /* handle success */ }
result.onFailure { error -> /* handle error */ }
```

### 5. Observer Pattern (Room Flow)
```kotlin
@Query("SELECT * FROM scan_history WHERE userId = :userId")
fun getUserScans(userId: String): Flow<List<ScanHistory>>

// Usage
lifecycleScope.launch {
    repository.getUserScans(userId).collect { scans ->
        adapter.submitList(scans)
    }
}
```

## Concurrency Model

### Coroutines

All long-running operations use Kotlin Coroutines:

```
Main Thread (UI)
    │
    ├─► lifecycleScope.launch
    │       │
    │       ▼
    │   IO Thread (Dispatchers.IO)
    │       │
    │       ├─► API Call (Retrofit)
    │       ├─► DB Query (Room)
    │       └─► Auth Operation (Firebase)
    │       │
    │       ▼
    │   Result returned to Main Thread
    │       │
    │       ▼
    │   UI Updated
    │
    └─► lifecycleScope.launch (bound to lifecycle)
```

**Benefits:**
- Non-blocking operations
- Automatic lifecycle handling
- Simpler async code than callbacks

## Error Handling

### Three-Layer Error Handling

```
1. Network Layer (Retrofit)
   ├── NetworkException
   ├── TimeoutException
   └── IOException
        │
        ▼ Result.failure()
        
2. Repository Layer
   ├── Wraps exceptions
   └── Returns Result<T>
        │
        ▼ result.onFailure()
        
3. UI Layer (Activity)
   ├── Shows error message
   ├── Provides retry option
   └── Logs for debugging
```

## State Management

### Activity State

Currently, state is managed at Activity level:
- Firebase user state
- Loading state (visibility flags)
- Error messages

**Future Improvement:**
- Move to ViewModel for better lifecycle handling
- Use LiveData for state observation
- Preserve state across configuration changes

## Thread Safety

### Key Considerations

1. **Room Database**
   - Uses internal synchronization
   - Safe for concurrent access
   - Queries run on background threads by default

2. **Firebase Auth**
   - Thread-safe by design
   - Tasks handled by Firebase SDK

3. **Retrofit**
   - OkHttp handles thread pooling
   - Requests run on background thread

4. **UI Updates**
   - All UI updates on Main thread via lifecycleScope

## Scalability Considerations

### Current Limitations
- Business logic in Activities (should move to ViewModel)
- No caching of product data
- Limited error analytics

### Future Improvements
1. **ViewModel Layer**
   ```kotlin
   class ProductDetailsViewModel : ViewModel() {
       val productDetails = MutableLiveData<Product>()
       fun loadProduct(barcode: String) { }
   }
   ```

2. **Dependency Injection**
   ```kotlin
   // Current: Manual instantiation
   val repo = ScanHistoryRepository(db.dao())
   
   // Future: Hilt/Dagger
   @Inject lateinit var repo: ScanHistoryRepository
   ```

3. **Caching**
   ```kotlin
   private val cache = ConcurrentHashMap<String, Product>()
   
   override suspend fun getProduct(barcode: String): Result<Product> {
       cache[barcode]?.let { return Result.success(it) }
       // Fetch from API...
   }
   ```

4. **Analytics**
   ```kotlin
   Firebase.analytics.logEvent("product_scanned") {
       param("barcode", barcode)
       param("success", true)
   }
   ```

## Testing Architecture

### Current
- No automated tests (manual testing via TESTING.md)

### Future
```
Unit Tests (Repository, Manager)
├── MockRepository
├── MockFirebase
└── Coroutines test dispatcher

Instrumented Tests (Activities)
├── ActivityScenario
├── Espresso assertions
└── MockWebServer for API

Integration Tests
├── End-to-end flows
├── Real API calls
└── Database integration
```

## Security Considerations

### Authentication
- Firebase handles secure password storage
- Tokens managed by Firebase SDK
- Session persisted securely

### Data Storage
- User IDs associated with scans
- No sensitive data in SharedPreferences
- Room database unencrypted (can encrypt with SQLCipher)

### Network
- HTTPS enforced (Retrofit over HTTPS)
- google-services.json never committed to git
- API keys managed by Firebase

### Code Obfuscation
- ProGuard rules configured
- Third-party libraries protected

## Performance Optimization

### Current
- Glide for efficient image loading
- Room database with proper indexing
- Retrofit with connection pooling

### Potential Improvements
1. **Pagination** - Load history in pages
2. **Database Indexing** - Add index on userId + scanDate
3. **Image Caching** - Glide already does this
4. **API Response Caching** - Retrofit + Okhttp interceptor
5. **Background Sync** - WorkManager for periodic sync

## Code Quality

### Kotlin Best Practices
- Null safety via nullable types (?)
- Extension functions for utilities
- Sealed classes for type-safe results
- Data classes for models
- Coroutine-safe operations

### Architecture Best Practices
- Dependency injection ready (but not using yet)
- Separation of concerns (UI, Business, Data)
- Single responsibility principle
- DRY (Don't Repeat Yourself)

## Integration Points

### External Services
1. **Firebase Authentication** - Email/Password auth
2. **Open Food Facts API** - Product data
3. **Google Code Scanner** - Barcode scanning
4. **Glide** - Image loading

### Internal Components
1. **Room ←→ Repository** - Data persistence
2. **Firebase ←→ Activity** - User management
3. **Retrofit ←→ Repository** - Remote data
4. **Adapter ←→ RecyclerView** - List display

## Documentation Structure

```
README.md
├── Quick start
├── Features overview
└── Setup instructions

SETUP.md
├── Firebase configuration
├── Local setup
└── Troubleshooting

TESTING.md
├── Test plan
├── Test cases
└── Sign-off checklist

ARCHITECTURE.md (this file)
├── Design patterns
├── Data flow
└── Future improvements
```

---

For quick start: See README.md
For setup instructions: See SETUP.md
For testing: See TESTING.md
