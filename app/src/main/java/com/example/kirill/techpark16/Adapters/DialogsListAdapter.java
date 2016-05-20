package com.example.kirill.techpark16.Adapters;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kirill.techpark16.R;
import com.vk.sdk.api.model.VKApiUser;
import com.vk.sdk.api.model.VKList;

import java.io.InputStream;
import java.util.ArrayList;

public class DialogsListAdapter extends BaseAdapter {
    private ArrayList<String> messages;
    private VKList<VKApiUser> users;
    private Context context;

    public DialogsListAdapter(Context context, VKList<VKApiUser> users, ArrayList<String> messages) {
        this.users = users;
        this.messages = messages;
        this.context = context;
    }

    @Override
    public int getCount() {
        return users.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        SetData setData = new SetData();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.fragment_dialogs_list, null);

        setData.user_name = (TextView) view.findViewById(R.id.user_name);
        setData.msg = (TextView) view.findViewById(R.id.msg);
        setData.avatar = (ImageView) view.findViewById(R.id.avatar);

        String name = users.get(position).first_name + " " + users.get(position).last_name;

        if ( name.length() > 18) {
            name = name.substring(0,18);
            name += " ...";
        }

        setData.user_name.setText(name);
        setData.msg.setText(messages.get(position));
        //setData.avatar.setImageURI(users.get(position).photo_100);
//        URL newurl;
//        try {
//            newurl = new URL(users.get(position).photo_100);
//            Log.i("bullshit", users.get(position).photo_100);
//            Bitmap mIcon_val = BitmapFactory.decodeStream(newurl.openStream());
//            setData.avatar.setImageBitmap(mIcon_val);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        new DownloadImageTask((ImageView) view.findViewById(R.id.avatar)).execute(users.get(position).photo_100);




        return view;
    }

    public class SetData {
        TextView user_name,msg;
        ImageView avatar;
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}
