# 马来西亚OSINT — ProGuard混淆规则

# Retrofit
-keepattributes Signature
-keepattributes Exceptions
-keep class com.osint.malaysia.model.** { *; }

# Gson
-keepattributes *Annotation*
-keep class com.google.gson.** { *; }

# OkHttp
-dontwarn okhttp3.**
-dontwarn okio.**
