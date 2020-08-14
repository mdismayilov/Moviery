package com.muradismayilov.martiandeveloper.moviesaverapp.filter.filter_recycler;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.muradismayilov.martiandeveloper.moviesaverapp.R;
import com.muradismayilov.martiandeveloper.moviesaverapp.filter.activity.FilterActivityMovie;
import com.muradismayilov.martiandeveloper.moviesaverapp.filter.filter_model.FilterMovie;
import com.muradismayilov.martiandeveloper.moviesaverapp.movie.movie_activity.MovieDetailActivity;

import java.util.List;

public class FilterMovieAdapter extends RecyclerView.Adapter<FilterMovieViewHolder> {

    private final Context mContext;
    private final List<FilterMovie> filterMovieList;

    private final FirebaseAnalytics mFirebaseAnalytics;

    public FilterMovieAdapter(Context mContext, List<FilterMovie> filterMovieList) {
        this.mContext = mContext;
        this.filterMovieList = filterMovieList;

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(mContext);
    }

    @NonNull
    @Override
    public FilterMovieViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater
                .from(mContext)
                .inflate(R.layout.filter_recycler_item_movie, viewGroup, false);

        return new FilterMovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final FilterMovieViewHolder myViewHolder, @SuppressLint("RecyclerView") final int i) {

        if (i % 2 == 0) {
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.END;
            myViewHolder.mainCL.setLayoutParams(params);
        } else {
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.START;
            myViewHolder.mainCL.setLayoutParams(params);
        }

        if (filterMovieList != null) {
            if (filterMovieList.size() != 0) {
                try {

                    Glide.with(mContext)
                            .load(filterMovieList.get(i).getPoster_path())
                            .diskCacheStrategy(DiskCacheStrategy.DATA)
                            .dontAnimate()
                            .dontTransform()
                            .placeholder(R.drawable.image_placeholder)
                            .priority(Priority.IMMEDIATE)
                            .into(myViewHolder.posterIV);

                    myViewHolder.titleTV.setText(filterMovieList.get(i).getTitle());
                    myViewHolder.rateTV.setText(filterMovieList.get(i).getVoteAverage());
                    String formattedYear = "(" + filterMovieList.get(i).getRelease_date() + ")";
                    myViewHolder.yearTV.setText(formattedYear);

                    Animation animation = AnimationUtils.loadAnimation(mContext, android.R.anim.fade_in);
                    myViewHolder.mainCL.setAnimation(animation);

                    myViewHolder.mainCL.setOnClickListener(v -> {
                        Bundle params = new Bundle();
                        params.putInt("ButtonID", v.getId());
                        String buttonName = "filter_movieItem";

                        ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
                        assert connectivityManager != null;
                        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {

                            goToDetail(i, myViewHolder);

                        } else {
                            try {
                                Toast.makeText(mContext, mContext.getResources().getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                if (e.getLocalizedMessage() != null) {
                                    Log.d("MartianDeveloper", e.getLocalizedMessage());
                                } else {
                                    Log.d("MartianDeveloper", "I do not know!");
                                }
                            }
                        }

                        mFirebaseAnalytics.logEvent(buttonName, params);
                    });
                } catch (Exception e) {
                    if (e.getLocalizedMessage() != null) {
                        Log.d("MartianDeveloper", "Error: " + e.getLocalizedMessage());
                    } else {
                        Log.d("MartianDeveloper", "Error: in the onBindViewHolder, MovieProductionAdapter");
                    }
                }
            }
        }
    }

    private void goToDetail(int i, FilterMovieViewHolder myViewHolder) {
        try {
            Intent intent = new Intent(mContext, MovieDetailActivity.class);
            intent.putExtra("id", filterMovieList.get(i).getId());
            intent.putExtra("originalTitle", filterMovieList.get(i).getOriginal_title());
            intent.putExtra("title", filterMovieList.get(i).getTitle());
            intent.putExtra("posterPath", filterMovieList.get(i).getPoster_pathForDB());
            intent.putExtra("releaseDate", filterMovieList.get(i).getRelease_date());

            @SuppressWarnings("unchecked")
            ActivityOptionsCompat activityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    FilterActivityMovie.mActivity,
                    new Pair<>(myViewHolder.itemView.findViewById(R.id.posterIV),
                            MovieDetailActivity.VIEW_NAME_HEADER_IMAGE));

            ActivityCompat.startActivity(mContext, intent, activityOptions.toBundle());

        } catch (Exception e) {
            if (e.getLocalizedMessage() != null) {
                Log.d("MartianDeveloper", "Error" + e.getLocalizedMessage());
            } else {
                Log.d("MartianDeveloper", "I do not know!");
            }
            Toast.makeText(mContext, mContext.getResources().getString(R.string.went_wrong), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public int getItemCount() {
        return filterMovieList.size();
    }
}

