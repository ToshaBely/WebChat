package chat.dao;

import chat.model.Message;

import java.util.List;

public interface MessageDAO {
    void add (Message message);

    void update (Message message);

    void delete (Message message);

    Message selectById (int id);

    List<Message> selectAll();
}
