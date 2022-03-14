package com.mahara.fxgenerator.service;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.mahara.fxgenerator.controller.HomeController;
import com.mahara.fxgenerator.dao.TableDao;
import com.mahara.fxgenerator.model.Column;
import com.mahara.fxgenerator.model.Constraint;
import com.mahara.fxgenerator.model.Table;
import com.mahara.fxgenerator.util.DataTypeMapper;
import com.mahara.fxgenerator.util.FreeMarkerConfig;
import com.mahara.fxgenerator.util.JDBCTemplateFactory;
import com.mahara.fxgenerator.util.NameConverter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Generator {
	private static final Logger log = LoggerFactory.getLogger(HomeController.class);
	
	public static final String CONSTRAINT_TYPE_UK = "UNIQUE";

	private TableDao tableDao;

	private DataTypeMapper dataTypeMapper = new DataTypeMapper();

	private Table table;
	private List<Column> columns;
	private Constraint firstConstraints;
	private List<Column> constraintColumns;
	
	// 数据表名称原始格式：aaa_bbb
	private String tableName;
	// 数据表名称大写驼峰格式：AaaBbb
	private String ucTableName;
	// 数据表名称小写驼峰格式：aaaBbb
	private String lcTableName;
	// 数据表名称中划线格式：aaa-bbb
	private String lhTableName;

	public Generator() {
		tableDao = new TableDao();
		tableDao.setJdbcTemplate(JDBCTemplateFactory.getInstance().jt());
	}

	public Generator loadTableInfo(String schemaName, String tn) {
		tableName = NameConverter.lowerCase(tn);
		ucTableName = NameConverter.upperCamel(tableName);
		lcTableName = NameConverter.lowerCamel(tableName);
		lhTableName = NameConverter.lowerHyphen(tableName);
		// 检索表信息
		table = tableDao.selectTableByName(schemaName, tn);

		// 检索表的字段信息，过滤掉id字段
		columns = tableDao.selectColumn(schemaName, tn).stream()
				.filter(c -> !StringUtils.equals("id", c.getColumnName()))
				.collect(Collectors.toList());

		// 检索表的唯一索引,只取第一个
		List<Constraint> constraints = tableDao.selectConstraint(schemaName, tn, Generator.CONSTRAINT_TYPE_UK);
		if (constraints != null && constraints.size() > 0) {
			firstConstraints = constraints.get(0);
		}

		if (firstConstraints != null) {
			var tmpColumnMap = columns.stream().collect(Collectors.toMap(Column::getColumnName, Function.identity()));
			// 有唯一索引，检索该索引的字段
			constraintColumns = tableDao.selectConstraintColumn(schemaName, tn, firstConstraints.getConstraintName()).stream()
					.map(cc -> tmpColumnMap.get(cc.getColumnName()))
					.collect(Collectors.toList());
		}
		return this;
	}

	public void generateAll(MetaData md) {
		generateModel(md);
		generateRepository(md);
		generateRepositoryImpl(md);
		generateControllerList(md);
		generateControllerEdit(md);
		generateViewList(md);
		generateViewEdit(md);
		generateAuthSQL(md);

	}

	public void generateModel(MetaData md) {
		OutputStreamWriter out = null;
		try {
			var template = FreeMarkerConfig.instance().cfg().getTemplate("model.ftl");

			var root = new HashMap<String, Object>();

			root.put("modelPkg", md.getModelPkg());
			root.put("modelName", getModelName());
			
			var fields = new ArrayList<Map<String, Object>>();
			for(var column : columns) {
				var fieldMap = new HashMap<String, Object>();

				fieldMap.put("ucColumnName", NameConverter.upperCamel(column.getColumnName()));
				fieldMap.put("isNullable", NameConverter.upperCase(column.getIsNullable()));
				fieldMap.put("lcColumnName", NameConverter.lowerCamel(column.getColumnName()));
				fieldMap.put("javaDataType", dataTypeMapper.getJavaDataType(NameConverter.lowerCase(column.getDataType())));
				
				fields.add(fieldMap);
			}
			root.put("fields", fields);

			out = new OutputStreamWriter(Files.newOutputStream(Paths.get(md.getModelPath(), getModelName()+".java")), "UTF-8");
			template.process(root, out);

			out.close();
		} catch (Exception e) {
			log.error("Failed to get template.", e);
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
				}
			}
		}
	}

	public void generateRepository(MetaData md) {
		OutputStreamWriter out = null;
		try {
			var template = FreeMarkerConfig.instance().cfg().getTemplate("repository.ftl");
			Map<String, Object> root = new HashMap<String, Object>();

			root.put("basePackage", md.getBasePkg());
			root.put("modelPkg", md.getModelPkg());
			root.put("repositoryPkg", md.getRepositoryPkg());
			root.put("modelName", getModelName());
			root.put("repositoryName", getRepositoryName());

			if (firstConstraints != null) {
				root.put("hasUniqueKey", true);

				List<Map<String, Object>> ukFields = new ArrayList<Map<String, Object>>();
				for(Column column : this.constraintColumns) {
					Map<String, Object> fieldMap = new HashMap<String, Object>();

					fieldMap.put("lcColumnName", NameConverter.lowerCamel(column.getColumnName()));
					fieldMap.put("javaDataType", dataTypeMapper.getJavaDataType(NameConverter.lowerCase(column.getDataType())));

					ukFields.add(fieldMap);
				}

				root.put("ukFields", ukFields);
			} else {
				root.put("hasUniqueKey", false);
			}

			out = new OutputStreamWriter(Files.newOutputStream(Paths.get(md.getRepositoryPath(), getRepositoryName()+".java")), "UTF-8");
			template.process(root, out);

			out.close();
		} catch (Exception e) {
			log.error("Failed to get template.", e);
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
				}
			}
		}
	}

	public void generateRepositoryImpl(MetaData md) {
		OutputStreamWriter out = null;
		try {
			var template = FreeMarkerConfig.instance().cfg().getTemplate("repository-impl.ftl");
			Map<String, Object> root = new HashMap<String, Object>();

			root.put("basePackage", md.getBasePkg());
			root.put("modelPkg", md.getModelPkg());
			root.put("repositoryPkg", md.getRepositoryPkg());
			root.put("repositoryImplPkg", md.getRepositoryImplPkg());
			root.put("modelName", getModelName());
			root.put("repositoryName", getRepositoryName());
			root.put("repositoryImplName", getRepositoryImplName());

			root.put("tableName", this.tableName);

			List<Map<String, Object>> fields = new ArrayList<Map<String, Object>>();
			for(Column column : this.columns) {
				Map<String, Object> fieldMap = new HashMap<String, Object>();

				fieldMap.put("columnName", column.getColumnName());
				fieldMap.put("lcColumnName", NameConverter.lowerCamel(column.getColumnName()));
				fieldMap.put("ucColumnName", NameConverter.upperCamel(column.getColumnName()));
				fieldMap.put("javaDataType", dataTypeMapper.getJavaDataType(NameConverter.lowerCase(column.getDataType())));

				fields.add(fieldMap);
			}
			root.put("fields", fields);

			if (firstConstraints != null) {
				root.put("hasUniqueKey", true);

				List<Map<String, Object>> ukFields = new ArrayList<Map<String, Object>>();
				for(Column column : this.constraintColumns) {
					Map<String, Object> fieldMap = new HashMap<String, Object>();

					fieldMap.put("columnName", column.getColumnName());
					fieldMap.put("lcColumnName", NameConverter.lowerCamel(column.getColumnName()));
					fieldMap.put("javaDataType", dataTypeMapper.getJavaDataType(NameConverter.lowerCase(column.getDataType())));

					ukFields.add(fieldMap);
				}

				root.put("ukFields", ukFields);
			} else {
				root.put("hasUniqueKey", false);
			}

			out = new OutputStreamWriter(Files.newOutputStream(Paths.get(md.getRepositoryImplPath(), getRepositoryImplName()+".java")), "UTF-8");
			template.process(root, out);

			out.close();
		} catch (Exception e) {
			log.error("Failed to get template.", e);
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
				}
			}
		}
	}

	public void generateControllerList(MetaData md) {
		OutputStreamWriter out = null;
		try {
			var template = FreeMarkerConfig.instance().cfg().getTemplate("controller-list.ftl");
			Map<String, Object> root = new HashMap<String, Object>();

			root.put("lcTableName", lcTableName);
			root.put("basePackage", md.getBasePkg());
			root.put("modelPkg", md.getModelPkg());
			root.put("repositoryPkg", md.getRepositoryPkg());
			root.put("controllerPkg", md.getControllerPkg());
			root.put("modelName", getModelName());
			root.put("repositoryName", getRepositoryName());
			root.put("listControllerName", getListControllerName());
			root.put("editControllerName", getEditControllerName());
			root.put("viewFolder", md.getViewFolder());
			root.put("editViewName", getEditViewName());

			List<Map<String, Object>> fields = new ArrayList<Map<String, Object>>();
			for(Column column : this.columns) {
				Map<String, Object> fieldMap = new HashMap<String, Object>();

				fieldMap.put("lcColumnName", NameConverter.lowerCamel(column.getColumnName()));
				fieldMap.put("ucColumnName", NameConverter.upperCamel(column.getColumnName()));
				fieldMap.put("javaDataType", dataTypeMapper.getJavaDataType(NameConverter.lowerCase(column.getDataType())));

				fields.add(fieldMap);
			}
			root.put("fields", fields);

			out = new OutputStreamWriter(Files.newOutputStream(Paths.get(md.getControllerPath(), getListControllerName()+".java")), "UTF-8");
			template.process(root, out);

			out.close();
		} catch (Exception e) {
			log.error("Failed to get template.", e);
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
				}
			}
		}
	}

	public void generateControllerEdit(MetaData md) {
		OutputStreamWriter out = null;
		try {
			var template = FreeMarkerConfig.instance().cfg().getTemplate("controller-edit.ftl");
			Map<String, Object> root = new HashMap<String, Object>();

			root.put("lcTableName", lcTableName);
			root.put("basePackage", md.getBasePkg());
			root.put("modelPkg", md.getModelPkg());
			root.put("repositoryPkg", md.getRepositoryPkg());
			root.put("controllerPkg", md.getControllerPkg());
			root.put("modelName", getModelName());
			root.put("repositoryName", getRepositoryName());
			root.put("listControllerName", getListControllerName());
			root.put("editControllerName", getEditControllerName());
			root.put("viewFolder", md.getViewFolder());
			root.put("editViewName", getEditViewName());

			List<Map<String, Object>> fields = new ArrayList<Map<String, Object>>();
			for(Column column : this.columns) {
				Map<String, Object> fieldMap = new HashMap<String, Object>();

				fieldMap.put("lcColumnName", NameConverter.lowerCamel(column.getColumnName()));
				fieldMap.put("ucColumnName", NameConverter.upperCamel(column.getColumnName()));
				fieldMap.put("javaDataType", dataTypeMapper.getJavaDataType(NameConverter.lowerCase(column.getDataType())));
				fieldMap.put("isNullable", NameConverter.upperCase(column.getIsNullable()));
				fieldMap.put("characterMaximumLength", column.getCharacterMaximumLength());
				fieldMap.put("columnTitle", NameConverter.title(column.getColumnName()));

				fields.add(fieldMap);
			}
			root.put("fields", fields);

			if (firstConstraints != null) {
				root.put("hasUniqueKey", true);

				List<Map<String, Object>> ukFields = new ArrayList<Map<String, Object>>();
				for(Column column : this.constraintColumns) {
					Map<String, Object> fieldMap = new HashMap<String, Object>();

					fieldMap.put("lcColumnName", NameConverter.lowerCamel(column.getColumnName()));
					fieldMap.put("javaDataType", dataTypeMapper.getJavaDataType(NameConverter.lowerCase(column.getDataType())));

					ukFields.add(fieldMap);
				}

				root.put("ukFields", ukFields);
			} else {
				root.put("hasUniqueKey", false);
			}

			out = new OutputStreamWriter(Files.newOutputStream(Paths.get(md.getControllerPath(), getEditControllerName()+".java")), "UTF-8");
			template.process(root, out);

			out.close();
		} catch (Exception e) {
			log.error("Failed to get template.", e);
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
				}
			}
		}
	}

	public void generateViewList(MetaData md) {
		OutputStreamWriter out = null;
		try {
			var template = FreeMarkerConfig.instance().cfg().getTemplate("view-list.ftl");
			Map<String, Object> root = new HashMap<String, Object>();

			root.put("lcTableName", lcTableName);
			root.put("controllerPkg", md.getControllerPkg());
			root.put("listControllerName", getListControllerName());

			List<Map<String, Object>> fields = new ArrayList<Map<String, Object>>();
			for(Column column : this.columns) {
				Map<String, Object> fieldMap = new HashMap<String, Object>();

				fieldMap.put("lcColumnName", NameConverter.lowerCamel(column.getColumnName()));
				fieldMap.put("columnTitle", NameConverter.title(column.getColumnName()));

				fields.add(fieldMap);
			}
			root.put("fields", fields);

			out = new OutputStreamWriter(Files.newOutputStream(Paths.get(md.getViewPath(), getListViewName()+".fxml")), "UTF-8");
			template.process(root, out);

			out.close();
		} catch (Exception e) {
			log.error("Failed to get template.", e);
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
				}
			}
		}
	}

	public void generateViewEdit(MetaData md) {
		OutputStreamWriter out = null;
		try {
			var template = FreeMarkerConfig.instance().cfg().getTemplate("view-edit.ftl");
			Map<String, Object> root = new HashMap<String, Object>();

			root.put("lcTableName", lcTableName);
			root.put("controllerPkg", md.getControllerPkg());
			root.put("editControllerName", getEditControllerName());

			var inputAreaHeight = 50.0 * this.columns.size();
			root.put("inputAreaHeight", inputAreaHeight);
			root.put("totalHeight", inputAreaHeight + 80);
			root.put("buttonBarY", inputAreaHeight + 20);


			List<Map<String, Object>> fields = new ArrayList<Map<String, Object>>();
			for(Column column : this.columns) {
				Map<String, Object> fieldMap = new HashMap<String, Object>();

				fieldMap.put("lcColumnName", NameConverter.lowerCamel(column.getColumnName()));
				fieldMap.put("columnTitle", NameConverter.title(column.getColumnName()));

				fields.add(fieldMap);
			}
			root.put("fields", fields);

			out = new OutputStreamWriter(Files.newOutputStream(Paths.get(md.getViewPath(), getEditViewName()+".fxml")), "UTF-8");
			template.process(root, out);

			out.close();
		} catch (Exception e) {
			log.error("Failed to get template.", e);
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
				}
			}
		}
	}

	public void generateAuthSQL(MetaData md) {
		OutputStreamWriter out = null;
		try {
			var template = FreeMarkerConfig.instance().cfg().getTemplate("auth-sql.ftl");
			Map<String, Object> root = new HashMap<String, Object>();

			root.put("ucTableName", ucTableName);

			out = new OutputStreamWriter(Files.newOutputStream(Paths.get(md.getBaseFolder(), tableName+".sql")), "UTF-8");
			template.process(root, out);

			out.close();
		} catch (Exception e) {
			log.error("Failed to get template.", e);
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
				}
			}
		}
	}

	private String getModelName() {
		return this.ucTableName;
	}
	private String getRepositoryName() {
		return this.ucTableName + "Repository";
	}
	private String getRepositoryImplName() {
		return this.ucTableName + "RepositoryImpl";
	}
	private String getListControllerName() {
		return this.ucTableName + "ListController";
	}
	private String getEditControllerName() {
		return this.ucTableName + "EditController";
	}
	private String getListViewName() {
		return this.ucTableName + "List";
	}
	private String getEditViewName() {
		return this.ucTableName + "Edit";
	}
}
