# JavaFX代码生成器
## 功能

- 根据界面输入，加载数据库表
- 选择表，生成所需代码：model\dao\fxml\controller和权限控制初始化数据

## 软件版本

- Java 17
- OpenFX 17
- MySQL 8

## 运行

- 在IDE中运行，执行mvn clean javafx:run

## 缺陷

- 只生成了简单的校验规则：判空和长度限制。且，错误消息中英混写，需修改。
- 生成的fxml，需要手动调整控件尺寸，修改标签名称。表单页只生成TextField，需要ComboBox、DatePicker等，需要修改。
- excel导入导出代码未生成
