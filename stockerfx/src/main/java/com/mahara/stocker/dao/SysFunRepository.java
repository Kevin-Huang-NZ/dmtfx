/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.mahara.stocker.dao;

import com.mahara.stocker.model.SysFun;
import com.mahara.stocker.util.PaginationIn;
import com.mahara.stocker.util.PaginationOut;

import java.util.List;

/**
 *
 * @author Kevin
 */
public interface SysFunRepository extends BaseRepository<SysFun, Long>{
    PaginationOut<SysFun> findByPageName(String pageName, PaginationIn pi);
    List<String> findFunNoByPageName(String pageName);
    int deleteByPageName(String pageName);
}
