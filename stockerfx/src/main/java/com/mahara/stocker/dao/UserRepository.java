/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.mahara.stocker.dao;

import com.mahara.stocker.model.User;
import com.mahara.stocker.util.PaginationIn;
import com.mahara.stocker.util.PaginationOut;

import java.util.List;

/**
 *
 * @author Kevin
 */
public interface UserRepository extends BaseRepository<User, Long>{
    PaginationOut<User> findByNameOrPhone(String keyWord, PaginationIn pi);
    int[] batchSave(List<User> users);
    User findByUniqueKey(String phone, Long id);
}
