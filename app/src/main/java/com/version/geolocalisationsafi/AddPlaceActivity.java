package com.version.geolocalisationsafi;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AddPlaceActivity extends AppCompatActivity implements LocationListener{

    ConstraintLayout Menuuu;
    private ImageView takePictureButton;
    private ImageView picture;
    private LocationManager locationManager;
    private Location location;
    private final int REQUEST_LOCATION = 200;
    private static final String TAG = "AddPlaceActivity";
    private EditText locationText , descriptionText , tagsText;
    private double longitude=0 , lattitude=0;

    DatabaseReference mDatabase , mDataUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_place);

        //get instance from database
        mDataUser = FirebaseDatabase.getInstance().getReference("User");
        mDatabase = FirebaseDatabase.getInstance().getReference("Data");

        descriptionText = (EditText) findViewById(R.id.description);
        tagsText = (EditText) findViewById(R.id.Tags);
        Menuuu=(ConstraintLayout) findViewById(R.id.menu);
        Menuuu.setVisibility(View.GONE);
        Menuuu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Menuuu.setVisibility(View.GONE);
            }
        });
        takePictureButton = (ImageView) findViewById(R.id.callcam);
        picture = (ImageView) findViewById(R.id.picture);
/////////////////////////////////////////////////////////////// pic
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            takePictureButton.setEnabled(
                    false
            );
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE }, 0);
        }
        ///////////////////////////// geolocalisation
        locationText =(EditText)findViewById(R.id.location);
        locationManager = (LocationManager) getSystemService(Service.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 2, this);
            if (locationManager != null) {
                location = getLastKnownLocation();
                Toast.makeText(this, ""+locationManager.toString(), Toast.LENGTH_SHORT).show();

            }
        }
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            if (location != null) {

               /*latitudePosition.setText(String.valueOf(location.getLatitude()));
                longitudePosition.setText(String.valueOf(location.getLongitude()));*/
               lattitude = location.getLatitude();
               longitude = location.getLongitude();
                getAddressFromLocation(location, getApplicationContext(), new GeoCoderHandler());
            }
        } else {
            showGPSDisabledAlertToUser();
        }
    }////////////////////////////////////////////////////
    private Location getLastKnownLocation() {
        locationManager = (LocationManager)getApplicationContext().getSystemService(LOCATION_SERVICE);
        List<String> providers = locationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            @SuppressLint("MissingPermission") Location l = locationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }
        return bestLocation;
    }
//////////////////////////////////////////////////
    public void hid(View view) {
        Menuuu.setVisibility(View.VISIBLE);
        Fragment fragment = new MenuFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.menu,fragment,fragment.getClass().getSimpleName())
                .addToBackStack(null).commit();
        Menuuu.bringToFront();
    }
///////////////////////////////////////////
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 0) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                takePictureButton.setEnabled(
                        true
                );
            }
        }
    }

    private Uri file;
////////////////////////////////////////////////////
    public void takePicture(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        file = Uri.fromFile(getOutputMediaFile());
        intent.putExtra(MediaStore.EXTRA_OUTPUT, file);
        StrictMode.VmPolicy.Builder builder=new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        startActivityForResult(intent, 100);
    }
