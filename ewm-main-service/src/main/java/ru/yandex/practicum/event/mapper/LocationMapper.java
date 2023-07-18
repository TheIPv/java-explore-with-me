package ru.yandex.practicum.event.mapper;

import ru.yandex.practicum.event.dto.LocationDto;
import ru.yandex.practicum.event.model.Location;


public class LocationMapper {
    public static Location toModel(LocationDto locationDto) {
        return Location.builder()
                .lon(locationDto.getLon())
                .lat(locationDto.getLat())
                .build();
    }
    public static LocationDto toDto(Location location) {
        return LocationDto.builder()
                .lon(location.getLon())
                .lat(location.getLat())
                .build();
    }
}
