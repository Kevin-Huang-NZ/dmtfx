# 外文地名翻译辅助工具
## 功能

- 以译写标准为组，管理罗马字母对照表、音译表、常用词。
- 使用罗马字母对照表对原文地名转写。
- 使用音译表和常用词构建AC自动机，对原文地名进行转换。
- 转换结果导出为excel，线下分发处理。

## 软件版本

- Java 17
- OpenFX 17
- MySQL 8

## 运行&打包

- 在IDE中运行，执行mvn clean javafx:run
- 打fatjar，执行clean compile package

## 相对web版的dmt

- 实现了所有匹配方式：精确、开头、结尾、前置词、后置词。
- 去掉了终译审核流程，改为excel导入、导出，线下人工分发、合并。
