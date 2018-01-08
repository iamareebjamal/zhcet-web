package amu.zhcet.data.department;

import amu.zhcet.common.error.DuplicateException;
import amu.zhcet.common.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class DepartmentService {

    private final DepartmentRepository departmentRepository;

    @Autowired
    public DepartmentService(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    public List<Department> findAll() {
        return departmentRepository.findAll();
    }

    public Department findOne(String code) {
        return departmentRepository.findOne(code);
    }

    public Optional<Department> findByName(String name) {
        return departmentRepository.findByName(name);
    }

    public boolean existsName(String departmentName) {
        return departmentRepository.findByName(StringUtils.capitalizeFirst(departmentName)) != null;
    }

    public boolean existsCode(String code) {
        return departmentRepository.findOne(StringUtils.capitalizeAll(code)) != null;
    }

    public void addDepartment(Department department) {
        if (existsCode(department.getCode())) {
            log.warn("Duplicate Department", department.getCode());
            throw new DuplicateException("Department", "code", department.getCode(), department);
        }

        if (existsName(department.getName())) {
            log.warn("Duplicate Department", department.getName());
            throw new DuplicateException("Department", "name", department.getName(), department);
        }

        department.setCode(StringUtils.capitalizeAll(department.getCode()));
        department.setName(StringUtils.capitalizeFirst(department.getName()));
        departmentRepository.save(department);
    }
}