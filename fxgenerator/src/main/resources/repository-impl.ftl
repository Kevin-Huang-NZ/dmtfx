package [=repositoryImplPkg];

import [=repositoryPkg].[=repositoryJdbcName];
import [=modelPkg].[=modelName];
import [=basePackage].util.JDBCTemplateUtil;
import [=basePackage].web.request.PaginationIn;
import [=basePackage].web.response.PaginationOut;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class [=repositoryJdbcImplName] implements [=repositoryJdbcName] {
  @Autowired
  private JDBCTemplateUtil jtUtil;

  private RowMapper<[=modelName]> rowMapper =
      (rs, row) -> {
        [=modelName] bean = new [=modelName]();
        bean.setId(rs.getLong("id"));
<#list fields as field>
    <#switch field.javaDataType>
        <#case 'String'>
        bean.set[=field.ucColumnName](rs.getString("[=field.columnName]"));
            <#break>
        <#case 'Long'>
        bean.set[=field.ucColumnName](rs.getLong("[=field.columnName]"));
            <#break>
        <#case 'Integer'>
        bean.set[=field.ucColumnName](rs.getInt("[=field.columnName]"));
            <#break>
        <#case 'BigDecimal'>
        bean.set[=field.ucColumnName](rs.getBigDecimal("[=field.columnName]"));
            <#break>
        <#case 'Timestamp'>
        bean.set[=field.ucColumnName](rs.getTimestamp("[=field.columnName]"));
            <#break>
        <#default>
        bean.set[=field.ucColumnName](([=field.javaDataType]) rs.getObject("[=field.columnName]"));
    </#switch>
</#list>
        return bean;
      };

  @Override
  public PaginationOut<[=modelName]> search(String keyword, PaginationIn pi) {
    String anyPosition = "%" + keyword + "%";
    String endWith = "%" + keyword;
    String startWith = keyword + "%";

    StringBuilder sql = new StringBuilder("select * from [=tableName] where 1=1 ");
    MapSqlParameterSource params = new MapSqlParameterSource();
    if (!StringUtils.isEmpty(keyword)) {
      // sql.append("and (user_name like :userName or phone like :phone) ");
      // params.addValue("userName", anyPosition);
      // params.addValue("phone", endWith);
    }

    return jtUtil.queryForPagination(sql.toString(), params, rowMapper, pi);
  }
<#if hasUniqueKey>
  @Override
  public Optional<User> findByUniqueKey(<#list ukFields as field>[=field.javaDataType] [=field.lcColumnName], </#list>Long id) {
    StringBuilder sql = new StringBuilder("select * from [=tableName] where <#list ukFields as field>[=field.columnName]=:[=field.lcColumnName]<#sep> and </#sep></#list>");
    MapSqlParameterSource params = new MapSqlParameterSource();
<#list ukFields as field>
    params.addValue("[=field.lcColumnName]", [=field.lcColumnName]);
</#list>
    if (id != null) {
      sql.append("AND id!=:id ");
      params.addValue("id", id);
    }
    var tmp = jtUtil.npjt().query(sql.toString(), params, rowMapper);
    if (tmp == null || tmp.size() == 0) {
      return Optional.ofNullable(null);
    } else {
      return Optional.ofNullable(tmp.get(0));
    }
  }
</#if>
}