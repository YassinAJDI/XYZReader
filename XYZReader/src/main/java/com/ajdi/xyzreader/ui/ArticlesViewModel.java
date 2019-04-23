package com.ajdi.xyzreader.ui;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.ajdi.xyzreader.data.ArticlesRepository;
import com.ajdi.xyzreader.data.model.Article;

import java.util.List;

/**
 * @author Yassin Ajdi
 * @since 4/16/2019.
 */
public class ArticlesViewModel extends ViewModel {

    private LiveData<List<Article>> articlesListLiveData;
    private int currentPosition;

    public ArticlesViewModel(ArticlesRepository articlesRepository) {
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
