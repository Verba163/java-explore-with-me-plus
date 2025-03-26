package ru.practicum.ewm.events.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.user.model.User;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false)
    String title;

    Long views;

    Long participantLimit;

    @ManyToOne
    @JoinColumn(name = "category_id")
    Category category;

    String description;

    @ManyToOne
    @JoinColumn(name = "initiator_id")
    User initiator;

    String annotation;

    LocalDateTime createdOn;

    LocalDateTime eventDate;

    LocalDateTime publishedOn;

    boolean requestModeration;

    Long confirmedRequests;

    @Enumerated(EnumType.STRING)
    State state;

    @ManyToOne
    @JoinColumn(name = "location_id")
    Location location;

    boolean paid;
}
