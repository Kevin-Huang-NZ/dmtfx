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

public class Standard {
    private final LongProperty id;
    private final StringProperty standardName;
    private final StringProperty versionCode;
    private final StringProperty publishYmd;
    private final StringProperty languageName;
    private final StringProperty countryRegion;
    private final StringProperty memo;

    public Standard() {
        this(0l);
    }

    public Standard(Long id) {
        this.id = new SimpleLongProperty(id);
        this.standardName = new SimpleStringProperty("");
        this.versionCode = new SimpleStringProperty("");
        this.publishYmd = new SimpleStringProperty("");
        this.languageName = new SimpleStringProperty("");
        this.countryRegion = new SimpleStringProperty("");
        this.memo = new SimpleStringProperty(null);
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

	public String getStandardName() {
		return standardName.get();
	}
    public StringProperty standardNameProperty() {
        return standardName;
    }
	public void setStandardName(String standardName) {
		this.standardName.set(standardName);
	}
	public String getVersionCode() {
		return versionCode.get();
	}
    public StringProperty versionCodeProperty() {
        return versionCode;
    }
	public void setVersionCode(String versionCode) {
		this.versionCode.set(versionCode);
	}
	public String getPublishYmd() {
		return publishYmd.get();
	}
    public StringProperty publishYmdProperty() {
        return publishYmd;
    }
	public void setPublishYmd(String publishYmd) {
		this.publishYmd.set(publishYmd);
	}
	public String getLanguageName() {
		return languageName.get();
	}
    public StringProperty languageNameProperty() {
        return languageName;
    }
	public void setLanguageName(String languageName) {
		this.languageName.set(languageName);
	}
	public String getCountryRegion() {
		return countryRegion.get();
	}
    public StringProperty countryRegionProperty() {
        return countryRegion;
    }
	public void setCountryRegion(String countryRegion) {
		this.countryRegion.set(countryRegion);
	}
	public String getMemo() {
		return memo.get();
	}
    public StringProperty memoProperty() {
        return memo;
    }
	public void setMemo(String memo) {
		this.memo.set(memo);
	}
}
