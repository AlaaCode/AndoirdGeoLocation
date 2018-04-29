package com.version.geolocalisationsafi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

/**
 * A fragment with a Google +1 button.
 */
public class MenuFragment extends Fragment implements View.OnClickListener {

    // The request code must be 0 or greater.
    private static final int PLUS_ONE_REQUEST_CODE = 0;
    // The URL to +1.  Must be a valid URL.
    private final String PLUS_ONE_URL = "http://developer.android.com";

    FirebaseAuth mAuth;
    SharedPreferences userData;
    Intent intent;
    Button add , localiser , deconnecte;
    ImageView picture;
    TextView name , email;

    public MenuFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_menu, container, false);
        name = (TextView) view.findViewById(R.id.Name);
        email = (TextView) view.findViewById(R.id.Email);
        picture = (ImageView) view.findViewById(R.id.picture);

        //initialiser le client
        mAuth = FirebaseAuth.getInstance();

        //Add events on buttons to navigate the application
        add = (Button) view.findViewById(R.id.add);
        add.setOnClickListener(this);
        localiser = (Button) view.findViewById(R.id.map);
        localiser.setOnClickListener(this);
        deconnecte = (Button) view.findViewById(R.id.deconnexion);
        deconnecte.setOnClickListener(this);
        return view;
    }


    @Override
    public void onStart() {
        super.onStart();
        //get User Authentified data
        if(mAuth.getCurrentUser() != null){
            userData = this.getActivity().getSharedPreferences("MyPref", Context.MODE_PRIVATE);
            if(userData!=null) {
                name.setText("Salut " + userData.getString("Nom", null));
                email.setText("Connected as  " + userData.getString("Email", null));
                Picasso.get().load(userData.getString("ImageUser", null)).into(picture);
            }
        }
        else {
            //startActivity(new Intent(getActivity(), LoginActivity.class));
            Toast.makeText(getActivity(), "Connexion perdu!!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add:
                intent = new Intent(getContext(), AddPlaceActivity.class);
                startActivity(intent);
                break;
            case R.id.map:
                intent = new Intent(getContext(), FirstActivity.class);
                startActivity(intent);
                break;
            case R.id.deconnexion:
                //clear sharedprefenrences
                userData.edit().clear().commit();
                //clear user session
                mAuth.signOut();
                //redirect
                intent = new Intent(getContext(), LoginActivity.class);
                startActivity(intent);
                break;
            default:

                break;
        }
    }
}
