/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.mahara.stocker.dao;

import com.mahara.stocker.model.SysRole;
import com.mahara.stocker.util.PaginationIn;
import com.mahara.stocker.util.PaginationOut;

import java.util.List;

/**
 *
 * @author Kevin
 */
public interface SysRoleRepository extends BaseRepository<SysRole, Long>{
    PaginationOut<SysRole> findByKeyWord(String keyWord, PaginationIn pi);
}
