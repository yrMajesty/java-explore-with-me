package ru.practicum.mainservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.mainservice.dto.compilation.CompilationDto;
import ru.practicum.mainservice.dto.compilation.CompilationNewDto;
import ru.practicum.mainservice.dto.compilation.CompilationUpdateDto;
import ru.practicum.mainservice.entity.Compilation;
import ru.practicum.mainservice.entity.Event;
import ru.practicum.mainservice.exception.NoFoundObjectException;
import ru.practicum.mainservice.repository.CompilationRepository;
import ru.practicum.mainservice.service.mapper.CompilationMapper;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventService eventService;
    private final CompilationMapper compilationMapper;

    @Transactional
    public CompilationDto createCompilation(CompilationNewDto request) {
        Compilation compilation = compilationMapper.fromDto(request);
        if (Objects.isNull(request.getPinned())) {
            compilation.setPinned(false);
        }

        if (Objects.nonNull(request.getEvents())) {
            List<Event> foundEvent = eventService.getEventsByIdIn(request.getEvents());
            compilation.setEvents(foundEvent);
        }

        Compilation savedCompilation = compilationRepository.save(compilation);
        return compilationMapper.toDto(savedCompilation);
    }

    @Transactional
    public CompilationDto updateCompilationById(Long compilationId, CompilationUpdateDto request) {
        Compilation compilation = getCompilationByIdIfExist(compilationId);

        if (Objects.nonNull(request.getTitle())) {
            compilation.setTitle(request.getTitle());
        }

        if (Objects.nonNull(request.getPinned())) {
            compilation.setPinned(false);
        }

        if (Objects.nonNull(request.getEvents())) {
            List<Event> foundEvents = eventService.getEventsByIdIn(request.getEvents());
            compilation.setEvents(foundEvents);
        }

        Compilation savedCompilation = compilationRepository.save(compilation);
        return compilationMapper.toDto(savedCompilation);
    }

    @Transactional
    public void deleteCompilationById(Long compilationId) {
        checkExistCompilationById(compilationId);
        compilationRepository.deleteById(compilationId);
    }

    public List<CompilationDto> getAllCompilations(Boolean pinned, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);

        List<Compilation> compilations = compilationRepository.findAllByPinned(pinned, pageable);
        return compilationMapper.toDtos(compilations);
    }

    public CompilationDto getCompilationById(Long compilationId) {
        Compilation compilation = getCompilationByIdIfExist(compilationId);
        return compilationMapper.toDto(compilation);
    }

    private Compilation getCompilationByIdIfExist(Long compilationId) {
        return compilationRepository.findById(compilationId).orElseThrow(() ->
                new NoFoundObjectException(String.format("Compilation with id='%s' not found", compilationId)));
    }

    private void checkExistCompilationById(Long compilationId) {
        if (Objects.equals(compilationRepository.countById(compilationId), 0)) {
            throw new NoFoundObjectException(String.format("Compilation with id='%s' not exist", compilationId));
        }
    }
}
