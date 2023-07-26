package ru.practicum.statsserver.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ViewStatsDto;
import ru.practicum.statsserver.model.ItemStats;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsRepository extends JpaRepository<ItemStats, Long> {
    @Query("select new ru.practicum.ViewStatsDto(h.app, h.uri, count(h.ip)) " +
            "from ItemStats h " +
            "where h.request_date_time between ?1 and ?2 " +
            "group by h.app, h.uri " +
            "order by count(h.ip) desc")
    List<ViewStatsDto> findAllWithoutUris(LocalDateTime start, LocalDateTime end);

    @Query("select new ru.practicum.ViewStatsDto(h.app, h.uri, count(h.ip)) " +
            "from ItemStats h " +
            "where h.request_date_time between ?1 and ?2 " +
            "and h.uri in (?3) " +
            "group by h.app, h.uri " +
            "order by count(h.ip) desc")
    List<ViewStatsDto> findAllNotUnique(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query("select new ru.practicum.ViewStatsDto(h.app, h.uri, count(distinct h.ip)) " +
            "from ItemStats h " +
            "where h.request_date_time between ?1 and ?2 " +
            "and h.uri in (?3) " +
            "group by h.app, h.uri " +
            "order by count(distinct h.ip) desc")
    List<ViewStatsDto> findAllUnique(LocalDateTime start, LocalDateTime end, List<String> uris);
}
