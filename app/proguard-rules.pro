# ProGuard rules for Drug-Free India
-keepattributes *Annotation*
-keepclassmembers class * {
    @androidx.room.* <methods>;
    @androidx.room.* <fields>;
}
-keep class * implements kotlinx.serialization.KSerializer { *; }
-keepclassmembers class * {
    @kotlinx.serialization.Serializable <fields>;
}
