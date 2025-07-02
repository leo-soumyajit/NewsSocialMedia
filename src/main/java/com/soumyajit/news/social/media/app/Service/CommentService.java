package com.soumyajit.news.social.media.app.Service;


import com.soumyajit.news.social.media.app.Dtos.CommentDtos;
import com.soumyajit.news.social.media.app.Dtos.CommentRequestDtos;

import java.util.List;

public interface CommentService {


    CommentDtos addComment(CommentRequestDtos commentRequestDtos , Long postId);
    void deleteComment(Long postId,Long commentId);
    CommentDtos updateComment(CommentRequestDtos commentRequestDtos , Long postId,Long commentId);


}
