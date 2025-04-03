package ru.practicum.ewm.comments.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.comments.model.Comment;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

}
