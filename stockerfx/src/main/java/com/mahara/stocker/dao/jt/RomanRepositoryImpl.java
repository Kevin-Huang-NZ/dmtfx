package com.mahara.stocker.dao.jt;

import com.mahara.stocker.dao.RomanRepository;
import com.mahara.stocker.model.Roman;
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
public class RomanRepositoryImpl implements RomanRepository {
    private static final Logger log = LoggerFactory.getLogger(RomanRepositoryImpl.class);

    private StringBuilder insertSql = new StringBuilder()
            .append("insert into roman ")
            .append("(standard_id,original_alpha,roman_alpha) ")
            .append(" values ")
            .append("(:standardId,:originalAlpha,:romanAlpha)");

    @Autowired
    private JDBCTemplateUtil jtUtil;

    private RowMapper<Roman> rowMapper = (rs, row) -> {
        Roman bean = new Roman();
        bean.setId(rs.getLong("id"));
        bean.setStandardId(rs.getLong("standard_id"));
        bean.setOriginalAlpha(rs.getString("original_alpha"));
        bean.setRomanAlpha(rs.getString("roman_alpha"));
        return bean;
    };

    @Override
    public Roman save(Roman bean) {
        KeyHolder kh = new GeneratedKeyHolder();
        jtUtil.npjt().update(insertSql.toString(), new BeanPropertySqlParameterSource(bean), kh);
        bean.setId(((Number)kh.getKeys().get(jtUtil.generatedKeyName("id"))).longValue());
       return bean;
    }

    @Override
    public int update(Roman bean) {
        StringBuilder sb = new StringBuilder();
        sb.append("update roman set ");
        sb.append("standard_id=:standardId,original_alpha=:originalAlpha,roman_alpha=:romanAlpha ");
        sb.append("where id=:id");
       return jtUtil.npjt().update(sb.toString(), new BeanPropertySqlParameterSource(bean));
    }

    @Override
    public int deleteById(Long id) {
        return jtUtil.jt().update("delete from roman where id=?", id);
    }

    @Override
    public Roman findById(Long id) {
        var tmp = jtUtil.jt().query("select * from roman where id=?",
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
    public List<Roman> findAll() {
        return jtUtil.jt().query("select * from roman",
                rowMapper);
    }

    @Override
    public PaginationOut<Roman> findByStandard(Long standardId, PaginationIn pi) {

        StringBuilder sql = new StringBuilder("select * from roman where 1=1 ");
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
    public List<Roman> findByStandard(Long standardId) {

        StringBuilder sql = new StringBuilder("select * from roman where 1=1 ");
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
    public int[] batchSave(List<Roman> beans) {
        return jtUtil.npjt().batchUpdate(insertSql.toString(), SqlParameterSourceUtils.createBatch(beans));
    }

    @Override
    public void deleteByStandard(Long standardId) {
        jtUtil.jt().update("delete from roman where standard_id=?", standardId);
    }
}
