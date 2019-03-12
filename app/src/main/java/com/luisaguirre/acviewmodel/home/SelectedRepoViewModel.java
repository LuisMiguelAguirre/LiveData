package com.luisaguirre.acviewmodel.home;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.os.Bundle;
import android.util.Log;

import com.luisaguirre.acviewmodel.model.Repo;
import com.luisaguirre.acviewmodel.networking.RepoApi;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SelectedRepoViewModel extends ViewModel {

    public static final String REPO_DETAILS = "repo_details";
    MutableLiveData<Repo> selectedRepo = new MutableLiveData<>();
    private Call<Repo> repoCall;

    public LiveData<Repo> getSelectedRepo() {
        return selectedRepo;
    }

    void setSelectedRepo(Repo repo) {
        selectedRepo.setValue(repo);
    }

    public void saveToBundle(Bundle outState) {
        if (selectedRepo.getValue() != null) {
            outState.putStringArray(REPO_DETAILS, new String[]{selectedRepo.getValue().owner.login, selectedRepo.getValue().name});
        }
    }

    public void restoreFromBundle(Bundle savedInstanceState) {
        if (selectedRepo.getValue() == null) {
            if (savedInstanceState != null && savedInstanceState.containsKey(REPO_DETAILS)) {
                loadRepo(savedInstanceState.getStringArray(REPO_DETAILS));
            }
        }
    }

    private void loadRepo(String[] repoDetails) {
        repoCall = RepoApi.getInstance().getRepo(repoDetails[0], repoDetails[1]);

        repoCall.enqueue(new Callback<Repo>() {
            @Override
            public void onResponse(Call<Repo> call, Response<Repo> response) {
                selectedRepo.setValue(response.body());
                repoCall = null;
            }

            @Override
            public void onFailure(Call<Repo> call, Throwable t) {
                Log.e(getClass().getSimpleName(), "Error loading repo", t);
            }
        });
    }

    @Override
    protected void onCleared() {
        if (repoCall != null) {
            repoCall.cancel();
        }
    }
}
