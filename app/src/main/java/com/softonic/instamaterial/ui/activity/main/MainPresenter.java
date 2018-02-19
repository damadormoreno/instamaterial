package com.softonic.instamaterial.ui.activity.main;

import android.util.Pair;

import com.softonic.instamaterial.data.repository.commons.EmptyUseCaseCallBack;
import com.softonic.instamaterial.domain.common.UseCaseCallback;
import com.softonic.instamaterial.domain.interactors.AddLikeNotifier;
import com.softonic.instamaterial.domain.interactors.AddPhotoNotifier;
import com.softonic.instamaterial.domain.interactors.GetAuthenticatedUserUid;
import com.softonic.instamaterial.domain.interactors.LikePhoto;
import com.softonic.instamaterial.domain.interactors.RemoveLikeNotifier;
import com.softonic.instamaterial.domain.interactors.RemovePhotoNotifier;
import com.softonic.instamaterial.domain.model.Like;
import com.softonic.instamaterial.domain.model.Photo;
import com.softonic.instamaterial.ui.model.FeedItem;
import com.softonic.instamaterial.ui.orchestrator.GetFeedItem;
import com.softonic.instamaterial.ui.orchestrator.GetFeedItems;
import com.softonic.instamaterial.ui.orchestrator.SignOut;
import com.softonic.instamaterial.ui.presenter.Presenter;
import java.util.List;

public class MainPresenter extends Presenter<MainPresenter.View> {
  public static final int LIKE_TAP_SOURCE_BUTTON = 0;
  public static final int LIKE_TAP_SOURCE_IMAGE = 1;
  public static final int LIKE_TAP_LIKED = 2;
  public static final int LIKE_REMOTE_LIKE = 4;
  public static final int LIKE_REMOTE_DISLIKE = 8;

  private final GetAuthenticatedUserUid getAuthenticatedUserUid;
  private final GetFeedItem getFeedItem;
  private final GetFeedItems getFeedItems;
  private final LikePhoto likePhoto;
  private final SignOut signOut;
  private final AddPhotoNotifier addPhotoNotifier;
  private final RemovePhotoNotifier removePhotoNotifier;
  private final AddLikeNotifier addLikeNotifier;
  private final RemoveLikeNotifier removeLikeNotifier;

  private String currentUserUid;

  public MainPresenter(GetAuthenticatedUserUid getAuthenticatedUserUid, GetFeedItem getFeedItem,
                       GetFeedItems getFeedItems, LikePhoto likePhoto, SignOut signOut, AddPhotoNotifier addPhotoNotifier,
                       RemovePhotoNotifier removePhotoNotifier, AddLikeNotifier addLikeNotifier, RemoveLikeNotifier removeLikeNotifier) {
    this.getAuthenticatedUserUid = getAuthenticatedUserUid;
    this.getFeedItem = getFeedItem;
    this.getFeedItems = getFeedItems;
    this.likePhoto = likePhoto;
    this.signOut = signOut;
    this.addPhotoNotifier = addPhotoNotifier;
    this.removePhotoNotifier = removePhotoNotifier;
    this.addLikeNotifier = addLikeNotifier;
    this.removeLikeNotifier = removeLikeNotifier;
  }

  @Override public void attach(View view) {
    super.attach(view);
  }

  public void requestLoggedUser() {
    getAuthenticatedUserUid.execute(new GetCurrentUserUidCallback());
  }

  public void requestFeedItem(Photo photo) {
    getFeedItem.execute(photo, new GetFeedItemCallback());
  }

  public void requestFeedItems() {
    getFeedItems.execute(new GetFeedItemsCallback());
  }

  public void requestSignOut() {
      signOut.execute(null, new SignOutCallBack());
  }

  public void onRequestLike(String photoId, int likeSource) {
    Like like = Like.Builder()
        .photoId(photoId)
        .userId(currentUserUid)
        .build();
    likePhoto.execute(like, new LikePhotoCallback(photoId, currentUserUid, likeSource));
  }

  public String getCurrentUserUid() {
    return currentUserUid;
  }

  public void onNotLoggedUser() {
    view.showErrorNotLoggedUser();
  }

  public void requestRemovePhotoNotifier() {
    removePhotoNotifier.execute(new EmptyUseCaseCallBack<Boolean>());
  }

