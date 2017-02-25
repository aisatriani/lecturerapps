package com.tenilodev.lecturermaps.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tenilodev.lecturermaps.R;

/**
 * Created by azisa on 2/21/2017.
 */

public class ChatHolder extends RecyclerView.ViewHolder {
    private final TextView mNameField;
    private final TextView mTextField;
    private final TextView mTextTime;
    private final LinearLayout boxChat;


    public ChatHolder(View itemView) {
        super(itemView);
        mNameField = (TextView) itemView.findViewById(R.id.message_user);
        mTextField = (TextView) itemView.findViewById(R.id.message_text);
        mTextTime = (TextView)itemView.findViewById(R.id.message_time);
        boxChat = (LinearLayout)itemView.findViewById(R.id.container_chat);
    }

    public void setName(String name) {
        mNameField.setText(name);
    }

    public void setText(String text) {
        mTextField.setText(text);
    }

    public void setmTextTime(String textTime){
        mTextTime.setText(textTime);
    }

    public LinearLayout getBoxChat() {
        return boxChat;
    }
}