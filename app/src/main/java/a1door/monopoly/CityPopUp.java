package a1door.monopoly;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class CityPopUp extends Activity {
    TextView cityNameTextview;
    TextView ownerNameTextview;
    TextView priceTextview;
    TextView fineTextview;
    RelativeLayout relativeLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.citypopupwindow);

        cityNameTextview = findViewById(R.id.city_popup_name_textview);
        ownerNameTextview = findViewById(R.id.city_popup_owner_textview);
        priceTextview = findViewById(R.id.city_popup_price_textview);
        fineTextview = findViewById(R.id.city_popup_fine_textview);
        relativeLayout = findViewById(R.id.city_popup_relativelayout);

        Bundle extras = getIntent().getExtras();
        if(extras == null){
            try {
                throw new Exception("City information is missing");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        else{
            cityNameTextview.setText(extras.getString("name"));
            ownerNameTextview.setText(extras.getString("ownerName").trim());
            priceTextview.setText(extras.getDouble("price")+"");
            fineTextview.setText(extras.getDouble("fine")+"");
            relativeLayout.setBackgroundResource(getCityBackGroundbyPrice(extras.getDouble("price")));
        }


        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width*0.8),(int)(height*0.6));
    }

    public int getCityBackGroundbyPrice(double price){
        if(price<100) return R.drawable.green_cube_city_background;
        else if(price<200) return R.drawable.yellow_cube_city_background;
        else if(price<300) return R.drawable.blue_cube_city_background;
        else return R.drawable.purple_cube_city_background;
    }
}
