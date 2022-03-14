package com.mahara.stocker.model;

import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class SysRole {
    private final LongProperty id;
    private final StringProperty roleNo;
    private final StringProperty roleName;
    private final StringProperty memo;

    public SysRole() {
        this(0l);
    }

    public SysRole(Long id) {
        this.id = new SimpleLongProperty(id);
        this.roleNo = new SimpleStringProperty("");
        this.roleName = new SimpleStringProperty("");
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

    public String getRoleNo() {
        return roleNo.get();
    }

    public StringProperty roleNoProperty() {
        return roleNo;
    }

    public void setRoleNo(String roleNo) {
        this.roleNo.set(roleNo);
    }

    public String getRoleName() {
        return roleName.get();
    }

    public StringProperty roleNameProperty() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName.set(roleName);
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
