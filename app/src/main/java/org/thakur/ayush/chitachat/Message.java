package org.thakur.ayush.chitachat;

/**
 * Created by win on 24-05-2018.
 */

public class Message {
    private String text; // message body
    private MemberData data; // data of the user that sent this message
    private boolean belongsToCurrentUser; // is this message sent by us?

    public Message(String text, MemberData data, boolean belongsToCurrentUser) {
        this.text = text;
        this.data = data;
        this.belongsToCurrentUser = belongsToCurrentUser;
    }

    public String getText() {
        return text;
    }

    public MemberData getData() {
        return data;
    }

    public boolean isBelongsToCurrentUser() {
        return belongsToCurrentUser;
    }
}
