package com.mahara.stocker.service;

public interface SysService<ID> {
    void deleteSysRole(ID id);
    void deleteSysFun(ID id);
    void deleteSysPage(ID id);
}
