package a1door.monopoly;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Random;

import a1door.monopoly.Entities.ElementEntity;
import a1door.monopoly.Entities.UserEntity;

public class GameActivity extends AppCompatActivity implements Adapter.OnCityListener, MyGameService.GameListener{
    private static final String TAG = "GameActivity";

    RecyclerView recyclerView;
    GridLayoutManager layoutManager;
    ArrayList<ElementEntity> convretedCityArrayList;
    ArrayList<ElementEntity> originalCityArrayList;
    ArrayList<UserEntity> playersArrayList;
    String myId;
    String ip;
    String port;
    UserEntity myPlayer;
    ImageView leftCube;
    ImageView rightCube;
    Adapter adapter;
    Button endTurnButton;
    Button rollDiceButton;
    Button buyButton;
    //Button payButton;
    TextView currentPlayerTurnTextView;
    TextView myMoneyTextView;
    LinearLayout cubesLinarLayout;
    LinearLayout losingUi;
    LinearLayout winingUi;
    LinearLayout waitUi;
    TextView waitUiPlayerNameTextView;
    ImageView gifUiDisplay;
    boolean mBound = false;
    MyGameService.LocalBinder mBinder;
    MyGameService myGameService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        myPlayer =  (UserEntity) getIntent().getSerializableExtra("userEntity");
        ip = getIntent().getStringExtra("ip");
        port = getIntent().getStringExtra("port");

        recyclerView = findViewById(R.id.game_board_recyclerview);

        layoutManager = new GridLayoutManager(GameActivity.this,4,GridLayoutManager.HORIZONTAL,false);
        layoutManager.canScrollHorizontally();

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        convretedCityArrayList = new ArrayList<>();
        originalCityArrayList = new ArrayList<>();
        playersArrayList = new ArrayList<>();
        myId = myPlayer.getKey();
        adapter = new Adapter(GameActivity.this,convretedCityArrayList,playersArrayList,this,myPlayer);
        recyclerView.setAdapter(adapter);

        DefaultItemAnimator animator = new DefaultItemAnimator() {
            @Override
            public boolean canReuseUpdatedViewHolder(RecyclerView.ViewHolder viewHolder) {
                return true;
            }
        };

        recyclerView.setItemAnimator(animator);

