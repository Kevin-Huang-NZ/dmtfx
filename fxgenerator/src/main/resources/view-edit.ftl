<?xml version="1.0" encoding="UTF-8"?>

<?import javafx.geometry.Insets?>
<?import javafx.scene.Cursor?>
<?import javafx.scene.control.Button?>
<?import javafx.scene.control.ButtonBar?>
<?import javafx.scene.control.ComboBox?>
<?import javafx.scene.control.Label?>
<?import javafx.scene.control.TextArea?>
<?import javafx.scene.control.TextField?>
<?import javafx.scene.layout.AnchorPane?>
<?import javafx.scene.layout.ColumnConstraints?>
<?import javafx.scene.layout.GridPane?>
<?import javafx.scene.layout.RowConstraints?>
<?import javafx.scene.control.PasswordField?>

<?import java.net.URL?>
<?import java.lang.String?>
<AnchorPane fx:id="[=lcTableName]EditPane" prefHeight="[=totalHeight]" prefWidth="600.0" xmlns="http://javafx.com/javafx/17" xmlns:fx="http://javafx.com/fxml/1" fx:controller="[=controllerPkg].[=editControllerName]">
    <children>
        <GridPane layoutX="12.0" layoutY="6.0" prefHeight="[=inputAreaHeight]" prefWidth="570.0">
            <columnConstraints>
                <ColumnConstraints hgrow="SOMETIMES" maxWidth="279.0" minWidth="10.0" prefWidth="128.0" />
                <ColumnConstraints hgrow="SOMETIMES" maxWidth="448.0" minWidth="10.0" prefWidth="442.0" />
            </columnConstraints>
            <rowConstraints>
                <#list fields as field>
                <RowConstraints maxHeight="50.0" minHeight="50.0" prefHeight="50.0" vgrow="SOMETIMES" />
                </#list>
            </rowConstraints>
            <children>
                <#list fields as field>
                <Label text="[=field.columnTitle]" GridPane.halignment="RIGHT" GridPane.columnIndex="0" GridPane.rowIndex="[=field?index]">
                    <GridPane.margin>
                        <Insets right="20.0" />
                    </GridPane.margin>
                </Label>
                <TextField fx:id="[=field.lcColumnName]Field" maxHeight="-Infinity" maxWidth="-Infinity" minHeight="-Infinity" minWidth="-Infinity" prefHeight="30.0" prefWidth="400.0" GridPane.columnIndex="1" GridPane.rowIndex="[=field?index]" />
                </#list>
            </children>
        </GridPane>
        <ButtonBar layoutX="342.0" layoutY="[=buttonBarY]" prefHeight="40.0" prefWidth="200.0">
            <buttons>
                <Button mnemonicParsing="false" onAction="#handleSave" text="保存">
                    <cursor>
                        <Cursor fx:constant="HAND" />
                    </cursor>
                    <styleClass>
                        <String fx:value="btn" />
                        <String fx:value="btn-primary" />
                    </styleClass>
                </Button>
                <Button cancelButton="true" mnemonicParsing="false" onAction="#handleCancel" text="取消">
                    <cursor>
                        <Cursor fx:constant="HAND" />
                    </cursor>
                    <styleClass>
                        <String fx:value="btn" />
                        <String fx:value="btn-default" />
                    </styleClass>
                </Button>
            </buttons>
        </ButtonBar>
    </children>
    <stylesheets>
        <URL value="@../../css/bootstrapfx.css" />
        <URL value="@../../css/customised.css" />
    </stylesheets>
</AnchorPane>
