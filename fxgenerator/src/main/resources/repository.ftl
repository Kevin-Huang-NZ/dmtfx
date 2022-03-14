package [=repositoryPkg];

import [=modelPkg].[=modelName];
import [=basePackage].util.PaginationIn;
import [=basePackage].util.PaginationOut;

import java.util.List;

public interface [=repositoryName] extends BaseRepository<[=modelName], Long>{
    PaginationOut<[=modelName]> findByKeyWord(String keyWord, PaginationIn pi);
    int[] batchSave(List<[=modelName]> beans);
	<#if hasUniqueKey >
    [=modelName] findByUniqueKey(<#list ukFields as field>[=field.javaDataType] [=field.lcColumnName], </#list>Long notThisId);
    </#if>
}
