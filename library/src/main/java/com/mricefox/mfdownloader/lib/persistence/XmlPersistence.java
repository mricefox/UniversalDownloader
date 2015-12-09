package com.mricefox.mfdownloader.lib.persistence;

import android.util.Xml;

import com.mricefox.mfdownloader.lib.Block;
import com.mricefox.mfdownloader.lib.Download;
import com.mricefox.mfdownloader.lib.DownloadParams;
import com.mricefox.mfdownloader.lib.assist.MFLog;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 * Author:zengzifeng email:zeng163mail@163.com
 * Description:
 * Date:2015/12/1
 */
public class XmlPersistence implements Persistence<Download> {
    private final static String XML_FILE_NAME = "mf_download.xml";
    private final static String ROOT_TAG = "download_group";
    private final static String MAX_ID_TAG = "max_id";
    private final static String ELEMENT_DOWNLOAD_TAG = "download";
    private final static String ELEMENT_BLOCK_TAG = "block";
    private static XmlPersistence instance;
    private static File xmlFile;

    private XmlPersistence() {
    }

    public static XmlPersistence getInstance() {
        if (instance == null) {
            synchronized (XmlPersistence.class) {
                if (instance == null) instance = new XmlPersistence();
            }
        }
        return instance;
    }

    public synchronized void init(String dir) throws IOException {
        long time = System.currentTimeMillis();
        xmlFile = new File(dir, XML_FILE_NAME);
        if (!xmlFile.exists()) {
            if (!xmlFile.createNewFile()) throw new IOException("create persistence file fail");
            else
                initRoot();// create success
        } else {//exists
        }
        MFLog.d("XmlPersistence init time:" + (System.currentTimeMillis() - time));
    }

