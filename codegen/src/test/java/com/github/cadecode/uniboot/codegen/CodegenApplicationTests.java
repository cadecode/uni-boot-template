package com.github.cadecode.uniboot.codegen;

import com.github.cadecode.uniboot.codegen.util.DbDocKit;
import com.github.cadecode.uniboot.codegen.util.GenCodeKit;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 代码生成器测试类
 *
 * @author Cade Li
 * @since 2024/4/25
 */
@SpringBootTest
public class CodegenApplicationTests {

    @Autowired
    private GenCodeKit genCodeKit;

    @Autowired
    private DbDocKit dbDocKit;

    /**
     * 生成代码
     */
    @Test
    public void genCode() {
        String outputDir = ".";
        String basePackage = "com.github.cadecode.uniboot.codegen.test";
        String author = "Cade Li";
        String tablePrefix = "";
        String[] tableNames = {};
        genCodeKit.codegen(outputDir, basePackage, author, tablePrefix, tableNames);
    }

    /**
     * 生成数据库文档
     */
    @Test
    public void genDbDoc() {
        String fileType = "WORD";
        String fileName = "myDB";
        String fileOutputDir = "./";
        String version = "1.0.0";
        String description = "数据库设计文档生成";
        String ignoreTablePrefix = "";
        String ignoreTableSuffix = "";
        String[] ignoreTableNames = {};
        dbDocKit.dbdocIgnore(fileType, fileName, fileOutputDir, version, description, ignoreTablePrefix, ignoreTableSuffix, ignoreTableNames);
    }

}
