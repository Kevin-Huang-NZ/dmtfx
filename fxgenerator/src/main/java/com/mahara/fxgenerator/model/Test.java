package com.mahara.fxgenerator.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;

public class Test {
    private LongProperty l;
    private StringProperty s;
    private IntegerProperty i;
    private ObjectProperty<BigDecimal> bd;
    private ObjectProperty<Timestamp> ts;
    private void f() {
        Timestamp.from(Instant.now());
    }
}
