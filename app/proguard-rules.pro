# ProGuard & R8 Optimization Rules

# Kotlinx Serialization
-keepattributes *Annotation*,InnerClasses
-dontnote kotlinx.serialization.SerializationKt
-keepclassmembers class * {
    *** Companion;
}
-keepclasseswithmembers class * {
    kotlinx.serialization.KSerializer serializer(...);
}
-keepclassmembers class * {
    @kotlinx.serialization.Serializable *;
}
-keep @kotlinx.serialization.Serializable class * { *; }

# Data models
-keep class com.angeldevtech.gol.data.model.** { *; }

# Hilt / Dagger
-keep class * extends dagger.hilt.internal.GeneratedComponent
-keep class androidx.hilt.navigation.compose.** { *; }

# Media3 / ExoPlayer
-keep class androidx.media3.** { *; }
-dontwarn androidx.media3.**

# Ktor & OkHttp
-dontwarn io.ktor.**
-keep class io.ktor.** { *; }
-dontwarn okhttp3.**
-dontwarn okio.**

# Jsoup
-keep class org.jsoup.** { *; }

# Coil
-dontwarn coil3.**
-dontwarn coil.**