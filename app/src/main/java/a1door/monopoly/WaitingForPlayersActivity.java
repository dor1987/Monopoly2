package a1door.monopoly;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import a1door.monopoly.Entities.ElementEntity;
import a1door.monopoly.Entities.UserEntity;
import a1door.monopoly.R;

public class WaitingForPlayersActivity extends AppCompatActivity implements MyGameService.WaitingForPlayersToStartGameListener {
    private static final String TAG = "WaitingPlayersActivity";
    boolean mBound = false;
    MyGameService.LocalBinder mBinder;
    MyGameService myGameService;

    ImageView topImageview;
    ImageView bottomImageview;
    TextView nameTextView;
    UserEntity userEntity;
    String ip;
    String port;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting_for_players);

        userEntity =  (UserEntity) getIntent().getSerializableExtra("userEntity");
        ip = getIntent().getStringExtra("ip");
        port = getIntent().getStringExtra("port");

        topImageview = findViewById(R.id.waiting_activity_top_image);
        bottomImageview = findViewById(R.id.waiting_activity_bottom_image);
        nameTextView = findViewById(R.id.waiting_activity_name);
        Glide.with(this).load(R.drawable.waitinggif).into(bottomImageview);
        Glide.with(this).load(R.drawable.waitinglogogif).into(topImageview);
        if(userEntity.getUsername()!=null && !userEntity.getUsername().trim().isEmpty())
            nameTextView.setText("Hey "+userEntity.getUsername());
    }


    @Override
    protected void onStart() {
        super.onStart();
        initService();
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
    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            Log.e(TAG,"onServiceConnected");
            mBinder = (MyGameService.LocalBinder) service;
            myGameService = mBinder.getService();
            mBinder.registerWaitingForEnoughPlayersToStartGameListener(WaitingForPlayersActivity.this);
            mBound = true;
            myGameService.checkIfEnoughPlayers(userEntity.getUserEmail(),userEntity.getUserSmartspace(),port,ip);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
            mBinder.unRegisterWaitingForEnoughPlayersToStartGameListener();
        }
    };

    public void onClickCancel(View view) {
        myGameService.cancelCheckIfEnoughPlayers();
        myGameService.logOut(userEntity.getUserEmail(),userEntity.getUserSmartspace(),port,ip);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void onEnoughPlayersToStartGame() {
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("userEntity", userEntity);
        intent.putExtra("ip",ip);
        intent.putExtra("port",port);
        startActivity(intent);
    }


}
