package a1door.monopoly;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.Serializable;

import a1door.monopoly.Entities.UserEntity;

public class MainActivity extends AppCompatActivity implements MyGameService.LogInToServerListener {
    private static final String TAG = "MainActivity";
    boolean mBound = false;
    MyGameService.LocalBinder mBinder;
    MyGameService myGameService;
    private View mSmallProgressBarView;
    EditText emailTextBox;
    EditText smartspaceTextBox;
    EditText ipTextBox;
    EditText portTextBox;
    ImageView funnyGifImageview;
    private String ip;
    private String port;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        funnyGifImageview = findViewById(R.id.login_screen_funnygif_imageview);

        Glide.with(this).load(R.drawable.giphy).into(funnyGifImageview);
        mSmallProgressBarView= findViewById(R.id.login_screen_progressbar);

        emailTextBox = findViewById(R.id.login_screen_email_textbox);
        smartspaceTextBox = findViewById(R.id.login_screen_smartspace_textbox);
        ipTextBox = findViewById(R.id.login_screen_ip_textbox);
        portTextBox = findViewById(R.id.login_screen_port_textbox);

    }

    @Override
    protected void onStart() {
        super.onStart();
        initService();
    }


    private void initService(){
        Log.e(TAG,"on start");
        Intent intent = new Intent(this, MyGameService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(connection);
        mBound = false;
    }

    public void onClickLogIn(View view) {
        String email,smartspace;
        email = (emailTextBox.getText().toString()).trim();
        smartspace = smartspaceTextBox.getText().toString().trim();
        ip = ipTextBox.getText().toString().trim();
        port = portTextBox.getText().toString().trim();


        myGameService.attemptToLogIn(email,smartspace,port,ip);
        showSmallProgressBar(true);

    }


    public void showSmallProgressBar(final Boolean toShow){
        if(toShow){ //disable/enable user interaction
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
        else{
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mSmallProgressBarView.setVisibility(toShow ? View.VISIBLE : View.GONE);
            mSmallProgressBarView.animate().setDuration(shortAnimTime).alpha(
                    toShow ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mSmallProgressBarView.setVisibility(toShow ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            mSmallProgressBarView.setVisibility(toShow ? View.VISIBLE : View.GONE);
        }
    }



    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            Log.e(TAG,"onServiceConnected");
            mBinder = (MyGameService.LocalBinder) service;
            myGameService = mBinder.getService();
            mBinder.registerLogInToServerListener(MainActivity.this);
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
            mBinder.unRegisterLogInToServerListener();
        }
    };


    @Override
    public void onLogInGood(UserEntity userEntity) {
        Toast.makeText(myGameService, "login successfully", Toast.LENGTH_SHORT).show();
        showSmallProgressBar(false);

        Intent intent = new Intent(this, WaitingForPlayersActivity.class);
        intent.putExtra("userEntity", userEntity);
        intent.putExtra("ip",ip);
        intent.putExtra("port",port);

        startActivity(intent);
    }

    @Override
    public void onLogInBad() {
        Toast.makeText(myGameService, "login failed", Toast.LENGTH_SHORT).show();
        showSmallProgressBar(false);
    }

    public void onClickSignIn(View view) {
        Intent intent = new Intent(this, SignInActivity.class);
        startActivity(intent);

    }
}
