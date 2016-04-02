package com.example.kirill.techpark16;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiMessage;
import com.vk.sdk.api.model.VKApiUser;
import com.vk.sdk.api.model.VKList;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static com.example.kirill.techpark16.R.layout.style_list_view;

/**
 * Created by konstantin on 02.04.16.
 */
public class FriendsCustomAdapter extends BaseAdapter {
    private ArrayList<String> users, messages;
    private Context context;

   // private VKList<VKApiDialog> list;

    private VKList<VKApiUser> list;


    public FriendsCustomAdapter(Context context,ArrayList<String> users, ArrayList<String> messages, VKList list) {
        this.users = users;
        this.messages = messages;
        this.context = context;
        this.list = list;
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
        View view = inflater.inflate(style_list_view,null);

        setData.user_name = (TextView) view.findViewById(R.id.user_name);
        setData.msg = (TextView) view.findViewById(R.id.msg);

        setData.user_name.setText(users.get(position));
        setData.msg.setText(messages.get(position));

        if (list != null)
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final ArrayList<String> inList = new ArrayList<>();
                    final ArrayList<String> outList = new ArrayList<>();
                   // final int id = list.get(position).message.user_id;
                    //final int id = 0;
                    final int id = list.getById(position).getId();

                    VKRequest request = new VKRequest("messages.getHistory", VKParameters.from(VKApiConst.USER_ID, id));
                    request.executeWithListener(new VKRequest.VKRequestListener() {
                        @Override
                        public void onComplete(VKResponse response) {
                            super.onComplete(response);

                            try {
                                JSONArray array = response.json.getJSONObject("response").getJSONArray("items");
                                VKApiMessage[] msg = new VKApiMessage[array.length()];

                                for (int i =0;  i < array.length(); i++) {
                                    VKApiMessage mes = new VKApiMessage(array.getJSONObject(i));
                                    msg[i] = mes;
                                }

                                Arrays.sort(messages.toArray(), Collections.reverseOrder());

                                for (VKApiMessage mess : msg) {
                                    if (mess.out) {
                                        outList.add(mess.body);
                                    } else {
                                        inList.add(mess.body);
                                    }
                                }
                                context.startActivity(new Intent(context,TestSingleDialogActivity.class).putExtra("id",id)
                                        .putExtra("in",inList).putExtra("out",outList));
                                System.out.println("START SINGLE!!!");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });


                }
            });

        return view;
    }

    public class SetData {
        TextView user_name,msg;
    }
}
