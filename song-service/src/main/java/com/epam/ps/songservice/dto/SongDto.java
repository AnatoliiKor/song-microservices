package com.epam.ps.songservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SongDto {

    @NotNull
    private Long id;

    @NotBlank(message = "Song name is required")
    @Size(message = "Song name is required", min = 1, max = 100)
    private String name;

    @NotBlank(message = "Artist name is required")
    @Size(message = "Artist name must be between 1 and 100 characters", min = 1, max = 100)
    private String artist;

    @NotBlank(message = "Album name is required")
    @Size(message = "Album name must be between 1 and 100 characters", min = 1, max = 100)
    private String album;

    @NotBlank(message = "Duration is required")
    @Pattern(
            regexp = "^\\d{2}:[0-5]\\d$",
            message = "Duration must be in mm:ss format with leading zeros and seconds between 00 and 59"
    )
    private String duration;

    @NotBlank(message = "Year is required")
    @Pattern(regexp = "^(19\\d{2}|20\\d{2})$", message = "Year must be between 1900 and 2099")
    private String year;
}