package com.example.leafdex.fragments;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.leafdex.Home;
import com.example.leafdex.R;
import com.example.leafdex.fragments.parsers.Result;
import com.example.leafdex.fragments.parsers.Root;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link camera#newInstance} factory method to
 * create an instance of this fragment.
 */
public class camera extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private View view;
    private ImageView uriExample;
    private TextView scoreTV, sciNameTV, comNamesTV;

    private Home home;

    public camera() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment camera.
     */
    // TODO: Rename and change types and number of parameters
    public static camera newInstance(String param1, String param2) {
        camera fragment = new camera();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_camera, container, false);
        uriExample = (ImageView) view.findViewById(R.id.uriExample);
        scoreTV = (TextView) view.findViewById(R.id.textView12);
        sciNameTV = (TextView) view.findViewById(R.id.textView13);
        comNamesTV = (TextView) view.findViewById(R.id.textView14);
        home = (Home) getActivity();
        Uri plantPicUri;
        String filePath = "";
        if (home.getIsFromGallery()) {
            plantPicUri = home.getPlantPicUriFromGallery();
            filePath = getRealPathFromURI(plantPicUri).substring(1);
        } else {
            plantPicUri = home.getPlantPicUriFromCamera();
            filePath = plantPicUri.toString().substring(8);
        }
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        final String imageUri = filePath;
        final String imageName = "image.jpeg";
        File file = new File(imageUri);
        final MediaType MEDIA_TYPE_JPEG = MediaType.parse("image/jpeg");
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("images", imageName,
                        RequestBody.create(file, MEDIA_TYPE_JPEG))
                .addFormDataPart("organs", "leaf")
                .build();
        Request request = new Request.Builder()
                .url("https://my-api.plantnet.org/v2/identify/all?include-related-images=true&api-key=2b10aINHPAiPDXBJNcQY89sCyu")
                .post(requestBody)
                .build();
        OkHttpClient client = new OkHttpClient();
        Response response;
        try {
            try {
                response = client.newCall(request).execute();
                String json = response.body().string();
                Moshi moshi = new Moshi.Builder().build();
                JsonAdapter<Root> jsonAdapter = moshi.adapter(Root.class);
                Root root = jsonAdapter.fromJson(json);
                List<Result> result = root.getResults();
                DecimalFormat df = new DecimalFormat("0.00");
                Log.d("TAG", "Image url: " + result.get(0).getImages().get(0).url.s);
                Glide.with(getActivity()).load(result.get(0).getImages().get(0).url.s).into(uriExample);
                Log.d("TAG", "Score: " + df.format(result.get(0).getScore() * 100) + "%");
                scoreTV.setText("Score: " + df.format(result.get(0).getScore() * 100) + "%");
                Log.d("TAG", "Scientific name: " + result.get(0).getSpecies().scientificNameWithoutAuthor);
                sciNameTV.setText("Scientific name: " + result.get(0).getSpecies().scientificNameWithoutAuthor);
                String comNames = "";
                for(int i = 0; i < result.get(0).getSpecies().commonNames.size(); i++) {
                    Log.d("TAG", "Common name " + (i + 1) + ": " + result.get(0).getSpecies().commonNames.get(i));
                    if(i == 0) {
                        comNames += result.get(0).getSpecies().commonNames.get(0);
                    } else {
                        comNames += ", " + result.get(0).getSpecies().commonNames.get(i);
                    }
                }
                comNamesTV.setText("Common names: " + comNames);
            } catch(IOException e) {
                Toast.makeText(getActivity(), "Please try again.", Toast.LENGTH_LONG).show();
                cancelChanges();
                e.printStackTrace();
            }
        } catch(RuntimeException e) {
            Toast.makeText(getActivity(), "An error occurred.", Toast.LENGTH_LONG).show();
            cancelChanges();
            e.printStackTrace();
        }

        return view;
    }

    private String getRealPathFromURI(Uri uri) {
        String res = null;
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = home.getContentResolver().query(uri, proj, null, null, null);
        if(cursor.moveToFirst()){;
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
    }

    private void cancelChanges() {
        Intent intent = new Intent(getActivity().getBaseContext(), Home.class);
        getActivity().startActivity(intent);
    }
}