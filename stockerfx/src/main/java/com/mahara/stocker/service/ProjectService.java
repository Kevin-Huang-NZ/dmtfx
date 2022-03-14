package com.mahara.stocker.service;

import com.mahara.stocker.model.Project;

public interface ProjectService {
    void deleteProject(Long id);
    void translate(Project project);
}
