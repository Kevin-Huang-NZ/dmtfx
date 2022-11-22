package [=serviceImplPkg];

import [=modelPkg].[=modelName];
import [=repositoryPkg].[=repositoryName];
import [=servicePkg].[=serviceName];
import [=basePackage].error.CustomizedException;
import [=basePackage].error.PredefinedError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class [=serviceImplName] implements [=serviceName] {
  @Autowired
  private [=repositoryName] repository;

  @Override
  public Optional<[=modelName]> findByUniqueKey(<#list ukFields as field>[=field.javaDataType] [=field.lcColumnName]<#sep> , </#sep></#list>) {
    return repository.findByUniqueKey(phone, null);
  }

  @Override
  public [=modelName] create([=modelName] entity) throws CustomizedException {
    var checkExist = repository.findByUniqueKey(<#list ukFields as field>entity.get[=field.ucColumnName](), </#list>0l);
    if (checkExist.isPresent()) {
      throw new CustomizedException(PredefinedError.DATA_NOT_EXIST, "数据违反唯一性约束，保存失败。");
    } else {
      return repository.save(entity);
    }
  }

  @Transactional
  @Override
  public [=modelName] update([=modelName] entity) throws CustomizedException {
    var checkExist = repository.findByUniqueKey(<#list ukFields as field>entity.get[=field.ucColumnName](), </#list>entity.getId());
    if (checkExist.isPresent()) {
      throw new CustomizedException(PredefinedError.DATA_NOT_EXIST, "数据违反唯一性约束，保存失败。");
    } else {
      return repository.save(entity);
    }
  }
}