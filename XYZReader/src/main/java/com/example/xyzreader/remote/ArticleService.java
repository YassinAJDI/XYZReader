package com.example.xyzreader.remote;

import com.example.xyzreader.data.model.Article;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * @author Yassin Ajdi
 * @since 4/16/2019.
 */
public interface ArticleService {

    @GET()
    Call<List<Article>> getAllArticles();
}
