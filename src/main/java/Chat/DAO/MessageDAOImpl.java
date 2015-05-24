/*package Chat.DAO;

import Chat.Model.Message;
import Chat.db.ConnectionManager;
import org.apache.log4j.Logger;
import java.sql.Connection;

import java.sql.PreparedStatement;
import java.util.List;

public class MessageDAOImpl implements MessageDAO {
    private static Logger logger = Logger.getLogger(MessageDAOImpl.class.getName());

   @Override
    public void add (Message message) {
        Connection connection = null;
       PreparedStatement preparedStatement = null;
       try {
           connection = ConnectionManager.getConnection();
           preparedStatement = connection.prepareStatement("INSERT INTO ")
       }
    }

    @Override
    public void update (Message message) {

    }

    @Override
    public void delete (Message message) {

    }

    /*
    @Override
    public Message selectById (int id) {

    }

    @Override
    public List<Message> selectAll() {

    }
    */
//}
