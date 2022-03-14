package com.mahara.stocker.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("jtUtil")
public class JDBCTemplateUtilMysqlImpl implements JDBCTemplateUtil {
    @Autowired
    private JdbcTemplate jdbcTmpl;
    @Autowired
    private NamedParameterJdbcTemplate npJdbcTmpl;

    @Override
    public JdbcTemplate jt() {
        return jdbcTmpl;
    }

    @Override
    public NamedParameterJdbcTemplate npjt() {
        return npJdbcTmpl;
    }

    @Override
    public <T> PaginationOut<T> queryForPagination(String sql, SqlParameterSource paramSource, RowMapper<T> rowMapper, PaginationIn paginationIn) {
        String countSql ="select count(1) from  ( "+ sql +" ) count_view";
        int totalSize = npJdbcTmpl.queryForObject(countSql, paramSource, Integer.class);
        int totalPage = (totalSize + paginationIn.getPageSize() - 1) / paginationIn.getPageSize();

        int offset = (paginationIn.getPageNo() - 1) * paginationIn.getPageSize();
        int rowCount = paginationIn.getPageSize();
        StringBuilder pageSql = new StringBuilder(sql);
        pageSql.append(" limit ").append(offset).append(",").append(rowCount);
        List<T> data = npJdbcTmpl.query(pageSql.toString(), paramSource, rowMapper);

        PaginationOut paginationOut = new PaginationOut(paginationIn.getPageNo(), paginationIn.getPageSize());
        paginationOut.setTotalSize(totalSize);
        paginationOut.setTotalPage(totalPage);
        paginationOut.setData(data);
        return paginationOut;
    }

    public String generatedKeyName(String columnName) {
        return "GENERATED_KEY";
    }
}
