package [=controllerPkg];

import [=basePackage].GlobalConst;
import [=modelPkg].[=modelName];
import [=repositoryPkg].[=repositoryName];
<#if hasUniqueKey>
import [=servicePkg].[=serviceName];
</#if>
import [=basePackage].error.CustomizedException;
import [=basePackage].error.PredefinedError;
import [=basePackage].web.request.PaginationIn;
import [=basePackage].web.response.Root;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;

@Tag(name = "[=tableName]")
@RestController
@RequestMapping("/api/[=lhTableName]")
public class [=controllerName] {
  @Autowired
  private [=repositoryName] repository;
<#if hasUniqueKey>
  @Autowired
  private [=serviceName] service;
</#if>

  @Operation(summary = "分页查询[=tableName]")
  @SecurityRequirement(name = GlobalConst.SECURITY_SCHEMES_KEY)
  @PreAuthorize("hasAnyAuthority('[=lhTableName]:*', '[=lhTableName]:retrieve')")
  @GetMapping("/")
  public Root search(
      @Parameter(name = "paginationIn", description = "分页信息，包含页码(number)和页大小(size)") @Validated
      PaginationIn paginationIn,
      @Parameter(name = "keyword", description = "查询关键字") @RequestParam(required = false)
      String keyword) {
    var searchResult = repository.search(keyword, paginationIn);
    return Root.create(searchResult);
  }

  @Operation(summary = "使用ID查询[=tableName]")
  @SecurityRequirement(name = GlobalConst.SECURITY_SCHEMES_KEY)
  @PreAuthorize("hasAnyAuthority('[=lhTableName]:*', '[=lhTableName]:retrieve')")
  @GetMapping("/{id}")
  public Root findOne(
      @Parameter(name = "id", description = "[=tableName]的ID") @PathVariable @Min(0l) Long id) {
    var bean = repository.findById(id);
    if (bean.isPresent()) {
      return Root.create(bean.get());
    } else {
      return Root.create(PredefinedError.DATA_NOT_EXIST);
    }
  }
<#if hasUniqueKey>
  @Operation(summary = "新建[=tableName]")
  @SecurityRequirement(name = GlobalConst.SECURITY_SCHEMES_KEY)
  @PreAuthorize("hasAnyAuthority('[=lhTableName]:*', '[=lhTableName]:create')")
  @PostMapping("/create")
  public Root create(@RequestBody @Validated(value = [=modelName].Create.class) [=modelName] entity)
      throws CustomizedException {
    return Root.create(service.create(entity));
  }

  @Operation(summary = "更新[=tableName]")
  @SecurityRequirement(name = GlobalConst.SECURITY_SCHEMES_KEY)
  @PreAuthorize("hasAnyAuthority('[=lhTableName]:*', '[=lhTableName]:update')")
  @PostMapping("/update")
  public Root update(@RequestBody @Validated(value = [=modelName].Update.class) [=modelName] entity)
      throws CustomizedException {
    return Root.create(service.update(entity));
  }
<#else>
  @Operation(summary = "新建[=tableName]")
  @SecurityRequirement(name = GlobalConst.SECURITY_SCHEMES_KEY)
  @PreAuthorize("hasAnyAuthority('[=lhTableName]:*', '[=lhTableName]:create')")
  @PostMapping("/create")
  public Root create(@RequestBody @Validated(value = [=modelName].Create.class) [=modelName] entity)
      throws CustomizedException {
    return Root.create(repository.save(entity));
  }

  @Operation(summary = "更新[=tableName]")
  @SecurityRequirement(name = GlobalConst.SECURITY_SCHEMES_KEY)
  @PreAuthorize("hasAnyAuthority('[=lhTableName]:*', '[=lhTableName]:update')")
  @PostMapping("/update")
  public Root update(@RequestBody @Validated(value = [=modelName].Update.class) [=modelName] entity)
      throws CustomizedException {
    return Root.create(repository.save(entity));
  }
</#if>

  @Operation(summary = "使用ID删除[=tableName]")
  @SecurityRequirement(name = GlobalConst.SECURITY_SCHEMES_KEY)
  @PreAuthorize("hasAnyAuthority('[=lhTableName]:*', '[=lhTableName]:delete')")
  @PostMapping("/{id}/del")
  public Root delete(
      @Parameter(name = "id", description = "[=tableName]的ID") @PathVariable @Min(0l) Long id) {
    repository.deleteById(id);
    return Root.create();
  }
}