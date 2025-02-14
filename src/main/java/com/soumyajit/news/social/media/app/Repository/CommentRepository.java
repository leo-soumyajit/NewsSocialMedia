package com.soumyajit.news.social.media.app.Repository;

import com.soumyajit.news.social.media.app.Entities.Comments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comments,Long> {
}
