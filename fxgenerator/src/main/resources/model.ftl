package [=modelPkg];

import cn.gov.mca.dmtp.util.ValidatorPattern;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Pattern;
import javax.validation.groups.Default;
import java.io.Serializable;

@Entity
@Data
@NoArgsConstructor
public class [=modelName] implements Serializable {
  public interface Create extends Default {
  }

  public interface Update extends Default {
  }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @NotNull(
      message = "请指定要修改的对象。",
      groups = {Update.class})
  @Null(
      message = "新建对象时，不能指定id。",
      groups = {Create.class})
  private Long id;

<#list fields as field>
    <#if field.isNullable == 'YES' >
        <#if field.characterMaximumLength?has_content >
  @Length(max = [=field.characterMaximumLength], message = "[=field.columnTitle]超长，最多[=field.characterMaximumLength]字符。")
        </#if>
    <#else>
        <#switch field.javaDataType>
            <#case 'String'>
  @NotBlank(message = "[=field.columnTitle]不能为空。")
                <#break>
            <#default>
  @NotNull(message = "[=field.columnTitle]不能为空。")
                <#break>
        </#switch>
        <#if field.characterMaximumLength?has_content >
  @Length(max = [=field.characterMaximumLength], message = "[=field.columnTitle]超长，最多[=field.characterMaximumLength]字符。")
        </#if>
    </#if>
  private [=field.javaDataType] [=field.lcColumnName];
</#list>
//  @Pattern(regexp = ValidatorPattern.REGEX_YMD, message = "生日格式不正确，格式要求：yyyy-MM-dd。")
//  @Pattern(regexp = ValidatorPattern.REGEX_MOBILE, message = "手机号格式不正确。")
//  @Pattern(
//      regexp = ValidatorPattern.REGEX_PASSWORD,
//      message = "密码不满足安全要求。要求如下：1、密码需包含数字、大小写英文字母；2-可以包含符号._~!@#$^&*；3-长度在8~20位之间。")
//  @Pattern(regexp = "^[01]$", message = "状态选择范围：0-冻结；1-正常。")
}