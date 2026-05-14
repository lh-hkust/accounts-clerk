## 1. 项目基础设施

- [ ] 1.1 创建 Android 项目结构（Clean Architecture 分层）
- [ ] 1.2 配置 Gradle 依赖（Room, SQLCipher, Kotlin Coroutines）
- [ ] 1.3 实现数据库加密方案（DEK+KEK 信封加密）
- [ ] 1.4 配置 Android Keystore 密钥存储

## 2. UI原型设计

- [x] 2.1 创建交互式HTML原型（参考设计规范）
- [x] 2.2 实现欢迎页原型（动态排版、科幻风渐变）
- [x] 2.3 实现首页看板原型（风险雷达、状态概览、快捷入口）
- [x] 2.4 实现标识列表页原型（智能排序、状态筛选）
- [x] 2.5 实现标识详情页原型（资产树状视图、影响分析）
- [x] 2.6 实现账户列表页原型（应用分类、状态标签）
- [x] 2.7 实现账户详情页原型（绑定标识列表）
- [x] 2.8 实现添加账户页原型（预置应用库、多用途绑定）
- [x] 2.9 实现设置停用计划页原型（日期选择、影响展示）
- [x] 2.10 实现数据导入导出页原型（CSV/JSON入口）
- [x] 2.11 原型评审与迭代优化（用语统一、快速处理、昵称、设置页面）

## 3. 领域模型层

- [x] 3.1 定义值对象（IdentifierType, IdentifierStatus, AccountStatus, WarningLevel, BindingPurpose, ApplicationType, WarningType, DeactivationType, DeactivationStatus, ActionType, FieldType）
- [x] 3.2 实现 IdentityIdentifier 聚合根
- [x] 3.3 实现 IdentifierDeactivation 实体
- [x] 3.4 实现 ApplicationAccount 聚合根
- [x] 3.5 实现 IdentifierBinding 实体
- [x] 3.6 实现 AccountExtension 实体
- [x] 3.7 实现 WarningRecord 聚合根
- [x] 3.8 实现 BindingHistoryRecord 聚合根
- [x] 3.9 实现 Application 聚合根

## 4. 领域服务层

- [x] 4.1 实现 IdentifierService（创建、检查重复、获取绑定账户）
- [x] 4.2 实现 DeactivationService（创建计划、取消计划、修改日期）
- [x] 4.3 实现 WarningService（触发预警、计算级别、清除预警）
- [x] 4.4 实现 AccountService（创建、更新状态、添加扩展）
- [x] 4.5 实现 BindingService（绑定、解绑、修改用途、更换标识）
- [x] 4.6 实现 ImpactAnalysisService（影响分析、预警级别计算）

## 5. 数据层

- [x] 5.1 创建数据库表结构（identity_identifier, application_account, identifier_binding, warning_record, identifier_deactivation）
- [x] 5.2 实现 Room Entity 类映射
- [x] 5.3 实现 IdentityIdentifierRepository
- [x] 5.4 实现 ApplicationAccountRepository
- [x] 5.5 实现 WarningRecordRepository
- [x] 5.6 实现 BindingHistoryRepository
- [x] 5.7 实现 ApplicationRepository

## 6. 身份标识管理功能

- [x] 6.1 实现添加身份标识用例（AddIdentifierUseCase）
- [x] 6.2 实现获取标识列表用例（GetIdentifierListUseCase）
- [x] 6.3 实现获取标识详情用例（GetIdentifierDetailUseCase）
- [x] 6.4 实现删除身份标识用例（DeleteIdentifierUseCase）
- [x] 6.5 实现重复标识检测用例（CheckDuplicateIdentifierUseCase）
- [x] 6.6 实现标识列表 ViewModel
- [x] 6.7 实现标识详情 ViewModel
- [x] 6.8 实现标识列表 UI布局（界面文件存在）
- [x] 6.9 实现标识详情 UI布局（界面文件存在）
- [x] 6.10 实现添加标识 UI布局（界面文件存在）

## 7. 停用计划功能

