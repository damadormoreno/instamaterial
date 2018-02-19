package com.softonic.instamaterial.data.repository.user;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.softonic.instamaterial.domain.common.ObservableTask;
import com.softonic.instamaterial.domain.common.Subscriber;
import com.softonic.instamaterial.domain.model.User;

import java.util.NoSuchElementException;

/**
 * Created by alnit on 17/02/2018.
 */

public class FirebaseUserDataSource implements UserDataSource {
    @Override
    public ObservableTask<User> get(final String userId) {
        return new ObservableTask<User>() {
            @Override
            public void run(final Subscriber<User> result) {
                DatabaseReference userRef = FirebaseDatabase.getInstance()
                        .getReference("Users")
                        .child(userId);
                userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        UserData userData = dataSnapshot.getValue(UserData.class);
                        if (userData != null){
                            result.onSuccess(createUser(userId, userData));
                        }else {
                            result.onError(new NoSuchElementException());
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        result.onError(databaseError.toException());
                    }
                });
            }
        };
    }



    @Override
    public ObservableTask<Boolean> put(final User user) {
        return new ObservableTask<Boolean>() {
            @Override
            public void run(final Subscriber<Boolean> result) {
                DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("Users");
                DatabaseReference userRef = usersRef.child(user.getId());
                UserData userData = createUserData(user);
                userRef.setValue(userData)
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                result.onError(e);
                            }
                        })
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                result.onSuccess(true);
                            }
                        });
            }
        };
    }

    private UserData createUserData(User user) {
        return new UserData(user.getUsername(), user.getPhotoUrl());
    }

    private User createUser(String userId, UserData userData) {
        return User.Builder()
                .id(userId)
                .username(userData.getDisplayName())
                .photoUrl(userData.getPhotoUrl())
                .build();
    }
}
