package com.mahara.stocker.dao;

import com.mahara.stocker.model.Roman;
import com.mahara.stocker.util.PaginationIn;
import com.mahara.stocker.util.PaginationOut;

import java.util.List;

public interface RomanRepository extends BaseRepository<Roman, Long>{
    PaginationOut<Roman> findByStandard(Long standardId, PaginationIn pi);
    List<Roman> findByStandard(Long standardId);
    int[] batchSave(List<Roman> beans);
    void deleteByStandard(Long standardId);
}