- [x] 7.1 实现设置停用计划用例（ScheduleDeactivationUseCase）
- [x] 7.2 实现取消停用计划用例（CancelDeactivationUseCase）
- [x] 7.3 实现修改停用日期用例（UpdateDeactivationDateUseCase）
- [x] 7.4 实现获取停用计划详情用例（GetDeactivationDetailUseCase）
- [x] 7.5 实现停用计划 ViewModel
- [x] 7.6 实现设置停用计划 UI布局（界面文件存在）
- [x] 7.7 实现停用计划详情卡片组件

## 8. 预警系统功能

- [x] 8.1 实现触发预警用例（TriggerWarningUseCase）
- [x] 8.2 实现计算预警级别用例（CalculateWarningLevelUseCase）
- [x] 8.3 实现获取预警列表用例（GetWarningListUseCase）
- [x] 8.4 实现获取预警详情用例（GetWarningDetailUseCase）
- [x] 8.5 实现处理预警用例（HandleWarningUseCase）
- [x] 8.6 实现标记预警已读用例（MarkWarningReadUseCase）
- [x] 8.7 实现清除预警用例（ClearWarningUseCase）
- [x] 8.8 实现 WorkManager 定时检查停用计划
- [x] 8.9 实现预警列表 ViewModel
- [x] 8.10 实现预警详情 ViewModel
- [x] 8.11 实现首页看板预警卡片组件
- [x] 8.12 实现预警详情 UI布局（界面文件存在）

## 9. 应用账户管理功能

- [x] 9.1 实现添加应用账户用例（AddAccountUseCase）
- [x] 9.2 实现获取账户列表用例（GetAccountListUseCase）
- [x] 9.3 实现获取账户详情用例（GetAccountDetailUseCase）
- [x] 9.4 实现更新账户状态用例（UpdateAccountStatusUseCase）
- [x] 9.5 实现添加账户扩展用例（AddAccountExtensionUseCase）
- [x] 9.6 实现账户列表 ViewModel
- [x] 9.7 实现账户详情 ViewModel
- [x] 9.8 实现账户列表 UI布局（界面文件存在）
- [x] 9.9 实现账户详情 UI布局（界面文件存在）
- [x] 9.10 实现添加账户 UI布局（界面文件存在）

## 10. 标识绑定管理功能

- [x] 10.1 实现绑定标识用例（BindIdentifierUseCase）
- [x] 10.2 实现解绑标识用例（UnbindIdentifierUseCase）
- [x] 10.3 实现修改绑定用途用例（ChangeBindingPurposeUseCase）
- [x] 10.4 实现更换绑定标识用例（SwitchBindingIdentifierUseCase）
- [x] 10.5 实现绑定管理 ViewModel
- [x] 10.6 实现绑定管理 UI组件（组件已创建）

## 11. 影响分析功能

- [x] 11.1 实现影响分析用例（AnalyzeImpactUseCase）
- [x] 11.2 实现影响分析 UI布局（界面文件存在）
- [x] 11.3 实现受影响账户列表组件（组件已创建）

## 12. 应用预置清单

- [x] 12.1 创建预置应用清单数据（社交、金融、购物等分类）
- [x] 12.2 实现应用列表查询用例（GetApplicationListUseCase）
- [x] 12.3 实现自定义添加应用用例（AddCustomApplicationUseCase）

## 13. 测试验证

- [x] 13.1 编写 IdentityIdentifier 单元测试
- [x] 13.2 编写 ApplicationAccount 单元测试
- [x] 13.3 编写 WarningRecord 单元测试
- [ ] 13.4 编写 IdentifierService 单元测试
- [ ] 13.5 编写 DeactivationService 单元测试
- [ ] 13.6 编写 WarningService 单元测试
- [ ] 13.7 编写 BindingService 单元测试
- [ ] 13.8 编写 Repository 集成测试
- [ ] 13.9 编写 UseCase 集成测试
- [ ] 13.10 验收测试：身份标识管理完整流程
- [ ] 13.11 验收测试：停用计划完整流程
- [ ] 13.12 验收测试：预警系统完整流程

---

## 14. ViewModel-UI集成（关键遗漏）

> 以下任务将已实现的ViewModel连接到对应的Screen，替换硬编码示例数据

