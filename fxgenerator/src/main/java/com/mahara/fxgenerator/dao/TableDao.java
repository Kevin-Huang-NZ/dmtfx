package com.mahara.fxgenerator.dao;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import com.mahara.fxgenerator.util.NameConverter;
import com.mahara.fxgenerator.model.Column;
import com.mahara.fxgenerator.model.Constraint;
import com.mahara.fxgenerator.model.ConstraintColumn;
import com.mahara.fxgenerator.model.Table;

public class TableDao {
	JdbcTemplate jdbcTemplate;

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public List<Table> selectTable(String schemaName) {
		String sql = "select `TABLE_NAME`, `TABLE_COMMENT` from `information_schema`.`TABLES` where `TABLE_SCHEMA`=?";
		return jdbcTemplate.query(sql, (rs, row) -> {
			Table t = new Table();
			t.setTableName(NameConverter.lowerCase(rs.getString("TABLE_NAME")));
			t.setTableComment(rs.getString("TABLE_COMMENT"));
			return t;
		}, schemaName);
	}

	public Table selectTableByName(String schemaName, String tableName) {
		String sql = "select `TABLE_NAME`, `TABLE_COMMENT` from `information_schema`.`TABLES` where `TABLE_SCHEMA`=? and `TABLE_NAME`=?";
		var tables = jdbcTemplate.query(sql, (rs, row) -> {
			Table t = new Table();
			t.setTableName(NameConverter.lowerCase(rs.getString("TABLE_NAME")));
			t.setTableComment(rs.getString("TABLE_COMMENT"));
			return t;
		}, schemaName, tableName);
		if (tables != null && !tables.isEmpty()) {
			return tables.get(0);
		} else {
			return null;
		}
	}

	public List<Column> selectColumn(String schemaName, String tableName) {
		StringBuilder sb = new StringBuilder();
		sb.append("select `ORDINAL_POSITION`,`COLUMN_NAME`,`DATA_TYPE`,`IS_NULLABLE`,`COLUMN_DEFAULT`,");
		sb.append("`CHARACTER_MAXIMUM_LENGTH`,`CHARACTER_OCTET_LENGTH`,");
		sb.append("`NUMERIC_PRECISION`,`NUMERIC_SCALE`,");
		sb.append("`DATETIME_PRECISION`,");
		sb.append("`COLUMN_KEY`,`EXTRA`,");
		sb.append("`COLUMN_COMMENT`");
		sb.append("from `information_schema`.`COLUMNS` where `TABLE_SCHEMA`=? and `TABLE_NAME`=? order by `ORDINAL_POSITION`");
		return jdbcTemplate.query(sb.toString(), (rs, row) -> {
			Column t = new Column();
			t.setOrdinalPosition(rs.getInt("ORDINAL_POSITION"));
			t.setColumnName(NameConverter.lowerCase(rs.getString("COLUMN_NAME")));
			t.setDataType(NameConverter.lowerCase(rs.getString("DATA_TYPE")));
			t.setIsNullable(NameConverter.lowerCase(rs.getString("IS_NULLABLE")));
			t.setColumnDefault(rs.getString("COLUMN_DEFAULT"));
			t.setCharacterMaximumLength(rs.getLong("CHARACTER_MAXIMUM_LENGTH"));
			t.setCharacterOctetLength(rs.getLong("CHARACTER_OCTET_LENGTH"));
			t.setNumericPrecision(rs.getLong("NUMERIC_PRECISION"));
			t.setNumericScale(rs.getLong("NUMERIC_SCALE"));
			t.setDatetimePrecision(rs.getInt("DATETIME_PRECISION"));
			t.setColumnKey(NameConverter.lowerCase(rs.getString("COLUMN_KEY")));
			t.setExtra(NameConverter.lowerCase(rs.getString("EXTRA")));
			t.setColumnComment(rs.getString("COLUMN_COMMENT"));
			return t;
		}, schemaName, tableName);
	}

	public List<Constraint> selectConstraint(String schemaName, String tableName, String constraintType) {
		String sql = "select `CONSTRAINT_NAME`, `CONSTRAINT_TYPE` from `information_schema`.`TABLE_CONSTRAINTS` where `TABLE_SCHEMA`=? and `TABLE_NAME`=? and `CONSTRAINT_TYPE`=?";
		return jdbcTemplate.query(sql, (rs, row) -> {
			Constraint t = new Constraint();
			t.setConstraintName(NameConverter.lowerCase(rs.getString("CONSTRAINT_NAME")));
			t.setConstraintType(NameConverter.lowerCase(rs.getString("CONSTRAINT_TYPE")));
			return t;
		}, schemaName, tableName, constraintType);
	}

	public List<ConstraintColumn> selectConstraintColumn(String schemaName, String tableName, String constraintName) {

		StringBuilder sb = new StringBuilder();
		sb.append("select `ORDINAL_POSITION`,`COLUMN_NAME`");
		sb.append("from `information_schema`.`KEY_COLUMN_USAGE` where `TABLE_SCHEMA`=? and `TABLE_NAME`=? and `CONSTRAINT_NAME`=? order by `ORDINAL_POSITION`");
		return jdbcTemplate.query(sb.toString(), (rs, row) -> {
		    ConstraintColumn tmp = new ConstraintColumn();
			tmp.setOrdinalPosition(rs.getInt("ORDINAL_POSITION"));
			tmp.setColumnName(NameConverter.lowerCase(rs.getString("COLUMN_NAME")));
		    return tmp;
		}, schemaName, tableName, constraintName);
	}

}
