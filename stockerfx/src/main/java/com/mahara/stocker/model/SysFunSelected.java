package com.mahara.stocker.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class SysFunSelected extends SysFun{

    private final BooleanProperty isSelected;


    public SysFunSelected() {
        super();
        this.isSelected = new SimpleBooleanProperty(false);
    }

    public boolean isIsSelected() {
        return isSelected.get();
    }

    public BooleanProperty isSelectedProperty() {
        return isSelected;
    }

    public void setIsSelected(boolean isSelected) {
        this.isSelected.set(isSelected);
    }
}
