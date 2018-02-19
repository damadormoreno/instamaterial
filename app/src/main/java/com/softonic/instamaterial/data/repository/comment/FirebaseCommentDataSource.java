package com.softonic.instamaterial.data.repository.comment;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.softonic.instamaterial.data.repository.commons.AdditionEventListener;
import com.softonic.instamaterial.domain.common.ObservableTask;
import com.softonic.instamaterial.domain.common.Subscriber;
import com.softonic.instamaterial.domain.model.Comment;
import com.softonic.instamaterial.domain.model.UnpublishedComment;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by alnit on 17/02/2018.
 */

public class FirebaseCommentDataSource implements CommentDataSource {

    Map<String, AdditionEventListener<CommentData>> listenerMap = new HashMap<>();

    @Override
    public ObservableTask<List<Comment>> getComments(final String photoId) {
        return new ObservableTask<List<Comment>>() {
            @Override
            public void run(final Subscriber<List<Comment>> result) {
                final DatabaseReference commentsRef = FirebaseDatabase.getInstance()
                        .getReference("Photos")
                        .child(photoId)
                        .child("Comments");
                commentsRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        List<Comment> comments = new LinkedList<>();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            CommentData commentData = snapshot.getValue(CommentData.class);
                            comments.add(createComment(photoId, snapshot.getKey(), commentData));
                        }
                        result.onSuccess(comments);
                        commentsRef.removeEventListener(this);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        result.onError(databaseError.toException());
                        commentsRef.removeEventListener(this);
                    }
                });
            }
        };
    }

    @Override
    public ObservableTask<Comment> publishComment(final UnpublishedComment unpublishedComment) {
        return new ObservableTask<Comment>() {
            @Override
            public void run(final Subscriber<Comment> result) {
                DatabaseReference commentsRef = FirebaseDatabase.getInstance().getReference("Photos")
                        .child(unpublishedComment.getPhotoId())
                        .child("Comments");
                final DatabaseReference commentRef = commentsRef.push();
                CommentData commentData = createCommentData(unpublishedComment);
                commentRef.setValue(commentData)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        result.onError(e);
                    }
                })
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        result.onSuccess(Comment.Builder()
                        .id(commentRef.getKey())
                        .photoId(unpublishedComment.getPhotoId())
                        .userId(unpublishedComment.getUserId())
                        .content(unpublishedComment.getContent())
                        .build());
                    }
                });
            }
        };
    }

    @Override
    public ObservableTask<Comment> addCommentNotifier(final String photoId) {
        return new ObservableTask<Comment>() {
            @Override
            public void run(final Subscriber<Comment> result) {
                DatabaseReference commentRef = FirebaseDatabase.getInstance()
                        .getReference("Photos")
                        .child(photoId)
                        .child("Comments");
                if (listenerMap.containsKey(photoId)){
                    commentRef.removeEventListener(listenerMap.get(photoId));
                    listenerMap.remove(photoId);
                }

                AdditionEventListener<CommentData> listener = new AdditionEventListener<CommentData>(commentRef, CommentData.class) {
                    @Override
                    protected void onChildAdded(String commentId, CommentData commentData) {
                        result.onSuccess(createComment(photoId, commentId, commentData));
                    }
                };

                listenerMap.put(photoId, listener);
            }
        };
    }

    @Override
    public ObservableTask<Boolean> removeCommentNotifier(final String photoId) {
        return new ObservableTask<Boolean>() {
            @Override
            public void run(Subscriber<Boolean> result) {
                if (listenerMap.containsKey(photoId)){

                }
            }
        };
    }

    private CommentData createCommentData(UnpublishedComment unpublishedComment) {
        return new CommentData(unpublishedComment.getUserId()
        , unpublishedComment.getContent());
    }

    private Comment createComment(String photoId, String key, CommentData commentData) {
        return Comment.Builder()
                .id(key)
                .photoId(photoId)
                .userId(commentData.getUserId())
                .content(commentData.getContent())
                .build();
    }
}