    private void initRoot() {
        XmlSerializer serializer = Xml.newSerializer();
        try {
            serializer.setOutput(new FileOutputStream(xmlFile), "UTF-8");
            serializer.startDocument("UTF-8", true);
            serializer.startTag(null, ROOT_TAG);
            serializer.attribute(null, MAX_ID_TAG, String.valueOf(-1));
            serializer.endTag(null, ROOT_TAG);
            serializer.endDocument();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Element convertEntityToElement(Download download, Document document) {
        Element element = document.createElement(ELEMENT_DOWNLOAD_TAG);
        element.setAttribute("id", String.valueOf(download.getId()));
        element.setAttribute("uri", download.getUri());
        element.setAttribute("path", download.getTargetFilePath());
        element.setAttribute("status", String.valueOf(download.getStatus()));
        element.setAttribute("totalBytes", String.valueOf(download.getTotalBytes()));
        element.setAttribute("currentBytes", String.valueOf(download.getCurrentBytes()));
        element.setAttribute("priority", String.valueOf(download.getPriority()));
        for (int i = 0, size = download.getBlocks().size(); i < size; ++i) {
            Block block = download.getBlocks().get(i);
            Element blockElement = document.createElement(ELEMENT_BLOCK_TAG);
            blockElement.setAttribute("index", String.valueOf(block.getIndex()));
            blockElement.setAttribute("startPos", String.valueOf(block.getStartPos()));
            blockElement.setAttribute("endPos", String.valueOf(block.getEndPos()));
            blockElement.setAttribute("downloadedBytes", String.valueOf(block.getDownloadedBytes()));
            element.appendChild(blockElement);
        }
        return element;
    }

    private void updateElement(Download download, Element element, Document document) {
        element.setAttribute("id", String.valueOf(download.getId()));
        element.setAttribute("uri", download.getUri());
        element.setAttribute("path", download.getTargetFilePath());
        element.setAttribute("status", String.valueOf(download.getStatus()));
        element.setAttribute("totalBytes", String.valueOf(download.getTotalBytes()));
        element.setAttribute("currentBytes", String.valueOf(download.getCurrentBytes()));
        element.setAttribute("priority", String.valueOf(download.getPriority()));

        NodeList blockElements = element.getChildNodes();
        for (int i = 0, size = blockElements.getLength(); i < size; ++i) {
            Node node = blockElements.item(i);
            element.removeChild(node);//remove for blocks number change
        }

        for (int i = 0, size = download.getBlocks().size(); i < size; ++i) {
            Block block = download.getBlocks().get(i);
            Element blockElement = document.createElement(ELEMENT_BLOCK_TAG);
            blockElement.setAttribute("index", String.valueOf(block.getIndex()));
            blockElement.setAttribute("startPos", String.valueOf(block.getStartPos()));
            blockElement.setAttribute("endPos", String.valueOf(block.getEndPos()));
            blockElement.setAttribute("downloadedBytes", String.valueOf(block.getDownloadedBytes()));
            element.appendChild(blockElement);
        }
    }

    @Override
    public synchronized List<Download> queryAll() {
        long time = System.currentTimeMillis();

        XmlPullParser parser = Xml.newPullParser();
        List<Download> downloadList = null;
        try {
            parser.setInput(new FileInputStream(xmlFile), "UTF-8");
            int eventType = parser.getEventType();
            Download download = null;
            Block block = null;
            List<Block> blockList = null;
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        if (parser.getName().equals(ROOT_TAG)) {
                            downloadList = new ArrayList<>();
                        } else if (parser.getName().equals(ELEMENT_DOWNLOAD_TAG)) {
                            String id = parser.getAttributeValue(null, "id");
                            String uri = parser.getAttributeValue(null, "uri");
                            String path = parser.getAttributeValue(null, "path");
                            String status = parser.getAttributeValue(null, "status");
                            String currentBytes = parser.getAttributeValue(null, "currentBytes");
                            String totalBytes = parser.getAttributeValue(null, "totalBytes");
                            String priority = parser.getAttributeValue(null, "priority");
                            download = new Download(new DownloadParams(uri, path).priority(Integer.valueOf(priority)));
                            download.setId(Long.valueOf(id));
                            download.setStatus(Integer.valueOf(status));
                            download.setCurrentBytes(Long.valueOf(currentBytes));
                            download.setTotalBytes(Long.valueOf(totalBytes));
                            blockList = new ArrayList<>();
                        } else if (parser.getName().equals(ELEMENT_BLOCK_TAG)) {
                            String index = parser.getAttributeValue(null, "index");
                            String startPos = parser.getAttributeValue(null, "startPos");
                            String endPos = parser.getAttributeValue(null, "endPos");
                            String downloadedBytes = parser.getAttributeValue(null, "downloadedBytes");
                            block = new Block(Integer.valueOf(index),
                                    Long.valueOf(startPos), Long.valueOf(endPos), Long.valueOf(downloadedBytes));
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if (parser.getName().equals(ROOT_TAG)) {
                        } else if (parser.getName().equals(ELEMENT_DOWNLOAD_TAG)) {
                            download.setBlocks(blockList);
                            downloadList.add(download);
                        } else if (parser.getName().equals(ELEMENT_BLOCK_TAG)) {
                            blockList.add(block);
                        }
                        break;

                }
                eventType = parser.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        MFLog.d("XmlPersistence readall time:" + (System.currentTimeMillis() - time));
        return downloadList;
    }

    @Override
    public synchronized long insert(Download download) {
        long time = System.currentTimeMillis();

        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = docFactory.newDocumentBuilder();
            Document document = builder.parse(xmlFile);
            Element rootElement = (Element) document.getElementsByTagName(ROOT_TAG).item(0);
            long maxId = Long.valueOf(rootElement.getAttribute(MAX_ID_TAG));
            download.setId(++maxId);
//            MFLog.d("root tag list size:" + document.getElementsByTagName(ROOT_TAG).getLength());
            Element downloadElement = convertEntityToElement(download, document);
            rootElement.appendChild(downloadElement);
            rootElement.setAttribute(MAX_ID_TAG, String.valueOf(maxId));
            writeBack(document, xmlFile);
            return maxId;
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }
        MFLog.d("XmlPersistence insert time:" + (System.currentTimeMillis() - time));
        return -1;
    }

    @Override
    public synchronized long update(Download download) {
//        long time = System.currentTimeMillis();

        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        long res = -1;
        try {
            DocumentBuilder builder = docFactory.newDocumentBuilder();
            Document document = builder.parse(xmlFile);
            NodeList downloads = document.getElementsByTagName(ELEMENT_DOWNLOAD_TAG);
//            MFLog.d("downloads tag list size:" + downloads.getLength());
            for (int i = 0, size = downloads.getLength(); i < size; ++i) {
                Element downloadElement = (Element) downloads.item(i);
                long id = Long.valueOf(downloadElement.getAttribute("id"));
                if (id == download.getId()) {
//                    MFLog.d("id == entity.id:" + id);
                    res = id;
                    updateElement(download, downloadElement, document);
                    writeBack(document, xmlFile);
                    break;
                }
            }
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }
//        MFLog.d("XmlPersistence update time:" + (System.currentTimeMillis() - time));
        return res;
    }

    @Override
    public synchronized long delete(Download download) {
        long time = System.currentTimeMillis();

        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        long res = -1;
        try {
            DocumentBuilder builder = docFactory.newDocumentBuilder();
            Document document = builder.parse(xmlFile);
            Element rootElement = (Element) document.getElementsByTagName(ROOT_TAG).item(0);
            NodeList downloads = document.getElementsByTagName(ELEMENT_DOWNLOAD_TAG);
            for (int i = 0, size = downloads.getLength(); i < size; ++i) {
                Element downloadElement = (Element) downloads.item(i);
                long id = Long.valueOf(downloadElement.getAttribute("id"));
                if (id == download.getId()) {
//                    MFLog.d("id == entity.id:" + id);
                    res = id;
                    rootElement.removeChild(downloadElement);
                    writeBack(document, xmlFile);
                    break;
                }
            }
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }
        MFLog.d("XmlPersistence delete time:" + (System.currentTimeMillis() - time));
        return res;
    }

    @Override
    public synchronized Download query(long id) {
        List<Download> all = queryAll();
        for (int i = 0, size = all.size(); i < size; ++i) {
            Download download = all.get(i);
            if (download.getId() == id) return download;
        }
        return null;
    }

    private void writeBack(Document document, File file) throws TransformerException {
        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer transformer = factory.newTransformer();
//        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        DOMSource source = new DOMSource(document);
        StreamResult result = new StreamResult(file);
        transformer.transform(source, result);
    }
}
