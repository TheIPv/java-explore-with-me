package ru.yandex.practicum.model;

import lombok.*;
import org.springframework.data.annotation.ReadOnlyProperty;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity(name = "hits")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EndpointHit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ReadOnlyProperty
    private Integer id;
    @Column(name = "app")
    private String app;
    @Column(name = "uri")
    private String uri;
    @Column(name = "ip")
    private String ip;
    @Column(name = "timestamp")
    private LocalDateTime timestamp;
}
