package ru.practicum.ewm.events.storage;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.events.model.Event;

public interface EventsRepository extends JpaRepository<Event, Long> {
    Page<Event> findAllByInitiatorIdIs(Long userId, Pageable pageable);
}
