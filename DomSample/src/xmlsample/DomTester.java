package xmlsample;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

public class DomTester {
    private String fname;
    private String schemaName;
    
    public DomTester(String fname, String schemaName)   {
        this.fname = fname;
        this.schemaName = schemaName;        
    }
    
    
    Document readXmlToDOMDocument() throws SAXException {
         DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        //  используется шаблон проектирования "Абстрактная фабрика"
        //  DocumentBuilderFactory.newInstance() порождает объект класса-наследникв
        //  от абстрактного класса DocumentBuilderFactory
        //  Объект какого типа будет порожден, зависит от конфигурации JDK
        //  Сейчас используется парсер Xerces и класс
        //     com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl
        System.out.println("DocumentBuilderFactory dbf = "+dbf);

        dbf.setNamespaceAware(false); // не поддерживать пространства имен
        // dbf.setValidating(true) - DTD проверка
       //dbf.setCoalescing(true) - склеивать CDATA с ближайшим текстовым блоком
        // dbf.setSchema( );// XML-схема или альтернативные вариантыи
        dbf.setIgnoringElementContentWhitespace(true);
        // XML -поток - источник данных схемы
        Source schemaSrc = new StreamSource(schemaName);

        // Фабрика - генератор схем
        SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        
        // читаем схему из файла
        Schema scheme = sf.newSchema(schemaSrc);
       // Schema scheme = sf.newSchema(new File("test.xsd")); // тоже годится
        dbf.setSchema(scheme);
        //dbf.setValidating(true);
        
        DocumentBuilder db=null;
        try {
            db = dbf.newDocumentBuilder();
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(DomTester.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        try {
            // собственно читаем документ - ВЕСЬ СРАЗУ В ПАМЯТЬ!
            return db.parse(fname);
        } catch (IOException ex) {
            Logger.getLogger(DomTester.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public void process()  {
        try {
            // собственно читаем документ - ВЕСЬ СРАЗУ В ПАМЯТЬ!
            System.out.println("Считываемем документ =====================================");
            Document doc = readXmlToDOMDocument();
            System.out.println("Изменяем документ =====================================");
            changeDocumentDemo(doc); 
            System.out.println("Рекурсивно просматриваем документ =====================================");
            viewDocument(doc);
            System.out.println("Сохраняем и печатаем документ =====================================");
            saveDemo(doc);
            System.out.println("Тестируем XPath-выражения =====================================");
            xpathDemo(doc);
            
        } catch (Exception ex) {
            Logger.getLogger(DomTester.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }
    
    private void changeDocumentDemo(Document doc) throws DOMException, NumberFormatException {
        Node n = doc.getDocumentElement();
        Element e = (Element) n; // корень - это Элемент, а не какой-нибудь там комментарий
        Element i1= (Element)e.getElementsByTagName("group").item(0);  // 1-я группа
        System.out.println("i1="+i1.getTextContent());
        Element i2= (Element) i1.getElementsByTagName("item").item(1); //2-й по счёту вложенный элемент "item" в 1-й группе
        System.out.println("i2="+i2.getTextContent());
        System.out.println("i2 childs: "+i2.getChildNodes().item(0).getNodeName()
                +" and "+i2.getChildNodes().item(1).getNodeName());
        System.out.println("i2 childs: "+i2.getChildNodes().item(0).getTextContent()
                +" and "+i2.getChildNodes().item(1).getTextContent());
        
        
        Text txt = doc.createTextNode("ZZZZZZ");
        n.appendChild(txt); // добавим в конец документа текст ZZZZZZ
        
        int price = Integer.parseInt(i2.getAttribute("price"));
        System.out.println("price was "+    price); 
        if (price < 50 ) {
            i2.setAttribute("price", String.valueOf(  price+1 )); // увеличим цену на 1
            
            Node in = i2.getElementsByTagName("material").item(0); //<material> ... 
            in.setTextContent(in.getTextContent()+"+");  // Сахар+
            
        }
        System.out.println("now price is "+ i2.getAttribute("price")); // 8
    }

    private void saveDemo(Document doc) throws IOException {
            
        Result sr = new StreamResult(new FileWriter("out.xml"));
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


        tr.transform(domSource, sr ); // в файл
        tr.transform(domSource, sr2 ); // на экран
        } catch (TransformerException ex) {
            Logger.getLogger(DomTester.class.getName()).log(Level.SEVERE, null, ex);
        }
       // tr.transform(schemaSrc, sr2 ); // а теперь схему на экран
    }

    private void viewDocument(Document doc) {
        Node n = doc.getDocumentElement();
        lookAttrs(n);            lookChildren(n,0);
    }

    private void lookChildren(Node n, int level) {
        NodeList nl=n.getChildNodes();
        String space = "                                         ".substring(0,level);
        int num = nl.getLength();
        Node curNode;
        for (int i=0;i<num;i++) {

            curNode = nl.item(i);

            // curNode.getNodeValue() -- значение, существует для типов
            // ATTRIBUTE, COMMENT, CDATA, TEXT

            // curNode.getNodeName() -- имя тега для ELEMENT
            // "#text" - для TEXT, "#cdata-section", "#comment","#document"
            // "#document-fragment"

            //System.out.printf("%s---%s\n",curNode.getNodeName(),curNode.getNodeValue());

            // в зависимости от типа элемента...
            switch(curNode.getNodeType()) {
                case Node.ELEMENT_NODE:
                    ((Element)curNode).setAttribute("a", "777"); // заменяем атрибут
                    System.out.printf(space+"ELEMENT: <%s>\n",curNode.getNodeName());
                    // читаем атрибуты
                    lookAttrs(curNode);
                    // рекурсивно просматриемм вложенные узлы
                    lookChildren(curNode, level+5);
                    break;
                case Node.COMMENT_NODE:
                    // печатаем комментарий
                    System.out.println(space+"COMMENT: "+curNode.getNodeValue());
                    break;
                case Node.TEXT_NODE:
                    // печатаем текст, если он не только из пробелов и переводов строк
                    String txt=curNode.getNodeValue().trim();
                    if (txt.length()>0) System.out.println(space+"TEXT: "+txt);
                    else System.out.println(space+" EMPTY TEXT");
                    break;
            }
        }
    }

    private void lookAttrs(Node n) {
        NamedNodeMap attrs = n.getAttributes();
        if (attrs==null) return;
        
        int num = attrs.getLength();
        if (num==0) return;

        Node curNode;
        System.out.print("[");
        for (int i=0;i<num;i++) {
            curNode = attrs.item(i);
            System.out.printf("%s=%s ",curNode.getNodeName(),curNode.getNodeValue());
        }
        System.out.printf("]\n");
    }

    private void xpathDemo(Document doc) throws DOMException, XPathExpressionException {
        XPath xpath = XPathFactory.newInstance().newXPath();
        System.out.println("============= XPATH demo =======================");
        // XPath Query for showing all nodes value
        XPathExpression expr = xpath.compile("//group/item[@price>2]/name/text()");
       // XPathExpression expr = xpath.compile("//group/item/name/text()");
        Object result = expr.evaluate(doc, XPathConstants.NODESET);
        NodeList nodes = (NodeList) result;
        for (int i = 0; i < nodes.getLength(); i++) {
            System.out.println(nodes.item(i).getNodeValue()); 
        }   
    }


}
