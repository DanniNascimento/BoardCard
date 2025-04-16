package com.danni.BoardCard.persistence.migration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import javax.sql.DataSource;

@Component
public class MigrationStrategy {
    private static final Logger logger = LoggerFactory.getLogger(MigrationStrategy.class);

    public MigrationStrategy(DataSource dataSource) {
        logger.info("Migration strategy initialized (Liquibase disabled)");
    }

    public void executeMigration() {
        logger.info("No migrations executed (Liquibase disabled)");
    }
}