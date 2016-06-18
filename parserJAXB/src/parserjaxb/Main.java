package parserjaxb;

import generated.ItemType;
import generated.PriceType;
import java.io.File;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

/**
 *
 * @author ilmir
 */
public class Main {

  PriceType price;


    void unmarshal(String name) throws JAXBException {
        JAXBContext jxc = JAXBContext.newInstance("generated");
        Unmarshaller u = jxc.createUnmarshaller();
        JAXBElement je = (JAXBElement) u.unmarshal(new File(name));
        
        price=(PriceType) je.getValue();
        System.out.println(je.getValue());
    }

    void marshal(OutputStream os) {
            Marshaller m;
        try {
            JAXBContext jxc = JAXBContext.newInstance(PriceType.class);
            m = jxc.createMarshaller();
            m.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, true );
            m.marshal(price, os);
            //m.marshal(price, domNode);
        } catch (JAXBException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
    }

    public static void main(String[] args) {
        Main m=new Main();
        try {
            if (true) {
            m.unmarshal("test.xml");
            System.out.println(m.price.getItem().get(0).getName());
            System.out.println(m.price.getItem().get(0).getClass());
            System.out.println("-----");
            m.marshalToConsole();
            System.out.println("-----");
            m.price.getItem().get(2).getNabor().remove(1);
            m.marshalToConsole(); //removed "3"
            } else {
                m.price=new PriceType();
            }
            ItemType itm = new ItemType();
            itm.setMaterial("Гречка");
            itm.setPrice(120);
            //itm.setName("Крупа");
            
            m.price.getItem().add(0,itm);
            m.marshalToConsole();
        } catch (Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void marshalToConsole() {
        marshal(System.out);
    }
  
}
