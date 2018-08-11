# TinkerTest
演示如何使用腾讯的热修复框架-Tinker

## Tinker热更新演示（请star支持）

![](https://github.com/xuexiangjys/TinkerTest/blob/master/img/demo.gif)

## 演示demo下载

[![](https://img.shields.io/badge/演示apk-1M-blue.svg)](https://github.com/xuexiangjys/XUpdate/blob/master/apk/app-release.apk)

[![](https://img.shields.io/badge/补丁包-4K-blue.svg)](https://github.com/xuexiangjys/XUpdate/blob/master/apk/patch_signed_7zip.apk)


## Tinker简介

> Tinker是微信官方的Android热补丁解决方案，它支持动态下发代码、So库以及资源，让应用能够在不需要重新安装的情况下实现更新。当然，你也可以使用Tinker来更新你的插件。

## 相关链接

* [Tinker Github](https://github.com/Tencent/tinker)

* [TinkerPatch Github](https://github.com/TinkerPatch)

* [Tinker Platform](http://www.tinkerpatch.com/)


## Tinker已知问题

由于原理与系统限制，Tinker有以下已知问题:

* Tinker不支持修改AndroidManifest.xml，Tinker不支持新增四大组件(1.9.0支持新增非export的Activity)；
* 由于Google Play的开发者条款限制，不建议在GP渠道动态更新代码；
* 在Android N上，补丁对应用启动时间有轻微的影响；
* 不支持部分三星android-21机型，加载补丁时会主动抛出"TinkerRuntimeException:checkDexInstall failed"；
* 对于资源替换，不支持修改remoteView。例如transition动画，notification icon以及桌面图标。

官方说明请[点击查看](https://github.com/Tencent/tinker/wiki).

## Tinker接入

### 添加依赖

1. 在Project的根目录的build.gradle下添加`tinkerpatch`插件：

```

buildscript {
    ...
    dependencies {
        ...
        classpath "com.tinkerpatch.sdk:tinkerpatch-gradle-plugin:1.2.8"
    }
}
```

2. 在module的build.gradle下增加`Tinker`的依赖。

```
dependencies {
    implementation 'com.android.support:multidex:1.0.3'
    //若使用annotation需要单独引用,对于tinker的其他库都无需再引用
    annotationProcessor 'com.tinkerpatch.tinker:tinker-android-anno:1.9.8'
    compileOnly 'com.tinkerpatch.tinker:tinker-android-anno:1.9.8'
    implementation 'com.tinkerpatch.sdk:tinkerpatch-android-sdk:1.2.8'
}
```

3. 配置代码混淆和打包配置。其中tinkerMultidexKeep.pro和proguardRules.pro可参考我的demo工程。

```
android {
    compileSdkVersion 27

    defaultConfig {
        applicationId "com.xuexiang.tinkertest"
        minSdkVersion 14
        targetSdkVersion 27
        versionCode 1
        versionName "1.0"

        multiDexEnabled true
        multiDexKeepProguard file("tinkerMultidexKeep.pro") //keep specific classes using proguard syntax

    }

    signingConfigs {
        release {
            //配置你的storekey
            storeFile file(app_release.storeFile)
            storePassword app_release.storePassword
            keyAlias app_release.keyAlias
            keyPassword app_release.keyPassword
        }
    }

    buildTypes {
        release {
            minifyEnabled true
            shrinkResources true
            signingConfig signingConfigs.release
            proguardFiles 'proguardRules.pro', getDefaultProguardFile('proguard-android.txt')
        }
        debug {
            debuggable true
            minifyEnabled false
        }
    }

    sourceSets {
        main {
            jniLibs.srcDirs = ['libs']
        }
    }
}
```

4. 引用`tinkerpatch.gradle`执行脚本.

```
apply from: 'tinkerpatch.gradle'
```

5. 配置`tinkerpatch.gradle`执行脚本.对于脚本的详细说明请参考[官方文档](http://www.tinkerpatch.com/Docs/SDK)。

这里我只想说几个比较关键的配置：

* bakPath: 这里存放的是每次我们打包生成的apk目录，作为生成补丁包的参考包路径。（一般配置了就不用动了）

* baseInfo: 这里设置的是，本次打补丁包参照的apk包所在文件夹路径，也就是旧APK的目录。（每次打补丁包的时候，都需要手动去更新）

* variantName: 设置打补丁包的类型是release还是debug。

* AppKey: 这是从[Tinker Platform](http://www.tinkerpatch.com/)上注册获得的应用的appkey。

* AppVersion: 这也是在[Tinker Platform](http://www.tinkerpatch.com/)上，每次上传补丁包时都需要填写的应用版本，并且必须是唯一的。

【注意】：AppKey和AppVersion都是用于[Tinker Platform](http://www.tinkerpatch.com/)自定发布补丁包所需要的。如果你不使用Tinker Platform来管理你的热更新的话，可以随便设置。

以下是`tinkerpatch.gradle`的配置样例：

```
apply plugin: 'tinkerpatch-support'

/**
 * TODO: 请按自己的需求修改为适应自己工程的参数
 */
def bakPath = file("${buildDir}/bakApk/")
/** 每次在打补丁包的时候，需要更新这里的旧包的位置  **/
def baseInfo = "app-1.0.0-0810-17-28-31"
def variantName = "release"

def AppKey = "4c118de195c79b14"
def AppVersion = "1.0.0"

/**
 * 对于插件各参数的详细解析请参考
 * http://tinkerpatch.com/Docs/SDK
 */
tinkerpatchSupport {

    /** 可以在debug的时候关闭 tinkerPatch **/
    /** 当disable tinker的时候需要添加multiDexKeepProguard和proguardFiles,
        这些配置文件本身由tinkerPatch的插件自动添加，当你disable后需要手动添加
        你可以copy本示例中的proguardRules.pro和tinkerMultidexKeep.pro,
        需要你手动修改'tinker.sample.android.app'本示例的包名为你自己的包名, com.xxx前缀的包名不用修改
     **/
    tinkerEnable = true

    /** 是否使用一键接入功能  **/

    reflectApplication = true
    /**
     * 是否开启加固模式，只能在APK将要进行加固时使用，否则会patch失败。
     * 如果只在某个渠道使用了加固，可使用多flavors配置
     **/
    protectedApp = false
    /**
     * 实验功能
     * 补丁是否支持新增 Activity (新增Activity的exported属性必须为false)
     **/
    supportComponent = true

    /** 在tinkerpatch.com得到的appKey,改成你的应用appKey **/

    appKey = "${AppKey}"

    /** 注意: 若发布新的全量包, appVersion一定要更新 **/
    appVersion = "${AppVersion}"

    autoBackupApkPath = "${bakPath}"

    def pathPrefix = "${bakPath}/${baseInfo}/${variantName}/"
    def name = "${project.name}-${variantName}"

    baseApkFile = "${pathPrefix}/${name}.apk"
    baseProguardMappingFile = "${pathPrefix}/${name}-mapping.txt"
    baseResourceRFile = "${pathPrefix}/${name}-R.txt"

    /**
     *  若有编译多flavors需求, 可以参照： https://github.com/TinkerPatch/tinkerpatch-flavors-sample
     *  注意: 除非你不同的flavor代码是不一样的,不然建议采用zip comment或者文件方式生成渠道信息（相关工具：walle 或者 packer-ng）
     **/
}

/**
 * 用于用户在代码中判断tinkerPatch是否被使能
 */
android {
    defaultConfig {
        buildConfigField "boolean", "TINKER_ENABLE", "${tinkerpatchSupport.tinkerEnable}"
    }
}

/**
 * 一般来说,我们无需对下面的参数做任何的修改
 * 对于各参数的详细介绍请参考:
 * https://github.com/Tencent/tinker/wiki/Tinker-%E6%8E%A5%E5%85%A5%E6%8C%87%E5%8D%97
 */
tinkerPatch {
    ignoreWarning = false
    useSign = true
    dex {
        dexMode = "jar"
        pattern = ["classes*.dex"]
        loader = []
    }
    lib {
        pattern = ["lib/*/*.so"]
    }

    res {
        pattern = ["res/*", "r/*", "assets/*", "resources.arsc", "AndroidManifest.xml"]
        ignoreChange = []
        largeModSize = 100
    }

    packageConfig {
    }
    sevenZip {
        zipArtifact = "com.tencent.mm:SevenZip:1.1.10"
//        path = "/usr/local/bin/7za"
    }
    buildConfig {
        keepDexApply = false
    }
}

```

6. 最后就是配置Application了。因为我在上面设置`reflectApplication = true`使用了一键接入功能，所以就不需要进行复杂的配置了，如下:

```
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // 我们可以从这里获得Tinker加载过程的信息
        ApplicationLike tinkerApplicationLike = TinkerPatchApplicationLike.getTinkerPatchApplicationLike();
        // 初始化TinkerPatch SDK, 更多配置可参照API章节中的,初始化SDK
        TinkerPatch.init(tinkerApplicationLike)
                .reflectPatchLibrary()
                .setPatchRollbackOnScreenOff(true)
                .setPatchRestartOnSrceenOff(true)
                .setPatchResultCallback(new ResultCallBack() {
                    @Override
                    public void onPatchResult(PatchResult patchResult) {
                        ToastUtils.toast("补丁修复:" + (patchResult.isSuccess ? "成功" : "失败"));
                    }
                })
                .setFetchPatchIntervalByHours(3);
        // 每隔3个小时(通过setFetchPatchIntervalByHours设置)去访问后台时候有更新,通过handler实现轮训的效果
        TinkerPatch.with().fetchPatchUpdateAndPollWithInterval();
    }

}
```

更多复杂的配置和高端自定义操作可参见[官方文档](http://www.tinkerpatch.com/Docs/api)。

以上就完成了Tinker的接入工作。

## 如何使用Tinker进行热修复

1. 先随便在程序中写一个bug，然后执行`./gradlew assembleRelease`进行打包。当然你也可以直接在AS右侧的`Gradle`中找到你的应用，并在`Tasks->build->assembleRelease`找到assembleRelease的任务，双击执行任务。

![](https://github.com/xuexiangjys/TinkerTest/blob/master/img/1.png)

执行完成后，你会在你模块的`build->bakApk`下看到你打的apk包。

![](https://github.com/xuexiangjys/TinkerTest/blob/master/img/2.png)

2. 你将刚才生成apk的那个文件夹的名称设置在之前说的`tinkerpatch.gradle`中的`baseInfo`。

3. 将bug修复后，执行`./gradlew tinkerPatchRelease`打补丁包。当然你也可以直接在AS右侧的`Gradle`中找到你的应用，并在`Tasks->tinker->tinkerPatchRelease`找到tinkerPatchRelease的任务，双击执行任务。

![](https://github.com/xuexiangjys/TinkerTest/blob/master/img/3.png)

执行完成后，你会在你模块的`build->outputs->apk->tinkerPatch->release`下看到你需要的补丁包`patch_signed_7zip.apk`。

![](https://github.com/xuexiangjys/TinkerTest/blob/master/img/4.png)

4. 最后调用`TinkerInstaller.onReceiveUpgradePatch(getApplicationContext(), path);`, path传入你补丁包的所在的路径即可完成热更新。

需要注意的是，执行热更新后，需要重启程序才能生效！


## 联系方式

[![](https://img.shields.io/badge/点击一键加入QQ交流群-602082750-blue.svg)](http://shang.qq.com/wpa/qunwpa?idkey=9922861ef85c19f1575aecea0e8680f60d9386080a97ed310c971ae074998887)

![](https://github.com/xuexiangjys/XPage/blob/master/img/qq_group.jpg)


