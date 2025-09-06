package com.github.cadecode.uniboot.starter.web.model;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 分页参数
 *
 * @author Cade Li
 * @date 2023/4/23
 */
@Data
public class PageParams {

    @NotNull
    private Integer pageSize = 1;
    @NotNull
    private Integer pageNum = 10;

    private String orderBy;

    /**
     * 前端回传总数
     * <p>在第二页时，可以不用查总数
     */
    private Integer total;

    /**
     * 是否需要 count
     */
    private Boolean countSql;
}
