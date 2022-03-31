package com.example.leafdex.fragments;

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

import com.example.leafdex.Home;
import com.example.leafdex.R;

import java.io.File;
import java.io.IOException;

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
    private Uri picUri;

    private View view;
    private TextView cameraLabel;
    private ImageView uriExample;

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
        cameraLabel = (TextView) view.findViewById(R.id.cameraLabel);
        uriExample = (ImageView) view.findViewById(R.id.uriExample);
        home = (Home) getActivity();
        // Uri plantPicUri = home.getPlantPicUriFromGallery();
        // Uri plantPicUri = home.getPlantPicUriFromCamera();
        Uri plantPicUri;
        String filePath = "";
        if (home.getIsFromGallery()) {
            plantPicUri = home.getPlantPicUriFromGallery();
            filePath = getRealPathFromURI(plantPicUri).substring(1);
        } else {
            plantPicUri = home.getPlantPicUriFromCamera();
            filePath = plantPicUri.toString().substring(8);
        }
        Toast.makeText(getActivity(), filePath, Toast.LENGTH_LONG).show();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        // uriExample.setImageURI(plantPicUri);
        Log.d("TAG", filePath);
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
            response = client.newCall(request).execute();
            Log.d("TAG", response.body().string());
        } catch (IOException e) {
            Toast.makeText(getActivity(), "Please try again.", Toast.LENGTH_LONG).show();
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
}