package com.softonic.instamaterial.data.repository.comment;

/**
 * Created by alnit on 17/02/2018.
 */

public class CommentData {
    private String userId;
    private String content;

    @SuppressWarnings("unused")
    public CommentData() {
    }

    public CommentData(String userId, String content) {
        this.userId = userId;
        this.content = content;
    }

    public String getUserId() {
        return userId;
    }

    public String getContent() {
        return content;
    }
}
