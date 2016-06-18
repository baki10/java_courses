package com.bakigoal.parser;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author ilmir
 */
public class DomParser {

  private final String fileName;
  private final String schemaName;

  public DomParser(String fileName, String schemaName) {
    this.fileName = fileName;
    this.schemaName = schemaName;
  }

  public void process() {
    try {
      // собственно читаем документ - ВЕСЬ СРАЗУ В ПАМЯТЬ!
      System.out.println("Считываемем документ =====================================");
      Document doc = readXmlToDOMDocument();
      System.out.println("Изменяем документ =====================================");
      changeDocument(doc);
      System.out.println("Сохраняем и печатаем документ =====================================");
      saveAndPrintDoc(doc);

    } catch (Exception ex) {
      Logger.getLogger(DomParser.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  private Document readXmlToDOMDocument() throws SAXException {
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    //  используется шаблон проектирования "Абстрактная фабрика"
    //  DocumentBuilderFactory.newInstance() порождает объект класса-наследникв
    //  от абстрактного класса DocumentBuilderFactory
    //  Объект какого типа будет порожден, зависит от конфигурации JDK
    //  Сейчас используется парсер Xerces и класс
    //     com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl
    System.out.println("DocumentBuilderFactory dbf = " + dbf);
    dbf.setNamespaceAware(false); // не поддерживать пространства имен
    // dbf.setValidating(true) - DTD проверка
    //dbf.setCoalescing(true) - склеивать CDATA с ближайшим текстовым блоком
    dbf.setIgnoringElementContentWhitespace(true);
    if (schemaName != null) {
      // XML -поток - источник данных схемы
      Source schemaSrc = new StreamSource(schemaName);
      // Фабрика - генератор схем
      SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
      // читаем схему из файла
      Schema scheme = sf.newSchema(schemaSrc);
      // Schema scheme = sf.newSchema(new File("test.xsd")); // тоже годится
      dbf.setSchema(scheme);
    }
    //dbf.setValidating(true);
    DocumentBuilder db;
    try {
      db = dbf.newDocumentBuilder();
      // собственно читаем документ - ВЕСЬ СРАЗУ В ПАМЯТЬ!
      return db.parse(fileName);
    } catch (ParserConfigurationException | IOException ex) {
      Logger.getLogger(DomParser.class.getName()).log(Level.SEVERE, null, ex);
      return null;
    }
  }

  private void saveAndPrintDoc(Document doc) throws IOException {

    Result sr = new StreamResult(new FileWriter("out.svg"));
    Result sr2 = new StreamResult(System.out);
    DOMSource domSource = new DOMSource(doc);

    //  DOMSource domSource = new DOMSource(i2);
    // в newTransformer() можно было бы передать xslt - преобразование
    Transformer tr;
    try {
      tr = TransformerFactory.newInstance().newTransformer();

      // настройки "преобразования"
      tr.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
      tr.setOutputProperty(OutputKeys.INDENT, "yes"); // отступы

      tr.transform(domSource, sr); // в файл
      tr.transform(domSource, sr2); // на экран
    } catch (TransformerException ex) {
      Logger.getLogger(DomParser.class.getName()).log(Level.SEVERE, null, ex);
    }
    // tr.transform(schemaSrc, sr2 ); // а теперь схему на экран
  }

  private void changeDocument(Document doc) throws DOMException, NumberFormatException {
    Node n = doc.getDocumentElement();
    Element e = (Element) n; // корень - это Элемент, а не какой-нибудь там комментарий
    NodeList rects = e.getElementsByTagName("rect");
    System.out.println("rects=" + rects.getLength());
    NodeList paths = e.getElementsByTagName("path");
    System.out.println("paths=" + paths.getLength());
    NodeList circles = e.getElementsByTagName("circle");
    System.out.println("circles=" + circles.getLength());
    NodeList lines = e.getElementsByTagName("line");
    System.out.println("lines=" + lines.getLength());

    addRedDot(circles);
    updateNodesAttribute(rects, "style", "fill: white");
    updateNodesAttribute(paths, "style", "fill: red; fill-rule: evenodd; stroke: #555; "
            + "opacity: 0; stroke-width: 1px; stroke-linecap: butt; "
            + "stroke-linejoin: miter; stroke-opacity: 1;");
  }

  private void addRedDot(NodeList circles) {
    Map<Node, Node> map = new HashMap<>();
    for (int i = 0; i < circles.getLength(); i++) {
      Node circle = circles.item(i);
      Node oldCircle = circle.cloneNode(false);
      map.put(circle, oldCircle);

      updateOneNodeAttribute(circle, "r", "5");
      updateOneNodeAttribute(circle, "fill", "red");
    }
    for (Node circle : map.keySet()) {
      circle.getParentNode().insertBefore(map.get(circle), circle);
    }
  }

  private void updateNodesAttribute(NodeList rects, String attr, String value) {
    for (int i = 0; i < rects.getLength(); i++) {
      Node node = rects.item(i);
      updateOneNodeAttribute(node, attr, value);
    }
  }

  private void updateOneNodeAttribute(Node node, String attr, String value) {
    NamedNodeMap attributes = node.getAttributes();
    Node attrNode = attributes.getNamedItem(attr);
    if (attrNode != null) {
      attrNode.setNodeValue(value);
    } else {
      Document doc = node.getOwnerDocument();
      Attr newAttr = doc.createAttribute(attr);
      newAttr.setValue(value);
      attributes.setNamedItem(newAttr);
    }
  }
}
