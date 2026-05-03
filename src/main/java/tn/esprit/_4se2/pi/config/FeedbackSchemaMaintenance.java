package tn.esprit._4se2.pi.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class FeedbackSchemaMaintenance implements ApplicationRunner {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(ApplicationArguments args) {
        ensureBookingLookupIndex();
        dropLegacyUniqueBookingIndex();
        ensureUserSportSpaceUniqueIndex();
    }

    private void ensureBookingLookupIndex() {
        Integer existingIndexCount = jdbcTemplate.queryForObject(
                """
                SELECT COUNT(*)
                FROM information_schema.statistics
                WHERE table_schema = DATABASE()
                  AND table_name = 'feedbacks'
                  AND index_name = 'idx_feedback_booking_id'
                """,
                Integer.class
        );

        if (existingIndexCount == null || existingIndexCount == 0) {
            jdbcTemplate.execute("ALTER TABLE feedbacks ADD INDEX idx_feedback_booking_id (booking_id)");
            log.info("Created non-unique feedback index on booking_id");
        }
    }

    private void dropLegacyUniqueBookingIndex() {
        List<String> legacyIndexes = jdbcTemplate.queryForList(
                """
                SELECT index_name
                FROM information_schema.statistics
                WHERE table_schema = DATABASE()
                  AND table_name = 'feedbacks'
                GROUP BY index_name
                HAVING MAX(non_unique) = 0
                   AND SUM(CASE WHEN column_name = 'booking_id' THEN 1 ELSE 0 END) = 1
                   AND COUNT(*) = 1
                   AND index_name <> 'PRIMARY'
                """,
                String.class
        );

        for (String indexName : legacyIndexes) {
            try {
                jdbcTemplate.execute("ALTER TABLE feedbacks DROP INDEX `" + indexName + "`");
                log.info("Dropped legacy unique feedback index on booking_id: {}", indexName);
            } catch (Exception exception) {
                log.warn("Unable to drop legacy feedback index {}: {}", indexName, exception.getMessage());
            }
        }
    }

    private void ensureUserSportSpaceUniqueIndex() {
        jdbcTemplate.execute(
                """
                DELETE f1 FROM feedbacks f1
                JOIN feedbacks f2
                  ON f1.user_id = f2.user_id
                 AND f1.sport_space_id = f2.sport_space_id
                 AND f1.id < f2.id
                """
        );

        Integer existingIndexCount = jdbcTemplate.queryForObject(
                """
                SELECT COUNT(*)
                FROM information_schema.statistics
                WHERE table_schema = DATABASE()
                  AND table_name = 'feedbacks'
                  AND index_name = 'uk_feedback_user_sport_space'
                """,
                Integer.class
        );

        if (existingIndexCount == null || existingIndexCount == 0) {
            jdbcTemplate.execute(
                    "ALTER TABLE feedbacks ADD UNIQUE INDEX uk_feedback_user_sport_space (user_id, sport_space_id)"
            );
            log.info("Created unique feedback index on (user_id, sport_space_id)");
        }
    }
}
