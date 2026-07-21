# Consumer R8/ProGuard rules for thorchain-kit-android.
#
# These are REQUIRED for correctness in minified apps: response parsing is
# reflection-based (Gson), so without keep rules R8 strips/renames DTO fields and
# parsing silently produces nulls or primitive defaults. The worst case is not a
# crash — e.g. a stripped TxResponse.code would deserialize as the default and a
# failed broadcast could be misread. Never remove these rules.

-keepattributes Signature
-keepattributes *Annotation*

# Gson TypeToken generic signatures (used by Room type converters)
-keep class com.google.gson.reflect.TypeToken { *; }
-keep class * extends com.google.gson.reflect.TypeToken

# Network DTOs populated by Gson via reflection
-keepclassmembers class io.horizontalsystems.thorchainkit.network.** {
    <fields>;
    <init>(...);
}

# Model classes serialized with Gson (Room type converters: CoinTransfer, ...)
-keepclassmembers class io.horizontalsystems.thorchainkit.models.** {
    <fields>;
    <init>(...);
}

# Protobuf-lite generated messages (looked up reflectively)
-keep class * extends com.google.protobuf.GeneratedMessageLite { *; }
