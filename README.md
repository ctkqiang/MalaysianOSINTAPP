# 马来西亚OSINT — 开源情报采集工具

马来西亚开源情报（OSINT）移动端应用，基于 Kotlin Jetpack Compose 构建，采用 MVVM 架构，集成多源马来西亚政府公开数据查询能力。

## 技术架构

| 层级 | 技术栈 |
|------|--------|
| UI框架 | Jetpack Compose + Material3 |
| 架构模式 | MVVM（Model-View-ViewModel） |
| 网络引擎 | Retrofit2 + OkHttp4 |
| HTML解析 | Jsoup |
| 状态管理 | Kotlin StateFlow + ViewModel |
| 持久化存储 | DataStore Preferences |
| 异步处理 | Kotlin Coroutines |
| 构建系统 | Gradle 9.x + AGP 8.7 + Kotlin 2.0 |

## 功能模块

### 1. 反诈骗查询（PDRM Semak Mule）
- 查询马来西亚皇家警察(PDRM)反诈骗数据库
- 支持电话号码、银行账号检索
- API端点: `semakmule.rmp.gov.my`

### 2. 身份证综合查询
- **SSPI移民局**: 查询身份证在移民局系统中的状态
- **MyKad解析**: 本地解析12位身份证号码，提取出生日期、州属、唯一标识符
- **PDRM通缉名单**: 比对皇家警察通缉人员数据库
- **SPRM反贪会**: 查询反贪污委员会腐败罪犯记录

### 3. 企业信息查询
- **SSM注册号解析**: 解析马来西亚公司委员会(SSM)12位注册号，识别实体类型
- **企业黄页搜索**: MalaysiaYP商业目录检索
- 联动反诈骗数据库交叉验证

### 4. 社交媒体足迹
- 跨平台用户名枚举，覆盖60+个主流社交平台
- 支持全球社交网络、开发者社区、论坛等
- 实时流式返回匹配结果

### 5. 电子法庭查询
- 搜索马来西亚联邦法院电子判决系统(e-Court)
- 支持姓名、案件关键词检索
- 自动重试机制(最多3次，间隔3秒)

### 6. 国行消费者警示
- 获取马来西亚国家银行(BNM)金融消费者警示名单
- 识别未经授权的金融实体和诈骗平台

## 数据来源

| 编号 | 数据源 | 机构 |
|------|--------|------|
| 1 | PDRM Semak Mule | 马来西亚皇家警察 |
| 2 | SSPI 移民局 | 马来西亚移民局 |
| 3 | PDRM 通缉名单 | 马来西亚皇家警察 |
| 4 | SPRM 反贪会 | 马来西亚反贪污委员会 |
| 5 | e-Court 电子法庭 | 马来西亚联邦法院 |
| 6 | MalaysiaYP 黄页 | 马来西亚商业目录 |
| 7 | BNM 消费者警示 | 马来西亚国家银行 |
| 8 | 社交媒体枚举 | 60+全球平台 |

## 项目结构

```
app/src/main/java/com/osint/malaysia/
├── MainActivity.kt              # 主Activity入口
├── OSINTApplication.kt           # Application初始化
├── model/
│   └── OSINTModels.kt            # 数据模型 & 枚举定义
├── data/
│   ├── api/
│   │   ├── ApiClient.kt          # OkHttp/Retrofit网络客户端
│   │   ├── OSINTService.kt       # Retrofit API接口定义
│   └── repository/
│       └── OSINTRepository.kt    # 核心OSINT引擎(数据仓库)
├── viewmodel/
│   ├── MainViewModel.kt          # 主业务逻辑视图模型
│   └── SettingsViewModel.kt      # 设置持久化视图模型
├── util/
│   ├── LogUtil.kt                # 日志工具
│   ├── MyKadParser.kt            # 身份证本地解析器
│   ├── SSMParser.kt              # SSM注册号解析器
│   └── SocialPlatforms.kt        # 社交媒体平台定义
└── ui/
    ├── theme/
    │   └── Theme.kt              # 警蓝主题系统(色彩/排版/主题)
    ├── components/
    │   └── Components.kt         # 可复用UI组件库
    ├── screens/
    │   ├── HomeScreen.kt         # 首页(反诈骗+BNM)
    │   ├── IDCheckScreen.kt      # 身份证查询
    │   ├── CompanyScreen.kt      # 企业查询
    │   ├── SocialScreen.kt       # 社交媒体搜索
    │   ├── CourtScreen.kt        # 电子法庭查询
    │   └── SettingsScreen.kt     # 设置页面
    └── navigation/
        └── Navigation.kt         # 底部导航+路由图
```

