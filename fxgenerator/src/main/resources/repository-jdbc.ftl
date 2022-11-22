package [=repositoryPkg];

import [=modelPkg].[=modelName];
import [=basePackage].web.request.PaginationIn;
import [=basePackage].web.response.PaginationOut;

import java.util.Optional;

public interface [=repositoryJdbcName] {
  PaginationOut<[=modelName]> search(String keyword, PaginationIn pi);
<#if hasUniqueKey>
  Optional<[=modelName]> findByUniqueKey(<#list ukFields as field>[=field.javaDataType] [=field.lcColumnName], </#list>Long id);
</#if>
}