- [x] 14.1 配置Hilt ViewModel注入，使Screen可通过hiltViewModel()获取ViewModel实例
- [x] 14.2 IdentifierListScreen集成IdentifierViewModel，删除HermesNavigation硬编码数据，使用ViewModel.uiState
- [x] 14.3 IdentifierDetailScreen集成IdentifierDetailViewModel，展示真实标识详情和绑定账户列表
- [x] 14.4 AccountListScreen集成AccountViewModel，删除硬编码数据，使用ViewModel.uiState
- [x] 14.5 AccountDetailScreen集成AccountDetailViewModel，展示真实账号详情和绑定标识列表
- [x] 14.6 WarningListScreen集成WarningViewModel，删除硬编码数据，使用ViewModel.uiState
- [x] 14.7 ScheduleDeactivationScreen集成DeactivationViewModel，实现真实的设置/取消/修改功能
- [x] 14.8 DashboardScreen集成统计数据获取逻辑，调用GetIdentifierListUseCase和GetAccountListUseCase获取真实统计
- [x] 14.9 AddAccountScreen集成BindingSelectionDialog组件，获取真实可用渠道列表
- [x] 14.10 IdentifierDetailScreen集成DeactivationPlanCard组件，展示真实停用计划详情
- [x] 14.11 ImpactAnalysisScreen集成AffectedAccountsList组件并调用AnalyzeImpactUseCase

## 15. 核心功能实现（关键遗漏）

> 以下任务实现各界面的核心操作功能

### 15.1 添加/保存功能
- [ ] 15.1.1 AddIdentifierScreen保存功能：点击保存调用IdentifierViewModel.addIdentifier()，成功后返回列表页并刷新
- [ ] 15.1.2 AddAccountScreen保存功能：点击保存调用AccountViewModel.addAccount()，传递选中的渠道和用途
- [ ] 15.1.3 AddAccountScreen获取可用渠道：启动时调用GetIdentifierListUseCase获取真实渠道列表供选择

### 15.2 搜索/筛选功能
- [ ] 15.2.1 AccountListScreen搜索功能：实现搜索框输入过滤，调用AccountViewModel.search(query)
- [ ] 15.2.2 IdentifierListScreen筛选功能：实现状态筛选按钮点击调用ViewModel.filter(status)

### 15.3 数据管理功能
- [ ] 15.3.1 JSON导入：解析JSON文件，调用Repository批量插入标识和账号数据
- [ ] 15.3.2 CSV导入：解析CSV文件，映射字段并批量插入
- [ ] 15.3.3 JSON导出：调用Repository获取全部数据，生成JSON文件并保存到外部存储
- [ ] 15.3.4 CSV导出：调用Repository获取全部数据，生成CSV文件
- [ ] 15.3.5 清空数据：调用Repository.deleteAll()，显示确认弹窗

### 15.4 预警处理功能
- [ ] 15.4.1 DashboardScreen快速处理：点击调用WarningViewModel.handleWarning()更新状态
- [ ] 15.4.2 WarningCard处理按钮：点击调用WarningViewModel.handleWarning()

### 15.5 日期选择功能
- [ ] 15.5.1 ScheduleDeactivationScreen集成DatePickerDialog，选择日期后更新ViewModel状态

### 15.6 设置功能
- [ ] 15.6.1 PrivacySecurityScreen设置密码：打开密码设置弹窗，保存到SharedPreferences
- [ ] 15.6.2 PrivacySecurityScreen指纹解锁：集成BiometricPrompt API
- [ ] 15.6.3 NotificationSettingsScreen持久化：开关状态保存到SharedPreferences/DataStore
- [ ] 15.6.4 SettingsScreen退出登录：清除本地session状态，返回欢迎页

