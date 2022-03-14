package com.mahara.stocker.model;

import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class SysPage {
    private final LongProperty id;
    private final StringProperty pageName;
    private final StringProperty pageTitle;
    private final StringProperty memo;

    public SysPage() {
        this(0l);
    }

    public SysPage(Long id) {
        this.id = new SimpleLongProperty(id);
        this.pageName = new SimpleStringProperty("");
        this.pageTitle = new SimpleStringProperty("");
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

    public String getPageName() {
        return pageName.get();
    }

    public StringProperty pageNameProperty() {
        return pageName;
    }

    public void setPageName(String pageName) {
        this.pageName.set(pageName);
    }

    public String getPageTitle() {
        return pageTitle.get();
    }

    public StringProperty pageTitleProperty() {
        return pageTitle;
    }

    public void setPageTitle(String pageTitle) {
        this.pageTitle.set(pageTitle);
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
