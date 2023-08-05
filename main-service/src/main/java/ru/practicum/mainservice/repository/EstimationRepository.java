package ru.practicum.mainservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.mainservice.entity.Estimation;
import ru.practicum.mainservice.entity.EstimationId;

import java.util.List;

public interface EstimationRepository extends JpaRepository<Estimation, EstimationId> {
    Boolean existsByUserIdAndEventId(Long userId, Long eventId);

    void deleteByUserIdAndEventId(Long userId, Long eventId);

    List<Estimation> findAllMarkByEventId(Long eventId);
}
