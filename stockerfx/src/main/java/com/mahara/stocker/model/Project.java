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

public class Project {
    private final LongProperty id;
    private final LongProperty standardId;
    private final StringProperty projectName;
    private final StringProperty startDate;
    private final StringProperty dueDate;
    private final StringProperty status;
    private final StringProperty memo;

    public Project() {
        this(0l);
    }

    public Project(Long id) {
        this.id = new SimpleLongProperty(id);
        this.standardId = new SimpleLongProperty(0l);
        this.projectName = new SimpleStringProperty("");
        this.startDate = new SimpleStringProperty("");
        this.dueDate = new SimpleStringProperty("");
        this.status = new SimpleStringProperty("");
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

	public Long getStandardId() {
		return standardId.get();
	}
    public LongProperty standardIdProperty() {
        return standardId;
    }
	public void setStandardId(Long standardId) {
		this.standardId.set(standardId);
	}
	public String getProjectName() {
		return projectName.get();
	}
    public StringProperty projectNameProperty() {
        return projectName;
    }
	public void setProjectName(String projectName) {
		this.projectName.set(projectName);
	}
	public String getStartDate() {
		return startDate.get();
	}
    public StringProperty startDateProperty() {
        return startDate;
    }
	public void setStartDate(String startDate) {
		this.startDate.set(startDate);
	}
	public String getDueDate() {
		return dueDate.get();
	}
    public StringProperty dueDateProperty() {
        return dueDate;
    }
	public void setDueDate(String dueDate) {
		this.dueDate.set(dueDate);
	}
	public String getStatus() {
		return status.get();
	}
    public StringProperty statusProperty() {
        return status;
    }
	public void setStatus(String status) {
		this.status.set(status);
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
