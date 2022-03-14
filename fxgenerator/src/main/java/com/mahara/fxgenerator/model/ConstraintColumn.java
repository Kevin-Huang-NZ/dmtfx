package com.mahara.fxgenerator.model;

public class ConstraintColumn {
	private Integer ordinalPosition;
	private String columnName;

	public ConstraintColumn() {
	}

	public Integer getOrdinalPosition() {
		return ordinalPosition;
	}

	public void setOrdinalPosition(Integer ordinalPosition) {
		this.ordinalPosition = ordinalPosition;
	}

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}
}
