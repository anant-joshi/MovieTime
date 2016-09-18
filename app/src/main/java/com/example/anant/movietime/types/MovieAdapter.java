package com.example.anant.movietime.types;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;

import com.example.anant.movietime.R;
import com.example.anant.movietime.data.MovieContract.MovieEntry;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

/**
 * Created by anant on 15/8/16.
 */
public class MovieAdapter extends CursorAdapter {
    private static final String LOG_TAG = MovieAdapter.class.getSimpleName();
    private int id;
    private boolean FLAG = true;
    private Context mContext;
    public static final String[] MOVIE_COLUMNS = {
            MovieEntry.TABLE_NAME + MovieEntry._ID, //0
            MovieEntry.COLUMN_TITLE, //1
            MovieEntry.COLUMN_SUMMARY, //2
            MovieEntry.COLUMN_RELEASE_DATE, //3
            MovieEntry.COLUMN_POPULARITY, //4
            MovieEntry.COLUMN_AVERAGE_VOTES, //5
            MovieEntry.COLUMN_VOTE_COUNT, //6
            MovieEntry.COLUMN_POSTER_URL, //7
            MovieEntry.COLUMN_BACKDROP_URL, //8

    };
    public static final int NAME = 0;
    public static final int TITLE = 1;
    public static final int SUMMARY = 2;
    public static final int RELEASE_DATE = 3;
    public static final int POPULARITY = 4;
    public static final int AVERAGE_VOTES = 5;
    public static final int VOTE_COUNT = 6;
    public static final int POSTER_URL = 7;
    public static final int BACKDROP_URL = 8;


    public MovieAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
//        View view = LayoutInflater.from(context).inflate(R.layout.grid_item_poster, viewGroup);
        View view = LayoutInflater.from(context).inflate(R.layout.grid_item_poster, viewGroup, false);
        ViewHolder holder = new ViewHolder(view);
        view.setTag(holder);
        mContext = context;
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        mContext = context;
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        String posterUrl = cursor.getString(POSTER_URL);


        Picasso.with(context)
                .load(posterUrl)
                .transform(new ResizeImageTransformation())
                .into(viewHolder.posterView);
        FLAG = false;

    }

    public static class ViewHolder {
        public final ImageView posterView;
//        public final TextView titleView;

        public ViewHolder(View view) {
            posterView = (ImageView) view.findViewById(R.id.item_poster_image);
        }
    }
    private float getAspectRatio(int width, int height){
        return ((float)height)/width;
    }
    class ResizeImageTransformation implements Transformation{
        @Override
        public String key() {
            return "square()";
        }

        @Override
        public Bitmap transform(Bitmap source) {
            DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
            int pxWidth = metrics.widthPixels;
            int imgWidth = source.getWidth();
            int imgHeight = source.getHeight();
            float ratio = getAspectRatio(imgWidth, imgHeight);
            pxWidth/=2;
            int pxHeight = Math.round(pxWidth*ratio);
            pxWidth-= TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,10,metrics);
            Bitmap result = Bitmap.createScaledBitmap(
                    source,
                    pxWidth,
                    pxHeight,
                    true
            );
            source.recycle();
            return result;
        }
    }
}