## 构建与运行

### 环境要求
- Android Studio Hedgehog (2023.1) 或更高版本
- JDK 17+
- Android SDK 35
- Gradle 9.5.1+

### 构建命令

```bash
# 调试构建
./gradlew assembleDebug

# 安装到设备
./gradlew installDebug

# 发布构建(混淆)
./gradlew assembleRelease

# 类型检查
./gradlew compileDebugKotlin
```

### 运行项目

1. 使用 Android Studio 打开项目根目录
2. 等待 Gradle 同步完成
3. 选择目标设备或模拟器(API 26+)
4. 点击运行或执行 `./gradlew installDebug`

## 配色主题

| 色阶 | 色值 | 用途 |
|------|------|------|
| Navy 950 | `#0A1628` | 主背景 |
| Navy 900 | `#0D1F3C` | 表面/卡片背景 |
| Navy 800 | `#122A52` | 表面变体 |
| Navy 700 | `#1A3A6E` | 边框/分隔线 |
| Navy 600 | `#244B8A` | 主按钮 |
| Navy 500 | `#2E5CA6` | 次要元素 |
| Navy 400 | `#3D74C9` | 主色调/强调 |
| Navy 300 | `#6B95DD` | 辅助文本 |
| Navy 200 | `#9AB8EE` | 浅色强调 |
| Navy 100 | `#C5D6F5` | 浅色文本 |
| Navy 50  | `#E8EEF9` | 最亮文本 |

## API认证密钥

本应用使用的API密钥来源于公开可获得的信息:

- **PDRM Semak Mule**: `apikey: j3j389#nklala2` (内嵌于PDRM官方网站前端代码)
- **SSPI 移民局**: 无认证密钥（表单提交）
- **e-Court**: 无认证密钥（公开JSON接口）
- **其他端点**: 均为公开访问

## 设计原则

1. **纯客户端架构**: 所有API请求直接从设备发出，不经过中间服务器
2. **最小依赖**: 仅引入必要的开源库，避免依赖膨胀
3. **安全合规**: 仅查询公开可访问的数据接口，遵循马来西亚法律法规
4. **资源优化**: SSL证书校验经配置允许兼容旧版政府网站证书
5. **日志透明**: 全中文日志系统，记录所有网络请求与响应

## 免责声明

本工具仅供安全研究、授权测试及合法信息检索使用。使用者应:

- 遵守马来西亚《个人数据保护法》(PDPA 2010)
- 遵守马来西亚《电脑犯罪法》(Computer Crimes Act 1997)
- 仅在获得适当授权的情况下使用本工具
- 对自身的使用行为承担全部法律责任

## 作者信息

| 项目 | 信息 |
|------|------|
| 作者 | 钟智强 |
| 邮箱 | ctkqiang@dingtalk.com |
| 仓库 | https://gitcode.com/ctkqiang_sr/MalaysianOSINTAPP.git |
| 参考项目 | https://github.com/ctkqiang/MalaysianOSINT (C语言原始实现) |
| 版本 | 1.0.0 |

## 许可证

本项目遵循开源许可协议，详见 LICENSE 文件。
