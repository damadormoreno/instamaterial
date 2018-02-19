package com.softonic.instamaterial.data.repository.photo;

import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.softonic.instamaterial.data.repository.commons.AdditionEventListener;
import com.softonic.instamaterial.domain.common.ObservableTask;
import com.softonic.instamaterial.domain.common.Subscriber;
import com.softonic.instamaterial.domain.model.Photo;
import com.softonic.instamaterial.domain.model.UnpublishedPhoto;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Created by alnit on 14/02/2018.
 */

public class FirebasePhotoDataSource implements PhotoDataSource {

    private AdditionEventListener<PhotoData> listener;

    @Override
    public ObservableTask<Photo> getPhoto(final String photoId) {
        return new ObservableTask<Photo>() {
            @Override
            public void run(final Subscriber<Photo> result) {
                DatabaseReference photoRef = FirebaseDatabase.getInstance()
                        .getReference("Photos")
                        .child(photoId);
                photoRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        PhotoData photoData = dataSnapshot.getValue(PhotoData.class);
                        if (photoData != null){
                            result.onSuccess(createPhoto(photoId, photoData));
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
    public ObservableTask<List<Photo>> getPhotos() {
        return new ObservableTask<List<Photo>>() {
            @Override
            public void run(final Subscriber<List<Photo>> result) {
                final DatabaseReference photosRef = FirebaseDatabase.getInstance().getReference("Photos");
                photosRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        List<Photo> photos = new LinkedList<>();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            try {
                                PhotoData photoData = snapshot.getValue(PhotoData.class);
                                if (photoData.isValid()){
                                    photos.add(0, createPhoto(snapshot.getKey(),photoData));
                                }
                            }catch (DatabaseException ignored){

                            }
                        }

                        result.onSuccess(photos);
                        photosRef.removeEventListener(this);
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
    public ObservableTask<Photo> publishPhoto(final UnpublishedPhoto unpublishedPhoto) {
        return new ObservableTask<Photo>() {
            @Override
            public void run(final Subscriber<Photo> result) {
                DatabaseReference photosRef = FirebaseDatabase.getInstance().getReference("Photos");
                final DatabaseReference photoRef = photosRef.push();
                final PhotoData photoData = createPhotoData(unpublishedPhoto);
                photoRef.setValue(photoData)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        result.onError(e);
                    }
                })
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        result.onSuccess(Photo.Builder()
                        .id(photoRef.getKey())
                        .sourceUrl(unpublishedPhoto.getPhotoUri())
                        .description(unpublishedPhoto.getDescription())
                        .userId(unpublishedPhoto.getUserId())
                        .build());
                    }
                });
            }
        };
    }
    @Override
    public ObservableTask<String> uploadPhoto(final String photoUri) {
        return new ObservableTask<String>() {
            @Override
            public void run(final Subscriber<String> result) {
                Uri localUri = Uri.parse(photoUri);
                FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
                StorageReference storage = firebaseStorage.getReference();
                StorageReference photoRef = storage.child("images/" + localUri.getLastPathSegment());
                photoRef.putFile(localUri)
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                result.onError(e);
                            }
                        })
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Uri downloadUri = taskSnapshot.getDownloadUrl();
                                if (downloadUri != null){
                                    result.onSuccess(downloadUri.toString());
                                }else {
                                    result.onError(new NoSuchElementException());
                                }
                            }
                        });
            }
        };
    }

    @Override
    public ObservableTask<Photo> addPhotoNotifier() {
        return new ObservableTask<Photo>() {
            @Override
            public void run(final Subscriber<Photo> result) {
                DatabaseReference photoRef = FirebaseDatabase.getInstance()
                        .getReference("Photos");
                if (listener != null){
                    photoRef.removeEventListener(listener);
                    listener = null;
                }
                listener = new AdditionEventListener<PhotoData>(photoRef, PhotoData.class) {
                    @Override
                    protected void onChildAdded(String photoId, PhotoData photoData) {
                        if (photoData.isValid()){
                            result.onSuccess(createPhoto(photoId, photoData));
                        }
                    }
                };

            }
        };
    }

    @Override
    public ObservableTask<Boolean> removePhotoNotifier() {
        return new ObservableTask<Boolean>() {
            @Override
            public void run(Subscriber<Boolean> result) {
                if (listener != null){
                    DatabaseReference photoRef = FirebaseDatabase.getInstance()
                            .getReference("Photos");
                    photoRef.removeEventListener(listener);
                    listener = null;
                    result.onSuccess(true);
                }else {
                    result.onSuccess(false);
                }


            }
        };
    }

    private Photo createPhoto(String photoId, PhotoData photoData) {
        return Photo.Builder()
                .id(photoId)
                .userId(photoData.getUserId())
                .sourceUrl(photoData.getSourceUrl())
                .description(photoData.getDescription())
                .build();
    }

    private PhotoData createPhotoData(UnpublishedPhoto unpublishedPhoto) {
        return new PhotoData(unpublishedPhoto.getUserId(),
                unpublishedPhoto.getPhotoUri(),
                unpublishedPhoto.getDescription());
    }
}
