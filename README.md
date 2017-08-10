## 关于微信支付和支付宝支付的几点注意：

####1: 打包release使用的Key store path必须是同一个。否则最终release打包的apk不是同一个。

####2，微信官网中对微信支付的功能要求生成一个应用签名，生成签名的方法是：第一步，正式打包

####apk，安装到手机。第三步，下载微信指定工具（生成应用签名的专用apk），并安卓到同一个手

####机上。第四步，打开应用签名生成工具输入对应应用的包名，生成对应地应用签名。

####&nbsp; &nbsp; &nbsp; &nbsp; 一旦生成，那么，这个应用签名与release打包的Key（即打包release时使用的jks）唯一对

####应。此时的jks一定要保存好，不得修改或丢失。一旦丢失，就要重新release打包，重新生成应用

####签名。很麻烦。

####3，因为微信支付是需要release打包的apk才能进行支付或调试，那么每次都要用对应的jks重新生

####成apk再进行安装调试相当麻烦。我们可以在build.gradle文件中对jks进行配置。那么每次我们用

####debug直接进行调试，也是可行的。下面是配置方法（module级别下的build.gradle文件）

```javascript
android {
    compileSdkVersion 24
    buildToolsVersion "24.0.3"
    defaultConfig {
        applicationId "com.fujisoft.campaign"
        minSdkVersion 19
        targetSdkVersion 19
        versionCode 1
        versionName "1.0"
        testInstrumentationRunner "android.support.test.runner.AndroidJUnitRunner"
        manifestPlaceholders = [
                GETUI_APP_ID    : "AeBGRs8MP98mawgJBkDkL",
                GETUI_APP_KEY   : "d25m6oqWTG7q8VYHTkzwx4",
                GETUI_APP_SECRET: "3uJSDbAG3D8IDAKQ8NbYJ2"
        ]
    }
    lintOptions {
        checkReleaseBuilds false
        abortOnError false
    }
    //signingConfigs节点全部复制过去修改一下
    signingConfigs {
        debugConfig {
            keyAlias 'diancan'//每次生成jks是都会自己填写一个Key alias,在这里替换一下就行
            keyPassword '123456'//密码
            storeFile file('C:/Users/860617010/Desktop/diancan/diancan.jks')//生成的jks对应的绝对路径，生成jks时可以自己指定路径。
            storePassword '123456'//密码
        }
    }
    buildTypes {
        //这个debug小节点也要复制到对应的位置。
        debug {
            signingConfig signingConfigs.debugConfig
        }
        release {
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'
        }
    }
}
```
