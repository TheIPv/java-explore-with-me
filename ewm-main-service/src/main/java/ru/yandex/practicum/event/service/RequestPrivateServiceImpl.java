package ru.yandex.practicum.event.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.event.dto.EventRequestStatusUpdateRequest;
import ru.yandex.practicum.event.dto.EventRequestStatusUpdateResult;
import ru.yandex.practicum.event.dto.ParticipationRequestDto;
import ru.yandex.practicum.event.enums.EventState;
import ru.yandex.practicum.event.enums.RequestStatusState;
import ru.yandex.practicum.event.mapper.RequestMapper;
import ru.yandex.practicum.event.model.Event;
import ru.yandex.practicum.event.model.Request;
import ru.yandex.practicum.event.repository.RequestRepository;
import ru.yandex.practicum.exception.ForbiddenException;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.user.model.User;
import ru.yandex.practicum.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RequestPrivateServiceImpl implements RequestPrivateService {

    private final RequestRepository requestRepository;
    private final UserService userService;
    private final EventPublicService eventPublicService;
    private final MainStatsService statsService;

    @Override
    public List<ParticipationRequestDto> getEventRequestsByUser(Long userId) {
        return requestRepository.findAllByRequesterId(userId)
                .stream()
                .map(RequestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public ParticipationRequestDto add(Long userId, Long eventId) {

        User user = userService.get(userId);
        Event event = eventPublicService.getEventById(eventId);

        if (Objects.equals(event.getInitiator().getId(), userId)) {
            throw new ForbiddenException("Нельзя создавать запрос на собственное событие.");
        }

        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new ForbiddenException("Нельзя создавать запрос на неопубликованное событие.");
        }

        Request oldRequest = requestRepository.findByEventIdAndRequesterId(eventId, userId);

        if (oldRequest != null) {
            throw new ForbiddenException("Создавать повторный запрос запрещено.");
        }

        checkIsNewLimitGreaterOld(
                statsService.getConfirmedRequests(List.of(event)).getOrDefault(eventId, 0L) + 1,
                event.getParticipantLimit()
        );

        Request newRequest = Request.builder()
                .event(event)
                .requester(user)
                .created(LocalDateTime.now())
                .build();


        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            newRequest.setStatus(RequestStatusState.CONFIRMED);
        } else {
            newRequest.setStatus(RequestStatusState.PENDING);
        }

        return RequestMapper.toParticipationRequestDto(requestRepository.save(newRequest));
    }

    @Override
    public ParticipationRequestDto cancel(Long userId, Long requestId) {

        userService.get(userId);

        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Заявки на участие с таким id не существует."));

        checkUserIsOwner(request.getRequester().getId(), userId);

        request.setStatus(RequestStatusState.CANCELED);

        return RequestMapper.toParticipationRequestDto(requestRepository.save(request));
    }

    @Override
    public List<ParticipationRequestDto> getEventRequestsByOwner(Long ownerId, Long eventId) {

        userService.get(ownerId);
        Event event = eventPublicService.getEventById(eventId);

        checkUserIsOwner(event.getInitiator().getId(), ownerId);

        return requestRepository.findAllByEventId(eventId)
                .stream()
                .map(RequestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public EventRequestStatusUpdateResult patchEventRequestByOwner(Long ownerId, Long eventId,
                                                                   EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {
        userService.get(ownerId);
        Event event = eventPublicService.getEventById(eventId);

        checkUserIsOwner(event.getInitiator().getId(), ownerId);

        if (!event.getRequestModeration() ||
                event.getParticipantLimit() == 0 ||
                eventRequestStatusUpdateRequest.getRequestIds().isEmpty()) {
            return new EventRequestStatusUpdateResult(List.of(), List.of());
        }

        List<Request> confirmedList = new ArrayList<>();
        List<Request> rejectedList = new ArrayList<>();

        List<Request> requests = requestRepository.findAllByIdIn(eventRequestStatusUpdateRequest.getRequestIds());

        if (requests.size() != eventRequestStatusUpdateRequest.getRequestIds().size()) {
            throw new NotFoundException("Некоторые запросы на участие не найдены.");
        }

        if (!requests.stream()
                .map(Request::getStatus)
                .allMatch(RequestStatusState.PENDING::equals)) {
            throw new ForbiddenException("Изменять можно только заявки, находящиеся в ожидании.");
        }

        if (eventRequestStatusUpdateRequest.getStatus().equals(RequestStatusState.REJECTED)) {
            rejectedList.addAll(changeStatusAndSave(requests, RequestStatusState.REJECTED));
        } else {
            Long newConfirmedRequests = statsService.getConfirmedRequests(List.of(event)).getOrDefault(eventId, 0L) +
                    eventRequestStatusUpdateRequest.getRequestIds().size();

            checkIsNewLimitGreaterOld(newConfirmedRequests, event.getParticipantLimit());

            confirmedList.addAll(changeStatusAndSave(requests, RequestStatusState.CONFIRMED));

            if (newConfirmedRequests >= event.getParticipantLimit()) {
                rejectedList.addAll(changeStatusAndSave(
                        requestRepository.findAllByEventIdAndStatus(eventId, RequestStatusState.PENDING),
                        RequestStatusState.REJECTED)
                );
            }
        }

        return new EventRequestStatusUpdateResult(confirmedList.stream()
                .map(RequestMapper::toParticipationRequestDto).collect(Collectors.toList()),
                rejectedList.stream().map(RequestMapper::toParticipationRequestDto)
                        .collect(Collectors.toList()));
    }

    private List<Request> changeStatusAndSave(List<Request> requests, RequestStatusState status) {
        requests.forEach(request -> request.setStatus(status));
        return requestRepository.saveAll(requests);
    }

    private void checkIsNewLimitGreaterOld(Long newLimit, Integer eventParticipantLimit) {
        if (eventParticipantLimit != 0 && (newLimit > eventParticipantLimit)) {
            throw new ForbiddenException(String.format("Достигнут лимит подтвержденных запросов на участие: %d",
                    eventParticipantLimit));
        }
    }

    private void checkUserIsOwner(Long id, Long userId) {
        if (!Objects.equals(id, userId)) {
            throw new ForbiddenException("Пользователь не является владельцем.");
        }
    }
}
