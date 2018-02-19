package com.softonic.instamaterial.ui.activity.login;

import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.softonic.instamaterial.domain.common.UseCaseCallback;
import com.softonic.instamaterial.ui.orchestrator.SignIn;
import com.softonic.instamaterial.ui.presenter.Presenter;

/**
 * Created by alnit on 13/02/2018.
 */

public class LoginPresenter extends Presenter<LoginPresenter.View> {

    private final SignIn signIn;

    public LoginPresenter(SignIn signIn) {
        this.signIn = signIn;
    }

    public void requestLogin(int requestCode){
        view.showLoading(true);
        signIn.execute(requestCode, new SignInCallBack());
    }

    public void handleSignInResult(GoogleSignInResult result){
        signIn.handleSignInResult(result);
    }

    private class SignInCallBack implements UseCaseCallback<Boolean> {
        @Override
        public void onSuccess(Boolean result) {
            view.showLoading(false);
            if (result){
                view.closeLoginRequest();
            }else {
                view.showErrorWhileLoginIn(null);
            }
        }

        @Override
        public void onError(Exception exception) {
            view.showLoading(false);
            view.showErrorWhileLoginIn(exception.getMessage());
        }
    }

    public interface View extends Presenter.View {

        void showLoading(boolean show);

        void closeLoginRequest();

        void showErrorWhileLoginIn(String errorMessage);
    }
}
