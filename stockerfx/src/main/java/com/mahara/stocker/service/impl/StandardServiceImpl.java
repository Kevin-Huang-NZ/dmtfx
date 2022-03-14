package com.mahara.stocker.service.impl;

import com.mahara.stocker.dao.*;
import com.mahara.stocker.service.StandardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StandardServiceImpl implements StandardService {

    @Autowired
    private StandardRepository standardRepository;
    @Autowired
    private RomanRepository romanRepository;
    @Autowired
    private CommonWordRepository commonWordRepository;
    @Autowired
    private TransliterationRepository transliterationRepository;

    @Override
    @Transactional
    public void deleteStandard(Long id) {
        standardRepository.deleteById(id);
        romanRepository.deleteByStandard(id);
        commonWordRepository.deleteByStandard(id);
        transliterationRepository.deleteByStandard(id);
    }
}
