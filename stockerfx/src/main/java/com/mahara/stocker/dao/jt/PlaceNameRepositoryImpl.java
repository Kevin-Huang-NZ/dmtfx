package com.mahara.stocker.dao.jt;

import com.mahara.stocker.dao.PlaceNameRepository;
import com.mahara.stocker.model.PlaceName;
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
public class PlaceNameRepositoryImpl implements PlaceNameRepository {
    private static final Logger log = LoggerFactory.getLogger(PlaceNameRepositoryImpl.class);

    private StringBuilder insertSql = new StringBuilder()
            .append("insert into place_name ")
            .append("(project_id,original,country,language,gec,memo,roman_status,roman,trans_status,transliteration,free_translation,emit_standard,trans_result) ")
            .append(" values ")
            .append("(:projectId,:original,:country,:language,:gec,:memo,:romanStatus,:roman,:transStatus,:transliteration,:freeTranslation,:emitStandard,:transResult)");

    @Autowired
    private JDBCTemplateUtil jtUtil;

    private RowMapper<PlaceName> rowMapper = (rs, row) -> {
        PlaceName bean = new PlaceName();
        bean.setId(rs.getLong("id"));
        bean.setProjectId(rs.getLong("project_id"));
        bean.setOriginal(rs.getString("original"));
        bean.setCountry(rs.getString("country"));
        bean.setLanguage(rs.getString("language"));
        bean.setGec(rs.getString("gec"));
        bean.setMemo(rs.getString("memo"));
        bean.setRomanStatus(rs.getString("roman_status"));
        bean.setRoman(rs.getString("roman"));
        bean.setTransStatus(rs.getString("trans_status"));
        bean.setTransliteration(rs.getString("transliteration"));
        bean.setFreeTranslation(rs.getString("free_translation"));
        bean.setEmitStandard(rs.getString("emit_standard"));
        bean.setTransResult(rs.getString("trans_result"));
        return bean;
    };

    @Override
    public PlaceName save(PlaceName bean) {
        KeyHolder kh = new GeneratedKeyHolder();
        jtUtil.npjt().update(insertSql.toString(), new BeanPropertySqlParameterSource(bean), kh);
        bean.setId(((Number)kh.getKeys().get(jtUtil.generatedKeyName("id"))).longValue());
       return bean;
    }

    @Override
    public int update(PlaceName bean) {
        StringBuilder sb = new StringBuilder();
        sb.append("update place_name set ");
        sb.append("project_id=:projectId,original=:original,country=:country,language=:language,gec=:gec,memo=:memo,roman_status=:romanStatus,roman=:roman,trans_status=:transStatus,transliteration=:transliteration,free_translation=:freeTranslation,emit_standard=:emitStandard,trans_result=:transResult ");
        sb.append("where id=:id");
       return jtUtil.npjt().update(sb.toString(), new BeanPropertySqlParameterSource(bean));
    }

    @Override
    public int deleteById(Long id) {
        return jtUtil.jt().update("delete from place_name where id=?", id);
    }

    @Override
    public PlaceName findById(Long id) {
        var tmp = jtUtil.jt().query("select * from place_name where id=?",
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
    public List<PlaceName> findAll() {
        return jtUtil.jt().query("select * from place_name",
                rowMapper);
    }

    @Override
    public PaginationOut<PlaceName> findByKeyWord(String keyWord, PaginationIn pi) {
        String anyPosition = "%" + keyWord + "%";
        String endWith = "%" + keyWord;

        StringBuilder sql = new StringBuilder("select * from place_name where 1=1 ");
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
    public int[] batchSave(List<PlaceName> beans) {
        return jtUtil.npjt().batchUpdate(insertSql.toString(), SqlParameterSourceUtils.createBatch(beans));
    }

    @Override
    public void deleteByProject(Long projectId) {
        jtUtil.jt().update("delete from place_name where project_id=?", projectId);
    }

    @Override
    public PaginationOut<PlaceName> findByProject(Long projectId, PaginationIn pi) {
        StringBuilder sql = new StringBuilder("select * from place_name where project_id=:projectId ");
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("projectId", projectId);

        return jtUtil.queryForPagination(sql.toString(),
                params,
                rowMapper,
                pi
        );
    }

    @Override
    public int[] batchUpdateResult(List<PlaceName> beans) {
        StringBuilder sb = new StringBuilder();
        sb.append("update place_name set ");
        sb.append("roman=:roman,trans_result=:transResult ");
        sb.append("where project_id=:projectId and original=:original");
        return jtUtil.npjt().batchUpdate(sb.toString(), SqlParameterSourceUtils.createBatch(beans));
    }

    @Override
    public int[] batchUpdateRoman(List<PlaceName> beans) {
        StringBuilder sb = new StringBuilder();
        sb.append("update place_name set ");
        sb.append("roman_status=:romanStatus,roman=:roman ");
        sb.append("where id=:id");
        return jtUtil.npjt().batchUpdate(sb.toString(), SqlParameterSourceUtils.createBatch(beans));
    }

    @Override
    public int[] batchUpdateAutoTrans(List<PlaceName> beans) {
        StringBuilder sb = new StringBuilder();
        sb.append("update place_name set ");
        sb.append("transliteration=:transliteration,free_translation=:freeTranslation,emit_standard=:emitStandard ");
        sb.append("where id=:id");
        return jtUtil.npjt().batchUpdate(sb.toString(), SqlParameterSourceUtils.createBatch(beans));
    }

}
