package com.mahara.stocker.dao;

import com.mahara.stocker.model.Standard;
import com.mahara.stocker.util.PaginationIn;
import com.mahara.stocker.util.PaginationOut;

import java.util.List;

public interface StandardRepository extends BaseRepository<Standard, Long>{
    PaginationOut<Standard> findByKeyWord(String keyWord, PaginationIn pi);
    int[] batchSave(List<Standard> beans);
}
