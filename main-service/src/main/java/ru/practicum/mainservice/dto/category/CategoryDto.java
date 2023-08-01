package ru.practicum.mainservice.dto.category;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Setter
@Getter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDto {
    private Long id;

    @Size(min = 1, max = 50, message = "Name must be 1-50 characters")
    @NotBlank(message = "Name cannot be empty or null")
    private String name;
}
