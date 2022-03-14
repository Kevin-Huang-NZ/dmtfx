/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.mahara.stocker.dao;

import java.util.List;

/**
 *
 * @author Kevin
 */
public interface BaseRepository<T, ID>  {
    T save(T t);
    int update(T t);
    int deleteById(ID id);
    T findById(ID id);
    List<T> findAll();
}
