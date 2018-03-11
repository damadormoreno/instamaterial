package com.softonic.instamaterial.ui.locator;

import android.support.v4.app.FragmentActivity;

import com.softonic.instamaterial.data.locator.DataServiceLocator;
import com.softonic.instamaterial.domain.executor.UseCaseExecutor;
import com.softonic.instamaterial.domain.interactors.AddCommentNotifier;
import com.softonic.instamaterial.domain.interactors.AddLikeNotifier;
import com.softonic.instamaterial.domain.interactors.AddPhotoNotifier;
import com.softonic.instamaterial.domain.interactors.GetAuthenticatedUserUid;
import com.softonic.instamaterial.domain.interactors.GetPhoto;
import com.softonic.instamaterial.domain.interactors.GetPhotoComments;
import com.softonic.instamaterial.domain.interactors.GetPhotoLikes;
import com.softonic.instamaterial.domain.interactors.GetPhotos;
import com.softonic.instamaterial.domain.interactors.GetUser;
import com.softonic.instamaterial.domain.interactors.InteractorLocator;
import com.softonic.instamaterial.domain.interactors.LikePhoto;
import com.softonic.instamaterial.domain.interactors.PublishComment;
import com.softonic.instamaterial.domain.interactors.PublishPhoto;
import com.softonic.instamaterial.domain.interactors.RemoveCommentNotifier;
import com.softonic.instamaterial.domain.interactors.RemoveLikeNotifier;
import com.softonic.instamaterial.domain.interactors.RemovePhotoNotifier;
import com.softonic.instamaterial.domain.interactors.UpdateUser;
import com.softonic.instamaterial.domain.interactors.UploadPhoto;
import com.softonic.instamaterial.domain.repository.CommentRepository;
import com.softonic.instamaterial.domain.repository.LikeRepository;
import com.softonic.instamaterial.domain.repository.PhotoRepository;
import com.softonic.instamaterial.domain.repository.RepositoryLocator;
import com.softonic.instamaterial.ui.activity.comments.CommentsPresenter;
import com.softonic.instamaterial.ui.activity.comments.CommentsPresenterLocator;
import com.softonic.instamaterial.ui.activity.login.LoginPresenter;
import com.softonic.instamaterial.ui.activity.login.LoginPresenterLocator;
import com.softonic.instamaterial.ui.activity.main.MainPresenter;
import com.softonic.instamaterial.ui.activity.main.MainPresenterLocator;
import com.softonic.instamaterial.ui.activity.publish.PublishPresenter;
import com.softonic.instamaterial.ui.activity.publish.PublishPresenterLocator;
import com.softonic.instamaterial.ui.orchestrator.GetCommentItem;
import com.softonic.instamaterial.ui.orchestrator.GetCommentItems;
import com.softonic.instamaterial.ui.orchestrator.GetFeedItem;
import com.softonic.instamaterial.ui.orchestrator.GetFeedItems;
import com.softonic.instamaterial.ui.orchestrator.OrchestratorLocator;
import com.softonic.instamaterial.ui.orchestrator.SignIn;
import com.softonic.instamaterial.ui.orchestrator.SignOut;

