package com.example.leafdex.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.leafdex.Encyclopedia;
import com.example.leafdex.Home;
import com.example.leafdex.Post_post;
import com.example.leafdex.R;
import com.example.leafdex.fragments.parsers.Result;
import com.example.leafdex.fragments.parsers.Root;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
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

    private String imageUri, comName, sciName;

    private View view;
    private ImageView uriExample;
    private TextView scoreTV, sciNameTV, comNamesTV;
    private Button postBtn, encBtn;

    private Home home;
    private ProgressDialog mProgressDialog = null;

    private static final float maxHeight = 1280.0f;
    private static final float maxWidth = 1280.0f;

    public camera(){

    }

    public camera(ProgressDialog mProgressDialog){
        this.mProgressDialog = mProgressDialog;
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
        postBtn = (Button) view.findViewById(R.id.button);
        encBtn = (Button) view.findViewById(R.id.button1);
        postBtn.setVisibility(View.INVISIBLE);
        encBtn.setVisibility(View.INVISIBLE);
        home = (Home) getActivity();
        Uri plantPicUri;
        String filePath = "";
        if(home.getIsFromGallery()) {
            plantPicUri = home.getPlantPicUriFromGallery();
            filePath = getRealPathFromURI(plantPicUri).substring(1);
        } else {
            plantPicUri = home.getPlantPicUriFromCamera();
            filePath = plantPicUri.toString().substring(8);
        }
        imageUri = compressImage(filePath);
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
        try {
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    backToHome1();
                }
                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if(response.isSuccessful()) {
                        String json = response.body().string();
                        if(!json.isEmpty()) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Moshi moshi = new Moshi.Builder().build();
                                    JsonAdapter<Root> jsonAdapter = moshi.adapter(Root.class);
                                    Root root;
                                    try {
                                        root = jsonAdapter.fromJson(json);
                                    } catch (IOException e) {
                                        backToHome2();
                                        return;
                                    }
                                    List<Result> result = root.getResults();
                                    if(result.get(0).getScore() < 0.1) {
                                        backToHome2();
                                        return;
                                    }
                                    DecimalFormat df = new DecimalFormat("0.00");
                                    Glide.with(getActivity()).load(result.get(0).getImages().get(0).url.getS()).into(uriExample);
                                    scoreTV.setText("Score: " + df.format(result.get(0).getScore() * 100) + "%");
                                    sciNameTV.setText("Scientific name: " + result.get(0).getSpecies().scientificNameWithoutAuthor);
                                    sciName = result.get(0).getSpecies().scientificNameWithoutAuthor;
                                    String comNames = "";
                                    if(result.get(0).getSpecies().commonNames.size() > 0) {
                                        for(int i = 0; i < result.get(0).getSpecies().commonNames.size(); i++) {
                                            if(i == 0) {
                                                comNames += result.get(0).getSpecies().commonNames.get(0);
                                                comName = result.get(0).getSpecies().commonNames.get(0);
                                            } else {
                                                comNames += ", " + result.get(0).getSpecies().commonNames.get(i);
                                            }
                                        }
                                        comNamesTV.setText("Common names: " + comNames);
                                    } else {
                                        comName = sciName;
                                    }
                                    if (mProgressDialog != null) {
                                        mProgressDialog.dismiss();
                                    }
                                    postBtn.setVisibility(View.VISIBLE);
                                    encBtn.setVisibility(View.VISIBLE);
                                }
                            });
                        } else {
                            backToHome1();
                        }
                    } else {
                        backToHome1();
                    }
                }
            });
        } catch(RuntimeException e) {
            backToHome1();
        }
        postBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(imageUri != null && comName != null) {
                    Intent intent = new Intent(getActivity().getBaseContext(), Post_post.class);
                    intent.putExtra("filePath", imageUri);
                    intent.putExtra("comName", comName);
                    intent.putExtra("sciName", sciName);
                    getActivity().startActivity(intent);
                }
            }
        });
        encBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(imageUri != null && comName != null) {
                    Intent intent = new Intent(getActivity().getBaseContext(), Encyclopedia.class);
                    intent.putExtra("comName", comName);
                    intent.putExtra("sciName", sciName);
                    getActivity().startActivity(intent);
                }
            }
        });

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

    private void backToHome1() {
        Intent intent = new Intent(getActivity().getBaseContext(), Home.class);
        intent.putExtra("signal", "error");
        getActivity().startActivity(intent);
    }

    private void backToHome2() {
        Toast.makeText(getActivity(), "Plant not found. Please try again.", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(getActivity().getBaseContext(), Home.class);
        getActivity().startActivity(intent);
    }

    public String compressImage(String imagePath) {
        Bitmap scaledBitmap = null;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(imagePath, options);

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;

        float imgRatio = (float) actualWidth / (float) actualHeight;
        float maxRatio = maxWidth / maxHeight;

        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if(imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if(imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;

            }
        }

        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);
        options.inJustDecodeBounds = false;
        options.inDither = false;
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];

        try {
            bmp = BitmapFactory.decodeFile(imagePath, options);
        } catch(OutOfMemoryError exception) {
            exception.printStackTrace();
        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.RGB_565);
        } catch(OutOfMemoryError exception) {
            exception.printStackTrace();
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

        if(bmp!=null) {
            bmp.recycle();
        }

        ExifInterface exif;
        try {
            exif = new ExifInterface(imagePath);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);
            Matrix matrix = new Matrix();
            if(orientation == 6) {
                matrix.postRotate(90);
            } else if(orientation == 3) {
                matrix.postRotate(180);
            } else if(orientation == 8) {
                matrix.postRotate(270);
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
        } catch(IOException e) {
            e.printStackTrace();
        }
        FileOutputStream out = null;
        String filepath = getFilename();
        try {
            out = new FileOutputStream(filepath);

            // Write the compressed bitmap at the destination specified by filename.
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);

        } catch(FileNotFoundException e) {
            e.printStackTrace();
        }

        return filepath;
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if(height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;

        while(totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
    }

    public String getFilename() {
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
                + "/Android/data/"
                + getActivity().getApplicationContext().getPackageName()
                + "/Files/Compressed");

        // Create the storage directory if it does not exist
        if(! mediaStorageDir.exists()) {
            mediaStorageDir.mkdirs();
        }

        String mImageName="IMG_"+ String.valueOf(System.currentTimeMillis()) +".jpg";
        String uriString = (mediaStorageDir.getAbsolutePath() + "/"+ mImageName);

        return uriString;
    }
}