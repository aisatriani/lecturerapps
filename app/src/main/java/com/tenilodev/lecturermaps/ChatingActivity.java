package com.tenilodev.lecturermaps;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.gson.Gson;
import com.tenilodev.lecturermaps.adapter.ChatHolder;
import com.tenilodev.lecturermaps.api.FCMApiGenerator;
import com.tenilodev.lecturermaps.api.FCMMethod;
import com.tenilodev.lecturermaps.fcm.Message;
import com.tenilodev.lecturermaps.fcm.NotifyData;
import com.tenilodev.lecturermaps.model.ChatMessage;
import com.tenilodev.lecturermaps.model.LokasiDosen;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatingActivity extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView rv;
    private FirebaseRecyclerAdapter<ChatMessage, ChatHolder> adapter;
    private EditText textPesan;
    private Button kirimPesan;
    private DatabaseReference ref;
    private LokasiDosen lokasiDosen;
    private ChatMessage mChatData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chating);

        handleIntent(getIntent());

        rv = (RecyclerView)findViewById(R.id.rv_chat);
        LinearLayoutManager layout = new LinearLayoutManager(this);
        layout.setStackFromEnd(true);
        rv.setLayoutManager(layout);


        textPesan = (EditText) findViewById(R.id.text_pesan);
        kirimPesan = (Button) findViewById(R.id.kirim_pesan);
        kirimPesan.setOnClickListener(this);

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        if(Pref.getInstance(this).getLoginState() == Config.LOGIN_STATE_DOSEN)
            ref = FirebaseDatabase.getInstance().getReference(mChatData.getPenerimaID()).child(mChatData.getPengirimID());
        else
            ref = FirebaseDatabase.getInstance().getReference(lokasiDosen.getNidn()).child(Pref.getInstance(this).getNim());

        adapter = new FirebaseRecyclerAdapter<ChatMessage, ChatHolder>(ChatMessage.class, R.layout.item_message, ChatHolder.class, ref) {
            @Override
            protected void populateViewHolder(ChatHolder viewHolder, ChatMessage model, int position) {
                viewHolder.setName(model.getMessageUser());
                viewHolder.setText(model.getMessageText());
                viewHolder.setmTextTime(String.valueOf(model.getMessageTime()));
            }
        };

        rv.setAdapter(adapter);


    }

    private void handleIntent(Intent intent) {
        if(Pref.getInstance(this).getLoginState() == Config.LOGIN_STATE_MAHASISWA) {
            lokasiDosen = (LokasiDosen) intent.getSerializableExtra("lokasidosen");
            if(lokasiDosen == null){
                mChatData = (ChatMessage) intent.getSerializableExtra("chatmessage");
                lokasiDosen = new LokasiDosen();
                lokasiDosen.setNama(mChatData.getMessageUser());
                lokasiDosen.setNidn(mChatData.getPengirimID());
            }
            setTitle(lokasiDosen.getNama());
        }
        if(Pref.getInstance(this).getLoginState() == Config.LOGIN_STATE_DOSEN){
            mChatData = (ChatMessage) intent.getSerializableExtra("chatmessage");
            setTitle(mChatData.getMessageUser());
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.kirim_pesan){
            if(validate())
                actionKirimPesan();
        }
    }

    private void actionKirimPesan() {
        if(Pref.getInstance(this).getLoginState() == Config.LOGIN_STATE_DOSEN){
            ChatMessage msg = new ChatMessage(textPesan.getText().toString(),
                    Pref.getInstance(this).getDataDosen().getNAMA(),
                    Pref.getInstance(this).getDataDosen().getNIDN(),
                    mChatData.getPengirimID()
            );
            ref.push().setValue(msg);
            //sendFCMNotify();
            sendFCMNotify(mChatData.getPengirimID(), msg);
        }
        if(Pref.getInstance(this).getLoginState() == Config.LOGIN_STATE_MAHASISWA){
            ChatMessage msg = new ChatMessage(textPesan.getText().toString(),
                    Pref.getInstance(this).getDataMahasiswa().getNAMA(),
                    Pref.getInstance(this).getNim(),
                    lokasiDosen.getNidn()
            );
            ref.push().setValue(msg);
            sendFCMNotify(lokasiDosen.getNidn(), msg);
        }



        textPesan.setText("");
    }

    private void sendFCMNotify(final String toTopik, final ChatMessage msg) {
        Gson gson = new Gson();
        String body = gson.toJson(msg);
        NotifyData notifydata = new NotifyData("Notification title", body);

        FCMMethod fcmMethod = FCMApiGenerator.createService(FCMMethod.class);
        Call<Message> call = fcmMethod.sendMessage(new Message("/topics/"+toTopik, notifydata));
        call.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(Call<Message> call, Response<Message> response) {

            }

            @Override
            public void onFailure(Call<Message> call, Throwable t) {
                //sendFCMNotify(toTopik, msg);
            }
        });
    }

    private boolean validate() {
        EditText[] editTexts = {textPesan};
        for (EditText editText : editTexts) {
            if (editText.getText().toString().trim().equalsIgnoreCase("")) {
                editText.setError("");
                editText.requestFocus();
                return false;
            }
        }
        return true;
    }
}
