package a1door.monopoly;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class AvatarAdapter extends ArrayAdapter<AvatarItem> {

    public AvatarAdapter(Context context, ArrayList<AvatarItem> avatarItemList){
        super(context,0,avatarItemList);

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, ViewGroup parent) {
        return initView(position,convertView,parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position,convertView,parent);
    }

    private View initView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.avatar_spinner_row,parent,false);
        }

        ImageView imageView = convertView.findViewById(R.id.image_view_avatar);
        TextView textView = convertView.findViewById(R.id.image_view_name);
        AvatarItem avatarItem = getItem(position);

        if(avatarItem!=null) {
            imageView.setImageResource(avatarItem.getmAvatarImage());
            textView.setText(avatarItem.getmAvatarName());
        }
    return convertView;
    }
}
