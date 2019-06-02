package a1door.monopoly;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class SignInActivity extends AppCompatActivity {
    private ArrayList<AvatarItem> mAvatarList;
    private AvatarAdapter mAdapter;
    private MyGameService.LocalBinder mBinder;
    private MyGameService myGameService;
    boolean mBound = false;
    private static final String TAG = "SignInActivity";

    private TextView ipTextView;
    private TextView portTextView;
    private TextView eMailTextView;
    private TextView userNameTextView;
    private AvatarItem clickedItem;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        ipTextView = findViewById(R.id.signin_screen_ip_textbox);
        portTextView = findViewById(R.id.signin_screen_port_textbox);
        eMailTextView = findViewById(R.id.signin_screen_email_textbox);
        userNameTextView = findViewById(R.id.signin_screen_username_textbox);

        initList();

        Spinner spinnerAvatars = findViewById(R.id.signin_screen_avatar_spinner);
        mAdapter = new AvatarAdapter(this,mAvatarList);
        spinnerAvatars.setAdapter(mAdapter);


        spinnerAvatars.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                clickedItem = (AvatarItem) adapterView.getItemAtPosition(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void initList(){
        mAvatarList = new ArrayList<>();
        mAvatarList.add(new AvatarItem("Dog",R.drawable.dog));
        mAvatarList.add(new AvatarItem("Car",R.drawable.car));
        mAvatarList.add(new AvatarItem("Wheelbarrow",R.drawable.wheelbarrow));
        mAvatarList.add(new AvatarItem("Thimble",R.drawable.thimble));
        mAvatarList.add(new AvatarItem("Iron",R.drawable.iron));
        mAvatarList.add(new AvatarItem("Ship",R.drawable.ship));
        mAvatarList.add(new AvatarItem("Shoe",R.drawable.shoe));
        mAvatarList.add(new AvatarItem("Hat",R.drawable.hat));
    }

    public void onSaveClicked(View view) {
        if (validateForm()) {
            String ip = ipTextView.getText().toString().trim();
            String port = portTextView.getText().toString().trim();
            String userName = userNameTextView.getText().toString().trim();
            String email = eMailTextView.getText().toString().trim();
            String avatar;

            if(clickedItem == null)
                avatar = "Dog";
            else
                avatar = clickedItem.getmAvatarName();
            Toast.makeText(myGameService, "Sign in complete", Toast.LENGTH_SHORT).show();
            myGameService.addNewUser(email,userName,avatar,port,ip);
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
        else{
            Toast.makeText(myGameService, "Error,Check your input", Toast.LENGTH_SHORT).show();
        }

    }

    public boolean validateForm(){
        boolean result = true;
        if(ipTextView.getText() == null
                || ipTextView.getText().toString().trim().isEmpty())
            result = false;

        if(userNameTextView.getText() == null
                || userNameTextView.getText().toString().trim().isEmpty())
            result = false;

        if(eMailTextView.getText() == null
                || eMailTextView.getText().toString().trim().isEmpty())
            result = false;

        if(ipTextView.getText() == null
                || ipTextView.getText().toString().trim().isEmpty())
            result = false;

        return result;
    }
    @Override
    protected void onStart() {
        initService();
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(connection);
        mBound = false;
    }

    private void initService(){
        Log.e(TAG,"on start");
        Intent intent = new Intent(this, MyGameService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            Log.e(TAG,"onServiceConnected");
            mBinder = (MyGameService.LocalBinder) service;
            myGameService = mBinder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };
}
