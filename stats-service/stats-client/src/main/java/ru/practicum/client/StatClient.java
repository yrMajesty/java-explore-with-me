package ru.practicum.client;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.practicum.HitDto;
import ru.practicum.ViewStatsDto;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class StatClient {
    private final RestTemplate rest;

    @Value("${stats-server.url}")
    private String serverUrl;

    public void saveInfo(HitDto hitDto) {
        rest.postForLocation(serverUrl.concat("/hit"), hitDto);
    }

    public List<ViewStatsDto> getStatistics(String start, String end, List<String> uris, Boolean unique) {
        Map<String, Object> parameters = Map.of(
                "start", start,
                "end", end,
                "uris", uris,
                "unique", unique);

        ViewStatsDto[] statistics = rest.getForObject(
                serverUrl.concat("/stats?start={start}&end={end}&uris={uris}&unique={unique}"),
                ViewStatsDto[].class,
                parameters);

        return Objects.isNull(statistics) ? List.of() : List.of(statistics);
    }
}