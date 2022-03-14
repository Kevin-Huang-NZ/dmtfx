/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mahara.stocker.dao.jt;

import com.mahara.stocker.dao.SysRoleRepository;
import com.mahara.stocker.model.SysRole;
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
@Repository("jtSysRoleRepository")
public class SysRoleRepositoryImpl implements SysRoleRepository {
    private static final Logger log = LoggerFactory.getLogger(SysRoleRepositoryImpl.class);
    
    @Autowired
    private JDBCTemplateUtil jtUtil;

    private RowMapper<SysRole> rowMapper = (rs, row) -> {
        SysRole bean = new SysRole();
        bean.setId(rs.getLong("id"));
        bean.setRoleNo(rs.getString("role_no"));
        bean.setRoleName(rs.getString("role_name"));
        bean.setMemo(rs.getString("memo"));
        return bean;
    };

    @Override
    public SysRole save(SysRole bean) {
        KeyHolder kh = new GeneratedKeyHolder();
        StringBuilder sb = new StringBuilder();
        sb.append("insert into sys_role ");
        sb.append("(role_no,role_name,memo) ");
        sb.append(" values ");
        sb.append("(:roleNo,:roleName,:memo)");
        jtUtil.npjt().update(sb.toString(), new BeanPropertySqlParameterSource(bean), kh);
        bean.setId(((Number)kh.getKeys().get(jtUtil.generatedKeyName("id"))).longValue());
       return bean;
    }

    @Override
    public int update(SysRole bean) {
        StringBuilder sb = new StringBuilder();
        sb.append("update sys_role ");
        sb.append("set role_no=:roleNo,role_name=:roleName,memo=:memo ");
        sb.append("where id=:id");
       return jtUtil.npjt().update(sb.toString(), new BeanPropertySqlParameterSource(bean));
    }

    @Override
    public int deleteById(Long id) {
        return jtUtil.jt().update("delete from sys_role where id=?", id);
    }

    @Override
    public SysRole findById(Long id) {
        var tmp = jtUtil.jt().query("select * from sys_role where id=?",
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
    public List<SysRole> findAll() {
        return jtUtil.jt().query("select * from sys_role where role_no <> 'Z'",
                rowMapper);
    }

    @Override
    public PaginationOut<SysRole> findByKeyWord(String keyWord, PaginationIn pi) {
        String anyPosition = "%" + keyWord + "%";

        StringBuilder sql = new StringBuilder("select * from sys_role where role_no <> 'Z' ");
        MapSqlParameterSource params = new MapSqlParameterSource();
        if (!StringUtils.isEmpty(keyWord)) {
            sql.append("AND (role_no like :roleNo OR role_name like :roleName) ");
            params.addValue("roleNo", anyPosition);
            params.addValue("roleName", anyPosition);
        }

        return jtUtil.queryForPagination(sql.toString(),
                params,
                rowMapper,
                pi
        );
    }
}
