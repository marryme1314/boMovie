# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.

# ==================== Retrofit ====================
-keepattributes Signature
-keepattributes *Annotation*
-keep class retrofit2.** { *; }
-keepattributes Exceptions
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}

# ==================== Gson ====================
-keepattributes Signature
-keepattributes *Annotation*
-keep class com.google.gson.** { *; }
-keep class com.biubiupapa.movie.model.** { *; }
-keep class * implements com.google.gson.TypeAdapter
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# Keep model classes that are serialized/deserialized by Gson
-keep class com.biubiupapa.movie.model.Movie { *; }
-keep class com.biubiupapa.movie.model.City { *; }
-keep class com.biubiupapa.movie.model.CityResponse { *; }
-keep class com.biubiupapa.movie.model.MovieResponse { *; }
-keep class com.biubiupapa.movie.model.MovieListResponse { *; }
-keep class com.biubiupapa.movie.model.MovieIntroResponse { *; }
-keep class com.biubiupapa.movie.model.ComingListResponse { *; }
-keep class com.biubiupapa.movie.model.MostExpectedResponse { *; }
-keep class com.biubiupapa.movie.model.MostExpectedResponse$ExpectedMovie { *; }
-keep class com.biubiupapa.movie.model.SearchSuggestItem { *; }
-keep class com.biubiupapa.movie.model.SearchMovieResponse { *; }
-keep class com.biubiupapa.movie.model.SearchMovieResponse$SearchMovies { *; }

# ==================== Glide ====================
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep class * extends com.bumptech.glide.module.AppGlideModule { <init>(...); }
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
-keep class com.bumptech.glide.load.data.ParcelFileDescriptorRewinder$InternalRewinder { *** rewind(); }

# ==================== OkHttp ====================
-dontwarn okhttp3.**
-dontwarn okio.**

# ==================== General ====================
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile
-keep class com.biubiupapa.movie.** { *; }