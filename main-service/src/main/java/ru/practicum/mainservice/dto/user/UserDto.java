package ru.practicum.mainservice.dto.user;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private Long id;

    @NotBlank(message = "Email cannot be empty or null")
    @Email(message = "Email is in incorrect format correct: email@email.com")
    @Size(min = 6, max = 254)
    private String email;

    @Size(min = 2, max = 250, message = "Name must be 2-250 characters")
    @NotBlank(message = "Name cannot be empty or null")
    private String name;
}
