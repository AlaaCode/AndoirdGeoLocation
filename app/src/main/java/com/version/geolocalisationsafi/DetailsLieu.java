package com.version.geolocalisationsafi;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResult;
import com.google.android.gms.location.places.PlacePhotoResult;
import com.google.android.gms.location.places.Places;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class DetailsLieu extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    String data="";
    private ImageView img;
    private GoogleApiClient mGoogleApiClient;
    private TextView phone;
    private TextView web;
    private TextView address;
    private TextView post;
    private TextView textview2;
    private TextView textview3;
    private RatingBar etoils;
    private ListView listView;
    private Button btn;
    private AlertDialog dialog;
    private RatingBar ratingBar2;
    private EditText editText;
    private RatingBar ratingBar;
    List<Avis> commentaire=new ArrayList<Avis>();
    SharedPreferences userData;
    FirebaseAuth mAuth;
    String compte;

    private DatabaseReference mDataBase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_lieu);

        //initialiser le client
        mAuth = FirebaseAuth.getInstance();
        //get User Authentified data
        if(mAuth.getCurrentUser() != null){
            userData = this.getSharedPreferences("MyPref", Context.MODE_PRIVATE);
            if(userData!=null) {
               compte= userData.getString("Nom", null);
            }
        }

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();
        Bundle extra = this.getIntent().getExtras();
        if(extra != null)
            data = extra.getString("macle");
        mDataBase= FirebaseDatabase.getInstance().getReference("Avis");
        phone=(TextView)findViewById(R.id.phone);
        web=(TextView)findViewById(R.id.web);
        post=(TextView)findViewById(R.id.textView);
        address=(TextView)findViewById(R.id.address);
        img=(ImageView)findViewById(R.id.picture1);
        listView=(ListView)findViewById(R.id.listView);
        etoils=(RatingBar)findViewById(R.id.etoils);
        mDataBase.orderByChild("placeid").equalTo(data).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    String id= child.getKey();
                    Avis comments=dataSnapshot.child(id).getValue(Avis.class);

                    commentaire.add(comments);

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        CustomerAdapter customerAdapter=new CustomerAdapter();
        listView.setAdapter(customerAdapter);

        Places.GeoDataApi.getPlacePhotos(mGoogleApiClient,data)
                .setResultCallback(new ResultCallback<PlacePhotoMetadataResult>() {


                    @Override
                    public void onResult(PlacePhotoMetadataResult photos) {
                        if (!photos.getStatus().isSuccess()) {
                            return;
                        }

                        PlacePhotoMetadataBuffer photoMetadataBuffer = photos.getPhotoMetadata();
                        if (photoMetadataBuffer.getCount() > 0) {
                            // Display the first bitmap in an ImageView in the size of the view
                            photoMetadataBuffer.get(0)
                                    .getScaledPhoto(mGoogleApiClient, img.getWidth(),
                                            img.getHeight())
                                    .setResultCallback(mDisplayPhotoResultCallback);
                        }
                        photoMetadataBuffer.release();
                    }
                });


        Places.GeoDataApi.getPlaceById(mGoogleApiClient,  data)
                .setResultCallback(new ResultCallback<PlaceBuffer>() {
                    @Override
                    public void onResult(PlaceBuffer places) {
                        if (places.getStatus().isSuccess() && places.getCount() > 0) {
                            final Place myPlace = places.get(0);
                            phone.setText(myPlace.getPhoneNumber());

                            web.setText(myPlace.getName());
                            address.setText(myPlace.getAddress());

                            etoils.setRating(myPlace.getRating());

                        } else {
                            Toast.makeText(DetailsLieu.this, "Place not found: " , Toast.LENGTH_SHORT).show();

                        }
                        places.release();
                    }
                });

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder mBuilder=new AlertDialog.Builder(DetailsLieu.this);
                View mview=getLayoutInflater().inflate(R.layout.ajoutpoitinterit,null);
                editText=(EditText)mview.findViewById(R.id.editText);
                ratingBar=(RatingBar)mview.findViewById(R.id.ratingBar);
                btn=(Button)mview.findViewById(R.id.button2);
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Avis avis=new Avis();
                        String id=mDataBase.push().getKey();
                        avis.setId(id);
                        if(!editText.getText().toString().isEmpty() && ratingBar.getRating()!=0  ){
                            avis.setAvis(editText.getText().toString());
                            avis.setNbrestars(ratingBar.getRating());
                            avis.setCompte(compte);
                            avis.setPlaceid(data);
                            mDataBase.child(id).setValue(avis);
                        }
                        else if(editText.getText().toString().isEmpty()) {
                            Toast.makeText(DetailsLieu.this, "vous devez ecrire quelque chose " , Toast.LENGTH_SHORT).show();

                        }
                        else {
                            Toast.makeText(DetailsLieu.this, "donnez votre avis en utilisant rating bar " , Toast.LENGTH_SHORT).show();
                            return;
                        }



                        dialog.dismiss();
                    }
                });
                mBuilder.setView(mview);
                dialog=mBuilder.create();

                dialog.show();
            }
        });

    }

    private ResultCallback<PlacePhotoResult> mDisplayPhotoResultCallback
            = new ResultCallback<PlacePhotoResult>() {
        @Override
        public void onResult(PlacePhotoResult placePhotoResult) {
            if (!placePhotoResult.getStatus().isSuccess()) {
                return;
            }
            img.setImageBitmap(placePhotoResult.getBitmap());
        }
    };
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
    class CustomerAdapter extends BaseAdapter{

        @Override
        public int getCount() {


            return commentaire.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View view1=getLayoutInflater().inflate(R.layout.customlayout,null);

            textview2=(TextView)view1.findViewById(R.id.textView2);
            textview3=(TextView)view1.findViewById(R.id.textView3);
            ratingBar2=(RatingBar)view1.findViewById(R.id.ratingBar2);
            Avis auxi=commentaire.get(i);
            textview2.setText(auxi.getAvis());
            textview3.setText(auxi.getCompte());
            ratingBar2.setRating(auxi.getNbrestars());
            return view1;
        }
    }
}
