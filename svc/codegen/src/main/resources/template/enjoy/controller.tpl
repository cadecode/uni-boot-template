#set(tableComment = table.getComment())
#set(entityClassName = table.buildEntityClassName())
#set(entityVarName = firstCharToLowerCase(entityClassName))
#set(serviceVarName = firstCharToLowerCase(table.buildServiceClassName()))
package #(packageConfig.controllerPackage);

#if(controllerConfig.superClass != null)
import #(controllerConfig.buildSuperClassImport());
#end
import #(packageConfig.entityPackage).#(entityClassName);
import #(packageConfig.servicePackage).#(table.buildServiceClassName());
import com.github.cadecode.uniboot.starter.web.annotation.ApiFormat;
import com.mybatisflex.core.paginate.Page;
#if(withSwagger && swaggerVersion.getName() == "FOX")
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
#end
#if(withSwagger && swaggerVersion.getName() == "DOC")
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
#end
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
#if(!controllerConfig.restStyle)
import org.springframework.stereotype.Controller;
#end

import java.io.Serializable;
import java.util.List;

/**
 * #(tableComment) 控制层。
 *
 * @author #(javadocConfig.getAuthor())
 * @since #(javadocConfig.getSince())
 */
@ApiFormat
@RequiredArgsConstructor
@Validated
#if(controllerConfig.restStyle)
@RestController
#else
@Controller
#end
#if(withSwagger && swaggerVersion.getName() == "FOX")
@Api("#(tableComment)接口")
#end
#if(withSwagger && swaggerVersion.getName() == "DOC")
@Tag(name = "#(tableComment)接口")
#end
@RequestMapping("/#(firstCharToLowerCase(entityClassName))")
public class #(table.buildControllerClassName()) #if(controllerConfig.superClass)extends #(controllerConfig.buildSuperClassName()) #end {

    private final #(table.buildServiceClassName()) #(serviceVarName);

    /**
     * 添加#(tableComment)。
     *
     * @param entity #(tableComment)
     * @return {@code true} 添加成功，{@code false} 添加失败
     */
    @PostMapping("save")
    #if(withSwagger && swaggerVersion.getName() == "FOX")
    @ApiOperation("保存#(tableComment)")
    #end
    #if(withSwagger && swaggerVersion.getName() == "DOC")
    @Operation(summary = "保存#(tableComment)")
    #end
    public boolean save(@RequestBody @Valid #if(withSwagger && swaggerVersion.getName() == "FOX")@ApiParam("#(tableComment)") #end #if(withSwagger && swaggerVersion.getName() == "DOC")@Parameter(description = "#(tableComment)")#end  #(entityClassName) entity) {
        return #(serviceVarName).save(entity);
    }

    /**
     * 根据主键删除#(tableComment)。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("remove/{id}")
    #if(withSwagger && swaggerVersion.getName() == "FOX")
    @ApiOperation("根据主键删除#(tableComment)")
    #end
    #if(withSwagger && swaggerVersion.getName() == "DOC")
    @Operation(summary = "根据主键删除#(tableComment)")
    #end
    public boolean remove(@PathVariable #if(withSwagger && swaggerVersion.getName() == "FOX")@ApiParam("#(tableComment)主键") #end #if(withSwagger && swaggerVersion.getName() == "DOC")@Parameter(description = "#(tableComment)主键")#end  Serializable id) {
        return #(serviceVarName).removeById(id);
    }

    /**
     * 根据主键列表删除#(tableComment)。
     *
     * @param idList 主键列表
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @PostMapping("removeList")
    #if(withSwagger && swaggerVersion.getName() == "FOX")
    @ApiOperation("根据主键列表删除#(tableComment)")
    #end
    #if(withSwagger && swaggerVersion.getName() == "DOC")
    @Operation(summary = "根据主键列表删除#(tableComment)")
    #end
    public boolean removeList(@RequestBody @NotEmpty #if(withSwagger && swaggerVersion.getName() == "FOX")@ApiParam("#(tableComment)主键列表") #end #if(withSwagger && swaggerVersion.getName() == "DOC")@Parameter(description = "#(tableComment)主键列表")#end  List<Serializable> idList) {
        return #(serviceVarName).removeByIds(idList);
    }

    /**
     * 根据主键更新#(tableComment)。
     *
     * @param entity #(tableComment)
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("update")
    #if(withSwagger && swaggerVersion.getName() == "FOX")
    @ApiOperation("根据主键更新#(tableComment)")
    #end
    #if(withSwagger && swaggerVersion.getName() == "DOC")
    @Operation(summary = "根据主键更新#(tableComment)")
    #end
    public boolean update(@RequestBody @Valid #if(withSwagger && swaggerVersion.getName() == "FOX")@ApiParam("#(tableComment)") #end #if(withSwagger && swaggerVersion.getName() == "DOC")@Parameter(description = "#(tableComment)")#end  #(entityClassName) entity) {
        return #(serviceVarName).updateById(entity);
    }

    /**
     * 根据主键更新#(tableComment)列表。
     *
     * @param entityList #(tableComment)列表
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("updateList")
    #if(withSwagger && swaggerVersion.getName() == "FOX")
    @ApiOperation("根据主键更新#(tableComment)列表")
    #end
    #if(withSwagger && swaggerVersion.getName() == "DOC")
    @Operation(summary = "根据主键更新#(tableComment)列表")
    #end
    public boolean updateList(@RequestBody @Valid #if(withSwagger && swaggerVersion.getName() == "FOX")@ApiParam("#(tableComment)列表") #end #if(withSwagger && swaggerVersion.getName() == "DOC")@Parameter(description = "#(tableComment)列表")#end  List<#(entityClassName)> entityList) {
        return #(serviceVarName).updateBatch(entityList);
    }

    /**
     * 查询所有#(tableComment)。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    #if(withSwagger && swaggerVersion.getName() == "FOX")
    @ApiOperation("查询所有#(tableComment)")
    #end
    #if(withSwagger && swaggerVersion.getName() == "DOC")
    @Operation(summary = "查询所有#(tableComment)")
    #end
    public List<#(entityClassName)> list() {
        return #(serviceVarName).list();
    }

    /**
     * 根据主键获取#(tableComment)。
     *
     * @param id #(tableComment)主键
     * @return #(tableComment)详情
     */
    @GetMapping("get/{id}")
    #if(withSwagger && swaggerVersion.getName() == "FOX")
    @ApiOperation("根据主键获取#(tableComment)")
    #end
    #if(withSwagger && swaggerVersion.getName() == "DOC")
    @Operation(summary = "根据主键获取#(tableComment)")
    #end
    public #(entityClassName) get(@PathVariable #if(withSwagger && swaggerVersion.getName() == "FOX")@ApiParam("#(tableComment)主键") #end #if(withSwagger && swaggerVersion.getName() == "DOC")@Parameter(description = "#(tableComment)主键")#end  Serializable id) {
        return #(serviceVarName).getById(id);
    }

    /**
     * 分页查询#(tableComment)。
     *
     * @param page 分页对象
     * @return 分页对象
     */
    @GetMapping("page")
    #if(withSwagger && swaggerVersion.getName() == "FOX")
    @ApiOperation("分页查询#(tableComment)")
    #end
    #if(withSwagger && swaggerVersion.getName() == "DOC")
    @Operation(summary = "分页查询#(tableComment)")
    #end
    public Page<#(entityClassName)> page(#if(withSwagger && swaggerVersion.getName() == "FOX")@ApiParam("分页信息") #end #if(withSwagger && swaggerVersion.getName() == "DOC")@Parameter(description = "分页信息")#end  Page<#(entityClassName)> page) {
        return #(serviceVarName).page(page);
    }

}
