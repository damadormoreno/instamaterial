package com.softonic.instamaterial.domain.interactors;

public interface InteractorLocator {
  GetPhoto getPhoto();

  GetPhotos getPhotos();

  GetUser getUser();

  UpdateUser updateUser();

  GetPhotoLikes getPhotoLikes();

  GetPhotoComments getPhotoComments();

  LikePhoto likePhoto();

  GetAuthenticatedUserUid getLoggedUser();

  PublishPhoto publishPhoto();

  UploadPhoto uploadPhoto();

  PublishComment publishComment();

  AddPhotoNotifier addPhotoNotifier();

  RemovePhotoNotifier removePhotoNotifier();

  AddLikeNotifier addLikeNotifier();

  RemoveLikeNotifier removeLikeNotifier();

  AddCommentNotifier addCommentNotifier();
}
