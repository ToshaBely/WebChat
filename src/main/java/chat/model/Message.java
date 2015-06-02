package chat.model;

public class Message {

    private String id;
    private String text;
    private String author;
    private String date;

    public Message(String id, String text, String author, String date) {
        this.id = id;
        this.text = text;
        this.author = author;
        this.date = date;
    }
    public String getId() {
            return id;
        }
    public void setId(String id) {
            this.id = id;
        }

    public String getText() {
            return text;
        }
    public void setText(String text) {
            this.text = text;
        }

    public String getAuthor() {
            return this.author;
        }
    public void setAuthor(String author) {
            this.author = author;
        }

    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }

    public String toString() {
        return "{\"id\":\"" + this.id + "\",\"author\":\"" + this.author + "\",\"text\":\"" + this.text + "\",\"date\":\"" + this.date + "\"}";
    }
}
