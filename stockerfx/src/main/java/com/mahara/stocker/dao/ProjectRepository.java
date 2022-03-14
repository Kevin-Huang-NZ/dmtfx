package com.mahara.stocker.dao;

import com.mahara.stocker.model.Project;
import com.mahara.stocker.util.PaginationIn;
import com.mahara.stocker.util.PaginationOut;

import java.util.List;

public interface ProjectRepository extends BaseRepository<Project, Long>{
    PaginationOut<Project> findByKeyWord(String keyWord, PaginationIn pi);
    int[] batchSave(List<Project> beans);
}
