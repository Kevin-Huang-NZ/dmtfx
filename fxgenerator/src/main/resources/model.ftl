package [=modelPkg];

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;

public class [=modelName] {
    private final LongProperty id;
	<#list fields as field>
        <#switch field.javaDataType>
            <#case 'String'>
    private final StringProperty [=field.lcColumnName];
                <#break>
            <#case 'Long'>
    private final LongProperty [=field.lcColumnName];
                <#break>
            <#case 'Integer'>
    private final IntegerProperty [=field.lcColumnName];
                <#break>
            <#default>
    private final ObjectProperty<[=field.javaDataType]> [=field.lcColumnName];
        </#switch>
	</#list>

    public [=modelName]() {
        this(0l);
    }

    public [=modelName](Long id) {
        this.id = new SimpleLongProperty(id);
        <#list fields as field>
            <#if field.isNullable == 'YES' >
                <#switch field.javaDataType>
                    <#case 'String'>
        this.[=field.lcColumnName] = new SimpleStringProperty(null);
                        <#break>
                    <#case 'Long'>
        this.[=field.lcColumnName] = new SimpleLongProperty(null);
                        <#break>
                    <#case 'Integer'>
        this.[=field.lcColumnName] = new IntegerProperty(null);
                        <#break>
                    <#default>
        this.[=field.lcColumnName] = new SimpleObjectProperty<[=field.javaDataType]>(null);
                </#switch>
            <#else>
                <#switch field.javaDataType>
                    <#case 'String'>
        this.[=field.lcColumnName] = new SimpleStringProperty("");
                        <#break>
                    <#case 'Long'>
        this.[=field.lcColumnName] = new SimpleLongProperty(0l);
                        <#break>
                    <#case 'Integer'>
        this.[=field.lcColumnName] = new IntegerProperty(0);
                        <#break>
                    <#case 'BigDecimal'>
        this.[=field.lcColumnName] = new SimpleObjectProperty<Timestamp>(BigDecimal.zero);
                        <#break>
                    <#case 'Timestamp'>
        this.[=field.lcColumnName] = new SimpleObjectProperty<Timestamp>(Timestamp.from(Instant.now()));
                        <#break>
                    <#default>
        this.[=field.lcColumnName] = new SimpleObjectProperty<[=field.javaDataType]>(null);
                </#switch>
            </#if>
        </#list>
    }

    public long getId() {
        return id.get();
    }

    public LongProperty idProperty() {
        return id;
    }

    public void setId(long id) {
        this.id.set(id);
    }

	<#list fields as field>
	public [=field.javaDataType] get[=field.ucColumnName]() {
		return [=field.lcColumnName].get();
	}
        <#switch field.javaDataType>
            <#case 'String'>
    public StringProperty [=field.lcColumnName]Property() {
                <#break>
            <#case 'Long'>
    public LongProperty [=field.lcColumnName]Property() {
                <#break>
            <#case 'Integer'>
    public IntegerProperty [=field.lcColumnName]Property() {
                <#break>
            <#default>
    public SimpleObjectProperty<[=field.javaDataType]> [=field.lcColumnName]Property() {
        </#switch>
        return [=field.lcColumnName];
    }
	public void set[=field.ucColumnName]([=field.javaDataType] [=field.lcColumnName]) {
		this.[=field.lcColumnName].set([=field.lcColumnName]);
	}
	</#list>
}
