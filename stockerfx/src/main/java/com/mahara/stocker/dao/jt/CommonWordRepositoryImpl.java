package com.mahara.stocker.dao.jt;

import com.mahara.stocker.dao.CommonWordRepository;
import com.mahara.stocker.model.CommonWord;
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
public class CommonWordRepositoryImpl implements CommonWordRepository {
    private static final Logger log = LoggerFactory.getLogger(CommonWordRepositoryImpl.class);

    private StringBuilder insertSql = new StringBuilder()
            .append("insert into common_word ")
            .append("(standard_id,original,original_abbr,original_type,roman,match_way,match_params,transliteration,free_translation) ")
            .append(" values ")
            .append("(:standardId,:original,:originalAbbr,:originalType,:roman,:matchWay,:matchParams,:transliteration,:freeTranslation)");

    @Autowired
    private JDBCTemplateUtil jtUtil;

    private RowMapper<CommonWord> rowMapper = (rs, row) -> {
        CommonWord bean = new CommonWord();
        bean.setId(rs.getLong("id"));
        bean.setStandardId(rs.getLong("standard_id"));
        bean.setOriginal(rs.getString("original"));
        bean.setOriginalAbbr(rs.getString("original_abbr"));
        bean.setOriginalType(rs.getString("original_type"));
        bean.setRoman(rs.getString("roman"));
        bean.setMatchWay(rs.getString("match_way"));
        bean.setMatchParams(rs.getString("match_params"));
        bean.setTransliteration(rs.getString("transliteration"));
        bean.setFreeTranslation(rs.getString("free_translation"));
        return bean;
    };

    @Override
    public CommonWord save(CommonWord bean) {
        KeyHolder kh = new GeneratedKeyHolder();
        jtUtil.npjt().update(insertSql.toString(), new BeanPropertySqlParameterSource(bean), kh);
        bean.setId(((Number)kh.getKeys().get(jtUtil.generatedKeyName("id"))).longValue());
       return bean;
    }

    @Override
    public int update(CommonWord bean) {
        StringBuilder sb = new StringBuilder();
        sb.append("update common_word set ");
        sb.append("standard_id=:standardId,original=:original,original_abbr=:originalAbbr,original_type=:originalType,roman=:roman,match_way=:matchWay,match_params=:matchParams,transliteration=:transliteration,free_translation=:freeTranslation ");
        sb.append("where id=:id");
       return jtUtil.npjt().update(sb.toString(), new BeanPropertySqlParameterSource(bean));
    }

    @Override
    public int deleteById(Long id) {
        return jtUtil.jt().update("delete from common_word where id=?", id);
    }

    @Override
    public CommonWord findById(Long id) {
        var tmp = jtUtil.jt().query("select * from common_word where id=?",
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
    public List<CommonWord> findAll() {
        return jtUtil.jt().query("select * from common_word",
                rowMapper);
    }

    @Override
    public PaginationOut<CommonWord> findByStandard(Long standardId, PaginationIn pi) {

        StringBuilder sql = new StringBuilder("select * from common_word where 1=1 ");
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
    public List<CommonWord> findByStandard(Long standardId) {

        StringBuilder sql = new StringBuilder("select * from common_word where 1=1 ");
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
    public int[] batchSave(List<CommonWord> beans) {
        return jtUtil.npjt().batchUpdate(insertSql.toString(), SqlParameterSourceUtils.createBatch(beans));
    }

    @Override
    public void deleteByStandard(Long standardId) {
        jtUtil.jt().update("delete from common_word where standard_id=?", standardId);
    }
}
