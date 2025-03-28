package ru.practicum.ewm.user.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "users", schema = "public")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @NotBlank(message = "Name must not be blank")
    @Size(min = 2, max = 250, message = "Name must be at least 2-250 characters long")
    @Column(nullable = false)
    String name;

    @NotBlank(message = "Email must not be blank")
    @Size(min = 6, max = 254, message = "Email must be between 6 and 254 characters long")
    @Pattern(
            regexp = "(?i)^[A-Za-z0-9._%+-]{1,63}@[A-Za-z0-9]([A-Za-z0-9-]{0,61}[A-Za-z0-9])?(\\.[A-Za-z0-9]([A-Za-z0-9-]{0,62}[A-Za-z0-9])?)*\\.[A-Za-z0-9]{2,63}$",
            message = "Email must be valid with a local part up to 64 characters and each domain part up to 63 characters, allowing multiple subdomains"
    )
    @Column(nullable = false, unique = true)
    String email;
}
