package com.recursify.recursify.dto;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class HeatmapDto {
    private LocalDate date;
    private int count;
}
