/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mahara.stocker.dao.jt;

import com.mahara.stocker.dao.SysPageRepository;
import com.mahara.stocker.model.SysPage;
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

/**
 *
 * @author Kevin
 */
@Repository("jtSysPageRepository")
public class SysPageRepositoryImpl implements SysPageRepository {
    private static final Logger log = LoggerFactory.getLogger(SysPageRepositoryImpl.class);

    private StringBuilder insertSql = new StringBuilder()
            .append("insert into sys_page ")
            .append("(page_name,page_title,memo) ")
            .append(" values ")
            .append("(:pageName,:pageTitle,:memo)");

    @Autowired
    private JDBCTemplateUtil jtUtil;

    private RowMapper<SysPage> rowMapper = (rs, row) -> {
        SysPage bean = new SysPage();
        bean.setId(rs.getLong("id"));
        bean.setPageName(rs.getString("page_name"));
        bean.setPageTitle(rs.getString("page_title"));
        bean.setMemo(rs.getString("memo"));
        return bean;
    };

    @Override
    public SysPage save(SysPage bean) {
        KeyHolder kh = new GeneratedKeyHolder();
        jtUtil.npjt().update(insertSql.toString(), new BeanPropertySqlParameterSource(bean), kh);
//        kh.getKeys().forEach((k, v) -> {
//            log.debug("{} is {}", k, v);
//        });
        bean.setId(((Number)kh.getKeys().get(jtUtil.generatedKeyName("id"))).longValue());
       return bean;
    }

    @Override
    public int update(SysPage bean) {
        StringBuilder sb = new StringBuilder();
        sb.append("update sys_page ");
        sb.append("set page_name=:pageName,page_title=:pageTitle,memo=:memo ");
        sb.append("where id=:id");
       return jtUtil.npjt().update(sb.toString(), new BeanPropertySqlParameterSource(bean));
    }

    @Override
    public int deleteById(Long id) {
        return jtUtil.jt().update("delete from sys_page where id=?", id);
    }

    @Override
    public SysPage findById(Long id) {
        var tmp = jtUtil.jt().query("select * from sys_page where id=?",
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
    public List<SysPage> findAll() {
        return jtUtil.jt().query("select * from sys_page",
                rowMapper);
    }

    @Override
    public PaginationOut<SysPage> findByKeyWord(String keyWord, PaginationIn pi) {
        String anyPosition = "%" + keyWord + "%";

        StringBuilder sql = new StringBuilder("select * from sys_page where 1=1 ");
        MapSqlParameterSource params = new MapSqlParameterSource();
        if (!StringUtils.isEmpty(keyWord)) {
            sql.append("AND (page_name like :pageName OR page_title like :pageTitle) ");
            params.addValue("pageName", anyPosition);
            params.addValue("pageTitle", anyPosition);
        }
        if (pi.getPageSize() == 0) {
            // 不分页
            List<SysPage> all = jtUtil.npjt().query(sql.toString(),
                params,
                rowMapper);
            return new PaginationOut(pi.getPageNo(), pi.getPageSize(), all);
        } else {
            return jtUtil.queryForPagination(sql.toString(),
                    params,
                    rowMapper,
                    pi
            );
        }
    }

    @Override
    public int[] batchSave(List<SysPage> sysPages) {
        return jtUtil.npjt().batchUpdate(insertSql.toString(), SqlParameterSourceUtils.createBatch(sysPages));
    }
}
