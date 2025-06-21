package org.lemanoman.filesyncserver.service;

import java.util.List;
import java.util.Map;

public class H2TableGenerator {
    public String generateCreateTableCommand(String tableName, List<Map<String, Object>> columns) {
        StringBuilder createTableCommand = new StringBuilder("CREATE TABLE " + tableName + " (");

        for (int i = 0; i < columns.size(); i++) {
            Map<String, Object> column = columns.get(i);
            String columnName = (String) column.getOrDefault("FIELD", null);
            String columnType = (String) column.getOrDefault("TYPE", null);
            boolean isNullable = "YES".equals((String) column.getOrDefault("NULL", ""));

            createTableCommand.append(columnName)
                              .append(" ")
                              .append(columnType);

            if (!isNullable) {
                createTableCommand.append(" NOT NULL");
            }

            if (i < columns.size() - 1) {
                createTableCommand.append(", ");
            }
        }

        createTableCommand.append(");");
        return createTableCommand.toString();
    }
}