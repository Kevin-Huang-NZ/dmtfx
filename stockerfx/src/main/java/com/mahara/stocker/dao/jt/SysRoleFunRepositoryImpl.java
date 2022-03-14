/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mahara.stocker.dao.jt;

import com.mahara.stocker.dao.SysRoleFunRepository;
import com.mahara.stocker.model.SysRoleFun;
import com.mahara.stocker.util.JDBCTemplateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Kevin
 */
@Repository("jtSysRoleFunRepository")
public class SysRoleFunRepositoryImpl implements SysRoleFunRepository {
    private static final Logger log = LoggerFactory.getLogger(SysRoleFunRepositoryImpl.class);
    
    @Autowired
    private JDBCTemplateUtil jtUtil;

    private RowMapper<SysRoleFun> rowMapper = (rs, row) -> {
        SysRoleFun bean = new SysRoleFun();
        bean.setId(rs.getLong("id"));
        bean.setRoleNo(rs.getString("role_no"));
        bean.setFunNo(rs.getString("fun_no"));
        return bean;
    };

    @Override
    public SysRoleFun save(SysRoleFun bean) {
        KeyHolder kh = new GeneratedKeyHolder();
        StringBuilder sb = new StringBuilder();
        sb.append("insert into sys_role_fun ");
        sb.append("(role_no,fun_no) ");
        sb.append(" values ");
        sb.append("(:roleNo,:funNo)");
        jtUtil.npjt().update(sb.toString(), new BeanPropertySqlParameterSource(bean), kh);
        bean.setId(((Number)kh.getKeys().get(jtUtil.generatedKeyName("id"))).longValue());
       return bean;
    }

    @Override
    public int update(SysRoleFun bean) {
        StringBuilder sb = new StringBuilder();
        sb.append("update sys_role_fun ");
        sb.append("set role_no=:roleNo,fun_no=:funNo ");
        sb.append("where id=:id");
       return jtUtil.npjt().update(sb.toString(), new BeanPropertySqlParameterSource(bean));
    }

    @Override
    public int deleteById(Long id) {
        return jtUtil.jt().update("delete from sys_role_fun where id=?", id);
    }

    @Override
    public SysRoleFun findById(Long id) {
        var tmp= jtUtil.jt().query("select * from sys_role_fun where id=?",
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
    public List<SysRoleFun> findAll() {
        return jtUtil.jt().query("select * from sys_role_fun",
                rowMapper);
    }

    @Override
    public List<SysRoleFun> findByRoleNos(List<String> roleNos) {
        if (roleNos == null || roleNos.isEmpty()) {
            return new ArrayList<SysRoleFun>();
        }
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("roleNo", roleNos);
        
        return jtUtil.npjt().query("select * from sys_role_fun where role_no in (:roleNo)",
                params,
                rowMapper
            );
    }

    @Override
    public int deleteByRoleNo(String roleNo) {
        return jtUtil.jt().update("delete from sys_role_fun where role_no=?", roleNo);
    }

    @Override
    public int deleteByFunNo(String funNo) {
        return jtUtil.jt().update("delete from sys_role_fun where fun_no=?", funNo);
    }

    @Override
    public int deleteByFunNos(List<String> funNos) {
        if (funNos == null || funNos.isEmpty()) {
            return 0;
        }
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("funNo", funNos);

        return jtUtil.npjt().update("delete from sys_role_fun where fun_no in (:funNo)",
                params
        );
    }

    @Override
    public int deleteByRoleFunNo(String roleNo, String funNo) {
        return jtUtil.jt().update("delete from sys_role_fun where role_no=? and fun_no=?", roleNo, funNo);
    }
}
