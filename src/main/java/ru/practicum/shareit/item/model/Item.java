package ru.practicum.shareit.item.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "items")
@Data
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    private Boolean available;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "request_id")
    private Long requestId;
}
