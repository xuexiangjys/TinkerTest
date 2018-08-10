
### 打包APK

#### 修改属性

将根目录下的`gradle.properties`的`isNeedPackage`属性置为true.

```
# 是否打包APK
isNeedPackage = true
```

#### 执行命令

```
./gradlew assembleRelease
```