package com.light0x00.mybatisext;

import com.light0x00.mybatisext.annotations.Column;
import com.light0x00.mybatisext.annotations.TableName;
import com.light0x00.mybatisext.exceptions.MyBatisExtException;
import com.light0x00.mybatisext.toolkit.Assert;
import com.light0x00.mybatisext.toolkit.ReflectionUtils;
import com.light0x00.mybatisext.toolkit.StringUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author light
 * @since 2022/2/19
 */
public class TableInfoHolder {

    private static Map<Class<?>, TableInfo> mapperToTableInfoCache = new ConcurrentHashMap<>();

    public static TableInfo get(Class<?> mapperType) {
        return mapperToTableInfoCache.computeIfAbsent(mapperType, (mapper) -> {
            Class<?>[] modelTypes = ReflectionUtils.resolveParameterizedClasses(mapper);
            if (modelTypes.length == 0) {
                throw new MyBatisExtException("The Mapper {0} has no parameterized type representing the corresponding entity",
                        mapper.getName());
            }
            Class<?> modelType = modelTypes[0];
            return resolveTableInfo(modelType);
        });
    }

    private static TableInfo resolveTableInfo(Class<?> modelClazz) {
        TableInfo tableInfo = new TableInfo();
        tableInfo.setEntityType(modelClazz);
        resolveTableName(modelClazz, tableInfo);
        resolveTableFields(modelClazz, tableInfo);
        return tableInfo;
    }

    private static void resolveTableFields(Class<?> modelClazz, TableInfo tableInfo) {
        List<Field> fieldList = ReflectionUtils.getInstanceSerializableFieldList(modelClazz);
        List<TableInfo.ColumnFieldMapping> fieldMappings = new ArrayList<>(fieldList.size());
        TableInfo.ColumnFieldMapping primaryMapping = null;
        for (Field field : fieldList) {
            String column = StringUtils.camelToUnderline(field.getName());
            TableInfo.ColumnFieldMapping mapping = new TableInfo.ColumnFieldMapping(column, field);
            Column annotation = field.getAnnotation(Column.class);
            if (annotation != null) {
                if (annotation.primary())
                    primaryMapping = mapping;
            }
            if (primaryMapping == null && "id" .equals(field.getName())) {
                primaryMapping = mapping;
            }
            fieldMappings.add(mapping);
        }
        Assert.notNull(primaryMapping, "There is no primary field found in the class {0}.", modelClazz.getName());
        tableInfo.setMappings(fieldMappings).setPrimary(primaryMapping);
    }

    private static void resolveTableName(Class<?> modelClazz, TableInfo tableInfo) {
        TableName annotation = modelClazz.getAnnotation(TableName.class);
        String tableName = null;
        String schema = null;
        if (annotation != null) {
            if (StringUtils.isNotBlank(annotation.value())) {
                tableName = StringUtils.camelToUnderline(annotation.value());
            }
            if (StringUtils.isNotBlank(annotation.schema())) {
                schema = annotation.schema();
            }
        }
        if (tableName == null)
            tableName = StringUtils.camelToUnderline(modelClazz.getSimpleName());
        if (schema == null)
            schema = "";
        tableInfo.setTableName(tableName).setSchema(schema);
    }

}
