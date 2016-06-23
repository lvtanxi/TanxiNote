# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\DevProgram\sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

#指定代码的压缩级别
-optimizationpasses 5
#包名不混合大小写
-dontusemixedcaseclassnames
#不去忽略非公共的库类
-dontskipnonpubliclibraryclasses
#不优化输入的类文件
-dontoptimize
#不执行预校验
-dontpreverify
#混淆时 输出生成信息
-verbose
#混淆时所采用的算法
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
#保护注解
-keepattributes *Annotation*
-keepattributes Signature
# 保持哪些类不被混淆
-keep class * extends android.app.Application
-keep class * extends android.app.Activity {*;}
-keep class * extends android.app.Fragment {*;}
-keep class android.support.design.** { *;}
-keep class android.support.v4 { *; }
-keep class android.support.v7 { *; }
-keep class * extends android.content.BroadcastReceiver
-keep class * extends android.content.ContentProvider
-keep class * extends android.app.backup.BackupAgentHelper
-keep class * extends android.preference.Preference
-keep class org.springframework.** { *; }
-keep class org.xmlpull.v1.** { *; }
#忽略警告
-ignorewarning

##记录生成的日志数据,gradle build时在本项目根目录输出##

#apk 包内所有 class 的内部结构
-dump class_files.txt
#未混淆的类和成员
-printseeds seeds.txt
#列出从 apk 中删除的代码
-printusage unused.txt
#混淆前后的映射
-printmapping mapping.txt

########记录生成的日志数据，gradle build时 在本项目根目录输出-end######
-libraryjars src/main/jniLibs/armeabi/libsdk_patcher_jni.so

#如果不想混淆 keep 掉
#保留一个完整的包
-keep class org.springframework.**{*; }
-dontwarn org.springframework.**

 # Gson specific classes
-keep class sun.misc.Unsafe { *; }
    # Application classes that will be serialized/deserialized over Gson
-keep class com.google.gson.examples.android.model.** { *; }


-keep class vi.com.gdi.bgl.android.**{*;}
-keep class sun.misc.Unsafe{ *;}
# Application classes that will be serialized/deserialized over Gson
#-keep class com.google.gson.examples.android.model.**{ *; }
-keepattributes *Annotation*
-keepattributes *JavascriptInterface*
-keep class android.webkit.JavascriptInterface {*;}

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
-keepclassmembers class * extends org.litepal.crud.DataSupport{*;}
#如果引用了v4或者v7包
-dontwarn android.support.**

# modify 修改合并
-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
    public void set*(...);
}

#保持 native 方法不被混淆
-keepclasseswithmembernames class * {
    native <methods>;
}

#保持自定义控件类不被混淆
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

#保持自定义控件类不被混淆
-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}

#保持 Parcelable 不被混淆
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

#保持 Serializable 不被混淆
-keepnames class * implements java.io.Serializable
-keepnames class * implements Response.Listener<JSONObject>
-keepnames class * extends JsonObjectRequest
-keepnames class * implements android.os.Parcelable


#保持 Serializable 不被混淆并且enum 类也不被混淆
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    !private <fields>;
    !private <methods>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

#保持枚举 enum 类不被混淆 如果混淆报错，建议直接使用上面的 -keepclassmembers class * implements java.io.Serializable即可
#-keepclassmembers enum * {
#  public static **[] values();
#  public static ** valueOf(java.lang.String);
#}

-keepclassmembers class * {
    public void *ButtonClicked(android.view.View);
}

#过滤R文件的混淆：
-keep class **.R$* {
 *;
}

####================================================================================================================####

#okhttputils
-dontwarn com.zhy.http.**
-keep class com.zhy.http.**{*;}
-keep interface com.zhy.http.**{*;}
-dontwarn com.tencent.bugly.**
-keep public class com.tencent.bugly.**{*;}

#okhttp
-dontwarn okhttp*.**
-keep class okhttp*.**{*;}
-keep interface okhttp*.**{*;}

#okio
-dontwarn okio.**
-keep class okio.**{*;}
-keep interface okio.**{*;}

#Gson
-dontwarn com.google.**
-keep class com.google.gson.** { *; }
-keepattributes Signature

#hawk
-dontwarn com.github.orhanobut.**
-keep class com.orhanobut.hawk.** { *; }

# 不混淆BmobSDK
-dontwarn com.github.orhanobut.**
-keep class cn.bmob.v3.** {*;}

# 保证继承自BmobObject、BmobUser类的JavaBean不被混淆
-keep class * extends cn.bmob.v3.BmobObject {
    *;
}

#protobuf
-dontwarn com.google.**
-keep class com.google.protobuf.** {*;}

# 如果你需要兼容6.0系统，请不要混淆org.apache.http.legacy.jar
 -dontwarn android.net.compatibility.**
 -dontwarn android.net.http.**
 -dontwarn com.android.internal.http.multipart.**
 -dontwarn org.apache.commons.**
 -dontwarn org.apache.http.**
 -keep class android.net.compatibility.**{*;}
 -keep class android.net.http.**{*;}
 -keep class com.android.internal.http.multipart.**{*;}
 -keep class org.apache.commons.**{*;}
 -keep class org.apache.http.**{*;}

  -keep class com.lv.note.entity.**{*;}

 -keep class com.xiaosu.**{*;}

 -keep class com.dalong.coverflow.**{*;}

-dontoptimize
-dontpreverify


