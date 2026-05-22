# Add project specific ProGuard rules here.
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Service
-keep public class * extends android.view.View

# Keep Hilt generated classes
-keep class dagger.hilt.** { *; }
-keep class * extends dagger.hilt.android.internal.managers.ViewComponentManager { *; }

# Keep Room classes
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-keep class * implements androidx.room.Dao { *; }

# Keep Kotlin data classes
-keep class com.hermes.domain.model.** { *; }
-keep class com.hermes.domain.valueobject.** { *; }