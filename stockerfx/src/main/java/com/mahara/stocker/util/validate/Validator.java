package com.mahara.stocker.util.validate;

import javafx.scene.Node;

import java.util.ArrayList;
import java.util.List;

public class Validator<T> {
    private static final String ERROR_STYLE_CLASS = "error-field";

    private List<ValidateRule<T>> validators = new ArrayList<>();
    private Node field;
    private T value;

    private Validator(Node field, T value) {
        this.field = field;
        this.value = value;
    }

    public static <T> Validator<T> build(Node field, T value){
        var tmp = new Validator<T>(field, value);
        return tmp;
    }

    public Validator<T> add(ValidateRule<T> rule) {
        validators.add(rule);
        return this;
    }
    public ValidateResult validate() {
        ValidateResult result = new ValidateResult();

        for (var rule : validators) {
            if(rule.getValidateFunction().apply(value)) {
                result.setSuccess(false);
                result.setErrorMessage(rule.getErrorMessage());
                break;
            }
        }

        if (result.isSuccess()) {
            field.getStyleClass().remove(ERROR_STYLE_CLASS);
        } else {
            field.getStyleClass().add(ERROR_STYLE_CLASS);
        }

        return result;
    }
}
