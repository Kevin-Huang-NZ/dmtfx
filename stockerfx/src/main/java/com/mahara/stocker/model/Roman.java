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

public class Roman {
    private final LongProperty id;
    private final LongProperty standardId;
    private final StringProperty originalAlpha;
    private final StringProperty romanAlpha;

    public Roman() {
        this(0l);
    }

    public Roman(Long id) {
        this.id = new SimpleLongProperty(id);
        this.standardId = new SimpleLongProperty(0l);
        this.originalAlpha = new SimpleStringProperty("");
        this.romanAlpha = new SimpleStringProperty("");
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
	public String getOriginalAlpha() {
		return originalAlpha.get();
	}
    public StringProperty originalAlphaProperty() {
        return originalAlpha;
    }
	public void setOriginalAlpha(String originalAlpha) {
		this.originalAlpha.set(originalAlpha);
	}
	public String getRomanAlpha() {
		return romanAlpha.get();
	}
    public StringProperty romanAlphaProperty() {
        return romanAlpha;
    }
	public void setRomanAlpha(String romanAlpha) {
		this.romanAlpha.set(romanAlpha);
	}
}
