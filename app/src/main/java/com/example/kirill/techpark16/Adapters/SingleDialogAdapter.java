package com.example.kirill.techpark16.Adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.kirill.techpark16.ChatMessage;
import com.example.kirill.techpark16.R;

import java.util.ArrayList;
import java.util.Objects;

public class SingleDialogAdapter extends BaseAdapter {
    private ArrayList<String> inList, outList;
    private ArrayList<ChatMessage> chatMessagesList = new ArrayList<>();
    private TextView textView;
    private Context context;


    public SingleDialogAdapter(Context context, ArrayList<String> inList, ArrayList<String> outList) {
        this.inList = inList;
        this.outList = outList;
        this.context = context;
        inList.addAll(outList);
    }

    public void add(ChatMessage obj){
        chatMessagesList.add(0,obj);
    }

    @Override
    public int getCount() {
        return chatMessagesList.size();
    }

    @Override
    public ChatMessage getItem(int position) {
        return this.chatMessagesList.get( getCount() - position - 1);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        ChatMessage chatMessageObj = getItem(position);
        View row;
        if (chatMessageObj.getOut()) {
            row = inflater.inflate(R.layout.dialog_fragment_out, null);
        } else {
            row = inflater.inflate(R.layout.dialog_fragment_in, null);
        }

        String media = "[MEDIA MESSAGE]";

        textView = (TextView) row.findViewById(R.id.msg);
        if (Objects.equals(chatMessageObj.getMsg(), "")) {
            textView.setText(media);
        } else {
            textView.setText(chatMessageObj.getMsg());
        }
        return row;
    }

}