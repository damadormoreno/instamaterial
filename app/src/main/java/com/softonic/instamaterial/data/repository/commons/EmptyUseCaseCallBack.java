package com.softonic.instamaterial.data.repository.commons;

import com.softonic.instamaterial.domain.common.UseCaseCallback;

/**
 * Created by alnit on 18/02/2018.
 */

public class EmptyUseCaseCallBack<R> implements UseCaseCallback<R> {
    @Override
    public void onSuccess(R result) {

    }

    @Override
    public void onError(Exception exception) {

    }
}
