package com.app.filmindonesia.ui.detail.movie;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;

import com.app.filmindonesia.data.local.entity.MovieLocal;
import com.app.filmindonesia.utils.GlideApp;
import com.app.filmindonesia.utils.GlobalFunction;
import com.app.filmindonesia.viewmodel.ViewModelFactory;
import com.bumptech.glide.request.RequestOptions;
import com.dzakdzaks.movieLocals.BuildConfig;
import com.dzakdzaks.movieLocals.R;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailActivity extends AppCompatActivity {

    public static final String EXTRA_MOVIE = "extra_movie";
    @BindView(R.id.imgThumb)
    ImageView imgThumb;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsingToolbarLayout;
    @BindView(R.id.appbar)
    AppBarLayout appBarLayout;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.imgPoster)
    ImageView imgPoster;
    @BindView(R.id.textTitle)
    TextView txtTitle;
    @BindView(R.id.textOriginalTitle)
    TextView txtOriginTitle;
    @BindView(R.id.textReleaseDate)
    TextView txtReleaseDate;
    @BindView(R.id.textCategory)
    TextView voteAverage;
    @BindView(R.id.textVoteAverage)
    TextView txtVoteAverage;
    @BindView(R.id.Country)
    TextView country;
    @BindView(R.id.textCountry)
    TextView txtCountry;
    @BindView(R.id.textOverview)
    TextView txtOverview;
    private DetailViewModel viewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);
        viewModel = obtainViewModel(this);
        setInit();
        setupActionBar();


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String movieId = extras.getString(EXTRA_MOVIE);
            if (movieId != null) {
                progressBar.setVisibility(View.VISIBLE);
                viewModel.setMovieId(movieId);
            }
        }

        viewModel.getDetailMovie().observe(this, movieLocal -> {
            if (movieLocal != null) {
                progressBar.setVisibility(View.GONE);
                showMovie(movieLocal);
            }
        });


    }

    private void setInit() {
        toolbar = findViewById(R.id.toolbar);
        appBarLayout = findViewById(R.id.appbar);
        collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar);
        imgThumb = findViewById(R.id.imgThumb);
        imgPoster = findViewById(R.id.imgPoster);
        txtTitle = findViewById(R.id.textTitle);
        txtOriginTitle = findViewById(R.id.textOriginalTitle);
        txtReleaseDate = findViewById(R.id.textReleaseDate);
        txtVoteAverage = findViewById(R.id.textVoteAverage);
        txtCountry = findViewById(R.id.textCountry);
        txtOverview = findViewById(R.id.textOverview);
        progressBar = findViewById(R.id.progress_bar);
    }

    private void setupActionBar() {
        setSupportActionBar(toolbar);
        collapsingToolbarLayout.setContentScrimColor(getResources().getColor(R.color.colorWhite));
        collapsingToolbarLayout.setCollapsedTitleTextColor(getResources().getColor(R.color.colorPrimary));
        collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(R.color.colorWhite));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    void setupColorActionBarIcon(Drawable favoriteItemColor) {
        appBarLayout.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            if ((collapsingToolbarLayout.getHeight() + verticalOffset) < (2 * ViewCompat.getMinimumHeight(collapsingToolbarLayout))) {
                if (toolbar.getNavigationIcon() != null)
                    toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
                favoriteItemColor.mutate().setColorFilter(getResources().getColor(R.color.colorPrimary),
                        PorterDuff.Mode.SRC_ATOP);

            } else {
                if (toolbar.getNavigationIcon() != null)
                    toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.colorWhite), PorterDuff.Mode.SRC_ATOP);
                favoriteItemColor.mutate().setColorFilter(getResources().getColor(R.color.colorWhite),
                        PorterDuff.Mode.SRC_ATOP);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        MenuItem favoriteItem = menu.findItem(R.id.favorite);
        Drawable favoriteItemColor = favoriteItem.getIcon();
        setupColorActionBarIcon(favoriteItemColor);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showMovie(MovieLocal movieLocal) {
        collapsingToolbarLayout.setTitle(movieLocal.getMovieTitle());
        setupActionBar();
        GlideApp.with(getApplicationContext())
                .load(BuildConfig.BASE_URL_IMG_LANDSCAPE + movieLocal.getMovieImage())
                .apply(RequestOptions.placeholderOf(R.drawable.ic_circle).error(R.drawable.ic_error))
                .into(imgThumb);
        GlideApp.with(getApplicationContext())
                .load(BuildConfig.BASE_URL_IMG + movieLocal.getMovieImage())
                .apply(RequestOptions.placeholderOf(R.drawable.ic_circle).error(R.drawable.ic_error))
                .into(imgPoster);
        txtTitle.setText(movieLocal.getMovieTitle());
        txtOriginTitle.setText(movieLocal.getMovieOriginalTitle());
        txtReleaseDate.setText(GlobalFunction.dateFormater(movieLocal.getMovieReleaseDate()));
        txtVoteAverage.setText(movieLocal.getMovieVote() + " " + getResources().getString(R.string.voteFull));
        txtCountry.setText(movieLocal.getMovieCountry());
        txtOverview.setText(movieLocal.getMovieOverview());
    }

    @NonNull
    private static DetailViewModel obtainViewModel(FragmentActivity activity) {
        ViewModelFactory factory = ViewModelFactory.getInstance();
        return ViewModelProviders.of(activity, factory).get(DetailViewModel.class);
    }


}
