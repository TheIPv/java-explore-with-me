package ru.yandex.practicum.event.model;

import lombok.*;
import ru.yandex.practicum.event.enums.RequestStatusState;
import ru.yandex.practicum.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity(name = "requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "event_id", referencedColumnName = "id")
    Event event;

    @ManyToOne
    @JoinColumn(name = "requester_id", referencedColumnName = "id")
    User requester;

    LocalDateTime created;

    @Enumerated(EnumType.STRING)
    RequestStatusState status;
}
