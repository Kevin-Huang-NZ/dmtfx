package com.mahara.stocker.model;

import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class SysRoleFun {
    private final LongProperty id;
    private final StringProperty roleNo;
    private final StringProperty funNo;

    public SysRoleFun() {
        this(0l);
    }

    public SysRoleFun(Long id) {
        this.id = new SimpleLongProperty(id);
        this.roleNo = new SimpleStringProperty("");
        this.funNo = new SimpleStringProperty("");
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

    public String getRoleNo() {
        return roleNo.get();
    }

    public StringProperty roleNoProperty() {
        return roleNo;
    }

    public void setRoleNo(String roleNo) {
        this.roleNo.set(roleNo);
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
}