        leftCube = findViewById(R.id.left_cube);
        rightCube = findViewById(R.id.right_cube);
        endTurnButton =findViewById(R.id.end_turn_button);
        rollDiceButton=findViewById(R.id.roll_dice_button);
        buyButton=findViewById(R.id.buy_button);
        currentPlayerTurnTextView = findViewById(R.id.hud_layout_turn);
        myMoneyTextView = findViewById(R.id.hud_layout_money);
        cubesLinarLayout =findViewById(R.id.cubes_linar_layout);
        losingUi = findViewById(R.id.losing_ui_layout);
        winingUi = findViewById(R.id.win_ui_layout);
        waitUi = findViewById(R.id.wait_ui_layout);
        waitUiPlayerNameTextView = findViewById(R.id.player_playing_textview);
        gifUiDisplay = findViewById(R.id.gif_imageview);
        myMoneyTextView.setText(myPlayer.getPoints()+"");
    }

    public void convretCityArrayListToBoardCityArrayList(ArrayList<ElementEntity> originalCityArrayList){
        convretedCityArrayList.clear();
        int revreseCounter = 0;
        int frontCounter = 0;

        if(originalCityArrayList.size()%2==0) {
            for (int i = 0; i < originalCityArrayList.size(); i++) {
                if (i >= 0 && i < 4) { //get 1st row
                    convretedCityArrayList.add(originalCityArrayList.get(i));
                    frontCounter++;
                } else if (i < originalCityArrayList.size() - 4 && i % 2 == 0) {
                    convretedCityArrayList.add(originalCityArrayList.get((originalCityArrayList.size() - 1) - (revreseCounter++)));
                    convretedCityArrayList.add(getEmptySpaceElementEntity());
                    convretedCityArrayList.add(getEmptySpaceElementEntity());
                } else if (i < originalCityArrayList.size() - 4 && i % 2 != 0) {
                    convretedCityArrayList.add(originalCityArrayList.get(frontCounter++));

                } else {
                    convretedCityArrayList.add(originalCityArrayList.get((originalCityArrayList.size() - 1) - (revreseCounter++)));
                }

            }
        }

    }

    public ElementEntity getEmptySpaceElementEntity(){
        //used to create the empty space in the middle of the board
        ElementEntity city = new ElementEntity();
        city.setElementId("");
        return city;
    }

    public void onRollDiceClicked(View view) {
        rollDiceButton.setVisibility(View.INVISIBLE);
        Glide.with(this).load(R.drawable.rollingdice).into(gifUiDisplay);
        gifUiDisplay.setVisibility(View.VISIBLE);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                final CubeResult cubeResult = rollDice();
                gifUiDisplay.setVisibility(View.INVISIBLE);
                leftCube.setImageResource(getCubeImage(cubeResult.getDice1()));
                rightCube.setImageResource(getCubeImage(cubeResult.getDice2()));
                rollDiceButton.setVisibility(View.INVISIBLE);
                endTurnButton.setVisibility(View.VISIBLE);
                cubesLinarLayout.setVisibility(View.VISIBLE);


                buyOrPayUiFunctionality(cubeResult.getDestCity());
            }
        }, 3000);


    }

    public void buyOrPayUiFunctionality(ElementEntity destCity){
        try {
            if (destCity.getMoreAttributes().get("ownerId") == null || destCity.getMoreAttributes().get("ownerId").toString().trim().isEmpty()) {
                if ((double)destCity.getMoreAttributes().get("price") < myPlayer.getPoints())
                    buyButton.setVisibility(View.VISIBLE);
            } else {
                if (!destCity.getMoreAttributes().get("ownerId").equals(myPlayer.getKey())) {

                    gifUiDisplay.setVisibility(View.VISIBLE);
                    Glide.with(this).load(R.drawable.paygif).into(gifUiDisplay);
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            gifUiDisplay.setVisibility(View.INVISIBLE);
                            endTurnButton.setVisibility(View.INVISIBLE);
                            checkIfLost();
                        }
                    }, 2000);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("buyOrPayUiFunctionality", e.getMessage());

        }
    }

    public int getCubeImage(int number){
        int[] myCubeImageList = new int[]{R.drawable.one, R.drawable.two,R.drawable.three,R.drawable.four,R.drawable.five,R.drawable.six};

        return myCubeImageList[number-1];
    }

    public void onBuyClicked(View view) {
        buyButton.setVisibility(View.INVISIBLE);
        try {
            myGameService.buyCity(myPlayer.getUserEmail(),myPlayer.getUserSmartspace(),port,ip,getPlayerSrcCity(myPlayer).getElementId(),getPlayerSrcCity(myPlayer).getElementSmartspace());
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("onBuyClicked", e.getMessage());
        }
    }

    public void checkIfLost() {
        ElementEntity city =  getPlayerSrcCity(myPlayer);
        try {
            if(myPlayer.getPoints()<(double)city.getMoreAttributes().get("price")){
                initLosingSequense();
                myGameService.endTurn(myPlayer.getUserEmail(),myPlayer.getUserSmartspace(),port,ip);
                myGameService.logOut(myPlayer.getUserEmail(),myPlayer.getUserSmartspace(),port,ip);
            }
            else{
                endTurnButton.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("onPayClicked", e.getMessage());
        }
    }

    private void initLosingSequense() {
        mBinder.unRegisterGameListener();
        myGameService.stopGettingTurnsUpdates();
        gifUiDisplay.setVisibility(View.VISIBLE);
        Glide.with(this).load(R.drawable.losegif).into(gifUiDisplay);
        losingUi.setVisibility(View.VISIBLE);
        myMoneyTextView.setText("0");
        cubesLinarLayout.setVisibility(View.INVISIBLE);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                gifUiDisplay.setVisibility(View.INVISIBLE);

            }
        }, 2000);

    }

    public void onEndTurnClicked(View view) {
        endTurnButton.setVisibility(View.INVISIBLE);
        myGameService.endTurn(myPlayer.getUserEmail(),myPlayer.getUserSmartspace(),port,ip);
    }


    //Listeners to server
    @Override
    public void onTurnChanges(UserEntity currentPlayerTurn) {
        Log.e(TAG,"onTurnChanges"+ currentPlayerTurn.getKey());
        if(currentPlayerTurn.getKey().equals(myPlayer.getKey())){
            rollDiceButton.setVisibility(View.VISIBLE);
            endTurnButton.setVisibility(View.INVISIBLE);
            buyButton.setVisibility(View.INVISIBLE);
            waitUi.setVisibility(View.INVISIBLE);

        }
        else{
            endTurnButton.setVisibility(View.INVISIBLE);
            buyButton.setVisibility(View.INVISIBLE);
            cubesLinarLayout.setVisibility(View.INVISIBLE);
            waitUiPlayerNameTextView.setText(currentPlayerTurn.getUsername()+" is playing");
            waitUi.setVisibility(View.VISIBLE);
        }
        currentPlayerTurnTextView.setText(currentPlayerTurn.getUsername());

    }

    @Override
    public void onDataChanged(ArrayList<?> arrayListFromServer) {
        if(arrayListFromServer.get(0) instanceof UserEntity){
            playersArrayList.clear();
            playersArrayList.addAll((ArrayList<UserEntity>)arrayListFromServer);
            checkIfGotPayed(myPlayer,getPlayerById(myPlayer.getKey()));
            myPlayer = getPlayerById(myPlayer.getKey());
            if(myPlayer != null)
                myMoneyTextView.setText(myPlayer.getPoints()+"");
        }
        else if(arrayListFromServer.get(0) instanceof  ElementEntity){
            originalCityArrayList.clear();
            originalCityArrayList.addAll((ArrayList<ElementEntity>) arrayListFromServer);
            convretCityArrayListToBoardCityArrayList(originalCityArrayList);
        }

        adapter.notifyDataSetChanged();
    }

    private void checkIfGotPayed(UserEntity myPlayerOld, UserEntity myPlayerUpdated) {
        if(myPlayerOld != null && myPlayerUpdated != null) {
            if (myPlayerOld.getPoints() != -1 &&
                    myPlayerOld.getPoints() < myPlayerUpdated.getPoints()) {
                gifUiDisplay.setVisibility(View.VISIBLE);
                Glide.with(this).load(R.drawable.gotpayedgif).into(gifUiDisplay);
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        gifUiDisplay.setVisibility(View.INVISIBLE);
                    }
                }, 2500);
            }
        }
    }


    @Override
    public void onPlayerLost(String losingPlayerName,String losingPlayerId) {
    if(!myPlayer.getKey().equals(losingPlayerId))
        Toast.makeText(this, losingPlayerName+" is out!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPlayerWin(String winingPlayerId) {
        if(myPlayer.getKey().equals(winingPlayerId)){
            win();
        }
    }

    @Override
    public void onPlayerJoined(String newPlayeName, String newPlayerId) {
        Toast.makeText(this, newPlayeName+" has joined", Toast.LENGTH_SHORT).show();
    }

    public void win() {
        gifUiDisplay.setVisibility(View.VISIBLE);
        Glide.with(this).load(R.drawable.gettingpaid).into(gifUiDisplay);
        waitUi.setVisibility(View.INVISIBLE);
        rollDiceButton.setVisibility(View.INVISIBLE);
        endTurnButton.setVisibility(View.INVISIBLE);
        buyButton.setVisibility(View.INVISIBLE);
        cubesLinarLayout.setVisibility(View.INVISIBLE);
        myGameService.endTurn(myPlayer.getUserEmail(),myPlayer.getUserSmartspace(),port,ip);
        myGameService.logOut(myPlayer.getUserEmail(),myPlayer.getUserSmartspace(),port,ip);
        mBinder.unRegisterGameListener();
        myGameService.stopGettingTurnsUpdates();

        winingUi.setVisibility(View.VISIBLE);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                gifUiDisplay.setVisibility(View.INVISIBLE);

            }
        }, 4000);
    }


    public UserEntity getPlayerById(String id){
        for(UserEntity player: playersArrayList){
            if(player.getKey().equals(id)){
                return player;
            }
        }
        return null;
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        initService();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(connection);
        myGameService.endTurn(myPlayer.getUserEmail(),myPlayer.getUserSmartspace(),port,ip);
        myGameService.logOut(myPlayer.getUserEmail(),myPlayer.getUserSmartspace(),port,ip);
        mBound = false;
        mBinder.unRegisterGameListener();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    public void onExitClicked(View view) {
        mBinder.unRegisterGameListener();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onCityClicked(int position) {
        if(position!=-1) {
            ElementEntity tempCity = convretedCityArrayList.get(position);
            if (!tempCity.getElementId().equals("")) {
                Intent intent = new Intent(GameActivity.this, CityPopUp.class);
                intent.putExtra("name", tempCity.getName());
                intent.putExtra("ownerName", tempCity.getMoreAttributes().get("ownerName") + "");
                intent.putExtra("price", (double) tempCity.getMoreAttributes().get("price"));
                intent.putExtra("fine", (double) tempCity.getMoreAttributes().get("fine"));
                startActivity(intent);
            }
        }
    }

    public CubeResult rollDice(){
        CubeResult cubeResult = new CubeResult();

        Random rand = new Random();
        cubeResult.setDice1(rand.nextInt(6)+1);
        cubeResult.setDice2(rand.nextInt(6)+1);
        cubeResult.setDestCity(getPlayerDestCity(myPlayer,cubeResult.getDice1()+cubeResult.getDice2()));
        ElementEntity srcCity = getPlayerSrcCity(myPlayer);
        myGameService.movePlayerToNewPosition(myPlayer.getUserSmartspace(),myPlayer.getUserEmail(),srcCity.getElementSmartspace(),srcCity.getElementId(),cubeResult.getDestCity().getElementSmartspace(),cubeResult.getDestCity().getElementId(),port,ip);

        return cubeResult;
    }

    public ElementEntity getPlayerDestCity(UserEntity player, int offSet){
        ElementEntity result = new ElementEntity();

        for (ElementEntity city: originalCityArrayList){
            if(isPlayerOnThatCity((ArrayList<String>)city.getMoreAttributes().get("visitors"),player.getKey())) {
                if(city.getLocation().getX()+offSet>= originalCityArrayList.size()){
                    result =  originalCityArrayList.get((int)city.getLocation().getX()+offSet-originalCityArrayList.size());
                    break;
                }
                else{
                    result = originalCityArrayList.get((int)city.getLocation().getX()+offSet);
                    break;
                }
            }
        }
        return  result;
    }

    public ElementEntity getPlayerSrcCity(UserEntity player){
        ElementEntity result = new ElementEntity();

        mainLoop: for(ElementEntity city :originalCityArrayList){
            if(isPlayerOnThatCity((ArrayList<String>)city.getMoreAttributes().get("visitors"),player.getKey())) {
                result = city;
                break mainLoop;
            }
        }
        return result;
    }

    public boolean isPlayerOnThatCity(ArrayList<String> playerArrayList,String playerKey){
        //helper function for getPlayerDestCity
        for(String playerKeyFromList : playerArrayList){
            if(playerKeyFromList.equals(playerKey)){
                return true;
            }
        }
        return false;
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
            mBinder.registerGameListener(GameActivity.this);
            myGameService.startGettingCitiesUpdates(myPlayer.getUserEmail(),myPlayer.getUserSmartspace(),port,ip);
            myGameService.startGettingPlayersUpdates(myPlayer.getUserEmail(),myPlayer.getUserSmartspace(),port,ip);
            myGameService.startGettingTurnsUpdates(myPlayer.getUserEmail(),myPlayer.getUserSmartspace(),port,ip);
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
           myGameService.stopGettingCitiesUpdates();
           myGameService.stopGettingPlayersUpdates();
           myGameService.stopGettingTurnsUpdates();
           myGameService.logOut(myPlayer.getUserEmail(),myPlayer.getUserSmartspace(),port,ip);
           mBinder.unRegisterGameListener();
           mBound = false;
        }
    };
}
