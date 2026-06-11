# Retrofit
-dontwarn okhttp3.**
-dontwarn okio.**
-keepattributes Signature
-keepattributes *Annotation*
-keep class com.yogesh.auraai.data.remote.dto.** { *; }

# Kotlinx Serialization
-keepattributes InnerClasses
-dontnote kotlinx.serialization.**
-keepclassmembers class com.yogesh.auraai.data.remote.dto.** {
    *** Companion;
}
-keepclasseswithmembers class com.yogesh.auraai.data.remote.dto.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# Room
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-dontwarn androidx.room.paging.**
