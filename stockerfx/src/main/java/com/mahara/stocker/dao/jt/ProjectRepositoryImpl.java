package com.mahara.stocker.dao.jt;

import com.mahara.stocker.dao.ProjectRepository;
import com.mahara.stocker.model.Project;
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
public class ProjectRepositoryImpl implements ProjectRepository {
    private static final Logger log = LoggerFactory.getLogger(ProjectRepositoryImpl.class);

    private StringBuilder insertSql = new StringBuilder()
            .append("insert into project ")
            .append("(standard_id,project_name,start_date,due_date,status,memo) ")
            .append(" values ")
            .append("(:standardId,:projectName,:startDate,:dueDate,:status,:memo)");

    @Autowired
    private JDBCTemplateUtil jtUtil;

    private RowMapper<Project> rowMapper = (rs, row) -> {
        Project bean = new Project();
        bean.setId(rs.getLong("id"));
        bean.setStandardId(rs.getLong("standard_id"));
        bean.setProjectName(rs.getString("project_name"));
        bean.setStartDate(rs.getString("start_date"));
        bean.setDueDate(rs.getString("due_date"));
        bean.setStatus(rs.getString("status"));
        bean.setMemo(rs.getString("memo"));
        return bean;
    };

    @Override
    public Project save(Project bean) {
        KeyHolder kh = new GeneratedKeyHolder();
        jtUtil.npjt().update(insertSql.toString(), new BeanPropertySqlParameterSource(bean), kh);
        bean.setId(((Number)kh.getKeys().get(jtUtil.generatedKeyName("id"))).longValue());
       return bean;
    }

    @Override
    public int update(Project bean) {
        StringBuilder sb = new StringBuilder();
        sb.append("update project set ");
        sb.append("standard_id=:standardId,project_name=:projectName,start_date=:startDate,due_date=:dueDate,status=:status,memo=:memo ");
        sb.append("where id=:id");
       return jtUtil.npjt().update(sb.toString(), new BeanPropertySqlParameterSource(bean));
    }

    @Override
    public int deleteById(Long id) {
        return jtUtil.jt().update("delete from project where id=?", id);
    }

    @Override
    public Project findById(Long id) {
        var tmp = jtUtil.jt().query("select * from project where id=?",
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
    public List<Project> findAll() {
        return jtUtil.jt().query("select * from project",
                rowMapper);
    }

    @Override
    public PaginationOut<Project> findByKeyWord(String keyWord, PaginationIn pi) {
        String anyPosition = "%" + keyWord + "%";
        String endWith = "%" + keyWord;

        StringBuilder sql = new StringBuilder("select * from project where 1=1 ");
        MapSqlParameterSource params = new MapSqlParameterSource();
        if (!StringUtils.isEmpty(keyWord)) {
             sql.append("AND (project_name like :projectName) ");
             params.addValue("projectName", anyPosition);
        }

        return jtUtil.queryForPagination(sql.toString(),
                params,
                rowMapper,
                pi
        );
    }

    @Override
    public int[] batchSave(List<Project> beans) {
        return jtUtil.npjt().batchUpdate(insertSql.toString(), SqlParameterSourceUtils.createBatch(beans));
    }
}
