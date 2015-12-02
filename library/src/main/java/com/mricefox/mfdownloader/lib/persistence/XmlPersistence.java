package com.mricefox.mfdownloader.lib.persistence;

import android.util.Xml;

import com.mricefox.mfdownloader.lib.Block;
import com.mricefox.mfdownloader.lib.Download;
import com.mricefox.mfdownloader.lib.DownloadWrapper;
import com.mricefox.mfdownloader.lib.assist.L;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
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
public class XmlPersistence implements Persistence<DownloadWrapper> {
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
                createRoot();// create success
        } else {//exists
        }
        L.d("XmlPersistence init time:" + (System.currentTimeMillis() - time));
    }

    private void createRoot() {
        XmlSerializer serializer = Xml.newSerializer();
        try {
            serializer.setOutput(new FileOutputStream(xmlFile), "UTF-8");
            serializer.startDocument("UTF-8", true);
            serializer.startTag(null, ROOT_TAG);
            serializer.attribute(null, MAX_ID_TAG, String.valueOf(0));
            serializer.endTag(null, ROOT_TAG);
            serializer.endDocument();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Element convertEntityToElement(DownloadWrapper wrapper, Document document) {
        Element element = document.createElement(ELEMENT_DOWNLOAD_TAG);
        element.setAttribute("id", String.valueOf(wrapper.getId()));
        element.setAttribute("uri", wrapper.getDownload().getUri());
        element.setAttribute("path", wrapper.getDownload().getTargetFilePath());
        for (int i = 0, size = wrapper.getBlocks().size(); i < size; ++i) {
            Block block = wrapper.getBlocks().get(i);
            Element blockElement = document.createElement(ELEMENT_BLOCK_TAG);
            blockElement.setAttribute("index", String.valueOf(block.getIndex()));
            blockElement.setAttribute("startPos", String.valueOf(block.getStartPos()));
            blockElement.setAttribute("endPos", String.valueOf(block.getEndPos()));
            blockElement.setAttribute("downloadedBytes", String.valueOf(block.getDownloadedBytes()));
            element.appendChild(blockElement);
        }
        return element;
    }

    private void updateElement(DownloadWrapper wrapper, Element element) {
        element.setAttribute("id", String.valueOf(wrapper.getId()));
        element.setAttribute("uri", wrapper.getDownload().getUri());
        element.setAttribute("path", wrapper.getDownload().getTargetFilePath());
        for (int i = 0, size = wrapper.getBlocks().size(); i < size; ++i) {
            Block block = wrapper.getBlocks().get(i);
            NodeList blockElements = element.getChildNodes();
            Element blockElement = (Element) blockElements.item(i);
            blockElement.setAttribute("index", String.valueOf(block.getIndex()));
            blockElement.setAttribute("startPos", String.valueOf(block.getStartPos()));
            blockElement.setAttribute("endPos", String.valueOf(block.getEndPos()));
            blockElement.setAttribute("downloadedBytes", String.valueOf(block.getDownloadedBytes()));
        }
    }

    @Override
    public List<DownloadWrapper> readAll() {
        long time = System.currentTimeMillis();

        XmlPullParser parser = Xml.newPullParser();
        List<DownloadWrapper> wrapperList = null;
        try {
            parser.setInput(new FileInputStream(xmlFile), "UTF-8");
            int eventType = parser.getEventType();
            DownloadWrapper wrapper = null;
            Block block = null;
            List<Block> blockList = null;
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        if (parser.getName().equals(ROOT_TAG)) {
                            wrapperList = new ArrayList<>();
                        } else if (parser.getName().equals(ELEMENT_DOWNLOAD_TAG)) {
                            String id = parser.getAttributeValue(null, "id");
                            String uri = parser.getAttributeValue(null, "uri");
                            String path = parser.getAttributeValue(null, "path");
                            wrapper = new DownloadWrapper(new Download(uri, path), Long.valueOf(id));
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
                            wrapper.setBlocks(blockList);
                            wrapperList.add(wrapper);
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
        L.d("XmlPersistence readall time:" + (System.currentTimeMillis() - time));
        return wrapperList;
    }

    /**
     * Insert download record, the id will auto increse
     *
     * @param entity
     * @return
     */
    @Override
    public long insert(DownloadWrapper entity) {
        long time = System.currentTimeMillis();

        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = docFactory.newDocumentBuilder();
            Document document = builder.parse(xmlFile);
            Element rootElement = (Element) document.getElementsByTagName(ROOT_TAG).item(0);
            long maxId = Long.valueOf(rootElement.getAttribute(MAX_ID_TAG));
            entity.setId(++maxId);
//            L.d("root tag list size:" + document.getElementsByTagName(ROOT_TAG).getLength());
            Element downloadElement = convertEntityToElement(entity, document);
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
        L.d("XmlPersistence insert time:" + (System.currentTimeMillis() - time));
        return -1;
    }

    /**
     * Update entity by id, return -1 if not found
     *
     * @param entity
     * @return
     */
    @Override
    public long update(DownloadWrapper entity) {
        long time = System.currentTimeMillis();

        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        long res = -1;
        try {
            DocumentBuilder builder = docFactory.newDocumentBuilder();
            Document document = builder.parse(xmlFile);
            NodeList downloads = document.getElementsByTagName(ELEMENT_DOWNLOAD_TAG);
            L.d("downloads tag list size:" + downloads.getLength());
            for (int i = 0, size = downloads.getLength(); i < size; ++i) {
                Element downloadElement = (Element) downloads.item(i);
                long id = Long.valueOf(downloadElement.getAttribute("id"));
                if (id == entity.getId()) {
//                    L.d("id == entity.id:" + id);
                    res = id;
                    updateElement(entity, downloadElement);
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
        L.d("XmlPersistence update time:" + (System.currentTimeMillis() - time));
        return res;
    }

    /**
     * delete download record by id, return -1 if not found
     *
     * @param entity
     * @return
     */
    @Override
    public long delete(DownloadWrapper entity) {
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
                if (id == entity.getId()) {
//                    L.d("id == entity.id:" + id);
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
        L.d("XmlPersistence delete time:" + (System.currentTimeMillis() - time));
        return res;
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
