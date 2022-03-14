package com.mahara.stocker.dao;

import com.mahara.stocker.model.PlaceName;
import com.mahara.stocker.util.PaginationIn;
import com.mahara.stocker.util.PaginationOut;

import java.util.List;

public interface PlaceNameRepository extends BaseRepository<PlaceName, Long>{
    PaginationOut<PlaceName> findByKeyWord(String keyWord, PaginationIn pi);
    int[] batchSave(List<PlaceName> beans);
    void deleteByProject(Long projectId);
    PaginationOut<PlaceName> findByProject(Long projectId, PaginationIn pi);
    int[] batchUpdateResult(List<PlaceName> beans);
    int[] batchUpdateRoman(List<PlaceName> beans);
    int[] batchUpdateAutoTrans(List<PlaceName> beans);
}
