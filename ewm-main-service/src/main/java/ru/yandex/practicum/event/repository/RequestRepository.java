package ru.yandex.practicum.event.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.yandex.practicum.event.dto.RequestStats;
import ru.yandex.practicum.event.enums.RequestStatusState;
import ru.yandex.practicum.event.model.Request;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {
    List<Request> findAllByRequesterId(Long requesterId);
    Request findByEventIdAndRequesterId(Long eventId, Long requesterId);
    List<Request> findAllByEventId(Long eventId);
    List<Request> findAllByIdIn(List<Long> requestIds);
    List<Request> findAllByEventIdAndStatus(Long eventId, RequestStatusState status);
    @Query("SELECT new ru.yandex.practicum.event.dto.RequestStats(r.event.id, count(r.id)) " +
            "FROM requests AS r " +
            "WHERE r.event.id IN :eventsId " +
            "AND r.status = 'CONFIRMED' " +
            "GROUP BY r.event.id")
    List<RequestStats> getConfirmedRequests(List<Long> eventsId);
}
