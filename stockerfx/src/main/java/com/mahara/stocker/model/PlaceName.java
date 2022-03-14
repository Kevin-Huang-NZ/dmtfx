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

public class PlaceName {
    private final LongProperty id;
    private final LongProperty projectId;
    private final StringProperty original;
    private final StringProperty country;
    private final StringProperty language;
    private final StringProperty gec;
    private final StringProperty memo;
    private final StringProperty romanStatus;
    private final StringProperty roman;
    private final StringProperty transStatus;
    private final StringProperty transliteration;
    private final StringProperty freeTranslation;
    private final StringProperty emitStandard;
    private final StringProperty transResult;

    public PlaceName() {
        this(0l);
    }

    public PlaceName(Long id) {
        this.id = new SimpleLongProperty(id);
        this.projectId = new SimpleLongProperty(0l);
        this.original = new SimpleStringProperty("");
        this.country = new SimpleStringProperty(null);
        this.language = new SimpleStringProperty(null);
        this.gec = new SimpleStringProperty(null);
        this.memo = new SimpleStringProperty(null);
        this.romanStatus = new SimpleStringProperty("");
        this.roman = new SimpleStringProperty(null);
        this.transStatus = new SimpleStringProperty("");
        this.transliteration = new SimpleStringProperty(null);
        this.freeTranslation = new SimpleStringProperty(null);
        this.emitStandard = new SimpleStringProperty(null);
        this.transResult = new SimpleStringProperty(null);
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

	public Long getProjectId() {
		return projectId.get();
	}
    public LongProperty projectIdProperty() {
        return projectId;
    }
	public void setProjectId(Long projectId) {
		this.projectId.set(projectId);
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
	public String getCountry() {
		return country.get();
	}
    public StringProperty countryProperty() {
        return country;
    }
	public void setCountry(String country) {
		this.country.set(country);
	}
	public String getLanguage() {
		return language.get();
	}
    public StringProperty languageProperty() {
        return language;
    }
	public void setLanguage(String language) {
		this.language.set(language);
	}
	public String getGec() {
		return gec.get();
	}
    public StringProperty gecProperty() {
        return gec;
    }
	public void setGec(String gec) {
		this.gec.set(gec);
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
	public String getRomanStatus() {
		return romanStatus.get();
	}
    public StringProperty romanStatusProperty() {
        return romanStatus;
    }
	public void setRomanStatus(String romanStatus) {
		this.romanStatus.set(romanStatus);
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
	public String getTransStatus() {
		return transStatus.get();
	}
    public StringProperty transStatusProperty() {
        return transStatus;
    }
	public void setTransStatus(String transStatus) {
		this.transStatus.set(transStatus);
	}
	public String getTransliteration() {
		return transliteration.get();
	}
    public StringProperty transliterationProperty() {
        return transliteration;
    }
	public void setTransliteration(String transliteration) {
		this.transliteration.set(transliteration);
	}
	public String getFreeTranslation() {
		return freeTranslation.get();
	}
    public StringProperty freeTranslationProperty() {
        return freeTranslation;
    }
	public void setFreeTranslation(String freeTranslation) {
		this.freeTranslation.set(freeTranslation);
	}
	public String getEmitStandard() {
		return emitStandard.get();
	}
    public StringProperty emitStandardProperty() {
        return emitStandard;
    }
	public void setEmitStandard(String emitStandard) {
		this.emitStandard.set(emitStandard);
	}
	public String getTransResult() {
		return transResult.get();
	}
    public StringProperty transResultProperty() {
        return transResult;
    }
	public void setTransResult(String transResult) {
		this.transResult.set(transResult);
	}
}
