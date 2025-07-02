package org.lemanoman.filesyncserver.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Service
@Profile("filedbsync")
public class DatabaseSyncService {

    private final JdbcTemplate memoryJdbcTemplate;
    private final JdbcTemplate fileJdbcTemplate;
    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Autowired
    public DatabaseSyncService(JdbcTemplate memoryJdbcTemplate) {
        this.memoryJdbcTemplate = memoryJdbcTemplate;

        // Configure file-based H2 database connection
        this.fileJdbcTemplate = new JdbcTemplate(
                new org.springframework.jdbc.datasource.DriverManagerDataSource(
                        "jdbc:h2:file:./data/filedb", "sa", ""
                )
        );
        readFileDatabase();
        executorService.submit(() -> {
            while (true) {
                try {
                    Thread.sleep(30000); // Sync every 5 seconds
                    writeToFileDatabase();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break; // Exit the loop if interrupted
                } catch (Exception e) {
                    System.err.println("Error during database sync: " + e.getMessage());
                }
            }
        });
    }

    private Set<String> getTablesFromTemplate(JdbcTemplate jdbcTemplate) {
        return jdbcTemplate.queryForList("SHOW TABLES").stream().map(
                entry -> (String) entry.getOrDefault("TABLE_NAME", "")).map(name -> name.replace("HTE_", "")).collect(Collectors.toSet());
    }


    private String generateInsert(String table, Map<String, Object> row) {
        StringBuilder insertCommand = new StringBuilder("INSERT INTO " + table);
        insertCommand.append(" (");
        for (String key : row.keySet()) {
            insertCommand.append(key).append(",");
        }
        insertCommand = new StringBuilder(insertCommand.substring(0, insertCommand.length() - 1) + ")");

        insertCommand.append(" VALUES (");
        for (Object value : row.values()) {
            if (value instanceof String) {
                insertCommand.append("'").append(value).append("',");
            } else {
                insertCommand.append(value).append(",");
            }
        }
        insertCommand = new StringBuilder(insertCommand.substring(0, insertCommand.length() - 1) + ")");
        return insertCommand.toString();
    }

    public void writeToFileDatabase() {
        try {
            Set<String> tables = getTablesFromTemplate(memoryJdbcTemplate);

            for (String table : tables) {
                List<Map<String, Object>> rows = memoryJdbcTemplate.queryForList("SELECT * FROM " + table);
                if (rows.isEmpty()) {
                    continue;
                }
                // Drop table in file database if it exists
                try {
                    fileJdbcTemplate.execute("DELETE FROM " + table);


                    // Copy data from memory database to file database

                    for (Map<String, Object> row : rows) {
                        final String insertData = generateInsert(table, row);
                        fileJdbcTemplate.execute(insertData);

                    }
                } catch (Exception ex) {
                    System.out.println("Error during delete from " + table);
                }
            }

            System.out.println("Database sync completed successfully.");
        } catch (Exception e) {
            System.err.println("Error syncing database: " + e.getMessage());
        }
    }

    public void readFileDatabase() {
        try {
            Map<String, Boolean> tableExists = new HashMap<>();

            HashSet<String> tableSchemas = new HashSet<>();
            for (String table : getTablesFromTemplate(fileJdbcTemplate)) {
                tableSchemas.add(table);
                tableExists.put("file:" + table, true);
            }

            for (String table : getTablesFromTemplate(memoryJdbcTemplate)) {
                tableSchemas.add(table);
                tableExists.put("memory:" + table, true);
                if (!tableExists.containsKey("file:" + table)) {
                    List<Map<String, Object>> columns = memoryJdbcTemplate.queryForList("show columns from " + table);
                    String createTableCommand = new H2TableGenerator().generateCreateTableCommand(table, columns);
                    fileJdbcTemplate.execute(createTableCommand);
                    continue;
                }
                List<Map<String, Object>> rows = fileJdbcTemplate.queryForList("SELECT * FROM " + table);
                Long lastId = 0L;
                Long lastSequence = 0L;
                for (Map<String, Object> row : rows) {
                    try {
                        memoryJdbcTemplate.execute(generateInsert(table, row));
                        lastSequence++;
                        if (row.containsKey("ID") || row.containsKey("id")) {
                            Long id = (Long) row.get("ID");
                            if (id == null) {
                                id = (Long) row.get("id");
                            }
                            if (id != null && id > lastId) {
                                lastId = id;
                            }
                        }
                    } catch (Exception e) {
                        System.err.println("Error inserting row into memory database: " + e.getMessage());
                    }
                }
                if (lastId == 0L) {
                    lastId = lastSequence;
                }
                if (lastId == 0) {
                    continue;
                }
                try {
                    memoryJdbcTemplate.execute("alter sequence " + table.replace("HTE_", "") + "_SEQ restart with " + lastId);
                } catch (Exception ex) {
                    System.err.println("Error restarting sequence for table " + table + ": " + ex.getMessage());
                }


            }


            System.out.println("Database sync completed successfully.");
        } catch (Exception e) {
            System.err.println("Error syncing database: " + e.getMessage());
        }
    }
}