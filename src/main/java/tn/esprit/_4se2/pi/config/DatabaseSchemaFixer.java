package tn.esprit._4se2.pi.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.core.annotation.Order;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
@Order(0)
@RequiredArgsConstructor
public class DatabaseSchemaFixer implements ApplicationRunner {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(ApplicationArguments args) {
        runSafely("ensureCommunityTables", this::ensureCommunityTables);
        runSafely("ensureTeamCategoryColumn", this::ensureTeamCategoryColumn);
        runSafely("ensurePostCommunityColumn", this::ensurePostCommunityColumn);
        runSafely("ensureCommentAndLikeTables", this::ensureCommentAndLikeTables);
        runSafely("repairPostCategoryForeignKey", this::repairPostCategoryForeignKey);
        runSafely("repairPostAuthorForeignKey", this::repairPostAuthorForeignKey);
        runSafely("repairTeamMemberTeamForeignKey", this::repairTeamMemberTeamForeignKey);
        runSafely("fixWrongUserForeignKeys", this::fixWrongUserForeignKeys);
    }

    private void runSafely(String step, Runnable action) {
        try {
            action.run();
        } catch (Exception ex) {
            log.warn("Schema repair step '{}' skipped: {}", step, ex.getMessage());
        }
    }

    private void ensureCommunityTables() {
        jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS communities (
                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                    name VARCHAR(255) NOT NULL,
                    description VARCHAR(1000),
                    category_id BIGINT NOT NULL,
                    created_at DATETIME,
                    CONSTRAINT uk_communities_category UNIQUE (category_id)
                ) ENGINE=InnoDB
                """);

        jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS community_members (
                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                    community_id BIGINT NOT NULL,
                    user_id BIGINT NOT NULL,
                    joined_at DATETIME,
                    CONSTRAINT uk_community_members UNIQUE (community_id, user_id)
                ) ENGINE=InnoDB
                """);

