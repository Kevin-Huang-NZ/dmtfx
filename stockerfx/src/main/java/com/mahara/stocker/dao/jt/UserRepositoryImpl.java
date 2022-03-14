/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mahara.stocker.dao.jt;

import com.mahara.stocker.dao.UserRepository;
import com.mahara.stocker.model.User;
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
@Repository("jtUserRepository")
public class UserRepositoryImpl implements UserRepository {
    private static final Logger log = LoggerFactory.getLogger(UserRepositoryImpl.class);

    private StringBuilder insertSql = new StringBuilder()
            .append("insert into user ")
            .append("(user_name,avatar,gender,birthday,phone,password,roles,status) ")
            .append(" values ")
            .append("(:userName,:avatar,:gender,:birthday,:phone,:password,:roles,:status)");

    @Autowired
    private JDBCTemplateUtil jtUtil;

    private RowMapper<User> rowMapper = (rs, row) -> {
        User bean = new User();
        bean.setId(rs.getLong("id"));
        bean.setUserName(rs.getString("user_name"));
        bean.setAvatar(rs.getString("avatar"));
        bean.setGender(rs.getString("gender"));
        bean.setBirthday(rs.getString("birthday"));
        bean.setPhone(rs.getString("phone"));
        bean.setPassword(rs.getString("password"));
        bean.setRoles(rs.getString("roles"));
        bean.setStatus(rs.getString("status"));
        return bean;
    };

    @Override
    public User save(User bean) {
        KeyHolder kh = new GeneratedKeyHolder();
        jtUtil.npjt().update(insertSql.toString(), new BeanPropertySqlParameterSource(bean), kh);
        bean.setId(((Number)kh.getKeys().get(jtUtil.generatedKeyName("id"))).longValue());
       return bean;
    }

    @Override
    public int update(User bean) {
        StringBuilder sb = new StringBuilder();
        sb.append("update user ");
        sb.append("set user_name=:userName,avatar=:avatar,gender=:gender,birthday=:birthday ");
        sb.append(",phone=:phone,password=:password,roles=:roles,status=:status ");
        sb.append("where id=:id");
       return jtUtil.npjt().update(sb.toString(), new BeanPropertySqlParameterSource(bean));
    }

    @Override
    public int deleteById(Long id) {
        return jtUtil.jt().update("delete from user where id=?", id);
    }

    @Override
    public User findById(Long id) {
        var tmp = jtUtil.jt().query("select * from user where id=?",
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
    public List<User> findAll() {
        return jtUtil.jt().query("select * from user where phone <> '13811650908'",
                rowMapper);
    }

    @Override
    public PaginationOut<User> findByNameOrPhone(String keyWord, PaginationIn pi) {
        String anyPosition = "%" + keyWord + "%";
        String endWith = "%" + keyWord;

        StringBuilder sql = new StringBuilder("select * from user where phone <> '13811650908' ");
        MapSqlParameterSource params = new MapSqlParameterSource();
        if (!StringUtils.isEmpty(keyWord)) {
            sql.append("AND (user_name like :userName OR phone like :phone) ");
            params.addValue("userName", anyPosition);
            params.addValue("phone", endWith);
        }

        return jtUtil.queryForPagination(sql.toString(),
                params,
                rowMapper,
                pi
        );
    }

//    @Override
//    public User findByPhone(String phone) {
//        var tmp = jtUtil.jt().query("select * from user where phone=?",
//                rowMapper,
//                new Object[]{phone}
//        );
//        if (tmp == null || tmp.size() == 0) {
//            return null;
//        } else {
//            return tmp.get(0);
//        }
//    }

    @Override
    public User findByUniqueKey(String phone, Long id) {
        StringBuilder sql = new StringBuilder("select * from user where phone=:phone ");
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("phone", phone);
        if (id != null) {
            sql.append("AND id!=:id ");
            params.addValue("id", id);
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

    @Override
    public int[] batchSave(List<User> users) {
        return jtUtil.npjt().batchUpdate(insertSql.toString(), SqlParameterSourceUtils.createBatch(users));
    }
}
