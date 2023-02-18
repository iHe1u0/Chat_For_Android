-keep class org.joda.** { *; }
-keep class org.jxmpp.** { *; }

-keep class org.kobjects.** { *; }
-keep class org.ksoap2.** { *; }
-keep class org.kxml2.** { *; }

-keep class org.bouncycastle.jsse.BCSSLParameters
-keep class org.bouncycastle.jsse.BCSSLSocket
-keep class org.bouncycastle.jsse.provider.BouncyCastleJsseProvider
-keep class org.conscrypt.Conscrypt$Version
-keep class org.conscrypt.Conscrypt
-keep class org.conscrypt.ConscryptHostnameVerifier
-keep class org.joda.convert.FromString
-keep class org.joda.convert.ToString
-keep class org.openjsse.javax.net.ssl.SSLParameters
-keep class org.openjsse.javax.net.ssl.SSLSocket
-keep class org.openjsse.net.ssl.OpenJSSE

-dontwarn org.kobjects.**
-dontwarn org.ksoap2.**
-dontwarn org.kxml2.**
-dontwarn org.xmlpull.v1.**

-keepclasseswithmembernames class * {
    native <methods>;
}

-dontwarn org.jivesoftware.**
-keep class org.igniterealtime.jbosh.**{*;}
-keep class org.jivesoftware.**{*;}
-keep interface org.jivesoftware.** {*;}
-keepclasseswithmembers class de.measite.smack.** {*;}

-keepclasseswithmembers class * extends org.jivesoftware.smack.sasl.SASLMechanism {
	public <init>(org.jivesoftware.smack.SASLAuthentication);
}