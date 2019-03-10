package com.luisaguirre.acviewmodel.home;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Log;

import com.luisaguirre.acviewmodel.model.Repo;
import com.luisaguirre.acviewmodel.networking.RepoApi;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListViewModel extends ViewModel {

    private final MutableLiveData<List<Repo>> repos = new MutableLiveData<>();
    private final MutableLiveData<Boolean> repoLoadError = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>();
    private Call<List<Repo>> repoCall;


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

        repoCall.enqueue(new Callback<List<Repo>>() {
            @Override
            public void onResponse(Call<List<Repo>> call, Response<List<Repo>> response) {
                loading.setValue(false);
                repoLoadError.setValue(false);
                repos.setValue(response.body());
                repoCall = null;
            }

            @Override
            public void onFailure(Call<List<Repo>> call, Throwable t) {
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
            repoCall.cancel();
            repoCall = null;
        }
    }
}
