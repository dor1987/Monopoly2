package a1door.monopoly;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.file.attribute.AclEntryType;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import a1door.monopoly.Boundaries.ActionBoundary;
import a1door.monopoly.Boundaries.ElementBoundary;
import a1door.monopoly.Boundaries.NewUserFormBoundary;
import a1door.monopoly.Boundaries.UserBoundary;
import a1door.monopoly.Entities.ActionEntity;
import a1door.monopoly.Entities.ElementEntity;
import a1door.monopoly.Entities.UserEntity;

public class MyGameService extends Service {
    private static final String TAG = "MyGameService";
    private RequestQueue queue;
    private WaitingForPlayersToStartGameListener mWaitingForEnoughPlayersListener;
    private LogInToServerListener mLogInToServerListener;
    private GameListener mGameListener;

    private Gson gson = new Gson();
    private ScheduledThreadPoolExecutor waitingForPlayersExecutor;
    private ScheduledThreadPoolExecutor playersUpdatesExecutor;
    private ScheduledThreadPoolExecutor citiesUpdatesExecutor;
    private ScheduledThreadPoolExecutor turnUpdatesExecutor;
    // Binder given to clients
    private final LocalBinder binder = new LocalBinder();
    private ElementEntity game=null;
    private UserEntity lastPlayerPlayed;
    private ArrayList<UserEntity> oldPlayerArrayList;//used to check if some 1 lost
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        queue = Volley.newRequestQueue(this);// this = context
    }

    public void attemptToLogIn(String email, String smartspace,String port, String ip){
        //3 stages 1. getuser 2.get game 3.loging seq
        playerLogin(email,smartspace,port,ip);
    }

    public void checkIfEnoughPlayers(final String email, final String smartspace,final String port, final String ip){
        waitingForPlayersExecutor = new ScheduledThreadPoolExecutor( 1 );

        Runnable task = new Runnable() {
            @Override
            public void run() {
                getAmountOfOnlinePlayersFromServer(email, smartspace,port, ip);
            }
        };
        waitingForPlayersExecutor.scheduleAtFixedRate(task, 0, 4000, TimeUnit.MILLISECONDS);
    }

    public void cancelCheckIfEnoughPlayers(){
        waitingForPlayersExecutor.shutdownNow();
    }

    public void movePlayerToNewPosition(String playerSmartspace,String playerEmail,String srcElementSmartspace,String srcElementId,String destElementSmartspace,String destElementId,String port,String ip){
        checkOut(playerSmartspace,playerEmail,srcElementSmartspace,srcElementId,port,ip);
        checkIn(playerSmartspace,playerEmail,destElementSmartspace,destElementId,port,ip);
    }

    public void buyCity(String email, String playerSmartspace,String port, String ip,String cityId,String citySmartspace){
        buyCityFromServer(email,playerSmartspace,port,ip,cityId,citySmartspace);
    }

    public void startGettingPlayersUpdates(final String email, final String smartspace,final String port, final String ip){
        playersUpdatesExecutor = new ScheduledThreadPoolExecutor( 1 );
        Runnable task = new Runnable() {
            @Override
            public void run() {
                getPlayersFromServer(email, smartspace,port, ip);
            }
        };

        playersUpdatesExecutor.scheduleAtFixedRate(task, 0, 1000, TimeUnit.MILLISECONDS);
    }

    public void stopGettingPlayersUpdates(){
        playersUpdatesExecutor.shutdownNow();
    }

    public void startGettingCitiesUpdates(final String email, final String smartspace,final String port, final String ip){
        citiesUpdatesExecutor = new ScheduledThreadPoolExecutor( 1 );
        Runnable task = new Runnable() {
            @Override
            public void run() {
                getCitiesFromServer(email, smartspace,port, ip);
            }
        };

        citiesUpdatesExecutor.scheduleAtFixedRate(task, 0, 1000, TimeUnit.MILLISECONDS);
    }

    public void stopGettingCitiesUpdates(){
        citiesUpdatesExecutor.shutdownNow();
    }

    public void logOut(String email, String smartspace,String port, String ip){
        lastPlayerPlayed = null;
        if(game==null)
            getGameForLogOut(email,smartspace,port,ip);//after getting game will do log out
        else
            logOutFromServer(smartspace,email,game,port,ip);
    }

    public void startGettingTurnsUpdates(final String email, final String smartspace,final String port, final String ip){
        turnUpdatesExecutor = new ScheduledThreadPoolExecutor( 1 );
        Runnable task = new Runnable() {
            @Override
            public void run() {
                getWhosTurnIsItFromServer(email, smartspace,port,ip);
            }
        };

        turnUpdatesExecutor.scheduleAtFixedRate(task, 0, 1000, TimeUnit.MILLISECONDS);
    }

    public void stopGettingTurnsUpdates(){
        turnUpdatesExecutor.shutdownNow();
    }

    public void endTurn(final String email, final String smartspace,String port, final String ip){
        if(game!=null) {
            endTurnFromServer(smartspace,email,port, ip);
        }
        else{
            throw new RuntimeException("Game is missing");
        }
    }

    public void addNewUser(String playerEmail, String playerName, String avatar,String port,String ip) {
        registerNewUser(playerEmail, playerName, avatar,port,ip);
    }

        public class LocalBinder extends Binder {
        public MyGameService getService() {
            // Return this instance of LocalService so clients can call public methods
            return MyGameService.this;
        }
        void registerWaitingForEnoughPlayersToStartGameListener(WaitingForPlayersToStartGameListener listener){
            mWaitingForEnoughPlayersListener = listener;
        }

        void unRegisterWaitingForEnoughPlayersToStartGameListener(){
            mWaitingForEnoughPlayersListener = null;
        }

        void registerLogInToServerListener(LogInToServerListener listener){
            mLogInToServerListener = listener;
        }

        void unRegisterLogInToServerListener(){
            mLogInToServerListener = null;
        }
        void registerGameListener(GameListener listener){
            mGameListener = listener;
        }

        void unRegisterGameListener(){
            mGameListener = null;
        }

    }

    public interface WaitingForPlayersToStartGameListener{
        //used at loging page to let the app know when more than 1 player is online
        //and a game can start
        void onEnoughPlayersToStartGame();
    }

    public interface LogInToServerListener{
        //used at loging page to let the app know when more than 1 player is online
        //and a game can start
        void onLogInGood(UserEntity userEntity);
        void onLogInBad();
    }

    public interface GameListener{
        //used at game activity
        void onTurnChanges(UserEntity currentPlayerTurnId);
        void onDataChanged(ArrayList<?> arrayListFromServer);
        void onPlayerLost(String losingPlayerName,String losingPlayerId);
        void onPlayerWin(String winingPlayerId);
        void onPlayerJoined(String newPlayeName, String newPlayerId);
    }

    //Server methods
    private void getAmountOfOnlinePlayersFromServer(final String email, final String smartspace,final String port, final String ip){
        final String url = "http://"+ip+":"+port+"/smartspace/users/getOnlinePlayers/"+smartspace+"/"+email;
        // prepare the Request
        final JsonArrayRequest getRequest = new JsonArrayRequest(Request.Method.GET,url,null,
                new Response.Listener<JSONArray>()
                {
                    @Override
                    public void onResponse(JSONArray response) {
                        // display response
                        Log.d("Response", response.toString());

                        if(response.length()>1){
                            waitingForPlayersExecutor.shutdownNow();
                            if(mWaitingForEnoughPlayersListener!= null){
                                mWaitingForEnoughPlayersListener.onEnoughPlayersToStartGame();
                            }
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error.Response", "error");
                    }
                }

        );

// add it to the RequestQueue
        queue.add(getRequest);

    }

    private void getCitiesFromServer(final String email, final String smartspace,final String port, final String ip){
        final String url = "http://"+ip+":"+port+"/smartspace/elements/"+smartspace+"/"+email+"?search=type&value=city";

        final ArrayList<ElementEntity> arrayOfCities = new ArrayList<>();

        // prepare the Request
        final JsonArrayRequest getRequest = new JsonArrayRequest(Request.Method.GET,url,null,
                new Response.Listener<JSONArray>()
                {
                    @Override
                    public void onResponse(JSONArray response) {
                        // display response
                        Log.d("Response", response.toString());

                        for(int i = 0; i< response.length();i++){
                            try {
                                JSONObject jresponse = response.getJSONObject(i);
                                ElementBoundary elementBoundary = gson.fromJson(jresponse.toString(),ElementBoundary.class);
                                arrayOfCities.add(elementBoundary.toEntity());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        if(mGameListener!=null)
                            mGameListener.onDataChanged(arrayOfCities);
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error.Response", "error");
                    }
                });

    // add it to the RequestQueue
        queue.add(getRequest);
    }

    private void getPlayersFromServer(final String email, final String smartspace,String port, final String ip){
        final String url = "http://"+ip+":"+port+"/smartspace/users/getOnlinePlayers/"+smartspace+"/"+email;
        final ArrayList<UserEntity> arrayOfPlayers = new ArrayList<>();

        // prepare the Request
        final JsonArrayRequest getRequest = new JsonArrayRequest(Request.Method.GET,url,null,
                new Response.Listener<JSONArray>()
                {
                    @Override
                    public void onResponse(JSONArray response) {
                        // display response
                        Log.d("Response", response.toString());

                        for(int i = 0; i< response.length();i++){
                            try {
                                JSONObject jresponse = response.getJSONObject(i);
                                UserBoundary userBoundary = gson.fromJson(jresponse.toString(),UserBoundary.class);
                                arrayOfPlayers.add(userBoundary.toEntity());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        if(mGameListener!=null) {
                            mGameListener.onDataChanged(arrayOfPlayers);
                            if(oldPlayerArrayList==null) {
                                oldPlayerArrayList = new ArrayList<>();
                                oldPlayerArrayList.addAll(arrayOfPlayers);
                            }
                            else if(oldPlayerArrayList.size()>arrayOfPlayers.size()){//check if player lost
                                UserEntity losingPlayer = findMissingPlayer(oldPlayerArrayList,arrayOfPlayers);
                                mGameListener.onPlayerLost(losingPlayer.getUsername(),losingPlayer.getKey());
                                oldPlayerArrayList.clear();
                                oldPlayerArrayList.addAll(arrayOfPlayers);
                            }
                            else if(arrayOfPlayers.size()==1){//check for winner
                                mGameListener.onPlayerWin(arrayOfPlayers.get(0).getKey());
                                oldPlayerArrayList.clear();
                                oldPlayerArrayList.addAll(arrayOfPlayers);
                            }

                            else if(oldPlayerArrayList.size()<arrayOfPlayers.size()){// check if new player joined the game
                                UserEntity newPlayer = findMissingPlayer(arrayOfPlayers,oldPlayerArrayList);
                                mGameListener.onPlayerJoined(newPlayer.getUsername(),newPlayer.getKey());
                                oldPlayerArrayList.clear();
                                oldPlayerArrayList.addAll(arrayOfPlayers);
                            }
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error.Response", error+"");
                    }
                });

        // add it to the RequestQueue
        queue.add(getRequest);
    }

    private UserEntity findMissingPlayer(ArrayList<UserEntity> oldPlayerArrayList, ArrayList<UserEntity> arrayOfPlayers) {
        UserEntity missingPlayer = new UserEntity();

        mainLoop:
        for(UserEntity player : oldPlayerArrayList){
            if(!isPlayerInList(player,arrayOfPlayers)) {
                missingPlayer = player;
                break mainLoop;
            }
        }
        return missingPlayer;
    }

    private boolean isPlayerInList(UserEntity player , ArrayList<UserEntity> playerList){
        boolean result = false;
        for(UserEntity p : playerList){
            if(p.getKey().equals(player.getKey()))
                result = true;
        }
        return result;
    }
    private void buyCityFromServer(String email, String playerSmartspace,String port, String ip,String cityId,String citySmartspace){
        final String url = "http://"+ip+":"+port+"/smartspace/actions";

        ActionEntity buyAction = new ActionEntity("buy");
        buyAction.setPlayerEmail(email);
        buyAction.setPlayerSmartspace(playerSmartspace);
        buyAction.setElementId(cityId);
        buyAction.setElementSmartspace(citySmartspace);
        try {
            ActionBoundary tempActionBoundary = new ActionBoundary(buyAction);
            Log.e(TAG,new JSONObject(gson.toJson(tempActionBoundary)).toString());
            JSONObject tempJson = new JSONObject(gson.toJson(tempActionBoundary));

            JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, url,tempJson,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d("buyAction Response", "buyAction Successful");
                        }
                    },
                    new Response.ErrorListener()
                    {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d("buyAction Error", error.getMessage()+"");
                        }
                    });
            //add it to the RequestQueue
            queue.add(postRequest);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void checkOut(String playerSmartspace,String playerEmail,String elementSmartspace,String elementId,String port,String ip){
        final String url = "http://"+ip+":"+port+"/smartspace/actions";
        ActionEntity checkOutAction = new ActionEntity("checkOut");
        checkOutAction.setPlayerSmartspace(playerSmartspace);
        checkOutAction.setPlayerEmail(playerEmail);
        checkOutAction.setElementSmartspace(elementSmartspace);
        checkOutAction.setElementId(elementId);
        try {
            ActionBoundary tempActionBoundary = new ActionBoundary(checkOutAction);
            Log.e(TAG,new JSONObject(gson.toJson(tempActionBoundary)).toString());
            JSONObject tempJson = new JSONObject(gson.toJson(tempActionBoundary));

            JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, url,tempJson,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d("Response", "checkout Successful");

                        }
                    },
                    new Response.ErrorListener()
                    {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d("Error.Response", "error");
                        }
                    });
            queue.add(postRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void checkIn(String playerSmartspace,String playerEmail,String elementSmartspace,String elementId,String port, String ip){
        final String url = "http://"+ip+":"+port+"/smartspace/actions";
        ActionEntity checkInAction = new ActionEntity("checkIn");
        checkInAction.setPlayerSmartspace(playerSmartspace);
        checkInAction.setPlayerEmail(playerEmail);
        checkInAction.setElementSmartspace(elementSmartspace);
        checkInAction.setElementId(elementId);
        try {
        ActionBoundary tempActionBoundary = new ActionBoundary(checkInAction);
        Log.e(TAG,new JSONObject(gson.toJson(tempActionBoundary)).toString());
        JSONObject tempJson = new JSONObject(gson.toJson(tempActionBoundary));

        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, url,tempJson,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("Response", "CheckIn Successful");

                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error.Response", "error");
                    }
                });
        queue.add(postRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void playerLogin(final String email, final String smartspace,final String port, final String ip){
        final String url = "http://"+ip+":"+port+"/smartspace/users/login/"+smartspace+"/"+email;
        // prepare the Request
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        // display response
                        Log.d("Response", response.toString());
                        UserBoundary userBoundary = gson.fromJson(response.toString(), UserBoundary.class);
                        UserEntity userEntity = userBoundary.toEntity();
                        if(game==null) {
                            getGameForLogIn(email, smartspace,port, ip, userEntity);
                        }
                        else{
                            initUserMoney(smartspace,email,game,port,ip,userEntity);
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if(mLogInToServerListener!=null){
                            mLogInToServerListener.onLogInBad();
                        }
                        Log.d("Error.Response", "error: "+error);
                    }
                }
        );
        //add it to the RequestQueue
        queue.add(getRequest);
    }

    private void getGameForLogIn(final String email, final String smartspace,final String port, final String ip, final UserEntity mPlayer) {
        final String url = "http://"+ip+":"+port+"/smartspace/elements/"+smartspace+"/"+email+"?search=type&value=game&page=0&size=1";
        JsonArrayRequest getRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("Response", response.toString());
                        try {
                            JSONObject jresponse = response.getJSONObject(0);
                            ElementBoundary elementBoundary = gson.fromJson(jresponse.toString(), ElementBoundary.class);
                            game = elementBoundary.toEntity();
                            initUserMoney(smartspace,email,game,port,ip,mPlayer);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error.Response", error.getMessage()+"");

                    }
                });
        //add it to the RequestQueue
        queue.add(getRequest);
    }

    private void initUserMoney(String playerSmartspace, String playerEmail, ElementEntity gameEntity,String port, String ip, final UserEntity mPlayer){
        final String url = "http://"+ip+":"+port+"/smartspace/actions";

        ActionEntity initUserMoneyAction = new ActionEntity("login");
        initUserMoneyAction.setPlayerSmartspace(playerSmartspace);
        initUserMoneyAction.setPlayerEmail(playerEmail);
        initUserMoneyAction.setElementSmartspace(gameEntity.getElementSmartspace());
        initUserMoneyAction.setElementId(gameEntity.getElementId());
        try {
            ActionBoundary tempActionBoundary = new ActionBoundary(initUserMoneyAction);
            Log.e(TAG,new JSONObject(gson.toJson(tempActionBoundary)).toString());
            JSONObject tempJson = new JSONObject(gson.toJson(tempActionBoundary));

        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, url,tempJson,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("Response", "init Successful");
                        if(mLogInToServerListener!= null){
                            mLogInToServerListener.onLogInGood(mPlayer);
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error.Response", error.getMessage()+"");
                    }
                });
        //add it to the RequestQueue
        queue.add(postRequest);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getGameForLogOut(final String email, final String smartspace, final String port, final String ip) {
        final String url = "http://"+ip+":"+port+"/smartspace/elements/"+smartspace+"/"+email+"?search=type&value=game&page=0&size=1";
        JsonArrayRequest getRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("Response", response.toString());
                        try {
                            JSONObject jresponse = response.getJSONObject(0);
                            ElementBoundary elementBoundary = gson.fromJson(jresponse.toString(), ElementBoundary.class);
                            game = elementBoundary.toEntity();
                            logOutFromServer(smartspace,email,game,port,ip);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error.Response", error.getMessage());

                    }
                });
        //add it to the RequestQueue
        queue.add(getRequest);
    }

    private void logOutFromServer(String playerSmartspace, String playerEmail, ElementEntity gameEntity,String port, String ip){
        final String url = "http://"+ip+":"+port+"/smartspace/actions";

        ActionEntity initUserMoneyAction = new ActionEntity("logout");
        initUserMoneyAction.setPlayerSmartspace(playerSmartspace);
        initUserMoneyAction.setPlayerEmail(playerEmail);
        initUserMoneyAction.setElementSmartspace(gameEntity.getElementSmartspace());
        initUserMoneyAction.setElementId(gameEntity.getElementId());
        try {
            ActionBoundary tempActionBoundary = new ActionBoundary(initUserMoneyAction);
            Log.e(TAG,new JSONObject(gson.toJson(tempActionBoundary)).toString());
            JSONObject tempJson = new JSONObject(gson.toJson(tempActionBoundary));

            JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, url,tempJson,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d("Response", "logout Successful");
                        }
                    },
                    new Response.ErrorListener()
                    {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d("Error.Response", error.getMessage()+"");
                        }
                    });
            //add it to the RequestQueue
            queue.add(postRequest);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getWhosTurnIsItFromServer(final String email, final String smartspace,String port ,final String ip){
        final String url = "http://"+ip+":"+port+"/smartspace/users/getTurn/"+smartspace+"/"+email;
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        // display response
                        Log.d("Response WhosTurn", response.toString());
                        UserBoundary userBoundary = gson.fromJson(response.toString(), UserBoundary.class);
                        UserEntity userEntity = userBoundary.toEntity();


                        if(lastPlayerPlayed == null || !lastPlayerPlayed.getKey().equals(userEntity.getKey())) {
                            lastPlayerPlayed = userEntity;
                            if(mGameListener!=null) {
                                mGameListener.onTurnChanges(lastPlayerPlayed);
                            }
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("WhosTurn Error", error+"");
                    }
                }
        );
        //add it to the RequestQueue
        queue.add(getRequest);
    }

    private void endTurnFromServer(String playerSmartspace, String playerEmail,String port, String ip){
        final String url = "http://"+ip+":"+port+"/smartspace/actions";

        ActionEntity endTurnAction = new ActionEntity("endTurn");
        endTurnAction.setPlayerEmail(playerEmail);
        endTurnAction.setPlayerSmartspace(playerSmartspace);
        endTurnAction.setElementId(game.getElementId());
        endTurnAction.setElementSmartspace(game.getElementSmartspace());
        try {
            ActionBoundary tempActionBoundary = new ActionBoundary(endTurnAction);
            Log.e(TAG,new JSONObject(gson.toJson(tempActionBoundary)).toString());
            JSONObject tempJson = new JSONObject(gson.toJson(tempActionBoundary));

            JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, url,tempJson,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d("endTurn Response", "End Turn Successful");
                        }
                    },
                    new Response.ErrorListener()
                    {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d("endTurn Error", error.getMessage()+"");
                        }
                    });
            //add it to the RequestQueue
            queue.add(postRequest);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void registerNewUser(String playerEmail, String playerName, String avatar,String port,String ip){
        final String url = "http://"+ip+":"+port+"/smartspace/users";

        NewUserFormBoundary newUserFormBoundary = new NewUserFormBoundary(playerEmail,"PLAYER",playerName,avatar);

        try {
            Log.e(TAG,new JSONObject(gson.toJson(newUserFormBoundary)).toString());
            JSONObject tempJson = new JSONObject(gson.toJson(newUserFormBoundary));

            JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, url,tempJson,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d("register Response", "register Successful");
                        }
                    },
                    new Response.ErrorListener()
                    {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d("register Error", error.getMessage()+"");
                        }
                    });
            //add it to the RequestQueue
            queue.add(postRequest);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
