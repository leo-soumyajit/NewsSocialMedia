package com.soumyajit.news.social.media.app.Entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@Data
@Table(name = "posts_table")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    @ElementCollection
    private String[] images;

    private Long likes;

    @Column(nullable = false)
    @ElementCollection
    @OneToMany(mappedBy = "post")
    private List<Comments> comments;

    //    @ManyToOne
    //    @JoinColumn(name = "user_id")
    //    private User user;

        @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

}
