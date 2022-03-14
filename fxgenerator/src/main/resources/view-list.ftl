<?xml version="1.0" encoding="UTF-8"?>

<?import java.lang.String?>
<?import java.net.URL?>
<?import javafx.geometry.Insets?>
<?import javafx.scene.control.Button?>
<?import javafx.scene.control.ButtonBar?>
<?import javafx.scene.control.Label?>
<?import javafx.scene.control.TableColumn?>
<?import javafx.scene.control.TableView?>
<?import javafx.scene.control.TextField?>
<?import javafx.scene.layout.AnchorPane?>
<?import javafx.scene.layout.ColumnConstraints?>
<?import javafx.scene.layout.GridPane?>
<?import javafx.scene.layout.RowConstraints?>
<?import javafx.scene.text.Font?>
<?import org.kordamp.ikonli.javafx.FontIcon?>

<AnchorPane fx:id="[=lcTableName]ListPane" prefHeight="700.0" prefWidth="1280.0" xmlns="http://javafx.com/javafx/17" xmlns:fx="http://javafx.com/fxml/1" fx:controller="[=controllerPkg].[=listControllerName]">
    <children>
        <GridPane layoutX="240.0" layoutY="134.0" AnchorPane.bottomAnchor="0.0" AnchorPane.leftAnchor="0.0" AnchorPane.rightAnchor="0.0" AnchorPane.topAnchor="0.0">
            <columnConstraints>
                <ColumnConstraints hgrow="SOMETIMES" minWidth="10.0" prefWidth="100.0" />
            </columnConstraints>
            <rowConstraints>
                <RowConstraints maxHeight="-Infinity" minHeight="35.0" prefHeight="55.0" vgrow="SOMETIMES" />
                <RowConstraints maxHeight="-Infinity" minHeight="10.0" prefHeight="590.0" vgrow="SOMETIMES" />
                <RowConstraints maxHeight="-Infinity" minHeight="35.0" prefHeight="55.0" vgrow="SOMETIMES" />
            </rowConstraints>
            <children>
                <GridPane GridPane.rowIndex="0">
                    <columnConstraints>
                        <ColumnConstraints hgrow="SOMETIMES" maxWidth="-Infinity" minWidth="10.0" prefWidth="240.0" />
                        <ColumnConstraints hgrow="SOMETIMES" maxWidth="-Infinity" minWidth="50.0" prefWidth="740.0" />
                        <ColumnConstraints hgrow="SOMETIMES" maxWidth="-Infinity" minWidth="300.0" prefWidth="300.0" />
                    </columnConstraints>
                    <rowConstraints>
                        <RowConstraints minHeight="10.0" prefHeight="30.0" vgrow="SOMETIMES" />
                    </rowConstraints>
                    <children>
                        <TextField fx:id="keyWordTextField" maxHeight="-Infinity" maxWidth="-Infinity" minHeight="-Infinity" minWidth="-Infinity" prefHeight="25.0" prefWidth="220.0" promptText="检索关键字">
                            <GridPane.margin>
                                <Insets left="10.0" />
                            </GridPane.margin>
                        </TextField>
                        <Button mnemonicParsing="false" nodeOrientation="LEFT_TO_RIGHT" onAction="#handleSearch" text="检索" GridPane.columnIndex="1" GridPane.halignment="LEFT">
                            <styleClass>
                                <String fx:value="btn" />
                                <String fx:value="btn-primary" />
                            </styleClass>
                            <graphic>
                                <FontIcon iconColor="#00000066" iconLiteral="fas-search" iconSize="15" selectionEnd="1" />
                            </graphic>
                        </Button>
                        <ButtonBar nodeOrientation="LEFT_TO_RIGHT" prefHeight="40.0" prefWidth="200.0" GridPane.columnIndex="2">
                            <buttons>
                                <Button mnemonicParsing="false" onAction="#handleNew" text="新建...">
                                    <styleClass>
                                        <String fx:value="btn" />
                                        <String fx:value="btn-success" />
                                    </styleClass>
                                </Button>
