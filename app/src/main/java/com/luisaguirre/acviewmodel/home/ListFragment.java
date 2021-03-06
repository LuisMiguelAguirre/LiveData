package com.luisaguirre.acviewmodel.home;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.luisaguirre.acviewmodel.R;
import com.luisaguirre.acviewmodel.details.DetailsFragment;
import com.luisaguirre.acviewmodel.model.Repo;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ListFragment extends Fragment implements RepoSelectedListener {

    private Unbinder unbinder;
    private ListViewModel viewModel;

    @BindView(R.id.recycler_view)
    RecyclerView listView;
    @BindView(R.id.loading)
    ProgressBar loading;
    @BindView(R.id.tv_error)
    TextView errorTextView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.screen_list, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        viewModel = ViewModelProviders.of(this).get(ListViewModel.class);
        listView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        listView.setAdapter(new RepoListAdapter(viewModel, this, this));
        listView.setLayoutManager(new LinearLayoutManager(getContext()));
        observeViewModel();
    }

    @Override
    public void onRepoSelected(Repo repo) {
        SelectedRepoViewModel selectedRepoViewModel = ViewModelProviders.of(getActivity()).get(SelectedRepoViewModel.class);
        selectedRepoViewModel.setSelectedRepo(repo);
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.screen_container, new DetailsFragment())
                .addToBackStack(null)
                .commit();
    }

    private void observeViewModel() {

        viewModel.getRepos().observe(this, repos -> {
            if (repos != null) {
                listView.setVisibility(View.VISIBLE);
            }
        });

        viewModel.getLoading().observe(this, isLoading -> {
            //noinspection ConstantConditions
            loading.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            if (isLoading) {
                listView.setVisibility(View.GONE);
                errorTextView.setVisibility(View.GONE);
            }

        });

        viewModel.getRepoLoadError().observe(this, isError -> {
            //noinspection ConstantConditions
            if (isError) {
                errorTextView.setVisibility(View.VISIBLE);
                listView.setVisibility(View.GONE);
                errorTextView.setText(R.string.api_error_repos);
            } else {
                errorTextView.setText(null);
                errorTextView.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null) {
            unbinder.unbind();
            unbinder = null;
        }
    }
}
