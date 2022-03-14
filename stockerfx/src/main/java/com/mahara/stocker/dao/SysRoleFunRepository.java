/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.mahara.stocker.dao;

import com.mahara.stocker.model.SysRoleFun;

import java.util.List;

/**
 *
 * @author Kevin
 */
public interface SysRoleFunRepository extends BaseRepository<SysRoleFun, Long>{
    List<SysRoleFun> findByRoleNos(List<String> roleNos);
    int deleteByRoleNo(String roleNo);
    int deleteByFunNo(String funNo);
    int deleteByFunNos(List<String> funNos);
    int deleteByRoleFunNo(String roleNo, String funNo);
}