        addForeignKeyIfMissing("communities", "category_id", "categories", "id", "fk_communities_category");
        addForeignKeyIfMissing("community_members", "community_id", "communities", "id", "fk_community_members_community");
        addForeignKeyIfMissing("community_members", "user_id", "users", "id", "fk_community_members_user");
    }

    private void ensureTeamCategoryColumn() {
        if (!columnExists("teams", "category_id")) {
            jdbcTemplate.execute("ALTER TABLE teams ADD COLUMN category_id BIGINT NULL");
        }
        addForeignKeyIfMissing("teams", "category_id", "categories", "id", "fk_teams_category");
    }

    private void ensurePostCommunityColumn() {
        if (!tableExists("post")) {
            jdbcTemplate.execute("""
                    CREATE TABLE IF NOT EXISTS post (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        title VARCHAR(255),
                        content LONGTEXT,
                        created_at DATETIME,
                        author_id BIGINT NULL,
                        category_id BIGINT NULL,
                        community_id BIGINT NULL
                    ) ENGINE=InnoDB
                    """);
        }

        if (!columnExists("post", "community_id")) {
            jdbcTemplate.execute("ALTER TABLE post ADD COLUMN community_id BIGINT NULL");
        }

        if (!columnExists("post", "author_id")) {
            jdbcTemplate.execute("ALTER TABLE post ADD COLUMN author_id BIGINT NULL");
        }

        if (!columnExists("post", "category_id")) {
            jdbcTemplate.execute("ALTER TABLE post ADD COLUMN category_id BIGINT NULL");
        }

        if (!columnExists("post", "created_at")) {
            jdbcTemplate.execute("ALTER TABLE post ADD COLUMN created_at DATETIME NULL");
        }

        addForeignKeyIfMissing("post", "community_id", "communities", "id", "fk_post_community");
        addForeignKeyIfMissing("post", "author_id", "users", "id", "fk_post_author");
        addForeignKeyIfMissing("post", "category_id", "categories", "id", "fk_post_category");
    }

    private void ensureCommentAndLikeTables() {
        // comment table
        jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS comment (
                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                    content LONGTEXT,
                    created_at DATETIME,
                    author_id BIGINT NULL,
                    post_id BIGINT NULL
                ) ENGINE=InnoDB
                """);

        if (!columnExists("comment", "author_id")) {
            jdbcTemplate.execute("ALTER TABLE comment ADD COLUMN author_id BIGINT NULL");
        }
        if (!columnExists("comment", "post_id")) {
            jdbcTemplate.execute("ALTER TABLE comment ADD COLUMN post_id BIGINT NULL");
        }

        addForeignKeyIfMissing("comment", "author_id", "users", "id", "fk_comment_author");
        addForeignKeyIfMissing("comment", "post_id", "post", "id", "fk_comment_post");

        // post_like table
        jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS post_like (
                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                    created_at DATETIME,
                    user_id BIGINT NULL,
                    post_id BIGINT NULL,
                    CONSTRAINT uk_post_like UNIQUE (user_id, post_id)
                ) ENGINE=InnoDB
                """);

        if (!columnExists("post_like", "user_id")) {
            jdbcTemplate.execute("ALTER TABLE post_like ADD COLUMN user_id BIGINT NULL");
        }
        if (!columnExists("post_like", "post_id")) {
            jdbcTemplate.execute("ALTER TABLE post_like ADD COLUMN post_id BIGINT NULL");
        }

        addForeignKeyIfMissing("post_like", "user_id", "users", "id", "fk_post_like_user");
        addForeignKeyIfMissing("post_like", "post_id", "post", "id", "fk_post_like_post");
    }

    private void addForeignKeyIfMissing(String tableName, String columnName, String referencedTable, String referencedColumn, String constraintName) {
        List<Map<String, Object>> constraints = jdbcTemplate.queryForList("""
                SELECT rc.CONSTRAINT_NAME
                FROM information_schema.REFERENTIAL_CONSTRAINTS rc
                JOIN information_schema.KEY_COLUMN_USAGE kcu
                  ON rc.CONSTRAINT_SCHEMA = kcu.CONSTRAINT_SCHEMA
                 AND rc.CONSTRAINT_NAME = kcu.CONSTRAINT_NAME
                 AND rc.TABLE_NAME = kcu.TABLE_NAME
                WHERE rc.CONSTRAINT_SCHEMA = DATABASE()
                  AND rc.TABLE_NAME = ?
                  AND kcu.COLUMN_NAME = ?
                LIMIT 1
                """, tableName, columnName);

        if (!constraints.isEmpty()) {
            return;
        }

        jdbcTemplate.execute(String.format(
                "ALTER TABLE `%s` ADD CONSTRAINT `%s` FOREIGN KEY (`%s`) REFERENCES `%s`(`%s`)",
                tableName, constraintName, columnName, referencedTable, referencedColumn
        ));
    }

    private boolean columnExists(String tableName, String columnName) {
        Integer count = jdbcTemplate.queryForObject("""
                SELECT COUNT(*)
                FROM information_schema.COLUMNS
                WHERE TABLE_SCHEMA = DATABASE()
                  AND TABLE_NAME = ?
                  AND COLUMN_NAME = ?
                """, Integer.class, tableName, columnName);
        return count != null && count > 0;
    }

    private boolean tableExists(String tableName) {
        Integer count = jdbcTemplate.queryForObject("""
                SELECT COUNT(*)
                FROM information_schema.TABLES
                WHERE TABLE_SCHEMA = DATABASE()
                  AND TABLE_NAME = ?
                """, Integer.class, tableName);
        return count != null && count > 0;
    }

    private void repairPostCategoryForeignKey() {
        List<Map<String, Object>> constraints = jdbcTemplate.queryForList("""
                SELECT rc.CONSTRAINT_NAME, kcu.REFERENCED_TABLE_NAME
                FROM information_schema.REFERENTIAL_CONSTRAINTS rc
                JOIN information_schema.KEY_COLUMN_USAGE kcu
                  ON rc.CONSTRAINT_SCHEMA = kcu.CONSTRAINT_SCHEMA
                 AND rc.CONSTRAINT_NAME = kcu.CONSTRAINT_NAME
                 AND rc.TABLE_NAME = kcu.TABLE_NAME
                WHERE rc.CONSTRAINT_SCHEMA = DATABASE()
                  AND rc.TABLE_NAME = 'post'
                  AND kcu.COLUMN_NAME = 'category_id'
                LIMIT 1
                """);

        if (constraints.isEmpty()) {
            addForeignKeyIfMissing("post", "category_id", "categories", "id", "fk_post_category");
            return;
        }

        String constraintName = String.valueOf(constraints.get(0).get("CONSTRAINT_NAME"));
        String referencedTable = String.valueOf(constraints.get(0).get("REFERENCED_TABLE_NAME"));

        if ("categories".equalsIgnoreCase(referencedTable)) {
            log.info("post.category_id foreign key already points to categories.");
            return;
        }

        log.warn("Repairing post.category_id foreign key '{}' from '{}' to 'categories'", constraintName, referencedTable);
        jdbcTemplate.execute("ALTER TABLE post DROP FOREIGN KEY `" + constraintName + "`");
        jdbcTemplate.execute("""
                ALTER TABLE post
                ADD CONSTRAINT fk_post_category
                FOREIGN KEY (category_id) REFERENCES categories(id)
                """);
        log.info("post.category_id foreign key repaired successfully.");
    }

    private void repairPostAuthorForeignKey() {
        List<Map<String, Object>> constraints = jdbcTemplate.queryForList("""
                SELECT rc.CONSTRAINT_NAME, kcu.REFERENCED_TABLE_NAME
                FROM information_schema.REFERENTIAL_CONSTRAINTS rc
                JOIN information_schema.KEY_COLUMN_USAGE kcu
                  ON rc.CONSTRAINT_SCHEMA = kcu.CONSTRAINT_SCHEMA
                 AND rc.CONSTRAINT_NAME = kcu.CONSTRAINT_NAME
                 AND rc.TABLE_NAME = kcu.TABLE_NAME
                WHERE rc.CONSTRAINT_SCHEMA = DATABASE()
                  AND rc.TABLE_NAME = 'post'
                  AND kcu.COLUMN_NAME = 'author_id'
                LIMIT 1
                """);

        if (constraints.isEmpty()) {
            addForeignKeyIfMissing("post", "author_id", "users", "id", "fk_post_author");
            return;
        }

        String constraintName = String.valueOf(constraints.get(0).get("CONSTRAINT_NAME"));
        String referencedTable = String.valueOf(constraints.get(0).get("REFERENCED_TABLE_NAME"));

        if ("users".equalsIgnoreCase(referencedTable)) {
            log.info("post.author_id foreign key already points to users.");
            return;
        }

        log.warn("Repairing post.author_id foreign key '{}' from '{}' to 'users'", constraintName, referencedTable);
        jdbcTemplate.execute("ALTER TABLE post DROP FOREIGN KEY `" + constraintName + "`");
        jdbcTemplate.execute("""
                ALTER TABLE post
                ADD CONSTRAINT fk_post_author
                FOREIGN KEY (author_id) REFERENCES users(id)
                """);
        log.info("post.author_id foreign key repaired successfully.");
    }

    private void repairTeamMemberTeamForeignKey() {
        List<Map<String, Object>> constraints = jdbcTemplate.queryForList("""
                SELECT rc.CONSTRAINT_NAME, kcu.REFERENCED_TABLE_NAME
                FROM information_schema.REFERENTIAL_CONSTRAINTS rc
                JOIN information_schema.KEY_COLUMN_USAGE kcu
                  ON rc.CONSTRAINT_SCHEMA = kcu.CONSTRAINT_SCHEMA
                 AND rc.CONSTRAINT_NAME = kcu.CONSTRAINT_NAME
                 AND rc.TABLE_NAME = kcu.TABLE_NAME
                WHERE rc.CONSTRAINT_SCHEMA = DATABASE()
                  AND rc.TABLE_NAME = 'team_member'
                  AND kcu.COLUMN_NAME = 'team_id'
                LIMIT 1
                """);

        if (constraints.isEmpty()) {
            log.info("No foreign key found on team_member.team_id, skipping repair.");
            return;
        }

        String constraintName = String.valueOf(constraints.get(0).get("CONSTRAINT_NAME"));
        String referencedTable = String.valueOf(constraints.get(0).get("REFERENCED_TABLE_NAME"));

        if ("teams".equalsIgnoreCase(referencedTable)) {
            log.info("team_member.team_id foreign key already points to teams.");
            return;
        }

        log.warn("Repairing team_member.team_id foreign key '{}' from '{}' to 'teams'", constraintName, referencedTable);
        jdbcTemplate.execute("ALTER TABLE team_member DROP FOREIGN KEY `" + constraintName + "`");
        jdbcTemplate.execute("""
                ALTER TABLE team_member
                ADD CONSTRAINT fk_team_member_team
                FOREIGN KEY (team_id) REFERENCES teams(id)
                """);
        log.info("team_member.team_id foreign key repaired successfully.");
    }

    private void fixWrongUserForeignKeys() {
        record UserFkSpec(String table, String column, String constraint) {}

        List<UserFkSpec> specs = List.of(
                new UserFkSpec("post", "author_id", "fk_post_author"),
                new UserFkSpec("comment", "author_id", "fk_comment_author"),
                new UserFkSpec("post_like", "user_id", "fk_post_like_user"),
                new UserFkSpec("community_members", "user_id", "fk_community_members_user")
        );

        for (UserFkSpec spec : specs) {
            if (!tableExists(spec.table()) || !columnExists(spec.table(), spec.column())) {
                continue;
            }

            List<Map<String, Object>> constraints = jdbcTemplate.queryForList("""
                    SELECT rc.CONSTRAINT_NAME
                    FROM information_schema.REFERENTIAL_CONSTRAINTS rc
                    JOIN information_schema.KEY_COLUMN_USAGE kcu
                      ON rc.CONSTRAINT_SCHEMA = kcu.CONSTRAINT_SCHEMA
                     AND rc.CONSTRAINT_NAME = kcu.CONSTRAINT_NAME
                     AND rc.TABLE_NAME = kcu.TABLE_NAME
                    WHERE rc.CONSTRAINT_SCHEMA = DATABASE()
                      AND rc.TABLE_NAME = ?
                      AND kcu.COLUMN_NAME = ?
                      AND kcu.REFERENCED_TABLE_NAME = 'user'
                    """, spec.table(), spec.column());

            for (Map<String, Object> constraint : constraints) {
                String constraintName = String.valueOf(constraint.get("CONSTRAINT_NAME"));
                log.warn("Dropping incorrect foreign key {} on {}.{} referencing 'user'", constraintName, spec.table(), spec.column());
                try {
                    jdbcTemplate.execute("ALTER TABLE `" + spec.table() + "` DROP FOREIGN KEY `" + constraintName + "`");
                } catch (Exception e) {
                    log.warn("Failed to drop FK {}: {}", constraintName, e.getMessage());
                }
            }

            addForeignKeyIfMissing(spec.table(), spec.column(), "users", "id", spec.constraint());
        }
    }
}