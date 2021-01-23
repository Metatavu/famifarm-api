package fi.metatavu.famifarm.test.functional.resources;

import java.util.HashMap;
import java.util.Map;

import org.testcontainers.containers.MySQLContainer;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

public class MysqlResource implements QuarkusTestResourceLifecycleManager {

    static MySQLContainer<?> db = new MySQLContainer<>("mysql:5.6")
        .withDatabaseName("db")
        .withUsername("fa")
        .withPassword("fa")
        .withCommand("--character-set-server=utf8mb4", "--collation-server=utf8mb4_unicode_ci", "--lower_case_table_names=1");

    @Override
    public Map<String, String> start() {
        db.start();
        Map<String, String> config = new HashMap<>();
        config.put("quarkus.datasource.username", "fa");
        config.put("quarkus.datasource.password", "fa");
        config.put("quarkus.datasource.jdbc.url", db.getJdbcUrl());
        return config;
    }

    @Override
    public void stop() {
        db.stop();
    }
    
}
