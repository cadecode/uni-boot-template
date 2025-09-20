package #(entityConvertPackage);

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * #(table.getComment()) bean 转换工具类。
 *
 * @author #(javadocConfig.getAuthor())
 * @since #(javadocConfig.getSince())
 */
@Mapper
public interface #(entityConvertClassName) {

    #(entityConvertClassName) INSTANCE = Mappers.getMapper(#(entityConvertClassName).class);

}
