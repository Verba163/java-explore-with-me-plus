package ru.practicum.ewm.user.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewUserRequestDto {

    @NotBlank
    @Size(min = 7, max = 64)
    @Column(nullable = false)
    String name;

    @NotBlank
    @Email
    @Size(min = 2, max = 250)
    @Column(nullable = false, unique = true)
    String email;
}


