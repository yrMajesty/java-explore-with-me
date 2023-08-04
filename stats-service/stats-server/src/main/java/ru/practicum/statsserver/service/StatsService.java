package ru.practicum.statsserver.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.statsdto.HitDto;
import ru.practicum.statsdto.ViewStatsDto;
import ru.practicum.statsserver.exception.NoValidParameterRequest;
import ru.practicum.statsserver.model.ItemStats;
import ru.practicum.statsserver.repository.StatsRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class StatsService {
    private final StatsRepository repository;

    @Transactional
    public void saveRecord(HitDto hitDto) {
        ItemStats itemStats = StatsMapper.toObject(hitDto);
        repository.save(itemStats);
    }

    public List<ViewStatsDto> getAllStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        validateDates(start, end);

        if (unique) {
            return repository.findAllUnique(start, end, uris);
        }
        if (Objects.isNull(uris) || uris.isEmpty()) {
            return repository.findAllWithoutUris(start, end);
        }
        return repository.findAllNotUnique(start, end, uris);
    }

    private void validateDates(LocalDateTime start, LocalDateTime end) {
        if (!end.isAfter(start)) {
            throw new NoValidParameterRequest("Start datetime must be before end datetime!");
        }
    }
}