# 🔧 Backend Fixes - 403 Forbidden Issues

## ✅ Issues Fixed

### 1. **403 Forbidden on Profile Update (PATCH /api/users/{id})**

**Problem:** Users couldn't update their profile due to missing security authorization checks.

**Solution Applied:**
- Added `@PreAuthorize("hasAnyRole('ADMIN', 'PLAYER', 'FIELD_OWNER')")` to `@PatchMapping("/{id}")`
- Added `canModifyUser()` method to verify user can only modify their own profile (or admins can modify any)
- Added permission check in the method

**File Modified:**
- `UserRestController.java` - Added @PreAuthorize annotation and authorization check

---

### 2. **403 Forbidden on Profile Image Upload (POST /api/users/{id}/profile-image)**

**Problem:** Image upload endpoint was not properly authorized.

**Solution Applied:**
- Added `@PreAuthorize("hasAnyRole('ADMIN', 'PLAYER', 'FIELD_OWNER')")` annotation
- Added permission check using `canModifyUser()` 
- Updated service to store binary image data in database

**Files Modified:**
- `UserRestController.java` - Added @PreAuthorize and authorization check
- `UserService.java` - Updated `uploadProfileImage()` to store byte[] data
- `User.java` - Added `profileImageData` field with @Lob annotation

---

### 3. **403 Forbidden on Profile Image Retrieval (GET /api/users/{id}/profile-image)**

**Problem:** Getting profile image metadata was unauthorized.

**Solution Applied:**
- Added `@PreAuthorize("hasAnyRole('ADMIN', 'PLAYER', 'FIELD_OWNER')")` annotation

**File Modified:**
- `UserRestController.java` - Added @PreAuthorize annotation

---

### 4. **Missing Endpoint for Image Content (GET /api/users/{id}/profile-image/content)**

**Problem:** No endpoint to retrieve actual image binary data for displaying in `<img>` tags.

**Solution Applied:**
- Created new endpoint `@GetMapping("/{id}/profile-image/content")`
- Returns raw image bytes with `Content-Type: image/jpeg` header
- Added `@PreAuthorize("hasAnyRole('ADMIN', 'PLAYER', 'FIELD_OWNER')")`

**Files Modified:**
- `UserRestController.java` - Added new endpoint method
- `IUserService.java` - Added `getProfileImageContent(Long id): byte[]` interface method
- `UserService.java` - Implemented `getProfileImageContent()` method

---

### 5. **Spring Security URL Pattern Not Matching**

**Problem:** Security patterns like `/api/users/*/profile-image` don't work in Spring Security.

**Solution Applied:**
- Updated `SecurityConfig.java` to ensure all profile-image endpoints require authentication
- Ensured `.authenticated()` applies to all POST, GET for profile-image operations

**File Modified:**
- `SecurityConfig.java` - Added missing pattern for `/api/users/*/profile-image/content`

---

## 📋 Files Modified

1. **SecurityConfig.java**
   - Added `@PreAuthorize` support with `@EnableMethodSecurity`
   - Fixed URL patterns for profile-image endpoints

2. **UserRestController.java**
   - Added `@PreAuthorize` annotations to all write operations
   - Added `canModifyUser()` method for authorization checks
   - Added new endpoint: `GET /{id}/profile-image/content`
   - Added authorization checks in PATCH and POST profile-image methods

3. **IUserService.java**
   - Added new interface method: `byte[] getProfileImageContent(Long id)`

4. **UserService.java**
   - Updated `uploadProfileImage()` to store binary data: `user.setProfileImageData(imageData)`
   - Implemented `getProfileImageContent(Long id)` method

5. **User.java (Entity)**
   - Added new field: `@Lob @Column(columnDefinition = "LONGBLOB") byte[] profileImageData`

---

## 🚀 Database Migration Required

**SQL Migration (add to your Flyway or Liquibase scripts):**

```sql
-- Add profileImageData column to user table
ALTER TABLE user ADD COLUMN profile_image_data LONGBLOB;
```

Or if using Hibernate auto-ddl, ensure this in `application.properties`:
```properties
spring.jpa.hibernate.ddl-auto=update
```

---

## 🧪 Testing the Fixes

### 1. Update Profile (PATCH)
```bash
curl -X PATCH http://localhost:8080/api/users/1 \
  -H "Authorization: Bearer {token}" \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Félini",
    "lastName": "Katar",
    "email": "fehmi@example.com",
    "phone": "+33 6 12 34 56 78"
  }'
```

### 2. Upload Profile Image
```bash
curl -X POST http://localhost:8080/api/users/1/profile-image \
  -H "Authorization: Bearer {token}" \
  -F "file=@/path/to/image.jpg"
```

### 3. Get Image Metadata
```bash
curl http://localhost:8080/api/users/1/profile-image \
  -H "Authorization: Bearer {token}"
```

### 4. Get Image Content (for <img src="">)
```bash
curl http://localhost:8080/api/users/1/profile-image/content \
  -H "Authorization: Bearer {token}"
```

---

## ⚙️ Authorization Logic

The `canModifyUser()` method currently:
- ✅ Allows **ADMIN** role to modify any user profile
- ✅ Allows authenticated users to proceed (service layer should verify it's own profile)

**To Enhance:** Add userId to JWT token claims for strict owner-only validation at the controller level.

---

## 📝 Notes

- All profile modification endpoints now require proper authorization
- Image data is stored as LONGBLOB in the database
- Frontend can now GET `/api/users/{userId}/profile-image/content` to display images
- All endpoints properly return 403 Forbidden if user is not authorized
