package com.mahara.stocker.model;

import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class SysFun {
    private final LongProperty id;
    private final StringProperty funNo;
    private final StringProperty pageName;
    private final StringProperty actionType;
    private final StringProperty actionNo;
    private final StringProperty actionName;
    private final StringProperty memo;

    public SysFun() {
        this(0l);
    }

    public SysFun(Long id) {
        this.id = new SimpleLongProperty(id);
        this.funNo = new SimpleStringProperty("");
        this.pageName = new SimpleStringProperty("");
        this.actionType = new SimpleStringProperty("l");
        this.actionNo = new SimpleStringProperty("");
        this.actionName = new SimpleStringProperty("");
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

    public String getFunNo() {
        return funNo.get();
    }

    public StringProperty funNoProperty() {
        return funNo;
    }

    public void setFunNo(String funNo) {
        this.funNo.set(funNo);
    }

    public String getPageName() {
        return pageName.get();
    }

    public StringProperty pageNameProperty() {
        return pageName;
    }

    public void setPageName(String pageName) {
        this.pageName.set(pageName);
    }

    public String getActionType() {
        return actionType.get();
    }

    public StringProperty actionTypeProperty() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType.set(actionType);
    }

    public String getActionNo() {
        return actionNo.get();
    }

    public StringProperty actionNoProperty() {
        return actionNo;
    }

    public void setActionNo(String actionNo) {
        this.actionNo.set(actionNo);
    }

    public String getActionName() {
        return actionName.get();
    }

    public StringProperty actionNameProperty() {
        return actionName;
    }

    public void setActionName(String actionName) {
        this.actionName.set(actionName);
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