/////////////////////////////////////////////////////
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 100) {
            if (resultCode == RESULT_OK) {
                picture.setImageURI(file);
            }
        }
    }
    ////////////////////////////////////////////////////
    private static File getOutputMediaFile(){
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "CameraDemo");

        if (!mediaStorageDir.exists()){
            if (!mediaStorageDir.mkdirs()){
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        return new File(mediaStorageDir.getPath() + File.separator +
                "IMG_"+ timeStamp + ".jpg");
    }

    /////////////////////////////////////////////////////////////////////////////////////methods geo
    @Override
    public void onLocationChanged(Location location) {
        /*latitudePosition.setText(String.valueOf(location.getLatitude()));
        longitudePosition.setText(String.valueOf(location.getLongitude()));
        getAddressFromLocation(location, getApplicationContext(), new GeoCoderHandler());
   */
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {
        if (s.equals(LocationManager.GPS_PROVIDER)) {
            showGPSDisabledAlertToUser();
        }
    }

    private void showGPSDisabledAlertToUser() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("GPS is disabled in your device. Would you like to enable it?")
                .setCancelable(false)
                .setPositiveButton("Goto Settings Page To Enable GPS", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent callGPSSettingIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(callGPSSettingIntent);
                    }
                });
        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    public static void getAddressFromLocation(final Location location, final Context context, final Handler handler) {
        Thread thread = new Thread() {
            @Override public void run() {
                Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                String result = null;
                try {
                    List<Address> list = geocoder.getFromLocation(
                            location.getLatitude(), location.getLongitude(), 1);
                    if (list != null && list.size() > 0) {
                        Address address = list.get(0);
                        // sending back first address line and locality
                        result = address.getAddressLine(0) + ", " + address.getLocality() + ", " +  address.getCountryName() ;
                    }
                } catch (IOException e) {
                    Log.e(TAG, "Impossible to connect to Geocoder", e);
                } finally {
                    Message msg = Message.obtain();
                    msg.setTarget(handler);
                    if (result != null) {
                        msg.what = 1;
                        Bundle bundle = new Bundle();
                        bundle.putString("address", result);
                        msg.setData(bundle);
                    } else
                        msg.what = 0;
                    msg.sendToTarget();
                }
            }
        };
        thread.start();
    }

    SharedPreferences userData;
    String client;
    public void addData(View view) {
        Date c = Calendar.getInstance().getTime();
        String dateAjout = c.toString();

        userData = getApplicationContext().getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        client =  userData.getString("Nom",null);

        String description = descriptionText.getText().toString();

        if(description == ""){
            Toast.makeText(AddPlaceActivity.this,"Please Enter Some Infos",Toast.LENGTH_SHORT).show();
        }
        else
        {

            //enregistrer l image su un storage firebase
            ////////////////////////// upload pic
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReferenceFromUrl("gs://geolocalisationsafi.appspot.com/").child(client+dateAjout+lattitude+longitude+".jpg");
            picture.setDrawingCacheEnabled(true);
           // picture.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
           // picture.layout(0, 0, picture.getMeasuredWidth(), picture.getMeasuredHeight());
            picture.buildDrawingCache();
            Bitmap bitmap = Bitmap.createBitmap(picture.getDrawingCache());
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            byte[] datas = outputStream.toByteArray();
            UploadTask uploadTask = storageRef.putBytes(datas);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Toast.makeText(AddPlaceActivity.this,"error",Toast.LENGTH_SHORT).show();

                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                   // Toast.makeText(AddPlaceActivity.this,"no error o_o",Toast.LENGTH_SHORT).show();

                }
            });

            //enregistrer dans la base de données
            String id = mDatabase.push().getKey();
            Data data = new Data(id,client,description,lattitude,longitude,dateAjout,client+dateAjout+lattitude+longitude+".jpg",tagsText.getText().toString(),0,"");
            mDatabase.child(id).setValue(data);
            Toast.makeText(AddPlaceActivity.this,"Emplacement Bien Ajouté!",Toast.LENGTH_SHORT).show();
            descriptionText.setText("");
            tagsText.setText("");
            picture.setImageResource(R.drawable.img);


            mDataUser.addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot){
                //get emails
                long value=dataSnapshot.getChildrenCount();
                Log.w(TAG,"no children : "+value);
                List<User> list = new ArrayList<User>();
                for (DataSnapshot child: dataSnapshot.getChildren()) {
                    list.add(child.getValue(User.class));
                }

                for(int i=0;i<list.size();i++){
                    if(list.get(i).getPseudo() != client) {
                        GMailSender sender = new GMailSender(list.get(i).getEmail(), "Application GEO ENSAS", client + " has added a new place, loging to see it ! ");
                        sender.Send();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error){
                // Failed to read value
                Log.w(TAG,"Failed to read value.",error.toException());
            }
        });
        }
    }

    public void cancelAdd(View view) {
        descriptionText.setText("");
        picture.setImageResource(R.drawable.img);
    }

    private class GeoCoderHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            String result;
            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    result = bundle.getString("address");
                    break;
                default:
                    result = null;
            }
            locationText.setText(result);
        }
    }
}
///////////////////////////////////////////////////////////