<!--                                <Button fx:id="btnImport" mnemonicParsing="false" onAction="#handleImport" text="导入">-->
<!--                                    <styleClass>-->
<!--                                        <String fx:value="btn" />-->
<!--                                        <String fx:value="btn-info" />-->
<!--                                    </styleClass>-->
<!--                                </Button>-->
<!--                                <Button fx:id="btnExport" mnemonicParsing="false" onAction="#handleExport" text="导出">-->
<!--                                    <styleClass>-->
<!--                                        <String fx:value="btn" />-->
<!--                                        <String fx:value="btn-warning" />-->
<!--                                    </styleClass>-->
<!--                                </Button>-->
                            </buttons>
                            <GridPane.margin>
                                <Insets right="10.0" />
                            </GridPane.margin>
                        </ButtonBar>
                    </children>
                </GridPane>
                <TableView fx:id="searchResultTable" accessibleRole="CHECK_BOX" prefHeight="381.0" prefWidth="900.0" tableMenuButtonVisible="true" GridPane.rowIndex="1">
                    <columns>
                        <#list fields as field>
                        <TableColumn fx:id="[=field.lcColumnName]Column" maxWidth="200.0" minWidth="50.0" prefWidth="100.0" text="[=field.columnTitle]" />
                        </#list>
                        <TableColumn fx:id="operationColumn" maxWidth="100.0" minWidth="100.0" prefWidth="100.0" />
                    </columns>
                    <GridPane.margin>
                        <Insets left="5.0" right="5.0" />
                    </GridPane.margin>
                    <columnResizePolicy>
                        <TableView fx:constant="CONSTRAINED_RESIZE_POLICY" />
                    </columnResizePolicy>
                </TableView>
                <GridPane GridPane.rowIndex="2">
                    <columnConstraints>
                        <ColumnConstraints hgrow="SOMETIMES" maxWidth="-Infinity" minWidth="100.0" prefWidth="200.0" />
                        <ColumnConstraints hgrow="SOMETIMES" maxWidth="-Infinity" minWidth="500.0" prefWidth="880.0" />
                        <ColumnConstraints hgrow="SOMETIMES" maxWidth="-Infinity" minWidth="10.0" prefWidth="200.0" />
                    </columnConstraints>
                    <rowConstraints>
                        <RowConstraints minHeight="10.0" prefHeight="30.0" vgrow="SOMETIMES" />
                    </rowConstraints>
                    <children>
                        <Button mnemonicParsing="false" onAction="#handlePrevious" text="上一页" GridPane.columnIndex="0">
                            <styleClass>
                                <String fx:value="btn" />
                                <String fx:value="btn-default" />
                            </styleClass>
                            <GridPane.margin>
                                <Insets left="10.0" />
                            </GridPane.margin>
                            <graphic>
                                <FontIcon iconColor="#00000066" iconLiteral="fas-arrow-left" iconSize="15" />
                            </graphic>
                        </Button>
                        <GridPane GridPane.columnIndex="1">
                            <columnConstraints>
                                <ColumnConstraints hgrow="SOMETIMES" maxWidth="240.0" minWidth="240.0" prefWidth="240.0" />
                                <ColumnConstraints hgrow="SOMETIMES" maxWidth="-Infinity" minWidth="-Infinity" prefWidth="80.0" />
                                <ColumnConstraints hgrow="SOMETIMES" maxWidth="-Infinity" minWidth="-Infinity" prefWidth="80.0" />
                                <ColumnConstraints hgrow="SOMETIMES" maxWidth="-Infinity" minWidth="-Infinity" prefWidth="80.0" />
                                <ColumnConstraints hgrow="SOMETIMES" maxWidth="-Infinity" minWidth="-Infinity" prefWidth="80.0" />
                                <ColumnConstraints hgrow="SOMETIMES" maxWidth="-Infinity" minWidth="-Infinity" prefWidth="80.0" />
                                <ColumnConstraints hgrow="SOMETIMES" maxWidth="250.0" minWidth="250.0" prefWidth="250.0" />
                            </columnConstraints>
                            <rowConstraints>
                                <RowConstraints minHeight="10.0" prefHeight="30.0" vgrow="SOMETIMES" />
                            </rowConstraints>
                            <children>
                                <Label opacity="0.6" text="总共 : " GridPane.columnIndex="1" GridPane.halignment="RIGHT" />
                                <Label fx:id="totalPageLabel" text="Label" GridPane.columnIndex="2" GridPane.halignment="CENTER">
                                    <font>
                                        <Font size="14.0" />
                                    </font>
                                </Label>
                                <Label opacity="0.6" text="页" GridPane.columnIndex="3" />
                                <Button mnemonicParsing="false" onAction="#handleGoto" text="跳转到" GridPane.columnIndex="4" GridPane.halignment="CENTER">
                                    <styleClass>
                                        <String fx:value="btn" />
                                        <String fx:value="btn-default" />
                                    </styleClass>
                                </Button>
                                <TextField fx:id="pageNoTextField" alignment="TOP_LEFT" maxHeight="-Infinity" maxWidth="-Infinity" minHeight="-Infinity" minWidth="-Infinity" prefHeight="28.0" prefWidth="40.0" GridPane.columnIndex="5" GridPane.halignment="LEFT">
                                    <GridPane.margin>
                                        <Insets left="5.0" />
                                    </GridPane.margin>
                                </TextField>
                            </children>
                        </GridPane>
                        <Button mnemonicParsing="false" nodeOrientation="RIGHT_TO_LEFT" onAction="#handleNext" text="下一页" GridPane.columnIndex="2" GridPane.halignment="RIGHT">
                            <styleClass>
                                <String fx:value="btn" />
                                <String fx:value="btn-default" />
                            </styleClass>
                            <GridPane.margin>
                                <Insets right="10.0" />
                            </GridPane.margin>
                            <graphic>
                                <FontIcon iconColor="#00000066" iconLiteral="fas-arrow-right" iconSize="15" />
                            </graphic>
                        </Button>
                    </children>
                </GridPane>
            </children>
        </GridPane>
    </children>
   <stylesheets>
      <URL value="@../../css/bootstrapfx.css" />
      <URL value="@../../css/customised.css" />
   </stylesheets>
</AnchorPane>
