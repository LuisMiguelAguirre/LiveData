package com.luisaguirre.acviewmodel.networking;

import com.luisaguirre.acviewmodel.model.Repo;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface RepoService {
    @GET("orgs/Google/repos")
    public Call<List<Repo>> getRepositories();
}
