package Chat.Controller;

import java.io.PrintWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.util.*;

import Chat.Util.XMLHistoryUtil;
import Chat.Model.Message;
import Chat.Util.ServletUtil;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import org.xml.sax.SAXException;

import static Chat.Util.MessageUtil.MESSAGES;
import static Chat.Util.MessageUtil.jsonToMessage;
import static Chat.Util.MessageUtil.stringToJson;

import javax.servlet.AsyncContext;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;

import org.apache.log4j.Logger;


@WebServlet(urlPatterns = {"/WebChat"}, asyncSupported = true)
public class MainServlet extends HttpServlet {
    /*
    Связывается одновременно лишь с одним клиентом :(
    Исправь!
     */

    private static Logger logger = Logger.getLogger(MainServlet.class.getName());
    private List <AsyncContext> contexts = new LinkedList<>();

    @Override
    public void init() throws ServletException {
        try {
            loadHistory();
            List<Message> messageList = XMLHistoryUtil.getMessages(0);
            for (Message message : messageList) {
                System.out.println(message.getDate() + " " + message.getAuthor() + ": " + message.getText());
            }
        } catch (SAXException | IOException | ParserConfigurationException | TransformerException e) {
            logger.error(e);
        }
    }

    private void loadHistory() throws SAXException, IOException, ParserConfigurationException, TransformerException {
        if (!XMLHistoryUtil.doesStorageExist()) {
            XMLHistoryUtil.createStorage();
        }
    }

    @SuppressWarnings("unchecked")
    private String initFormResponse() throws SAXException, IOException, ParserConfigurationException {

        JSONObject jsonObject = new JSONObject();
        jsonObject.put(MESSAGES, XMLHistoryUtil.getMessages(0));
        return jsonObject.toJSONString();
    }

    @SuppressWarnings("unchecked")
    private String formResponse(Message message, String action) throws SAXException, IOException, ParserConfigurationException {

        List<Message> list = new ArrayList<>();
        list.add(message);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(MESSAGES, list);
        jsonObject.put("action", action);
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
        logger.info("doGet");

        response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, PUT, DELETE, HEAD");

        response.setContentType(ServletUtil.APPLICATION_JSON);
        response.setCharacterEncoding("UTF-8");

        if ("false".equals(request.getParameter("first"))) {
            final AsyncContext asyncContext = request.startAsync(request, response);
            asyncContext.addListener(new AsyncListener() {
                @Override
                public void onComplete(AsyncEvent asyncEvent) throws IOException {
                    System.out.println("Async complete.");
                }

                @Override
                public void onTimeout(AsyncEvent asyncEvent) throws IOException {
                    System.out.println("Timed out..");
                    contexts.remove(asyncContext);
                    asyncContext.complete();
                }

                @Override
                public void onError(AsyncEvent asyncEvent) throws IOException {
                    System.out.println("Error..");
                }

                @Override
                public void onStartAsync(AsyncEvent asyncEvent) throws IOException {
                    System.out.println("Start Async..");
                }
            });

            asyncContext.setTimeout(10 * 1000);
            contexts.add(asyncContext);
        } else {
            try {
                String responseMessage = initFormResponse();
                PrintWriter writer = response.getWriter();
                writer.print(responseMessage);
                writer.flush();
            } catch (SAXException | ParserConfigurationException e) {
                logger.error(e);
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        logger.info("doPost");
        String data = ServletUtil.getMessageBody(request);
        logger.info(data);

        response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, PUT, DELETE, HEAD");

        List<AsyncContext> asyncContexts = new ArrayList<>(this.contexts);
        this.contexts.clear();

        try {
            String date = getDate();

            JSONObject json = stringToJson(data);
            Message message = jsonToMessage(json);
            message.setDate(date);
            XMLHistoryUtil.addData(message);
            response.setStatus(HttpServletResponse.SC_OK);

            String responseMessage = formResponse(message, "ADD");

            for (AsyncContext asyncContext: asyncContexts) {
                PrintWriter writer = asyncContext.getResponse().getWriter();
                writer.println(responseMessage);
                writer.flush();
                asyncContext.complete();
            }
            System.out.print(date + "  ");
            System.out.println(json.get("author") + " : " + json.get("text"));
            System.out.flush();

        } catch (ParseException | ParserConfigurationException | SAXException | TransformerException e) {
            logger.error(e);
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        logger.info("doPut");
        String data = ServletUtil.getMessageBody(request);
        logger.info(data);

        response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, PUT, DELETE, HEAD");

        List<AsyncContext> asyncContexts = new ArrayList<>(this.contexts);
        this.contexts.clear();

        try {
            JSONObject json = stringToJson(data);
            Message message = jsonToMessage(json);
            message.setDate(getDate());
            XMLHistoryUtil.updateData(message);
            response.setStatus(HttpServletResponse.SC_OK);

            String responseMessage = formResponse(message, "CHANGE");

            for (AsyncContext asyncContext: asyncContexts) {
                PrintWriter writer = asyncContext.getResponse().getWriter();
                writer.println(responseMessage);
                writer.flush();
                asyncContext.complete();
            }

        } catch (ParseException | ParserConfigurationException | SAXException | TransformerException | XPathExpressionException e) {
            logger.error(e);
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        logger.info("doDelete");

        response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, PUT, DELETE, HEAD");

        String data = ServletUtil.getMessageBody(request);
        logger.info(data);

        List<AsyncContext> asyncContexts = new ArrayList<>(this.contexts);
        this.contexts.clear();

        try {
            JSONObject json = stringToJson(data);
            Message message = jsonToMessage(json);
            message.setDate(getDate());
            XMLHistoryUtil.deleteData(message);
            response.setStatus(HttpServletResponse.SC_OK);

            String responseMessage = formResponse(message, "DELETE");

            for (AsyncContext asyncContext: asyncContexts) {
                PrintWriter writer = asyncContext.getResponse().getWriter();
                writer.println(responseMessage);
                writer.flush();
                asyncContext.complete();
            }

        } catch (ParseException | ParserConfigurationException | SAXException | TransformerException | XPathExpressionException e) {
            logger.error(e);
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
}