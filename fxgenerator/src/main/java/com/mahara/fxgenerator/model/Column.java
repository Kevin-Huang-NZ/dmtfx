package com.mahara.fxgenerator.model;

public class Column {
	private Integer ordinalPosition;
	private String columnName;
	private String dataType;
	private String isNullable;
	private String columnDefault;
	private Long characterMaximumLength;
	private Long characterOctetLength;
	private Long numericPrecision;
	private Long numericScale;
	private Integer datetimePrecision;
	// "" "PRI" "UNI" "MUL"
	private String columnKey;
	private String extra;
	private String columnComment;

	public Column() {
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

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public String getIsNullable() {
		return isNullable;
	}

	public void setIsNullable(String isNullable) {
		this.isNullable = isNullable;
	}

	public String getColumnDefault() {
		return columnDefault;
	}

	public void setColumnDefault(String columnDefault) {
		this.columnDefault = columnDefault;
	}

	public Long getCharacterMaximumLength() {
		return characterMaximumLength;
	}

	public void setCharacterMaximumLength(Long characterMaximumLength) {
		this.characterMaximumLength = characterMaximumLength;
	}

	public Long getCharacterOctetLength() {
		return characterOctetLength;
	}

	public void setCharacterOctetLength(Long characterOctetLength) {
		this.characterOctetLength = characterOctetLength;
	}

	public Long getNumericPrecision() {
		return numericPrecision;
	}

	public void setNumericPrecision(Long numericPrecision) {
		this.numericPrecision = numericPrecision;
	}

	public Long getNumericScale() {
		return numericScale;
	}

	public void setNumericScale(Long numericScale) {
		this.numericScale = numericScale;
	}

	public Integer getDatetimePrecision() {
		return datetimePrecision;
	}

	public void setDatetimePrecision(Integer datetimePrecision) {
		this.datetimePrecision = datetimePrecision;
	}

	public String getColumnKey() {
		return columnKey;
	}

	public void setColumnKey(String columnKey) {
		this.columnKey = columnKey;
	}

	public String getExtra() {
		return extra;
	}

	public void setExtra(String extra) {
		this.extra = extra;
	}

	public String getColumnComment() {
		return columnComment;
	}

	public void setColumnComment(String columnComment) {
		this.columnComment = columnComment;
	}
}