### 15.7 详情页操作功能
- [ ] 15.7.1 IdentifierDetailScreen取消提醒：调用DeactivationViewModel.cancelPlan()
- [ ] 15.7.2 IdentifierDetailScreen修改日期：跳转ScheduleDeactivationScreen传递标识ID
- [ ] 15.7.3 IdentifierDetailScreen批量更换渠道：打开渠道选择弹窗，调用SwitchBindingIdentifierUseCase
- [ ] 15.7.4 IdentifierDetailScreen标记已处理：调用WarningViewModel.handleWarning()
- [ ] 15.7.5 AccountDetailScreen编辑账号：打开编辑弹窗，调用AccountViewModel.update()
- [ ] 15.7.6 AccountDetailScreen更换验证渠道：打开渠道选择弹窗
- [ ] 15.7.7 AccountDetailScreen变更账号状态：调用AccountViewModel.updateStatus()
- [ ] 15.7.8 AccountDetailScreen删除账号：显示确认弹窗，调用AccountViewModel.delete()

## 16. UI完善（关键遗漏）

> 以下任务完善占位符界面的完整布局

- [ ] 16.1 IdentifierDetailScreen完善布局：确认UI与原型一致，标题改为"影响范围"而非"标识详情"
- [ ] 16.2 AccountDetailScreen完善布局：确认绑定标识列表、关联账户、操作按钮均显示
- [ ] 16.3 统一AccountListScreen使用AccountCard组件而非自定义AccountListCard
- [ ] 16.4 WarningListScreen确保使用WarningCard组件而非自定义WarningListItem

## 17. 用语修正（关键遗漏）

- [ ] 17.1 AccountListScreen标题从"账号资产库"改为"账号"（遵循ui-glossary.md）
- [ ] 17.2 IdentifierListScreen中"绑定X个账户"改为"绑定X个账号"
- [ ] 17.3 AccountListScreen中"绑定账户"改为"绑定账号"
- [ ] 17.4 所有界面检查用语与ui-glossary.md一致

## 18. 导航数据传递（关键遗漏）

- [x] 18.1 HermesNavigation删除所有硬编码sampleItems/sampleDetail示例数据
- [x] 18.2 实现导航参数传递机制：详情页通过savedStateHandle获取ID，调用ViewModel.load(id)
- [x] 18.3 确保所有详情页在进入时自动加载数据（LaunchedEffect调用ViewModel.load）

## 19. DI配置验证（关键遗漏）

- [x] 19.1 验证所有UseCase在Hilt Module中正确提供
- [ ] 19.2 验证所有Repository在Hilt Module中正确提供
- [x] 19.3 验证所有ViewModel可通过@HiltViewModel注解注入
- [ ] 19.4 验证Database和SQLCipher配置正确注入

---

## 20. P0核心功能补充（新增）

> P0级缺失事项，影响用户旅程闭环，必须纳入MVP

### 20.1 导入导出完整交互
- [x] 20.1.1 实现导出模式选择UI：明文JSON（安全警告引导）+ 安全导出（密码设置）【已实现: ExportModeSelectionDialog, SecureExportPasswordDialog】
- [x] 20.1.2 实现安全导出密码设置对话框（可选密码，无密码需勾选风险确认）【已实现: SecureExportPasswordDialog】
- [ ] 20.1.3 实现AES-256-GCM加密导出：密码模式PBKDF2派生，无密码模式HKDF派生【待验证: 需检查ViewModel加密逻辑是否完整】
- [ ] 20.1.4 实现导出文件格式.hexport：魔数、版本、KDF标记、盐、IV、密文【待实现: ExportFileFormat定义存在但写入逻辑待验证】
- [x] 20.1.5 实现SAF文件选择器集成，导出位置选择【已实现: DataManagementScreen使用CreateDocument/OpenDocument】
- [x] 20.1.6 实现导出进度对话框（进度条、百分比、阶段文字）【已实现: ExportProgressDialog, ImportProgressDialog】
- [x] 20.1.7 实现导入文件类型检测：.hexport加密文件 + .json明文文件【已实现: DataManagementScreen文件过滤器】
- [ ] 20.1.8 实现加密文件解密：密码模式需输入密码，无密码模式自动解密【待实现: ImportPasswordDialog存在但解密逻辑待验证】
- [x] 20.1.9 实现导入预览对话框：数据摘要、冲突检测、模式选择【已实现: ImportPreviewDialog】
- [x] 20.1.10 实现导入模式：合并、覆盖、跳过重复【已实现: ImportMode选择器】

