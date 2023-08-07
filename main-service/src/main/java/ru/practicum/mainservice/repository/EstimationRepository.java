package ru.practicum.mainservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.mainservice.dto.db.EventMarkRequestResult;
import ru.practicum.mainservice.entity.Estimation;
import ru.practicum.mainservice.entity.EstimationId;

import java.util.List;
import java.util.Optional;

public interface EstimationRepository extends JpaRepository<Estimation, EstimationId> {
    Boolean existsByUserIdAndEventId(Long userId, Long eventId);

    void deleteByUserIdAndEventId(Long userId, Long eventId);

    @Query("select avg(e.mark) from Estimation e where e.eventId =:eventId group by e.eventId")
    Optional<Double> getRatingByEventId(Long eventId);

    @Query("select new ru.practicum.mainservice.dto.db.EventMarkRequestResult(e.eventId, avg(e.mark))" +
            " from Estimation e where e.eventId in (:ids) group by e.eventId")
    List<EventMarkRequestResult> findAvgMarkByEventIdIn(List<Long> ids);
}
