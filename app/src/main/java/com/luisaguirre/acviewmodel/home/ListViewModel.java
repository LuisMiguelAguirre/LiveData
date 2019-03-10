package com.luisaguirre.acviewmodel.home;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Log;

import com.luisaguirre.acviewmodel.model.Repo;
import com.luisaguirre.acviewmodel.networking.RepoApi;

import java.util.List;

import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ListViewModel extends ViewModel {

    private final MutableLiveData<List<Repo>> repos = new MutableLiveData<>();
    private final MutableLiveData<Boolean> repoLoadError = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>();
    private Single<List<Repo>> repoCall;
    private Disposable dispose;

    public LiveData<List<Repo>> getRepos() {
        return repos;
    }

    public LiveData<Boolean> getRepoLoadError() {
        return repoLoadError;
    }

    public LiveData<Boolean> getLoading() {
        return loading;
    }

    public ListViewModel() {
        fetchRepos();
    }


    private void fetchRepos() {
        loading.setValue(true);
        repoCall = RepoApi.getInstance().getRepositories();

        repoCall.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<List<Repo>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        dispose = d;
                    }

                    @Override
                    public void onSuccess(List<Repo> repositories) {
                        loading.setValue(false);
                        repoLoadError.setValue(false);
                        repos.setValue(repositories);
                        repoCall = null;
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(getClass().getSimpleName(), "Error loading repos: ");
                        loading.setValue(false);
                        repoLoadError.setValue(true);
                        repoCall = null;
                    }
                });
    }

    @Override
    protected void onCleared() {
        if (repoCall != null) {
            dispose.dispose();
            repoCall = null;
        }
    }
}
