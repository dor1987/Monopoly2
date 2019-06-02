package a1door.monopoly;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import a1door.monopoly.Entities.ElementEntity;
import a1door.monopoly.Entities.UserEntity;

public class Adapter extends RecyclerView.Adapter<Adapter.Holder> {
    private Context context;
    private ArrayList<ElementEntity> citiesArrayList;
    private ArrayList<UserEntity> playersArrayList;
    private OnCityListener mOnCityListener;
    private UserEntity mPlayer;

    public Adapter(Context context, ArrayList<ElementEntity> citiesArrayList,ArrayList<UserEntity> playerArrayList, OnCityListener onCityListener,UserEntity mPlayer) {
        this.context = context;
        this.citiesArrayList = citiesArrayList;
        this.playersArrayList = playerArrayList;
        this.mOnCityListener = onCityListener;
        this.mPlayer = mPlayer;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

      View layout = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.city_layout,null);
      Holder holder =new Holder(layout,mOnCityListener);
      return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull Holder viewHolder, int i) {
        if(citiesArrayList.get(i).getElementId()!= null && !citiesArrayList.get(i).getElementId().isEmpty()) {
            viewHolder.cityName.setText(citiesArrayList.get(i).getName());
            viewHolder.cityPrice.setText("" + citiesArrayList.get(i).getMoreAttributes().get("price"));
            viewHolder.relativeLayoutForcolor.setBackgroundResource(getCityBackGroundbyPrice((double)(citiesArrayList.get(i).getMoreAttributes().get("price"))));

            viewHolder.cityOwner.setText(citiesArrayList.get(i).getMoreAttributes().get("ownerName")+"");
            viewHolder.cityOwner.setVisibility(View.VISIBLE);
            viewHolder.cityOwnerTitle.setVisibility(View.VISIBLE);
            viewHolder.cityPriceTitle.setVisibility(View.VISIBLE);
            viewHolder.playerLayout.removeAllViewsInLayout();

            ArrayList<String> playersIdArrayList = new ArrayList<>();
            playersIdArrayList.addAll((ArrayList<String>)citiesArrayList.get(i).getMoreAttributes().get("visitors"));
            final Animation animationScale = AnimationUtils.loadAnimation(context, R.anim.scale);

            for(String playerId: playersIdArrayList){
                ImageView image = new ImageView(context);
                image.setLayoutParams(new ViewGroup.LayoutParams(100,100));
                image.setImageResource(getPlayerAvatarById(playerId));
                viewHolder.playerLayout.addView(image);

                if(playerId.equals(mPlayer.getKey())){
                    image.startAnimation(animationScale);
                }
            }

        }
        else{
            viewHolder.cityName.setText("");
            viewHolder.cityPrice.setText("");
            viewHolder.cityOwner.setText("");
           // viewHolder.relativeLayoutForcolor.setBackgroundColor(Color.parseColor("#238904"));
            viewHolder.relativeLayoutForcolor.setBackgroundResource(R.drawable.pattern);
            viewHolder.cityOwnerTitle.setVisibility(View.INVISIBLE);
            viewHolder.cityOwner.setVisibility(View.INVISIBLE);
            viewHolder.cityPriceTitle.setVisibility(View.INVISIBLE);
            viewHolder.playerLayout.removeAllViewsInLayout();

        }
    }

    @Override
    public int getItemCount() {
        return citiesArrayList.size();
    }

    public int getCityBackGroundbyPrice(double price){
        if(price<100) return R.drawable.green_cube_city_background2;
        else if(price<200) return R.drawable.yellow_cube_city_background2;
        else if(price<300) return R.drawable.blue_cube_city_background2;
        else return R.drawable.purple_cube_city_background2;
    }


    public int getPlayerAvatarById(String playerId){
        int result = R.drawable.dog;

        for(UserEntity player :playersArrayList){
            if(player.getKey().equals(playerId)) {
                result = getAvatarImage(player.getAvatar());
                break;
            }
        }
        return result;
    }
    public int getAvatarImage(String avatar){
        if(avatar.equals("Dog"))
           return R.drawable.dog;
        else if(avatar.equals("Car"))
            return R.drawable.car;
        else if(avatar.equals("Wheelbarrow"))
            return R.drawable.wheelbarrow;
        else if(avatar.equals("Thimble"))
            return R.drawable.thimble;
        else if(avatar.equals("Iron"))
            return R.drawable.iron;
        else if(avatar.equals("Ship"))
            return R.drawable.ship;
        else if(avatar.equals("Hat"))
            return R.drawable.hat;
        else
            return R.drawable.shoe;
    }


    public UserEntity getPlayerById(String id){
        for(UserEntity player: playersArrayList){
            if(player.getKey().equals(id)){
                return player;
            }
        }
        return null;
    }
    public static  class Holder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView cityName;
        TextView cityPrice;
        TextView cityOwner;
        TextView cityPriceTitle;
        TextView cityOwnerTitle;
        GridLayout playerLayout;
        OnCityListener onCityListener;

        RelativeLayout relativeLayoutForcolor;

        public Holder(@NonNull View itemView,OnCityListener onCityListener) {
            super(itemView);
            this.onCityListener = onCityListener;
            cityName = itemView.findViewById(R.id.city_name_textview);
            cityPrice = itemView.findViewById(R.id.city_price_textview);
            cityOwner = itemView.findViewById(R.id.city_owner_textview);
            cityPriceTitle = itemView.findViewById(R.id.city_price_title_textview);
            cityOwnerTitle = itemView.findViewById(R.id.city_owner_title_textview);
            playerLayout = itemView.findViewById(R.id.city_player_list_display);
            relativeLayoutForcolor = itemView.findViewById(R.id.city_layout_color_control);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onCityListener.onCityClicked(getAdapterPosition());
        }
    }

    public interface OnCityListener{
        void onCityClicked(int position);
    }
}
