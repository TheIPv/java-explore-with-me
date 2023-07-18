package ru.yandex.practicum.category.dto;

import lombok.*;

import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryDto {
    Long id;
    @Size(min = 1, max = 50)
    String name;
}
