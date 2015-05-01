package Chat.Controller;

import java.io.BufferedWriter;
import java.text.DateFormat;
import java.util.Date;
import java.text.SimpleDateFormat;

import Chat.Model.Message;
import Chat.Model.MessageStorage;
import Chat.Util.ServletUtil;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import static Chat.Util.MessageUtil.TOKEN;
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
import java.io.IOException;
import java.io.PrintWriter;
import java.util.TimeZone;


@WebServlet("/WebChat")
public class MainServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    //private static Logger logger = Logger.getLogger(TaskServlet.class.getName());

    @Override
    public void init() throws ServletException {
        addStubData();
    }


    private void addStubData() {
        Message[] stubMessage = {
                new Message("1", "Create markup", "Anton"),
                new Message("2", "Learn JavaScript", "Anton"),
                new Message("3", "Learn Java Servlet Technology", "Anton"),
                new Message("4", "Write The Chat !", "Anton"), };
        MessageStorage.addAll(stubMessage);
    }

    @SuppressWarnings("unchecked")
    private String formResponse(int index) {

        JSONObject jsonObject = new JSONObject();
        jsonObject.put(MESSAGES, MessageStorage.getSubMessageByIndex(index));
        jsonObject.put(TOKEN, getToken(MessageStorage.getSize()));
        return jsonObject.toJSONString();
    }

    private static String getDate() {
        DateFormat formatter;
        formatter = DateFormat.getDateTimeInstance();
        formatter.setTimeZone(TimeZone.getTimeZone("Europe/Minsk"));
        return formatter.format(new Date());
    }
/*
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //logger.info("doGet");
        String token = request.getParameter(TOKEN);
        //logger.info("Token " + token);

        if (token != null && !"".equals(token)) {
            int index = getIndex(token);
            response.setContentType(ServletUtil.APPLICATION_JSON);
            //logger.info("Index " + index);
            String messages = formResponse(index);
            response.setContentType(ServletUtil.APPLICATION_JSON);
            PrintWriter out = response.getWriter();

            out.print(messages);
            out.flush();
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "'token' parameter needed");
        }
    }
    */

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //logger.info("doPost");
        String data = ServletUtil.getMessageBody(request);
        //logger.info(data);
        try {
            String date = getDate();

            JSONObject json = stringToJson(data);
            Message message = jsonToMessage(json);
            MessageStorage.addMessage(message);
            response.setStatus(HttpServletResponse.SC_OK);
            System.out.print(date + "  ");
            System.out.println(json.get("author") + " : " + json.get("text"));
            System.out.flush();
        } catch (ParseException e) {
            //logger.error(e);
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

    @SuppressWarnings("unchecked")
    private String formResponse(int index) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(TASKS, TaskStorage.getSubTasksByIndex(index));
        jsonObject.put(TOKEN, getToken(TaskStorage.getSize()));
        return jsonObject.toJSONString();
    }

    private void addStubData() {
        Task[] stubTasks = {
                new Task("1", "Create markup", true),
                new Task("2", "Learn JavaScript", true),
                new Task("3", "Learn Java Servlet Technology", false),
                new Task("4", "Write The Chat !", false), };
        TaskStorage.addAll(stubTasks);
    }
*/

/*
    //private MessageExchange messageExchange = new MessageExchange();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        PrintWriter pw = response.getWriter();
        pw.println("method GET");
/*
        //logger.info("doGet");
        String token = request.getParameter("token");
        //logger.info("Token " + token);

        if (token != null && !"".equals(token)) {
            int index = messageExchange.getIndex(token);
            //logger.info("Index " + index);
            //String tasks = messageExchange.formResponse(index);
            //response.setContentType(ServletUtil.APPLICATION_JSON);
            PrintWriter out = response.getWriter();
            out.print(index);
            out.flush();
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "'token' parameter needed");
        }*/
       /* String token = request.getParameter("TOKEN");

        if (token != null && !"".equals(token)) {
            int index = messageExchange.getIndex(token);
            String messages = messageExchange.getServerResponse(index);
            response.setContentType("ServletUtil.APPLICATION_JSON");
            PrintWriter out = response.getWriter();
            out.print(messages);
            out.flush();
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "'token' parameter needed");
        }*/
       //PrintWriter pw = response.getWriter();
       //pw.println("method GET");
    //}
}
