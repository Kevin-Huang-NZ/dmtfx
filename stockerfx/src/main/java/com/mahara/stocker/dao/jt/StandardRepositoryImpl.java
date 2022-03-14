package com.mahara.stocker.dao.jt;

import com.mahara.stocker.dao.StandardRepository;
import com.mahara.stocker.model.Standard;
import com.mahara.stocker.util.JDBCTemplateUtil;
import com.mahara.stocker.util.PaginationIn;
import com.mahara.stocker.util.PaginationOut;
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
public class StandardRepositoryImpl implements StandardRepository {
    private static final Logger log = LoggerFactory.getLogger(StandardRepositoryImpl.class);

    private StringBuilder insertSql = new StringBuilder()
            .append("insert into standard ")
            .append("(standard_name,version_code,publish_ymd,language_name,country_region,memo) ")
            .append(" values ")
            .append("(:standardName,:versionCode,:publishYmd,:languageName,:countryRegion,:memo)");

    @Autowired
    private JDBCTemplateUtil jtUtil;

    private RowMapper<Standard> rowMapper = (rs, row) -> {
        Standard bean = new Standard();
        bean.setId(rs.getLong("id"));
        bean.setStandardName(rs.getString("standard_name"));
        bean.setVersionCode(rs.getString("version_code"));
        bean.setPublishYmd(rs.getString("publish_ymd"));
        bean.setLanguageName(rs.getString("language_name"));
        bean.setCountryRegion(rs.getString("country_region"));
        bean.setMemo(rs.getString("memo"));
        return bean;
    };

    @Override
    public Standard save(Standard bean) {
        KeyHolder kh = new GeneratedKeyHolder();
        jtUtil.npjt().update(insertSql.toString(), new BeanPropertySqlParameterSource(bean), kh);
        bean.setId(((Number)kh.getKeys().get(jtUtil.generatedKeyName("id"))).longValue());
       return bean;
    }

    @Override
    public int update(Standard bean) {
        StringBuilder sb = new StringBuilder();
        sb.append("update standard set ");
        sb.append("standard_name=:standardName,version_code=:versionCode,publish_ymd=:publishYmd,language_name=:languageName,country_region=:countryRegion,memo=:memo ");
        sb.append("where id=:id");
       return jtUtil.npjt().update(sb.toString(), new BeanPropertySqlParameterSource(bean));
    }

    @Override
    public int deleteById(Long id) {
        return jtUtil.jt().update("delete from standard where id=?", id);
    }

    @Override
    public Standard findById(Long id) {
        var tmp = jtUtil.jt().query("select * from standard where id=?",
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
    public List<Standard> findAll() {
        return jtUtil.jt().query("select * from standard",
                rowMapper);
    }

    @Override
    public PaginationOut<Standard> findByKeyWord(String keyWord, PaginationIn pi) {
        String anyPosition = "%" + keyWord + "%";
        String endWith = "%" + keyWord;

        StringBuilder sql = new StringBuilder("select * from standard where 1=1 ");
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
    public int[] batchSave(List<Standard> beans) {
        return jtUtil.npjt().batchUpdate(insertSql.toString(), SqlParameterSourceUtils.createBatch(beans));
    }
}
