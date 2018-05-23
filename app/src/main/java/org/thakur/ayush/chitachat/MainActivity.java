package org.thakur.ayush.chitachat;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scaledrone.lib.Listener;
import com.scaledrone.lib.Member;
import com.scaledrone.lib.Room;
import com.scaledrone.lib.RoomListener;
import com.scaledrone.lib.Scaledrone;

import java.util.Random;

public class MainActivity extends AppCompatActivity implements RoomListener {

    private String channelID = "TZ4jy037Qs4IDlpr";
    private String roomName = "observable-room";
    private EditText editText;
    private Scaledrone scaledrone;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText = (EditText) findViewById(R.id.editText);

        MemberData data = new MemberData(getRandomName(), getRandomColor());
        Log.d(TAG, "onCreate: " + data.getName() + data.getColor());
        Log.d(TAG, "onCreate: MemberData object created");
        scaledrone = new Scaledrone(channelID, data);
        scaledrone.connect(new Listener() {
            @Override
            public void onOpen() {
                System.out.println("Scaledrone connection open");
                // Since the MainActivity itself already implement RoomListener we can pass it as a target
                scaledrone.subscribe(roomName, MainActivity.this);
            }

            @Override
            public void onOpenFailure(Exception ex) {
                System.out.println("Open Failure");
                System.err.println(ex);
            }

            @Override
            public void onFailure(Exception ex) {
                System.out.println("onFAILURE");
                System.err.println(ex);
            }

            @Override
            public void onClosed(String reason) {
                System.err.println(reason);
            }
        });

    }

    @Override
    public void onOpen(Room room) {
        System.out.println("Conneted to room");
    }

    // Connecting to Scaledrone room failed
    @Override
    public void onOpenFailure(Room room, Exception ex) {
        System.err.println(ex);
    }

    // Received a message from Scaledrone room
    @Override
    public void onMessage(Room room, final JsonNode json, final Member member) {
        final ObjectMapper mapper = new ObjectMapper();
        final MessageAdapter messageAdapter = new MessageAdapter(this);
        try {

            // member.clientData is a MemberData object, let's parse it as such
            final MemberData data = mapper.treeToValue(member.getClientData(), MemberData.class);
            // if the clientID of the message sender is the same as our's it was sent by us
            boolean belongsToCurrentUser = member.getId().equals(scaledrone.getClientID());
            // since the message body is a simple string in our case we can use json.asText() to parse it as such
            // if it was instead an object we could use a similar pattern to data parsing
            final Message message = new Message(json.asText(), data, belongsToCurrentUser);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "run: message recieved by server is "+ message.getText());
                    messageAdapter.add(message);

                    // scroll the ListView to the last added element

                    //messagesView.setSelection(messagesView.getCount() - 1);
                }
            });
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }






    private String getRandomName() {
        String[] adjs = { "Spider-Man", "Wolverine", "the Hulk", "Thor", "Iron Man", "Captain America", "Daredevil", "Ghost Rider", "Dr. Strange", "Punisher", "Deadpool","SuperMan","BatMan"};
        String[] nouns = { "Thakur","Sharma","Singh","Maheshwari","Sinha","Mishra","Mehta","Jain","Chauhan"};
       return (
                adjs[(int) Math.floor(Math.random() * adjs.length)] +
                        "_" +
                        nouns[(int) Math.floor(Math.random() * nouns.length)]
        );
    }



    private String getRandomColor() {
        Random r = new Random();
        StringBuffer sb = new StringBuffer("#");
        while(sb.length() < 7){
            sb.append(Integer.toHexString(r.nextInt()));
        }
        return sb.toString().substring(0, 7);
    }



    public void sendMessage(View view) {
        String message = editText.getText().toString();
        if (message.length() > 0) {
            System.out.println("Message recived in system" + message);
            scaledrone.publish("observable-room", message);
            editText.getText().clear();
        }
    }
}


