package [=servicePkg];

import [=modelPkg].[=modelName];
import [=basePackage].error.CustomizedException;

import java.util.List;
import java.util.Optional;

public interface [=serviceName] {
  Optional<[=modelName]> findByUniqueKey(<#list ukFields as field>[=field.javaDataType] [=field.lcColumnName]<#sep> , </#sep></#list>);
  [=modelName] create([=modelName] entity) throws CustomizedException;
  [=modelName] update([=modelName] entity) throws CustomizedException;
}
