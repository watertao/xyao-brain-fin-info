package io.github.watertao.xyao.infras;

import javax.security.auth.login.Configuration;

public class XyaoInstruction {

  private Contact from;
  private Room room;
  private String text;

  public Contact getFrom() {
    return from;
  }

  public void setFrom(Contact from) {
    this.from = from;
  }

  public Room getRoom() {
    return room;
  }

  public void setRoom(Room room) {
    this.room = room;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public static class Room {
    private String id;
    private String topic;

    public String getId() {
      return id;
    }

    public void setId(String id) {
      this.id = id;
    }

    public String getTopic() {
      return topic;
    }

    public void setTopic(String topic) {
      this.topic = topic;
    }
  }

  public static class Contact {
    private String id;
    private String name;
    private boolean isMaster;

    public boolean getIsMaster() {
      return isMaster;
    }

    public void setIsMaster(boolean master) {
      isMaster = master;
    }

    public String getId() {
      return id;
    }

    public void setId(String id) {
      this.id = id;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }
  }

}
