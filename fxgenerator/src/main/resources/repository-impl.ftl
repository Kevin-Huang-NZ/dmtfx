package [=repositoryImplPkg];

import [=repositoryPkg].[=repositoryName];
import [=modelPkg].[=modelName];
import [=basePackage].util.JDBCTemplateUtil;
import [=basePackage].util.PaginationIn;
import [=basePackage].util.PaginationOut;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class [=repositoryImplName] implements [=repositoryName] {
    private static final Logger log = LoggerFactory.getLogger([=repositoryImplName].class);

    private StringBuilder insertSql = new StringBuilder()
            .append("insert into [=tableName] ")
            .append("(<#list fields as field>[=field.columnName]<#sep>,</#sep></#list>) ")
            .append(" values ")
            .append("(<#list fields as field>:[=field.lcColumnName]<#sep>,</#sep></#list>)");

    @Autowired
    private JDBCTemplateUtil jtUtil;

    private RowMapper<[=modelName]> rowMapper = (rs, row) -> {
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
    public [=modelName] save([=modelName] bean) {
        KeyHolder kh = new GeneratedKeyHolder();
        jtUtil.npjt().update(insertSql.toString(), new BeanPropertySqlParameterSource(bean), kh);
        bean.setId(((Number)kh.getKeys().get(jtUtil.generatedKeyName("id"))).longValue());
       return bean;
    }

    @Override
    public int update([=modelName] bean) {
        StringBuilder sb = new StringBuilder();
        sb.append("update [=tableName] set ");
        sb.append("<#list fields as field>[=field.columnName]=:[=field.lcColumnName]<#sep>,</#sep></#list> ");
        sb.append("where id=:id");
       return jtUtil.npjt().update(sb.toString(), new BeanPropertySqlParameterSource(bean));
    }

    @Override
    public int deleteById(Long id) {
        return jtUtil.jt().update("delete from [=tableName] where id=?", id);
    }

    @Override
    public [=modelName] findById(Long id) {
        var tmp = jtUtil.jt().query("select * from [=tableName] where id=?",
                rowMapper,
                new Object[]{id}
            );
        if (tmp == null || tmp.size() == 0) {
            return null;
        } else {
            return tmp.get(0);
        }
    }

    @Override
    public List<[=modelName]> findAll() {
        return jtUtil.jt().query("select * from [=tableName]",
                rowMapper);
    }

    @Override
    public PaginationOut<[=modelName]> findByKeyWord(String keyWord, PaginationIn pi) {
        String anyPosition = "%" + keyWord + "%";
        String endWith = "%" + keyWord;

        StringBuilder sql = new StringBuilder("select * from [=tableName] where 1=1 ");
        MapSqlParameterSource params = new MapSqlParameterSource();
        if (!StringUtils.isEmpty(keyWord)) {
            // sql.append("AND (user_name like :userName OR phone like :phone) ");
            // params.addValue("userName", anyPosition);
            // params.addValue("phone", endWith);
        }

        return jtUtil.queryForPagination(sql.toString(),
                params,
                rowMapper,
                pi
        );
    }

    @Override
    public int[] batchSave(List<[=modelName]> beans) {
        return jtUtil.npjt().batchUpdate(insertSql.toString(), SqlParameterSourceUtils.createBatch(beans));
    }
    <#if hasUniqueKey >

    @Override
    public [=modelName] findByUniqueKey(<#list ukFields as field>[=field.javaDataType] [=field.lcColumnName], </#list>Long notThisId) {
        StringBuilder sql = new StringBuilder("select * from [=tableName] where <#list ukFields as field>[=field.columnName]=:[=field.lcColumnName]<#sep>,</#sep></#list> ");
        MapSqlParameterSource params = new MapSqlParameterSource();
        <#list ukFields as field>
        params.addValue("[=field.lcColumnName]", [=field.lcColumnName]);
        </#list>
        if (notThisId != null) {
            sql.append("AND id!=:notThisId ");
            params.addValue("notThisId", notThisId);
        }
        var tmp= jtUtil.npjt().query(sql.toString(),
                params,
                rowMapper
        );
        if (tmp == null || tmp.size() == 0) {
            return null;
        } else {
            return tmp.get(0);
        }
    }
    </#if>
}
