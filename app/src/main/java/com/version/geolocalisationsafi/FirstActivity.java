package com.version.geolocalisationsafi;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PointOfInterest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class FirstActivity extends AppCompatActivity  implements LocationListener{
    ConstraintLayout Menuuu;
    LocationManager lm;
    private DatabaseReference databaseTeachers;
    private static final  int PERMS_CALL_ID=1234;
    private MapFragment mapFragment;
    private DatabaseReference mDataBase , mDataUser;
    private GoogleMap googleMap;
    Data point;
    String id;
    SharedPreferences userData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
        Menuuu = (ConstraintLayout) findViewById(R.id.menu);
        Menuuu.setVisibility(View.GONE);
        Menuuu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Menuuu.setVisibility(View.GONE);
            }
        });

        FragmentManager fragmentManager=getFragmentManager();
        mapFragment=(MapFragment)fragmentManager.findFragmentById(R.id.map);

        //Add user or not
        mDataUser = FirebaseDatabase.getInstance().getReference("User");
        userData = this.getSharedPreferences("MyPref", Context.MODE_PRIVATE);

        mDataUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User util=null;
                boolean exists = false;
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    Map<String, Object> model = (Map<String, Object>) child.getValue();
                    util = new User("0",userData.getString("Nom",null) , userData.getString("Email", null), userData.getString("ImageUser" , null));

                    if(model.get("email").equals(util.getEmail())) {
                        exists = true;
                        break;
                    }
                }

                if(exists == false) {
                    String id = mDataUser.push().getKey();
                    util.setId(id);
                    mDataUser.child(id).setValue(util);
                }
            }
           @Override
           public void onCancelled(DatabaseError databaseError) {

           }
        });
        mDataBase =FirebaseDatabase.getInstance().getReference("Data");
        mDataBase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    String id= child.getKey();
                    Data lokasi3 =dataSnapshot.child(id).getValue(Data.class);

                    LatLng cod = new LatLng(lokasi3.getLattitude(),lokasi3.getLongitude());
                    String message="";
                    if(lokasi3.getNbrestars()!=0){
                        message="le nombre d'etoiles est  "+lokasi3.getNbrestars();
                    }
                    googleMap.addMarker(new MarkerOptions().position(cod).title(lokasi3.getDescription()+" "+lokasi3.getAvis()+"  "+message));

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPermissions();

    }
    private  void checkPermissions(){

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION},PERMS_CALL_ID);
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }   lm = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {


            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 0, this);
        }
        if(lm.isProviderEnabled(LocationManager.PASSIVE_PROVIDER)){

            lm.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER,10000,0,this);
        }
        if(lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){

            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,10000,0,this);
        }
        loadMap();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==PERMS_CALL_ID){
            checkPermissions();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(lm!=null){
            lm.removeUpdates(this);
        }
    }
    @SuppressWarnings("MissingPermission")
    private void loadMap(){

        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(final GoogleMap googleMap) {
                FirstActivity.this.googleMap=googleMap;
                googleMap.moveCamera(CameraUpdateFactory.zoomBy(15));
                googleMap.setMyLocationEnabled(true);

                //final MediaPlayer mediaPlayer= MediaPlayer.create(FirstActivity.this,R.raw.classic);
                googleMap.setOnPoiClickListener(new GoogleMap.OnPoiClickListener(){
                    @Override
                    public void onPoiClick(PointOfInterest pointOfInterest) {
                        //Intent intent = new Intent()
                        Intent intent = new Intent(getBaseContext(),DetailsLieu.class);
                        intent.putExtra("macle", pointOfInterest.placeId);
                        startActivity(intent);
                    }
                } );
            }
        });
    }
    @Override
    public void onLocationChanged(Location location) {

        double latitude=location.getLatitude();
        double longtitude=location.getLongitude();


        if(googleMap!=null){

            LatLng googleLocation=new LatLng(latitude,longtitude);
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(googleLocation));
            //googleMap.addMarker(new MarkerOptions().position(googleLocation).title("infini"));
        }

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    //Cllick listener to call menu fragment
    public void hid(View view) {
        Menuuu.setVisibility(View.VISIBLE);
        Fragment fragment = new MenuFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.menu,fragment,fragment.getClass().getSimpleName())
                .addToBackStack(null).commit();
        Menuuu.bringToFront();
    }


}
