package com.mricefox.mfdownloader.lib;

import android.util.Xml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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
    private final static String ELEMENT_DOWNLOAD_TAG = "download";
    private final static String ELEMENT_BLOCK_TAG = "block";
    private static XmlPersistence instance;
    private static File file;

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
        file = new File(dir, XML_FILE_NAME);
        if (!file.exists() && !file.createNewFile())
            throw new IOException("create persistence file fail");
        //exists or create success
        createRoot();
        L.d("XmlPersistence init time:" + (System.currentTimeMillis() - time));
    }

    private void createRoot() {
        XmlSerializer serializer = Xml.newSerializer();
        try {
            serializer.setOutput(new FileOutputStream(file), "UTF-8");
            serializer.startDocument("UTF-8", true);
            serializer.startTag(null, ROOT_TAG);

//            serializer.startTag(null, "message");
//            serializer.attribute(null,"id","1");
//            serializer.endTag(null, "message");

            serializer.endTag(null, ROOT_TAG);
            serializer.endDocument();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Element convertEntityToElement(DownloadWrapper wrapper, Document document) {
        Element element = document.createElement(ELEMENT_DOWNLOAD_TAG);
        element.setAttribute("id", String.valueOf(wrapper.id));
        element.setAttribute("uri", wrapper.download.getUri());
        element.setAttribute("path", wrapper.download.getTargetFilePath());
        for (int i = 0, size = wrapper.blocks.size(); i < size; ++i) {
            Block block = wrapper.blocks.get(i);
            Element blockElement = document.createElement(ELEMENT_BLOCK_TAG);
            blockElement.setAttribute("index", String.valueOf(block.index));
            blockElement.setAttribute("startPos", String.valueOf(block.startPos));
            blockElement.setAttribute("endPos", String.valueOf(block.endPos));
            blockElement.setAttribute("downloadedBytes", String.valueOf(block.downloadedBytes));
            element.appendChild(blockElement);
        }
        return element;
    }

    @Override
    public List readAll() {
        return null;
    }

    @Override
    public boolean insert(DownloadWrapper entity) {
        long time = System.currentTimeMillis();

        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = docFactory.newDocumentBuilder();
            Document document = builder.parse(file);
            Node rootNode = document.getElementsByTagName(ROOT_TAG).item(0);
            Element downloadElement = convertEntityToElement(entity, document);
            rootNode.appendChild(downloadElement);
            writeBack(document, file);
            return true;
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

        return false;
    }

    @Override
    public long update(DownloadWrapper entity) {
        return 0;
    }

    private void writeBack(Document document, File file) throws TransformerException {
        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer transformer = factory.newTransformer();
        DOMSource source = new DOMSource(document);
        StreamResult result = new StreamResult(file);
        transformer.transform(source, result);
    }
}
