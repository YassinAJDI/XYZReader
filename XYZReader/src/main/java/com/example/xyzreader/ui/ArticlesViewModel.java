package com.example.xyzreader.ui;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
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

    private MutableLiveData<Integer> currentSelectedArticlePosition = new MutableLiveData<>();

    public ArticlesViewModel(ArticlesRepository articlesRepository) {
        Timber.d("init");
        articlesListLiveData = articlesRepository.getAllArticles();
    }

    public LiveData<List<Article>> getArticlesListLiveData() {
        return articlesListLiveData;
    }

    public LiveData<Integer> getCurrentSelectedArticlePosition() {
        return currentSelectedArticlePosition;
    }

    public void setCurrentSelectedArticlePosition(int selectedPosition) {
        currentSelectedArticlePosition.setValue(selectedPosition);
    }
}
