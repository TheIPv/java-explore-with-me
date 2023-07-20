package ru.yandex.practicum.user.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewUserRequest {
    @Email
    @Size(min = 6, max = 254)
    String email;
    @Size(min = 2, max = 250)
    String name;
}
