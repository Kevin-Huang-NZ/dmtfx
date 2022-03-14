/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.mahara.stocker.dao;

import com.mahara.stocker.model.SysPage;
import com.mahara.stocker.util.PaginationIn;
import com.mahara.stocker.util.PaginationOut;

import java.util.List;

/**
 *
 * @author Kevin
 */
public interface SysPageRepository extends BaseRepository<SysPage, Long>{
    PaginationOut<SysPage> findByKeyWord(String keyWord, PaginationIn pi);
    int[] batchSave(List<SysPage> sysPages);
}
