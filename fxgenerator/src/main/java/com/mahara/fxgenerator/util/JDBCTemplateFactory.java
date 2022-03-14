package com.mahara.fxgenerator.util;

import com.alibaba.druid.pool.DruidDataSource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;

public class JDBCTemplateFactory {
    private static JDBCTemplateFactory instance;
    private JdbcTemplate jt;
    private String jdbcUrl;
    private String userName;
    private String password;

    private JDBCTemplateFactory() {
    }

    public static JDBCTemplateFactory build(String jdbcUrl, String userName, String password) {
        var needCreate = false;
        if (instance == null) {
            needCreate = true;
        } else {
            if (!StringUtils.equals(instance.jdbcUrl, jdbcUrl)
                || !StringUtils.equals(instance.userName, userName)
                || !StringUtils.equals(instance.password, password)) {
                needCreate = true;
            }
        }
        if (needCreate) {
            instance = new JDBCTemplateFactory();
            DruidDataSource dds = new DruidDataSource();
            dds.setDriverClassName("com.mysql.cj.jdbc.Driver");
            dds.setUrl(jdbcUrl);
            dds.setUsername(userName);
            dds.setPassword(password);
            instance.jt = new JdbcTemplate(dds);
        }
        return instance;
    }

    public static JDBCTemplateFactory getInstance() {
        return instance;
    }

    public JdbcTemplate jt() {
        return jt;
    }
}