public class ActivityServiceLocator
    implements MainPresenterLocator, PublishPresenterLocator, CommentsPresenterLocator,
    InteractorLocator, OrchestratorLocator, LoginPresenterLocator {

  private MainPresenter mainPresenter;
  private LoginPresenter loginPresenter;
  private PublishPresenter publishPresenter;
  private CommentsPresenter commentsPresenter;
  private GetPhoto getPhoto;
  private GetPhotos getPhotos;
  private GetUser getUser;
  private UpdateUser updateUser;
  private GetPhotoLikes getPhotoLikes;
  private GetPhotoComments getPhotoComments;
  private LikePhoto likePhoto;
  private GetAuthenticatedUserUid getAuthenticatedUserUid;
  private PublishPhoto publishPhoto;
  private UploadPhoto uploadPhoto;
  private PublishComment publishComment;
  private GetFeedItem getFeedItem;
  private GetFeedItems getFeedItems;
  private GetCommentItems getCommentItems;
  private GetCommentItem getCommentItem;
  private SignIn signIn;
  private SignOut signOut;
  private AddPhotoNotifier addPhotoNotifier;
  private RemovePhotoNotifier removePhotoNotifier;
  private AddLikeNotifier addLikeNotifier;
  private RemoveLikeNotifier removeLikeNotifier;
  private AddCommentNotifier addCommentNotifier;
  private RemoveCommentNotifier removeCommentNotifier;

  @Override public MainPresenter mainPresenter(FragmentActivity activity) {
    if (mainPresenter == null) {
      mainPresenter =
          new MainPresenter(getLoggedUser(), getFeedItem(), getFeedItems(), likePhoto(), signOut(activity),
                  addPhotoNotifier(), removePhotoNotifier(), addLikeNotifier(), removeLikeNotifier());
    }
    return mainPresenter;
  }

  @Override public PublishPresenter publishPresenter() {
    if (publishPresenter == null) {
      publishPresenter = new PublishPresenter(getLoggedUser(), publishPhoto());
    }
    return publishPresenter;
  }

  @Override public CommentsPresenter commentsPresenter() {
    if (commentsPresenter == null) {
      commentsPresenter =
          new CommentsPresenter(getCommentItems(), getLoggedUser(), publishComment(),
              getCommentItem(), addCommentNotifier(), removeCommentNotifier());
    }
    return commentsPresenter;
  }

  @Override public GetPhoto getPhoto() {
    if (getPhoto == null) {
      RepositoryLocator repositoryLocator = DataServiceLocator.getInstance();
      getPhoto = new GetPhoto(useCaseExecutor(), repositoryLocator.photoRepository());
    }
    return getPhoto;
  }

  @Override public GetPhotos getPhotos() {
    if (getPhotos == null) {
      RepositoryLocator repositoryLocator = DataServiceLocator.getInstance();
      getPhotos = new GetPhotos(useCaseExecutor(), repositoryLocator.photoRepository());
    }
    return getPhotos;
  }

  @Override public GetUser getUser() {
    if (getUser == null) {
      RepositoryLocator repositoryLocator = DataServiceLocator.getInstance();
      getUser = new GetUser(useCaseExecutor(), repositoryLocator.userRepository());
    }
    return getUser;
  }

  @Override public UpdateUser updateUser() {
    if (updateUser == null) {
      RepositoryLocator repositoryLocator = DataServiceLocator.getInstance();
      updateUser = new UpdateUser(useCaseExecutor(), repositoryLocator.userRepository());
    }
    return updateUser;
  }

  @Override public GetPhotoLikes getPhotoLikes() {
    if (getPhotoLikes == null) {
      RepositoryLocator repositoryLocator = DataServiceLocator.getInstance();
      getPhotoLikes = new GetPhotoLikes(useCaseExecutor(), repositoryLocator.likeRepository());
    }
    return getPhotoLikes;
  }

  @Override public GetPhotoComments getPhotoComments() {
    if (getPhotoComments == null) {
      RepositoryLocator repositoryLocator = DataServiceLocator.getInstance();
      getPhotoComments =
          new GetPhotoComments(useCaseExecutor(), repositoryLocator.commentRepository());
    }
    return getPhotoComments;
  }

  @Override public LikePhoto likePhoto() {
    if (likePhoto == null) {
      RepositoryLocator repositoryLocator = DataServiceLocator.getInstance();
      likePhoto = new LikePhoto(useCaseExecutor(), repositoryLocator.likeRepository());
    }
    return likePhoto;
  }

  @Override public GetAuthenticatedUserUid getLoggedUser() {
    if (getAuthenticatedUserUid == null) {
      RepositoryLocator repositoryLocator = DataServiceLocator.getInstance();
      getAuthenticatedUserUid =
          new GetAuthenticatedUserUid(useCaseExecutor(), repositoryLocator.loggedUserRepository());
    }
    return getAuthenticatedUserUid;
  }

  @Override public PublishPhoto publishPhoto() {
    if (publishPhoto == null) {
      RepositoryLocator repositoryLocator = DataServiceLocator.getInstance();
      publishPhoto =
          new PublishPhoto(useCaseExecutor(), uploadPhoto(), repositoryLocator.photoRepository());
    }
    return publishPhoto;
  }

  @Override public UploadPhoto uploadPhoto() {
    if (uploadPhoto == null) {
      RepositoryLocator repositoryLocator = DataServiceLocator.getInstance();
      uploadPhoto = new UploadPhoto(useCaseExecutor(), repositoryLocator.photoRepository());
    }
    return uploadPhoto;
  }

  @Override public PublishComment publishComment() {
    if (publishComment == null) {
      RepositoryLocator repositoryLocator = DataServiceLocator.getInstance();
      publishComment = new PublishComment(useCaseExecutor(), repositoryLocator.commentRepository());
    }
    return publishComment;
  }

  @Override
  public AddPhotoNotifier addPhotoNotifier() {
    if (addPhotoNotifier == null){
      PhotoRepository photoRepository = DataServiceLocator.getInstance().photoRepository();
      addPhotoNotifier = new AddPhotoNotifier(useCaseExecutor(), photoRepository);
    }

    return addPhotoNotifier;
  }

  @Override
  public RemovePhotoNotifier removePhotoNotifier() {
    if (removePhotoNotifier == null){
      PhotoRepository photoRepository = DataServiceLocator.getInstance().photoRepository();
      removePhotoNotifier = new RemovePhotoNotifier(useCaseExecutor(), photoRepository);
    }

    return removePhotoNotifier;
  }

  @Override
  public AddLikeNotifier addLikeNotifier() {
    if (addLikeNotifier == null){
      LikeRepository likeRepository = DataServiceLocator.getInstance().likeRepository();
      addLikeNotifier = new AddLikeNotifier(useCaseExecutor(), likeRepository);
    }

    return addLikeNotifier;
  }

  @Override
  public RemoveLikeNotifier removeLikeNotifier() {
    if (removeLikeNotifier == null){
      LikeRepository likeRepository = DataServiceLocator.getInstance().likeRepository();
      removeLikeNotifier = new RemoveLikeNotifier(useCaseExecutor(), likeRepository);
    }

    return removeLikeNotifier;
  }

  @Override
  public AddCommentNotifier addCommentNotifier() {
    if (addCommentNotifier == null){
      CommentRepository commentRepository = DataServiceLocator.getInstance().commentRepository();
      addCommentNotifier = new AddCommentNotifier(useCaseExecutor(), commentRepository);
    }

    return addCommentNotifier;
  }

  @Override
  public RemoveCommentNotifier removeCommentNotifier() {
    if (removeCommentNotifier == null){
      CommentRepository commentRepository = DataServiceLocator.getInstance().commentRepository();
      removeCommentNotifier = new RemoveCommentNotifier(useCaseExecutor(), commentRepository);
    }
    return removeCommentNotifier;
  }

  @Override public GetFeedItem getFeedItem() {
    if (getFeedItem == null) {
      getFeedItem = new GetFeedItem(useCaseExecutor(), getUser(), getPhotoLikes());
    }
    return getFeedItem;
  }

  @Override public GetFeedItems getFeedItems() {
    if (getFeedItems == null) {
      getFeedItems = new GetFeedItems(useCaseExecutor(), getPhotos(), getFeedItem());
    }
    return getFeedItems;
  }

  @Override public GetCommentItems getCommentItems() {
    if (getCommentItems == null) {
      getCommentItems = new GetCommentItems(useCaseExecutor(),
          getPhotoComments(), getCommentItem());
    }
    return getCommentItems;
  }

  @Override public GetCommentItem getCommentItem() {
    if (getCommentItem == null) {
      getCommentItem = new GetCommentItem(useCaseExecutor(), getUser());
    }
    return getCommentItem;
  }

  @Override
  public SignIn signIn(FragmentActivity activity) {
    if (signIn == null){
      signIn = new SignIn(useCaseExecutor(), updateUser(), activity);
    }
    return signIn;
  }

  @Override
  public SignOut signOut(FragmentActivity activity) {
    if (signOut == null){
      signOut = new SignOut(useCaseExecutor(), activity);
    }
    return signOut;
  }

  private UseCaseExecutor useCaseExecutor() {
    return AppServiceLocator.getInstance().useCaseExecutor();
  }

  @Override
  public LoginPresenter loginPresenter(FragmentActivity activity) {
    if (loginPresenter == null){
      loginPresenter = new LoginPresenter(signIn(activity));
    }
    return loginPresenter;
  }
}
