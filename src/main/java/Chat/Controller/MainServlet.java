package Chat.Controller;

import java.io.PrintWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;

import Chat.Util.XMLHistoryUtil;
import Chat.Model.Message;
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
import javax.xml.xpath.XPathExpressionException;

import java.util.List;
import java.util.TimeZone;
import org.apache.log4j.Logger;


@WebServlet("/WebChat")
public class MainServlet extends HttpServlet {

    private Integer versionServer;
    private static Logger logger = Logger.getLogger(MainServlet.class.getName());

    @Override
    public void init() throws ServletException {
        try {
            versionServer = 0;
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
    private String formResponse(int index) throws SAXException, IOException, ParserConfigurationException {

        JSONObject jsonObject = new JSONObject();
        jsonObject.put(MESSAGES, XMLHistoryUtil.getMessages(index));
        jsonObject.put(TOKEN, getToken(XMLHistoryUtil.getStorageSize()));
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
        logger.info("doGet");
        String token = request.getParameter(TOKEN);
        logger.info("Token: " + token);
        Integer version = Integer.parseInt(request.getParameter(VERSION));
        logger.info("Version: " + version);

        try {
            if (token != null && !"".equals(token) && !"".equals(version.toString())) {
                int index = getIndex(token);
                logger.info("Index: " + index);
                if (version.equals(versionServer) && index == XMLHistoryUtil.getStorageSize()) {
                    response.sendError(HttpServletResponse.SC_NOT_MODIFIED);
                }
                else {
                    response.setContentType(ServletUtil.APPLICATION_JSON);
                    response.setCharacterEncoding("UTF-8");
                    PrintWriter out = response.getWriter();
                    String messages;
                    if (versionServer.equals(version)) {
                        messages = formResponse(index);
                    } else {
                        messages = formResponse(0);
                    }
                    out.print(messages);
                    out.flush();
                }
            } else {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "'token' and 'version' parameters needed");
            }
        }
        catch (SAXException | ParserConfigurationException e) {
            logger.error(e);
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        logger.info("doPost");
        String data = ServletUtil.getMessageBody(request);
        logger.info(data);
        try {
            String date = getDate();

            JSONObject json = stringToJson(data);
            Message message = jsonToMessage(json);
            message.setDate(date);
            XMLHistoryUtil.addData(message);
            response.setStatus(HttpServletResponse.SC_OK);
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
        versionServer++;
        String data = ServletUtil.getMessageBody(request);
        logger.info(data);
        try {
            JSONObject json = stringToJson(data);
            Message message = jsonToMessage(json);
            message.setDate(getDate());
            XMLHistoryUtil.updateData(message);
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (ParseException | ParserConfigurationException | SAXException | TransformerException | XPathExpressionException e) {
            logger.error(e);
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        logger.info("doDelete");
        versionServer++;
        String data = ServletUtil.getMessageBody(request);
        logger.info(data);
        try {
            JSONObject json = stringToJson(data);
            Message message = jsonToMessage(json);
            message.setDate(getDate());
            XMLHistoryUtil.deleteData(message);
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (ParseException | ParserConfigurationException | SAXException | TransformerException | XPathExpressionException e) {
            logger.error(e);
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
}