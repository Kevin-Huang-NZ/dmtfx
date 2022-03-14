/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mahara.stocker.dao.jt;

import com.mahara.stocker.dao.SysFunRepository;
import com.mahara.stocker.model.SysFun;
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
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 *
 * @author Kevin
 */
@Repository("jtSysFunRepository")
public class SysFunRepositoryImpl implements SysFunRepository {
    private static final Logger log = LoggerFactory.getLogger(SysFunRepositoryImpl.class);
    
    @Autowired
    private JDBCTemplateUtil jtUtil;

    private RowMapper<SysFun> rowMapper = (rs, row) -> {
        SysFun bean = new SysFun();
        bean.setId(rs.getLong("id"));
        bean.setFunNo(rs.getString("fun_no"));
        bean.setPageName(rs.getString("page_name"));
        bean.setActionType(rs.getString("action_type"));
        bean.setActionNo(rs.getString("action_no"));
        bean.setActionName(rs.getString("action_name"));
        bean.setMemo(rs.getString("memo"));
        return bean;
    };

    @Override
    public SysFun save(SysFun bean) {
        KeyHolder kh = new GeneratedKeyHolder();
        StringBuilder sb = new StringBuilder();
        sb.append("insert into sys_fun ");
        sb.append("(fun_no,page_name,action_type,action_no,action_name,memo) ");
        sb.append(" values ");
        sb.append("(:funNo,:pageName,:actionType,:actionNo,:actionName,:memo)");
        jtUtil.npjt().update(sb.toString(), new BeanPropertySqlParameterSource(bean), kh);
        bean.setId(((Number)kh.getKeys().get(jtUtil.generatedKeyName("id"))).longValue());
       return bean;
    }

    @Override
    public int update(SysFun bean) {
        StringBuilder sb = new StringBuilder();
        sb.append("update sys_fun ");
        sb.append("set fun_no=:funNo,page_name=:pageName,action_type=:actionType,action_no=:actionNo,action_name=:actionName,memo=:memo ");
        sb.append("where id=:id");
       return jtUtil.npjt().update(sb.toString(), new BeanPropertySqlParameterSource(bean));
    }

    @Override
    public int deleteById(Long id) {
        return jtUtil.jt().update("delete from sys_fun where id=?", id);
    }

    @Override
    public SysFun findById(Long id) {
        var tmp = jtUtil.jt().query("select * from sys_fun where id=?",
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
    public List<SysFun> findAll() {
        return jtUtil.jt().query("select * from sys_fun",
                rowMapper);
    }

    @Override
    public PaginationOut<SysFun> findByPageName(String pageName, PaginationIn pi) {
        StringBuilder sql = new StringBuilder("select * from sys_fun where 1=1 ");
        MapSqlParameterSource params = new MapSqlParameterSource();
        if (!StringUtils.isEmpty(pageName)) {
            sql.append("AND page_name = :pageName ");
            params.addValue("pageName", pageName);
        }
        
        return jtUtil.queryForPagination(sql.toString(),
                params,
                rowMapper,
                pi
            );
    }

    @Override
    public List<String> findFunNoByPageName(String pageName) {
        StringBuilder sql = new StringBuilder("select fun_no from sys_fun where page_name = :pageName ");
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("pageName", pageName);

        return jtUtil.npjt().query(sql.toString(),
                params,
                (rs, row) -> rs.getString("fun_no")
            );
    }

    @Override
    public int deleteByPageName(String pageName) {
        return jtUtil.jt().update("delete from sys_fun where page_name=?", pageName);
    }
}
