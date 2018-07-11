package ve.gob.tla.telearagua.telearagua;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.GridView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private ImageAdapter adapter;

    private SwipeRefreshLayout swipeRefreshLayout;

    private List<Post> movieList;


    public static final String TAG = MainActivity.class
            .getSimpleName();


    private static MyApplication mInstance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        movieList = new ArrayList<>();

        GridView gridview = (GridView) findViewById(R.id.gridview);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);

        adapter = new ImageAdapter(this, movieList);

        gridview.setAdapter(adapter);

        swipeRefreshLayout.setOnRefreshListener(this);

        swipeRefreshLayout.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        swipeRefreshLayout.setRefreshing(true);
                                        fetchMovies();
                                    }
                                }
        );



    }

    @Override
    public void onRefresh() {
        movieList.clear();
        fetchMovies();

    }

    private void fetchMovies() {

        // showing refresh animation before making http call
        swipeRefreshLayout.setRefreshing(true);

        // appending offset to url
        String url = "http://tla.gob.ve/api/get/imagenes/?o=tiempo&s=desc";

        // olley's json array request object
        // Volley's json array request object
        JsonObjectRequest req = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());

                        if(response.length() > 0) {


                            for (int i = 9; i >= 0; i--) {


                                try {
                                    JSONObject post = response.getJSONArray("data").getJSONObject(i);
                                    String titulo = post.getString("titulo");
                                    String img = post.getString("img");
                                    String contenido = post.getString("contenido");
                                    String fecha = post.getString("fecha");
                                    String categoria = post.getString("categoria");
                                    Post new_post = new Post(titulo, contenido, img, fecha, categoria);
                                    movieList.add(0, new_post);
                                } catch (JSONException e) {
                                    Log.e(TAG, "JSON Parsing error: " + e.getMessage());
                                }


                            }


                            adapter.notifyDataSetChanged();
                        }

                        swipeRefreshLayout.setRefreshing(false);


                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Server Error: " + error.getMessage());

                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();

                        // stopping swipe refresh
                        swipeRefreshLayout.setRefreshing(false);

                    }
                });

        // Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(req);
    }


}




