package Chat.Model;

public class Message {

        private String id;
        private String text;
        private String author;

        public Message(String id, String text, String author) {
            this.id = id;
            this.text = text;
            this.author = author;
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

        public String toString() {
            return "{\"id\":\"" + this.id + "\",\"author\":\"" + this.author + "\",\"text\":" + this.text + "}";
        }

}