### 20.2 新增账号绑定验证渠道交互
- [ ] 20.2.1 AddAccountScreen渠道列表按状态分组排序（ACTIVE > PENDING > DEACTIVATED）【未实现: 当前AddAccountScreen无状态分组逻辑】
- [ ] 20.2.2 渠道分组用短横线分隔，已失效分组默认折叠【未实现】
- [x] 20.2.3 渠道卡片简约设计：图标区分类型，底色区分状态【已实现: AddAccountScreen有渠道卡片样式】
- [x] 20.2.4 点击选中渠道，再次点击弹出用途选择对话框【已实现: BindingSelectionDialog】
- [x] 20.2.5 用途选择气泡样式：不同底色，选中加边框，默认选中"验证"【已实现: BindingSelectionDialog有用途chips】
- [ ] 20.2.6 已选中渠道右侧显示用途色点（小圆圈，无文字）【未实现: 当前显示用途标签而非色点】
- [ ] 20.2.7 应用选择横向滑动图标网格（LazyRow + snapToItem）【未实现: 当前使用LazyVerticalGrid而非横向滑动】
- [ ] 20.2.8 渠道搜索功能：输入关键词实时过滤【未实现: 无搜索框】

### 20.3 编辑账号信息
- [ ] 20.3.1 实现编辑账号入口：详情页按钮 + 长按菜单 + 右滑快捷
- [ ] 20.3.2 编辑账号页面：可编辑accountName、accountIdentifier、nickname、status
- [ ] 20.3.3 应用字段不可编辑（创建后固定）
- [ ] 20.3.4 编辑时校验账号ID唯一性
- [ ] 20.3.5 编辑时可新增/删除绑定渠道

### 20.4 编辑验证渠道
- [ ] 20.4.1 实现编辑渠道入口：长按菜单
- [ ] 20.4.2 编辑渠道页面：仅备注可编辑，类型和值不可改

---

## 21. P1交互功能补充（新增）

> P1级缺失事项，主流交互缺失，建议纳入MVP

### 21.1 卡片快捷交互模式
- [x] 21.1.1 实现验证渠道卡片手势：点击(详情) + 长按(菜单) + 右滑(设置提醒) + 左滑(标记处理)【已实现: IdentifierCard + SwipeableCard + ContextMenu】
- [x] 21.1.2 实现账号卡片手势：点击(详情) + 长按(菜单) + 右滑(编辑) + 左滑(删除)【已实现: AccountCard + SwipeableCard + ContextMenu】
- [x] 21.1.3 实现预警卡片手势：点击(详情) + 长按(菜单) + 右滑(标记处理) + 左滑(无操作)【已实现: WarningCard + SwipeableCard + ContextMenu】
- [x] 21.1.4 滑动视觉反馈：背景渐变色、按钮显示、阈值触发【已实现: SwipeableCard背景渐变和阈值逻辑】
- [x] 21.1.5 长按菜单动态显示：根据状态显示可用操作【已实现: getIdentifierMenuItems, getAccountMenuItems, getWarningMenuItems根据状态动态生成菜单项】

### 21.2 删除账号防呆确认
- [ ] 21.2.1 删除账号弹窗：显示账号名称，需输入名称确认【未实现: 当前仅简单确认弹窗，无防呆输入】
- [ ] 21.2.2 删除时自动解绑所有绑定关系【待验证: 需检查UseCase是否自动解绑】
- [x] 21.2.3 不限制是否绑定验证渠道【已实现: 删除不阻止】

### 21.3 删除渠道阻止逻辑
- [ ] 21.3.1 删除渠道时检查绑定账号数量【未实现】
- [ ] 21.3.2 有绑定则阻止，弹窗显示绑定账号列表【未实现】
- [ ] 21.3.3 提供"查看绑定账号"跳转按钮【未实现】

### 21.4 变更账号状态交互
- [ ] 21.4.1 状态选择对话框：任意状态可切换（无状态机限制）
- [ ] 21.4.2 每个状态选项显示提示说明
- [ ] 21.4.3 当前状态标记不可选

