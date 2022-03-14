package com.mahara.stocker.dao.jt;

import com.mahara.stocker.dao.TransliterationRepository;
import com.mahara.stocker.model.Transliteration;
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
public class TransliterationRepositoryImpl implements TransliterationRepository {
    private static final Logger log = LoggerFactory.getLogger(TransliterationRepositoryImpl.class);

    private StringBuilder insertSql = new StringBuilder()
            .append("insert into transliteration ")
            .append("(standard_id,original,roman,match_way,match_params,chinese) ")
            .append(" values ")
            .append("(:standardId,:original,:roman,:matchWay,:matchParams,:chinese)");

    @Autowired
    private JDBCTemplateUtil jtUtil;

    private RowMapper<Transliteration> rowMapper = (rs, row) -> {
        Transliteration bean = new Transliteration();
        bean.setId(rs.getLong("id"));
        bean.setStandardId(rs.getLong("standard_id"));
        bean.setOriginal(rs.getString("original"));
        bean.setRoman(rs.getString("roman"));
        bean.setMatchWay(rs.getString("match_way"));
        bean.setMatchParams(rs.getString("match_params"));
        bean.setChinese(rs.getString("chinese"));
        return bean;
    };

    @Override
    public Transliteration save(Transliteration bean) {
        KeyHolder kh = new GeneratedKeyHolder();
        jtUtil.npjt().update(insertSql.toString(), new BeanPropertySqlParameterSource(bean), kh);
        bean.setId(((Number)kh.getKeys().get(jtUtil.generatedKeyName("id"))).longValue());
       return bean;
    }

    @Override
    public int update(Transliteration bean) {
        StringBuilder sb = new StringBuilder();
        sb.append("update transliteration set ");
        sb.append("standard_id=:standardId,original=:original,roman=:roman,match_way=:matchWay,match_params=:matchParams,chinese=:chinese ");
        sb.append("where id=:id");
       return jtUtil.npjt().update(sb.toString(), new BeanPropertySqlParameterSource(bean));
    }

    @Override
    public int deleteById(Long id) {
        return jtUtil.jt().update("delete from transliteration where id=?", id);
    }

    @Override
    public Transliteration findById(Long id) {
        var tmp = jtUtil.jt().query("select * from transliteration where id=?",
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
    public List<Transliteration> findAll() {
        return jtUtil.jt().query("select * from transliteration",
                rowMapper);
    }

    @Override
    public PaginationOut<Transliteration> findByStandard(Long standardId, PaginationIn pi) {
        StringBuilder sql = new StringBuilder("select * from transliteration where 1=1 ");
        MapSqlParameterSource params = new MapSqlParameterSource();
        if (standardId != null) {
            sql.append("AND standard_id=:standardId ");
            params.addValue("standardId", standardId);
        }

        return jtUtil.queryForPagination(sql.toString(),
                params,
                rowMapper,
                pi
        );
    }

    @Override
    public List<Transliteration> findByStandard(Long standardId) {
        StringBuilder sql = new StringBuilder("select * from transliteration where 1=1 ");
        MapSqlParameterSource params = new MapSqlParameterSource();
        if (standardId != null) {
            sql.append("AND standard_id=:standardId ");
            params.addValue("standardId", standardId);
        }

        return jtUtil.npjt().query(sql.toString(),
                params,
                rowMapper
        );
    }

    @Override
    public int[] batchSave(List<Transliteration> beans) {
        return jtUtil.npjt().batchUpdate(insertSql.toString(), SqlParameterSourceUtils.createBatch(beans));
    }

    @Override
    public void deleteByStandard(Long standardId) {
        jtUtil.jt().update("delete from transliteration where standard_id=?", standardId);
    }
}
