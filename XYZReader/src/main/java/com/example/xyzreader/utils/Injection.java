package com.example.xyzreader.utils;

import android.content.Context;

import com.example.xyzreader.data.ArticlesRepository;
import com.example.xyzreader.remote.ApiClient;
import com.example.xyzreader.remote.ArticleService;

/**
 * @author Yassin Ajdi
 * @since 4/16/2019.
 */
public class Injection {

    public static ViewModelFactory provideViewModelFactory(Context context) {
        ArticlesRepository repository = provideArticlesRepository(context);
        return ViewModelFactory.getInstance(repository);
    }

    public static ArticlesRepository provideArticlesRepository(Context context) {
        ArticleService apiService = ApiClient.getInstance();
        AppExecutors executors = AppExecutors.getInstance();
        return ArticlesRepository.getInstance(
                apiService,
                executors
        );
    }
}
