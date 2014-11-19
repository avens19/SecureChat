package com.andrewovens.securechat;

import java.util.Date;

public class Message {
	public long id;
    public EncryptedMessage[] contents;
    public User sender;
    public String messageType;
    //public Date dateCreated;
}
