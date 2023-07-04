package ru.yandex.practicum.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.yandex.practicum.model.EndpointHit;
import ru.yandex.practicum.model.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsRepository extends JpaRepository<EndpointHit, Integer> {

    @Query(" SELECT h " +
            "FROM hits h " +
            "WHERE h.timestamp BETWEEN :start AND :end " +
            "AND (h.uri IN :uris OR :uris is NULL) " +
            "GROUP BY h.id, h.app, h.uri " +
            "ORDER BY COUNT(DISTINCT h.ip) DESC ")
    List<ViewStats> getUniqueViewStats(LocalDateTime start, LocalDateTime end, List<String> uris, PageRequest pageable);

    @Query(" SELECT h FROM hits h " +
            "WHERE h.timestamp BETWEEN :start AND :end " +
            "AND (h.uri IN :uris OR :uris is NULL) " +
            "GROUP BY h.id, h.app, h.uri " +
            "ORDER BY COUNT(h.ip) DESC ")
    List<ViewStats> getViewStats(LocalDateTime start, LocalDateTime end, List<String> uris, PageRequest pageable);
}
