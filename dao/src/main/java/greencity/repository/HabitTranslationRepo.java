package greencity.repository;

import greencity.entity.Habit;
import greencity.entity.HabitTranslation;
import greencity.entity.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Provides an interface to manage {@link HabitTranslation} entity.
 *
 * @author Volodymyr Turko
 */
public interface HabitTranslationRepo extends JpaRepository<HabitTranslation, Long> {
    /**
     * Method with return {@link Optional} of {@link HabitTranslation}.
     *
     * @param name     of {@link HabitTranslation}.
     * @param language code language.
     * @return {@link Optional} of {@link HabitTranslation}.
     */
    Optional<HabitTranslation> findByNameAndLanguageCode(String name, String language);

    /**
     * Method return {@link Optional} of {@link HabitTranslation}.
     *
     * @param habit    {@link Habit}.
     * @param language code language.
     * @return {@link Optional} of {@link HabitTranslation}.
     */
    Optional<HabitTranslation> findByHabitAndLanguageCode(Habit habit, String language);

    /**
     * Method returns available {@link HabitTranslation}'s for specific user.
     *
     * @param userId   {@link User} id which we use to filter.
     * @param language code language.
     * @param acquired habit acquired status.
     * @return List of available {@link HabitTranslation}`s.
     */
    @Query(value = "SELECT ht FROM HabitTranslation ht "
        + "WHERE ht.language.code = :language AND ht.habit.id IN "
        + "(SELECT ha.habit.id FROM HabitAssign ha "
        + "WHERE ha.user.id = :userId AND ha.acquired = :acquired)")
    List<HabitTranslation> findHabitTranslationsByUserAndAcquiredStatus(@Param("userId") Long userId,
        @Param("language") String language,
        @Param("acquired") boolean acquired);

    /**
     * Method returns all {@link Habit}'s by language.
     *
     * @param language code language.
     * @return Pageable of available {@link HabitTranslation}`s.
     * @author Dovganyuk Taras
     */
    Page<HabitTranslation> findAllByLanguageCode(Pageable pageable, String language);

    /**
     * Method deletes all {@link HabitTranslation}'s by {@link Habit} instance.
     *
     * @param habit {@link Habit} instance.
     */
    void deleteAllByHabit(Habit habit);

    /**
     * Method that find all habit's translations by language code and tags.
     *
     * @param pageable     {@link Pageable}
     * @param tags         {@link List} of {@link String} tags
     * @param languageCode language code {@link String}
     *
     * @return {@link List} of {@link HabitTranslation}.
     * @author Markiyan Derevetskyi
     */
    @Query(nativeQuery = true, value = "select distinct ht.* from habit_translation as ht "
        + "inner join habits_tags as htg on ht.habit_id = htg.habit_id "
        + "inner join tags as t on t.id = htg.tag_id "
        + "inner join languages as l on l.id = ht.language_id "
        + "where t.name in (:tags) and l.code = :languageCode")
    Page<HabitTranslation> findAllByTagsAndLanguageCode(Pageable pageable, List<String> tags, String languageCode);
}
