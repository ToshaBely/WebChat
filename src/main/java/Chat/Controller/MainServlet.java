package Chat.Controller;

import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.util.Date;
import java.text.SimpleDateFormat;

import Chat.Util.XMLHistoryUtil;
import Chat.Model.Message;
import Chat.Model.MessageStorage;
import Chat.Util.ServletUtil;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.xml.sax.SAXException;

import static Chat.Util.MessageUtil.TOKEN;
import static Chat.Util.MessageUtil.VERSION;
import static Chat.Util.MessageUtil.MESSAGES;
import static Chat.Util.MessageUtil.getIndex;
import static Chat.Util.MessageUtil.getToken;
import static Chat.Util.MessageUtil.jsonToMessage;
import static Chat.Util.MessageUtil.stringToJson;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.util.List;
import java.util.TimeZone;


@WebServlet("/WebChat")
public class MainServlet extends HttpServlet {
    
    private Integer versionServer;

    @Override
    public void init() throws ServletException {
        try {
            versionServer = 0;
            loadHistory();
            List<Message> messageList = XMLHistoryUtil.getMessages();
            for (Message message: messageList) {
                System.out.println(message.getDate() + " " + message.getAuthor() + ": " + message.getText());
            }
        } catch (SAXException e) {
            //logger.error(e);
            System.out.println(e.toString());
        }
        catch (IOException e) {
            System.out.println(e.toString());
        }
        catch (ParserConfigurationException e) {
            System.out.println(e.toString());
        }
        catch (TransformerException e) {
            System.out.println(e.toString());
        }
    }

    private void loadHistory() throws SAXException, IOException, ParserConfigurationException, TransformerException {
        if (XMLHistoryUtil.doesStorageExist()) {
            MessageStorage.addAll(XMLHistoryUtil.getMessages());
        } else {
            XMLHistoryUtil.createStorage();
            addStubData();
     	}
    }

    private void addStubData() {
        Message[] stubMessage = {
                new Message("1", "Create markup", "Anton", "now"),
                new Message("2", "Learn JavaScript", "Anton", "now"),
                new Message("3", "Learn Java Servlet Technology", "Anton", "now"),
                new Message("4", "Write The Chat !", "Anton", "now"), };
        MessageStorage.addAll(stubMessage);
        for (Message message : stubMessage) {
            try {
                XMLHistoryUtil.addData(message);
            } catch (ParserConfigurationException e) {
                System.out.println(e.toString());
            }
            catch (SAXException e) {
                System.out.println(e.toString());
            }
            catch (IOException e) {
                System.out.println(e.toString());
            }
            catch (TransformerException e) {
                System.out.println(e.toString());
            }

        }
    }

    @SuppressWarnings("unchecked")
    private String formResponse(int index) {

        JSONObject jsonObject = new JSONObject();
        jsonObject.put(MESSAGES, MessageStorage.getSubMessageByIndex(index));
        jsonObject.put(TOKEN, getToken(MessageStorage.getSize()));
        jsonObject.put(VERSION, versionServer.toString());
        return jsonObject.toJSONString();
    }

    private static String getDate() {
        DateFormat formatter;
        formatter = DateFormat.getDateTimeInstance();
        formatter.setTimeZone(TimeZone.getTimeZone("Europe/Minsk"));
        return formatter.format(new Date());
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String token = request.getParameter(TOKEN);
        int version = Integer.parseInt(request.getParameter(VERSION));

        if (token != null && !"".equals(token)) {
            int index = getIndex(token);
            response.setContentType(ServletUtil.APPLICATION_JSON);
            PrintWriter out = response.getWriter();
            String messages;
            if (versionServer.equals(version)) {
                messages = formResponse(index);
            } else {
                messages = formResponse(0);
            }
            out.print(messages);
            out.flush();
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "'token' parameter needed");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String data = ServletUtil.getMessageBody(request);
        try {
            String date = getDate();

            JSONObject json = stringToJson(data);
            Message message = jsonToMessage(json);
            message.setDate(date);
            MessageStorage.addMessage(message);
            XMLHistoryUtil.addData(message);
            response.setStatus(HttpServletResponse.SC_OK);
            System.out.print(date + "  ");
            System.out.println(json.get("author") + " : " + json.get("text"));
            System.out.flush();

        } catch (ParseException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
        catch (ParserConfigurationException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
        catch (SAXException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
        catch (TransformerException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
/*
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        logger.info("doPut");
        String data = ServletUtil.getMessageBody(request);
        logger.info(data);
        try {
            JSONObject json = stringToJson(data);
            Task task = jsonToTask(json);
            String id = task.getId();
            Task taskToUpdate = TaskStorage.getTaskById(id);
            if (taskToUpdate != null) {
                taskToUpdate.setDescription(task.getDescription());
                taskToUpdate.setDone(task.isDone());
                response.setStatus(HttpServletResponse.SC_OK);
            } else {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Task does not exist");
            }
        } catch (ParseException e) {
            logger.error(e);
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
*/
}
