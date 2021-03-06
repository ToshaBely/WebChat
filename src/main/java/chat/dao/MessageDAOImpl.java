package chat.dao;

import chat.model.Message;
import chat.db.ConnectionManager;
import org.apache.log4j.Logger;

import java.sql.*;

import java.util.ArrayList;
import java.util.List;

public class MessageDAOImpl implements MessageDAO {
    private static Logger logger = Logger.getLogger(MessageDAOImpl.class.getName());

   @Override
    public void add (Message message) {
       Connection connection = null;
       Statement statement = null;
       ResultSet resultSet = null;
       PreparedStatement preparedStatement = null;

       try {
           connection = ConnectionManager.getConnection();
           statement = connection.createStatement();
           preparedStatement = connection.prepareStatement("SELECT * FROM users WHERE name = ?");
           preparedStatement.setString(1, message.getAuthor());
           resultSet = preparedStatement.executeQuery();
           //resultSet = statement.executeQuery("SELECT * FROM users WHERE name = \"" + message.getAuthor() + "\"");
           int authorID;
           if (resultSet.next()) {
               authorID = resultSet.getInt("id");
           }
           else {
               preparedStatement = connection.prepareStatement("INSERT INTO users (name) VALUES (?)");
               preparedStatement.setString(1, message.getAuthor());
               preparedStatement.executeUpdate();

               //resultSet = statement.executeQuery("SELECT * FROM users WHERE name = \"" + message.getAuthor() + "\"");
               preparedStatement = connection.prepareStatement("SELECT * FROM users WHERE name = ?");
               preparedStatement.setString(1, message.getAuthor());
               resultSet = preparedStatement.executeQuery();
               resultSet.next();
               authorID = resultSet.getInt("id");
           }
           preparedStatement = connection.prepareStatement("INSERT INTO messages_store (id, text, dateMessage, user_id) VALUES (?, ?, now(), ?)");
           preparedStatement.setString(1, message.getId());
           preparedStatement.setString(2, message.getText());
           preparedStatement.setInt(3, authorID);
           preparedStatement.executeUpdate();
       }

       catch (SQLException e) {
           logger.error(e);
       } finally {
           if (preparedStatement != null) {
               try {
                   preparedStatement.close();
               } catch (SQLException e) {
                   logger.error(e);
               }
           }

           if (connection != null) {
               try {
                   connection.close();
               } catch (SQLException e) {
                   logger.error(e);
               }
           }
       }

    }

    @Override
    public void update(Message message) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = ConnectionManager.getConnection();
            preparedStatement = connection.prepareStatement("Update messages_store SET text = ? WHERE id = ?");
            preparedStatement.setString(1, message.getText());
            preparedStatement.setString(2, message.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            logger.error(e);
        } finally {
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    logger.error(e);
                }
            }

            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    logger.error(e);
                }
            }
        }
    }

    @Override
    public void delete (Message message) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = ConnectionManager.getConnection();
            // полностью удалить:
            //preparedStatement = connection.prepareStatement("DELETE  from messages WHERE id = ?");
            preparedStatement = connection.prepareStatement("Update messages_store SET text = ? WHERE id = ?");
            preparedStatement.setString(1, message.getText());
            preparedStatement.setString(2, message.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            logger.error(e);
        } finally {
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    logger.error(e);
                }
            }

            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    logger.error(e);
                }
            }
        }
    }


    @Override
    public Message selectById (int id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Message> selectAll() {
        List<Message> messages = new ArrayList<>();
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            connection = ConnectionManager.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT users.name, messages_store.id, text, dateMessage FROM messages_store" +
                    " JOIN users ON messages_store.user_id = users.id ORDER BY dateMessage");
            while (resultSet.next()) {
                String id = resultSet.getString("id");
                String text = resultSet.getString("text");
                String date = resultSet.getString("dateMessage");
                String  author = resultSet.getString("name");
                messages.add(new Message(id, text, author, date));
            }
        } catch (SQLException e) {
            logger.error(e);
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    logger.error(e);
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    logger.error(e);
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    logger.error(e);
                }
            }
        }
        return messages;
    }
}
