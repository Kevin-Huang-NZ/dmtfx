package com.mahara.stocker.dao;

import com.mahara.stocker.model.CommonWord;
import com.mahara.stocker.util.PaginationIn;
import com.mahara.stocker.util.PaginationOut;

import java.util.List;

public interface CommonWordRepository extends BaseRepository<CommonWord, Long>{
    PaginationOut<CommonWord> findByStandard(Long standardId, PaginationIn pi);
    List<CommonWord> findByStandard(Long standardId);
    int[] batchSave(List<CommonWord> beans);
    void deleteByStandard(Long standardId);
}
