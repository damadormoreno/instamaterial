package com.softonic.instamaterial.domain.interactors;

import com.softonic.instamaterial.domain.common.ObservableTask;
import com.softonic.instamaterial.domain.common.UseCase;
import com.softonic.instamaterial.domain.executor.UseCaseExecutor;
import com.softonic.instamaterial.domain.repository.PhotoRepository;

/**
 * Created by alnit on 18/02/2018.
 */

public class RemovePhotoNotifier extends UseCase<Void, Boolean> {

    private final PhotoRepository photoRepository;

    public RemovePhotoNotifier(UseCaseExecutor useCaseExecutor, PhotoRepository photoRepository) {
        super(useCaseExecutor);
        this.photoRepository = photoRepository;
    }

    @Override
    public ObservableTask<Boolean> createObservableTask(Void input) {
        return photoRepository.removePhotoNotifier();
    }
}