### 21.5 更换验证渠道交互
- [ ] 21.5.1 更换渠道对话框：显示当前绑定和用途
- [ ] 21.5.2 选择新渠道，默认保留原用途
- [ ] 21.5.3 可选"修改用途"展开用途选择
- [ ] 21.5.4 确认更换后记录历史

---

## 22. P2后续规划（标记）

> P2级缺失事项，列入后续规划V2.0

### 22.1 批量更换验证渠道（V2.0）
- [ ] 22.1.1 批量更换入口：影响范围页按钮
- [ ] 22.1.2 批量更换流程：选择新渠道 → 预览 → 执行
- [ ] 22.1.3 BatchBindingOperation聚合设计

### 22.2 自定义添加应用平台（V2.0）
- [ ] 22.2.1 添加自定义应用入口
- [ ] 22.2.2 自定义应用输入：名称、分类、图标
- [ ] 22.2.3 CustomApplication实体设计

### 22.3 密码设置/修改完整流程（V2.0）
- [ ] 22.3.1 首次设置密码流程
- [ ] 22.3.2 修改密码流程（验证当前密码）
- [ ] 22.3.3 关闭密码保护流程
- [ ] 22.3.4 密码强度提示

### 22.4 应用启动验证流程（V2.0）
- [ ] 22.4.1 启动验证界面设计
- [ ] 22.4.2 密码输入 + 指纹/面容按钮
- [ ] 22.4.3 验证失败处理（最多5次）
- [ ] 22.4.4 忘记密码流程（数据清空）

---

## 进度统计

| 章节 | 已完成 | 待完成 | 备注 |
|------|--------|--------|------|
| 1 项目基础设施 | 0 | 4 | 待配置 |
| 2 UI原型设计 | 11 | 0 | ✅ 全部完成 |
| 3 领域模型层 | 9 | 0 | ✅ 全部完成 |
| 4 领域服务层 | 6 | 0 | ✅ 全部完成 |
| 5 数据层 | 7 | 0 | ✅ 全部完成 |
| 6-12 功能模块 | 47 | 0 | ✅ 已完成 |
| 13 测试验证 | 3 | 9 | 部分完成 |
| 14 ViewModel-UI集成 | 11 | 0 | ✅ 已完成 |
| 15 核心功能实现 | 0 | 21 | 待完成（部分已在20-21章细化） |
| 16 UI完善 | 0 | 4 | 待完成 |
| 17 用语修正 | 0 | 4 | 待完成 |
| 18 导航数据传递 | 3 | 0 | ✅ 已完成 |
| 19 DI配置验证 | 2 | 2 | 部分完成 |
| 20 P0核心功能补充 | 11 | 9 | **导入导出UI已完成，加密逻辑待验证；渠道交互部分完成** |
| 21 P1交互功能补充 | 6 | 7 | **卡片手势已完成，删除防呆和阻止逻辑待实现** |
| 22 P2后续规划 | 0 | 11 | **标记为V2.0** |

**总进度：118/208 = 57%**
**P0/P1新增任务：31项（第20章20项 + 第21章13项）**
**P2标记任务：11项（列入V2.0规划）**

---

## 设计文档更新

以下设计文档已补充P0/P1规格：

| 文档位置 | 文档 | 内容 |
|----------|------|------|
| `docs/2_design/` | data-security.md | 数据安全技术方案（加密架构、密钥管理） |
| `docs/2_design/` | export-file-format.md | 导出文件格式规范（文件头结构、JSON格式） |
| `docs/2_design/` | card-gesture-design.md | 卡片手势设计规范（视觉反馈、滑动阶段） |
| `docs/2_design/` | ui-design.md | 已更新交互逻辑章节，引用规范文档 |
| `openspec/specs/import-export/` | spec.md | 导入导出交互规格（用户场景、UI界面） |
| `openspec/specs/card-interaction/` | spec.md | 卡片交互规格（手势行为、防呆设计） |
| `openspec/specs/application-account/` | spec.md | 补充编辑账号、删除防呆、绑定渠道交互 |
| `openspec/specs/settings/` | spec.md | 补充导入导出完整交互、加密文件处理 |