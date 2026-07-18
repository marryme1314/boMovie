## 阿博电影 (boMovie)

一款基于猫眼电影接口的 Android 电影资讯 App，采用传统 Activity + XML + Fragment 架构开发。

### 技术栈

- **语言**: Java
- **网络请求**: OkHttp
- **数据解析**: Gson
- **图片加载**: Glide
- **架构**: Activity + Fragment + RecyclerView

### 接口来源

[猫眼电影 API](https://apis.netstart.cn/maoyan/)

### 功能特性

1. **正在热映** - 获取当前院线热映电影列表，展示海报、片名、评分
2. **即将上映** - 查看即将上映的电影及上映日期
3. **电影详情** - 查看电影详细信息，包括海报、评分、简介、演职人员等
4. **城市切换** - 支持切换当前城市，热门城市快速选择，支持按名称/拼音搜索
5. **最受期待** - 横向卡片展示最受期待的电影，包含想看人数和上映日期
6. **电影搜索** - 支持关键词搜索电影，输入时实时显示搜索建议，展示海报、评分、类型等信息

### 项目结构

```
boMovie-main/
├── app/src/main/
│   ├── java/com/biubiupapa/movie/
│   │   ├── MainActivity.java          # 主页面（热映、即将上映、最受期待）
│   │   ├── DetailActivity.java        # 电影详情页
│   │   ├── CityPickerActivity.java    # 城市选择页
│   │   ├── SearchActivity.java        # 电影搜索页
│   │   ├── adapter/                   # 适配器
│   │   │   ├── MovieAdapter.java
│   │   │   ├── BannerAdapter.java
│   │   │   ├── ExpectedAdapter.java
│   │   │   └── SearchResultAdapter.java
│   │   ├── model/                     # 数据模型
│   │   │   ├── Movie.java
│   │   │   ├── City.java
│   │   │   ├── CityResponse.java
│   │   │   ├── MovieListResponse.java
│   │   │   ├── MostExpectedResponse.java
│   │   │   ├── SearchSuggestItem.java
│   │   │   └── SearchMovieResponse.java
│   │   └── util/
│   │       └── ApiService.java        # API 接口定义
│   └── res/layout/                    # 布局文件
├── build.gradle
└── settings.gradle
```

### 编译与运行

使用 Android Studio 打开项目，配置 JDK 21，连接设备或模拟器后直接运行。

```bash
# 或通过命令行编译
./gradlew assembleDebug

# 安装到设备
adb install app/build/outputs/apk/debug/app-debug.apk
```

### 效果展示

##### 主页效果
![主页](img.png)

##### 详情页展示