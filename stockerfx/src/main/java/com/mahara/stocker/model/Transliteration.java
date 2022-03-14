package com.mahara.stocker.model;

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

public class Transliteration {
    private final LongProperty id;
    private final LongProperty standardId;
    private final StringProperty original;
    private final StringProperty roman;
    private final StringProperty matchWay;
    private final StringProperty matchParams;
    private final StringProperty chinese;

    public Transliteration() {
        this(0l);
    }

    public Transliteration(Long id) {
        this.id = new SimpleLongProperty(id);
        this.standardId = new SimpleLongProperty(0l);
        this.original = new SimpleStringProperty("");
        this.roman = new SimpleStringProperty(null);
        this.matchWay = new SimpleStringProperty("");
        this.matchParams = new SimpleStringProperty(null);
        this.chinese = new SimpleStringProperty("");
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

	public Long getStandardId() {
		return standardId.get();
	}
    public LongProperty standardIdProperty() {
        return standardId;
    }
	public void setStandardId(Long standardId) {
		this.standardId.set(standardId);
	}
	public String getOriginal() {
		return original.get();
	}
    public StringProperty originalProperty() {
        return original;
    }
	public void setOriginal(String original) {
		this.original.set(original);
	}
	public String getRoman() {
		return roman.get();
	}
    public StringProperty romanProperty() {
        return roman;
    }
	public void setRoman(String roman) {
		this.roman.set(roman);
	}
	public String getMatchWay() {
		return matchWay.get();
	}
    public StringProperty matchWayProperty() {
        return matchWay;
    }
	public void setMatchWay(String matchWay) {
		this.matchWay.set(matchWay);
	}
	public String getMatchParams() {
		return matchParams.get();
	}
    public StringProperty matchParamsProperty() {
        return matchParams;
    }
	public void setMatchParams(String matchParams) {
		this.matchParams.set(matchParams);
	}
	public String getChinese() {
		return chinese.get();
	}
    public StringProperty chineseProperty() {
        return chinese;
    }
	public void setChinese(String chinese) {
		this.chinese.set(chinese);
	}
}
