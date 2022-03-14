package com.mahara.stocker.dao;

import com.mahara.stocker.model.Transliteration;
import com.mahara.stocker.util.PaginationIn;
import com.mahara.stocker.util.PaginationOut;

import java.util.List;

public interface TransliterationRepository extends BaseRepository<Transliteration, Long>{
    PaginationOut<Transliteration> findByStandard(Long standardId, PaginationIn pi);
    List<Transliteration> findByStandard(Long standardId);
    int[] batchSave(List<Transliteration> beans);
    void deleteByStandard(Long standardId);
}
