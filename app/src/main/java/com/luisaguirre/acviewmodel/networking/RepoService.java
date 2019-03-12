package com.luisaguirre.acviewmodel.networking;

import com.luisaguirre.acviewmodel.model.Repo;

import java.util.List;

import io.reactivex.Single;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface RepoService {
    @GET("orgs/Google/repos")
    Single<List<Repo>> getRepositories();

    @GET("repos/{owner}/{name}")
    Call<Repo> getRepo(@Path("owner") String repoOwner, @Path("name") String repoName);
}
