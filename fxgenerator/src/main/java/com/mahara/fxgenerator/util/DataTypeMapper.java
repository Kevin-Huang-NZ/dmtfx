package com.mahara.fxgenerator.util;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;

public class DataTypeMapper {
	private Map<String, String> javaDataTypeMap = Stream.of(new String[][] { 
		{ "char", "String" }, 
		{ "varchar", "String" }, 
		{ "bigint", "Long" }, 
		{ "int", "Integer" }, 
		{ "decimal", "BigDecimal" }, 
		{ "timestamp", "Timestamp" }
	}).collect(Collectors.toMap(data -> data[0], data -> data[1]));
	
	private Map<String, String> jdbcDataTypeMap = Stream.of(new String[][] { 
		{ "char", "CHAR" }, 
		{ "varchar", "VARCHAR" }, 
		{ "bigint", "BIGINT" }, 
		{ "int", "INTEGER" }, 
		{ "decimal", "DECIMAL" }, 
		{ "timestamp", "TIMESTAMP" }
	}).collect(Collectors.toMap(data -> data[0], data -> data[1]));
	
	public String getJavaDataType(String jdbcType) {
		if (StringUtils.isEmpty(jdbcType)) {
			return null;
		}
		return javaDataTypeMap.get(jdbcType);
	}
	
	public String getJdbcDataType(String jdbcType) {
		if (StringUtils.isEmpty(jdbcType)) {
			return null;
		}
		return jdbcDataTypeMap.get(jdbcType);
	}
}