  public void requestAddLikeNotifier(String photoId) {
    addLikeNotifier.execute(photoId, new AddLikeNotifierCallBack());
  }

  public void requestRemoveLikeNotifier(String photoId) {
    removeLikeNotifier.execute(photoId, new EmptyUseCaseCallBack<Boolean>());
  }

  private class GetCurrentUserUidCallback implements UseCaseCallback<String> {

    @Override public void onSuccess(String result) {
      if (currentUserUid == null || !currentUserUid.equals(result)) {
        currentUserUid = result;
        if (currentUserUid != null) {
          view.setupFeed(currentUserUid);
          addPhotoNotifier.execute(new AddPhotoNotifierCallBack());
          requestFeedItems();
        } else {
          view.loginUser();
        }
      }
    }

    @Override public void onError(Exception exception) {
      view.loginUser();
    }
  }

  private class GetFeedItemCallback implements UseCaseCallback<FeedItem> {

    @Override public void onSuccess(FeedItem result) {
      view.addFeedItem(result);
    }

    @Override public void onError(Exception exception) {
      view.showErrorWhileUpdatingFeed();
    }
  }

  private class GetFeedItemsCallback implements UseCaseCallback<List<FeedItem>> {

    @Override public void onSuccess(List<FeedItem> feedItems) {
      if (feedItems.isEmpty()) {
        view.showEmptyFeed();
      } else {
        view.removeLikeNotifiers();
        view.updateFeed(feedItems);
        view.addLikeNotifier();
      }
    }

    @Override public void onError(Exception exception) {
      view.showErrorWhileRequestingFeed();
    }
  }

  private class LikePhotoCallback implements UseCaseCallback<Boolean> {
    private final String photoId;
    private final String userId;

    private final int likeSource;

    private LikePhotoCallback(String photoId, String userId, int likeSource) {
      this.photoId = photoId;
      this.userId = userId;
      this.likeSource = likeSource;
    }

    @Override public void onSuccess(Boolean result) {
      int likeSourceFlags = likeSource;
      likeSourceFlags |= result ? LIKE_TAP_LIKED : 0;
      view.updatePhotoLike(photoId, userId, likeSourceFlags);
    }

    @Override public void onError(Exception exception) {
      view.showErrorWhileLikingPhoto();
    }
  }

  private class SignOutCallBack implements UseCaseCallback<Boolean>{

    @Override
    public void onSuccess(Boolean result) {
      if (result){
        view.signedOut();
      }else {
        view.showErrorWhileSigningOut();
      }
    }

    @Override
    public void onError(Exception exception) {
      view.showErrorWhileSigningOut();
    }
  }

  private class AddPhotoNotifierCallBack implements UseCaseCallback<Photo>{

    @Override
    public void onSuccess(Photo photo) {
      requestFeedItem(photo);
    }

    @Override
    public void onError(Exception exception) {
      view.showErrorWhileRequestingFeed();
    }
  }

  private class AddLikeNotifierCallBack implements UseCaseCallback<Pair<Like, Boolean>>{

    @Override
    public void onSuccess(Pair<Like, Boolean> result) {
      Like like = result.first;
      if (like.getUserId().equals(currentUserUid)){
        view.updateLikePhotoLikeCounter(like.getPhotoId(), like.getUserId(),
                result.second ? MainPresenter.LIKE_REMOTE_LIKE : MainPresenter.LIKE_REMOTE_DISLIKE);
      }
    }

    @Override
    public void onError(Exception exception) {

    }
  }

  public interface View extends Presenter.View {
    void setupFeed(String userId);

    void updateFeed(List<FeedItem> feedItems);

    void updatePhotoLike(String photoId, String userId, int likeTapFlags);

    void addFeedItem(FeedItem feedItem);

    void showEmptyFeed();

    void loginUser();

    void showErrorWhileRequestingFeed();

    void showErrorWhileLikingPhoto();

    void showErrorWhileUpdatingFeed();

    void showErrorNotLoggedUser();

      void signedOut();

      void showErrorWhileSigningOut();

      void addLikeNotifier();

      void removeLikeNotifiers();

    void updateLikePhotoLikeCounter(String photoId, String userId, int likeTagFlags);
  }
}
