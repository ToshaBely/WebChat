package Chat.Model;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MessageStorage {
    private static final List<Message> INSTANSE = Collections.synchronizedList(new ArrayList<Message>());

    private MessageStorage() {
    }

    public static List<Message> getStorage() {
        return INSTANSE;
    }

    public static void addMessage(Message message) {
        INSTANSE.add(message);
    }

    public static void addAll(Message[] tasks) {
        INSTANSE.addAll(Arrays.asList(tasks));
    }

    public static int getSize() {
        return INSTANSE.size();
    }

    public static List<Message> getSubMessageByIndex(int index) {
        return INSTANSE.subList(index, INSTANSE.size());
    }

    public static Message getMessageById(String id) {
        for (Message message : INSTANSE) {
            if (message.getId().equals(id)) {
                return message;
            }
        }
        return null;
    }

}