package com.example.xyzreader.ui;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.xyzreader.data.ArticlesRepository;
import com.example.xyzreader.data.model.Article;

import java.util.List;

import timber.log.Timber;

/**
 * @author Yassin Ajdi
 * @since 4/16/2019.
 */
public class ArticlesViewModel extends ViewModel {

    private LiveData<List<Article>> articlesListLiveData;
    private int currentPosition;

    public ArticlesViewModel(ArticlesRepository articlesRepository) {
        Timber.d("init");
        articlesListLiveData = articlesRepository.getAllArticles();
    }

    public LiveData<List<Article>> getArticlesListLiveData() {
        return articlesListLiveData;
    }

    public int getCurrentSelectedPosition() {
        return currentPosition;
    }

    public void setCurrentPosition(int position) {
        currentPosition = position;
    }
}
