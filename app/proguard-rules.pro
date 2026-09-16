# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Users\rivel\AppData\Local\Android\sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.kts.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools-proguard.html

# Keep for AndroidX Startup
-keep class androidx.startup.InitializationProvider
-keep class * implements androidx.startup.Initializer {
    <init>();
}
-keep class * implements androidx.startup.Initializer

# Room / WorkManager
-dontwarn androidx.room.**
-dontwarn androidx.work.**
-keep class androidx.work.impl.WorkDatabase_Impl {
    public <init>(...);
}
-keep class * extends androidx.room.RoomDatabase {
    public <init>(...);
}

# Keep Language enum and its members for localization
-keepclassmembers enum com.rukinpavel.wordlyapp.core.model.Language {
    *;
}
-keep class com.rukinpavel.wordlyapp.core.model.Language {
    *;
}

