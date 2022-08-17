-keep public class * implements com.bumptech.glide.module.GlideModule
-keep class * extends com.bumptech.glide.module.AppGlideModule {
 <init>(...);
}
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
-keep class com.bumptech.glide.load.data.ParcelFileDescriptorRewinder$InternalRewinder {
  *** rewind();
}

-keep class org.xmlpull.** { *; }
-keep class org.jivesoftware.** { *; }
-keep class org.joda.** { *; }
-keep class org.jxmpp.** { *; }

-keepnames class org.jxmpp.** { *; }
-keepnames class org.joda.** { *; }
-keepnames class org.jivesoftware.** { *; }
-keepnames class org.xmlpull.** { *; }
# keep everything in this package
# -keep class org.** { *; }
# -keepnames class org.** { *; }
# -keep interface org.joda.time.** { *;}
# -dontwarn org.joda.convert.**
# -dontwarn org.joda.time.**
# -keep class org.joda.time.** { *; }
# This is generated automatically by the Android Gradle plugin.
-dontwarn javax.naming.NamingEnumeration
-dontwarn javax.naming.NamingException
-dontwarn javax.naming.directory.Attribute
-dontwarn javax.naming.directory.Attributes
-dontwarn javax.naming.directory.DirContext
-dontwarn javax.naming.directory.InitialDirContext
-dontwarn javax.naming.directory.SearchControls
-dontwarn javax.naming.directory.SearchResult
-dontwarn org.bouncycastle.jsse.BCSSLParameters
-dontwarn org.bouncycastle.jsse.BCSSLSocket
-dontwarn org.bouncycastle.jsse.provider.BouncyCastleJsseProvider
-dontwarn org.conscrypt.Conscrypt$Version
-dontwarn org.conscrypt.Conscrypt
-dontwarn org.conscrypt.ConscryptHostnameVerifier
-dontwarn org.openjsse.javax.net.ssl.SSLParameters
-dontwarn org.openjsse.javax.net.ssl.SSLSocket
-dontwarn org.openjsse.net.ssl.OpenJSSE
-dontwarn org.xmlpull.mxp1.MXParser,org.xmlpull.mxp1_serializer.MXSerializer
