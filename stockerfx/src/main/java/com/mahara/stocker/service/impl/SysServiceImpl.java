package com.mahara.stocker.service.impl;

import com.mahara.stocker.dao.SysFunRepository;
import com.mahara.stocker.dao.SysPageRepository;
import com.mahara.stocker.dao.SysRoleFunRepository;
import com.mahara.stocker.dao.SysRoleRepository;
import com.mahara.stocker.model.SysFun;
import com.mahara.stocker.model.SysPage;
import com.mahara.stocker.model.SysRole;
import com.mahara.stocker.service.SysService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SysServiceImpl implements SysService<Long> {

    @Autowired
    private SysPageRepository sysPageRepos;
    @Autowired
    private SysFunRepository sysFunRepos;
    @Autowired
    private SysRoleRepository sysRoleRepos;
    @Autowired
    private SysRoleFunRepository sysRoleFunRepos;

    @Override
    @Transactional
    public void deleteSysRole(Long id) {
        SysRole sysRole = sysRoleRepos.findById(id);
        if (sysRole != null) {
            sysRoleFunRepos.deleteByRoleNo(sysRole.getRoleNo());
            sysRoleRepos.deleteById(id);
        }
    }

    @Override
    @Transactional
    public void deleteSysFun(Long id) {
        SysFun sysFun = sysFunRepos.findById(id);
        if (sysFun != null) {
            sysRoleFunRepos.deleteByFunNo(sysFun.getFunNo());
            sysFunRepos.deleteById(id);
        }
    }

    @Override
    @Transactional
    public void deleteSysPage(Long id) {
        SysPage sysPage = sysPageRepos.findById(id);
        if (sysPage != null) {
            List<String> funNos = sysFunRepos.findFunNoByPageName(sysPage.getPageName());
            sysRoleFunRepos.deleteByFunNos(funNos);
            sysFunRepos.deleteByPageName(sysPage.getPageName());
            sysPageRepos.deleteById(id);
        }
    }
}
