## 阿博电影
本项目采用从猫眼电影获取接口的方式来实现获取电影列表
* 接口地址https://apis.netstart.cn/maoyan/
* 通过远程调用来进行详情页的展示以及获取电影信息
### 具体功能
1. 及时获取最新的电影动态
2. 查看大众对其的评价
3. 查看评分

## 使用Android Studio编译

### 执行Android Studio上Build APK的操作, 然后将apk推送到设备上所在的目录
```agsl
adb push F:/androidprojects/AndroidStudioProjects/maoyan/app/build/outputs/apk/debug/app-debug.apk 
```
或者
```agsl
adb install app-debug.apk
```
两种方法都可以
### 效果展示
![img_1.png](img.png)
##### 主页效果
![img_1.png](img_1.png)
##### 详情页展示
