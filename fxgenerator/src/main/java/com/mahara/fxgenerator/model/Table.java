package com.mahara.fxgenerator.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Table {
	private final StringProperty tableName;
	private final StringProperty tableComment;

	public Table() {
		tableName = new SimpleStringProperty("");
		tableComment = new SimpleStringProperty("");
	}

	public String getTableName() {
		return tableName.get();
	}

	public StringProperty tableNameProperty() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName.set(tableName);
	}

	public String getTableComment() {
		return tableComment.get();
	}

	public StringProperty tableCommentProperty() {
		return tableComment;
	}

	public void setTableComment(String tableComment) {
		this.tableComment.set(tableComment);
	}
}
