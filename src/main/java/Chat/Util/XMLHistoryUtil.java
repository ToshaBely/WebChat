package Chat.Util;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import Chat.Model.Message;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


public final class XMLHistoryUtil {

    private static final String STORAGE_LOCATION = System.getProperty("user.home") +  File.separator + "history.xml"; // history.xml will be located in the home directory
    private static final String MESSAGES = "messages";
    private static final String MESSAGE = "message";
    private static final String ID = "id";
    private static final String TEXT = "text";
    private static final String AUTHOR = "author";
    private static final String DATE = "date";

    private XMLHistoryUtil() {
    }

    public static synchronized void createStorage() throws ParserConfigurationException, TransformerException {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

        Document doc = docBuilder.newDocument();
        Element rootElement = doc.createElement(MESSAGES);
        doc.appendChild(rootElement);

        Transformer transformer = getTransformer();
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new File(STORAGE_LOCATION));
        transformer.transform(source, result);
    }

    public static synchronized void addData(Message message) throws ParserConfigurationException, SAXException, IOException, TransformerException {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.parse(STORAGE_LOCATION);
        document.getDocumentElement().normalize();

        Element root = document.getDocumentElement(); // Root <messages> element

        Element taskElement = document.createElement(MESSAGE);
        root.appendChild(taskElement);

        taskElement.setAttribute(ID, message.getId());

        Element author = document.createElement(AUTHOR);
        author.appendChild(document.createTextNode(message.getAuthor()));
        taskElement.appendChild(author);

        Element text = document.createElement(TEXT);
        text.appendChild(document.createTextNode(message.getText()));
        taskElement.appendChild(text);

        Element date = document.createElement(DATE);
        date.appendChild(document.createTextNode(message.getDate()));
        taskElement.appendChild(date);

        DOMSource source = new DOMSource(document);

        Transformer transformer = getTransformer();

        StreamResult result = new StreamResult(STORAGE_LOCATION);
        transformer.transform(source, result);
    }

    public static synchronized void deleteData (Message message) throws ParserConfigurationException, SAXException, IOException, TransformerException, XPathExpressionException {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.parse(STORAGE_LOCATION);
        document.getDocumentElement().normalize();
        Node messageToDelete = getNodeById(document, message.getId());

        if (messageToDelete != null) {
            NodeList childNodes = messageToDelete.getChildNodes();

            for (int i = 0; i < childNodes.getLength(); i++) {

                Node node = childNodes.item(i);

                if (TEXT.equals(node.getNodeName())) {
                    node.setTextContent(message.getText());
                }

                if(DATE.equals(node.getNodeName())) {
                    node.setTextContent(message.getDate());
                }

            }
            Transformer transformer = getTransformer();

            DOMSource source = new DOMSource(document);
            StreamResult result = new StreamResult(new File(STORAGE_LOCATION));
            transformer.transform(source, result);
        } else {
            throw new NullPointerException();
        }
    }

    public static synchronized void updateData(Message message) throws ParserConfigurationException, SAXException, IOException, TransformerException, XPathExpressionException {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.parse(STORAGE_LOCATION);
        document.getDocumentElement().normalize();
        Node messageToUpdate = getNodeById(document, message.getId());

        if (messageToUpdate != null) {
            NodeList childNodes = messageToUpdate.getChildNodes();

            for (int i = 0; i < childNodes.getLength(); i++) {

                Node node = childNodes.item(i);

                if (AUTHOR.equals(node.getNodeName())) {
                    node.setTextContent(message.getAuthor());
                }

                if (TEXT.equals(node.getNodeName())) {
                    node.setTextContent(message.getText());
                }

                if(DATE.equals(node.getNodeName())) {
                    node.setTextContent(message.getDate());
                }

            }

            Transformer transformer = getTransformer();

            DOMSource source = new DOMSource(document);
            StreamResult result = new StreamResult(new File(STORAGE_LOCATION));
            transformer.transform(source, result);
        } else {
            throw new NullPointerException();
        }
    }

    public static synchronized boolean doesStorageExist() {
        File file = new File(STORAGE_LOCATION);
        return file.exists();
    }

    public static synchronized List<Message> getMessages(int index) throws SAXException, IOException, ParserConfigurationException {
        List<Message> messages = new ArrayList<Message>();
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.parse(STORAGE_LOCATION);
        document.getDocumentElement().normalize();
        Element root = document.getDocumentElement(); // Root <messages> element
        NodeList messageList = root.getElementsByTagName(MESSAGE);
        for (int i = index; i < messageList.getLength(); i++) {
            Element messageElement = (Element) messageList.item(i);
            String id = messageElement.getAttribute(ID);
            String text = messageElement.getElementsByTagName(TEXT).item(0).getTextContent();
            String author = messageElement.getElementsByTagName(AUTHOR).item(0).getTextContent();
            String date = messageElement.getElementsByTagName(DATE).item(0).getTextContent();
            messages.add(new Message(id, text, author, date));
        }
        return messages;
    }

    public static int getCount() throws ParserConfigurationException, SAXException, IOException{
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.parse(STORAGE_LOCATION);
        document.getDocumentElement().normalize();
        Element root = document.getDocumentElement(); // Root <messages> element
        NodeList messageList = root.getElementsByTagName(MESSAGE);
        return messageList.getLength();
    }

    public static synchronized int getStorageSize() throws SAXException, IOException, ParserConfigurationException {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.parse(STORAGE_LOCATION);
        document.getDocumentElement().normalize();
        Element root = document.getDocumentElement(); // Root <tasks> element
        return root.getElementsByTagName(MESSAGE).getLength();
    }

    private static Node getNodeById(Document doc, String id) throws XPathExpressionException {
        XPath xpath = XPathFactory.newInstance().newXPath();
        XPathExpression expr = xpath.compile("//" + MESSAGE + "[@id='" + id + "']");
        return (Node) expr.evaluate(doc, XPathConstants.NODE);
    }

    private static Transformer getTransformer() throws TransformerConfigurationException {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
         // Formatting XML properly
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        return transformer;
    }

}
