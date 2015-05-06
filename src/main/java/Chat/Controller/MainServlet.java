package Chat.Controller;

import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.util.Date;
import java.text.SimpleDateFormat;

import Chat.Util.XMLHistoryUtil;
import Chat.Model.Message;
//import Chat.Model.MessageStorage;
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
            List<Message> messageList = XMLHistoryUtil.getMessages(0);
            for (Message message : messageList) {
                System.out.println(message.getDate() + " " + message.getAuthor() + ": " + message.getText());
            }
        } catch (SAXException e) {
            System.out.println(e.toString());
        } catch (IOException e) {
            System.out.println(e.toString());
        } catch (ParserConfigurationException e) {
            System.out.println(e.toString());
        } catch (TransformerException e) {
            System.out.println(e.toString());
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
        jsonObject.put(TOKEN, getToken(XMLHistoryUtil.getCount()));
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
        Integer version = Integer.parseInt(request.getParameter(VERSION));

        try {
            if (token != null && !"".equals(token) && !"".equals(version.toString())) {
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
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "'token' and 'version' parameters needed");
            }
        }
        catch (SAXException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
        catch (ParserConfigurationException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
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
            XMLHistoryUtil.addData(message);
            response.setStatus(HttpServletResponse.SC_OK);
            System.out.print(date + "  ");
            System.out.println(json.get("author") + " : " + json.get("text"));
            System.out.flush();

        } catch (ParseException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        } catch (ParserConfigurationException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        } catch (SAXException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        } catch (TransformerException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        versionServer++;
        String data = ServletUtil.getMessageBody(request);
        try {
            JSONObject json = stringToJson(data);
            Message message = jsonToMessage(json);
            message.setDate(getDate());
                XMLHistoryUtil.updateData(message);
                response.setStatus(HttpServletResponse.SC_OK);
        } catch (ParseException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        } catch (ParserConfigurationException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        } catch (SAXException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        } catch (TransformerException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        } catch (XPathExpressionException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        versionServer++;
        String data = ServletUtil.getMessageBody(request);
        try {
            JSONObject json = stringToJson(data);
            Message message = jsonToMessage(json);
            message.setDate(getDate());
            XMLHistoryUtil.deleteData(message);
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (ParseException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        } catch (ParserConfigurationException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        } catch (SAXException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        } catch (TransformerException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        } catch (XPathExpressionException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
}