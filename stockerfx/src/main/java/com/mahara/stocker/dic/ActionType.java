package com.mahara.stocker.dic;

public class ActionType {
    private String actionType;
    private String actionTypeName;

    public ActionType(String actionType, String actionTypeName) {
        this.actionType = actionType;
        this.actionTypeName = actionTypeName;
    }

    public String getActionType() {
        return actionType;
    }

    public String getActionTypeName() {
        return actionTypeName;
    }
}
