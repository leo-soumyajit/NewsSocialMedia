package com.soumyajit.news.social.media.app.Repository;

import com.soumyajit.news.social.media.app.Entities.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post,Long> {
    List<Post> findByTitleContainingOrDescriptionContaining(
            String titleKeyword, String descriptionKeyword);
}
