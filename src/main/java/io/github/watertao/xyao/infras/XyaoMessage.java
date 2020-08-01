package io.github.watertao.xyao.infras;

import java.util.ArrayList;
import java.util.List;

public class XyaoMessage {

  /**
   * 如果私聊，则是发送对象
   * 若是群聊，则是 @ 对象
   */
  private List<Contact> to;
  private XyaoInstruction.Room room;

  private List<Object> entities = new ArrayList<>();

  public List<Contact> getTo() {
    return to;
  }

  public XyaoMessage() {
    to = new ArrayList<>();
  }

  public XyaoInstruction.Room getRoom() {
    return room;
  }

  public void setRoom(XyaoInstruction.Room room) {
    this.room = room;
  }

  public List<Object> getEntities() {
    return entities;
  }

  public static enum EntityType {
    STRING,
    CONTACT,
    FILE_BOX,
    URL_LINK
  }

  public static class StringEntity {
    private EntityType type = EntityType.STRING;
    private String payload;

    public StringEntity() {
    }

    public StringEntity(String payload) {
      this.payload = payload;
    }

    public String getPayload() {
      return payload;
    }

    public void setPayload(String payload) {
      this.payload = payload;
    }

    public EntityType getType() {
      return type;
    }

    public void setType(EntityType type) {
      this.type = type;
    }
  }

  public static class ContactEntity {
    private EntityType type = EntityType.CONTACT;

    private String id;
    private String name;

    public ContactEntity(String id, String name) {
      this.id = id;
      this.name = name;
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

    public EntityType getType() {
      return type;
    }

    public void setType(EntityType type) {
      this.type = type;
    }
  }

  public static class FileBoxEntity {
    private EntityType type = EntityType.FILE_BOX;

    private String fileName;
    private String content;

    public FileBoxEntity(String fileName, String content) {
      this.fileName = fileName;
      this.content = content;
    }

    public String getFileName() {
      return fileName;
    }

    public void setFileName(String fileName) {
      this.fileName = fileName;
    }

    public String getContent() {
      return content;
    }

    public void setContent(String content) {
      this.content = content;
    }

    public EntityType getType() {
      return type;
    }

    public void setType(EntityType type) {
      this.type = type;
    }
  }

  public static class URLLinkEntity {
    private EntityType type = EntityType.URL_LINK;

    private String description;
    private String thumbnailUrl;
    private String title;
    private String url;

    public String getDescription() {
      return description;
    }

    public void setDescription(String description) {
      this.description = description;
    }

    public String getThumbnailUrl() {
      return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
      this.thumbnailUrl = thumbnailUrl;
    }

    public String getTitle() {
      return title;
    }

    public void setTitle(String title) {
      this.title = title;
    }

    public String getUrl() {
      return url;
    }

    public void setUrl(String url) {
      this.url = url;
    }

    public EntityType getType() {
      return type;
    }

    public void setType(EntityType type) {
      this.type = type;
    }
  }

  public static class Contact {
    private String id;
    private String name;
    private boolean isMention;

    public boolean getIsMention() {
      return isMention;
    }

    public void setIsMention(boolean mention) {
      isMention = mention;
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
