package ru.yandex.practicum.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.yandex.practicum.model.EndpointHit;
import ru.yandex.practicum.model.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsRepository extends JpaRepository<EndpointHit, Integer> {

    @Query(" SELECT eh FROM hits eh " +
            "WHERE eh.timestamp BETWEEN :start AND :end " +
            "AND (eh.uri IN :uris OR :uris is NULL) " +
            "GROUP BY eh.app, eh.uri " +
            "ORDER BY COUNT(DISTINCT eh.ip) DESC ")
    List<ViewStats> getUniqueViewStats(LocalDateTime start, LocalDateTime end, List<String> uris, PageRequest pageable);

    @Query(" SELECT eh FROM hits eh " +
            "WHERE eh.timestamp BETWEEN :start AND :end " +
            "AND (eh.uri IN :uris OR :uris is NULL) " +
            "GROUP BY eh.app, eh.uri " +
            "ORDER BY COUNT(eh.ip) DESC ")
    List<ViewStats> getViewStats(LocalDateTime start, LocalDateTime end, List<String> uris, PageRequest pageable);
}
