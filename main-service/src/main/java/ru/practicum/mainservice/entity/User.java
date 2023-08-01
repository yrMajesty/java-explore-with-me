package ru.practicum.mainservice.entity;

import lombok.*;

import javax.persistence.*;

/**
 * Пользователь
 */

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "email", unique = true, nullable = false, length = 254)
    private String email;

    @Column(name = "name", nullable = false, length = 250)
    private String name;
}
