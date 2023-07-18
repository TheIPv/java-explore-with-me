package ru.yandex.practicum.user.model;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

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
