package com.example.xyzreader.data;

import com.example.xyzreader.data.model.Article;
import com.example.xyzreader.remote.ArticleService;
import com.example.xyzreader.utils.AppExecutors;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @author Yassin Ajdi
 * @since 4/16/2019.
 */
public class ArticlesRepository {

    private static volatile ArticlesRepository sInstance;

    private final AppExecutors mExecutors;

    private final ArticleService mArticleService;

    private ArticlesRepository(ArticleService articleService,
                               AppExecutors executors) {
        mArticleService = articleService;
        mExecutors = executors;
    }

    public static ArticlesRepository getInstance(ArticleService articleService,
                                                 AppExecutors executors) {
        if (sInstance == null) {
            synchronized (ArticlesRepository.class) {
                if (sInstance == null) {
                    sInstance = new ArticlesRepository(articleService, executors);
                }
            }
        }
        return sInstance;
    }

    public LiveData<List<Article>> getAllArticles() {
        final MutableLiveData<List<Article>> articleListLiveData = new MutableLiveData<>();
        mArticleService.getAllArticles().enqueue(new Callback<List<Article>>() {
            @Override
            public void onResponse(Call<List<Article>> call, Response<List<Article>> response) {
                if (response.isSuccessful()) {
                    List<Article> articleList = response.body();
                    articleListLiveData.postValue(articleList);
                }
            }

            @Override
            public void onFailure(Call<List<Article>> call, Throwable t) {
                // TODO: 4/16/2019 handle errors here
            }
        });
        return articleListLiveData;
    }
}
