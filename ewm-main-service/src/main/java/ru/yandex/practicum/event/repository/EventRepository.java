package ru.yandex.practicum.event.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.yandex.practicum.event.enums.EventState;
import ru.yandex.practicum.event.model.Event;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    @Query("SELECT e FROM events e "
            + "WHERE (e.initiator.id IN :users OR :users IS NULL) "
            + "AND (e.state IN :states OR :states IS NULL) "
            + "AND (e.category.id IN :categories OR :categories IS NULL)")
    List<Event> getEventsByAdminWithoutDate(List<Long> users, List<EventState> states, List<Long> categories, PageRequest pageable);

    @Query("SELECT e FROM events e "
            + "WHERE (e.eventDate BETWEEN :rangeStart AND :rangeEnd) "
            + "AND (e.initiator.id IN :users OR :users IS NULL) "
            + "AND (e.state IN :states OR :states IS NULL) "
            + "AND (e.category.id IN :categories OR :categories IS NULL)")
    List<Event> getEventsByAdmin(List<Long> users, List<EventState> states, List<Long> categories,
                                 LocalDateTime rangeStart, LocalDateTime rangeEnd, PageRequest pageable);

    @Query("SELECT e FROM events e "
            + "WHERE (e.eventDate BETWEEN :rangeStart AND :rangeEnd) "
            + "AND (e.annotation LIKE CONCAT('%', :text, '%') OR e.description LIKE CONCAT('%', :text, '%') OR :text IS NULL) "
            + "AND (e.paid = :paid) OR :paid IS NULL "
            + "AND (e.category.id IN :categories OR :categories IS NULL)")
    List<Event> getEventsByPublic(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart,
                                  LocalDateTime rangeEnd, PageRequest pageable);

    @Query("    SELECT e FROM events e "
            + " WHERE e.initiator.id = :initiatorId ")
    List<Event> findAllByInitiatorId(Long initiatorId, Pageable pageable);

    @Query("    SELECT e FROM events e "
            + " WHERE e.initiator.id = :initiatorId "
            + " AND e.id = :eventId ")
    Event findEventByIdAndInitiatorId(Long eventId, Long initiatorId);

    List<Event> findAllByIdIn(List<Long> eventsId);

}
