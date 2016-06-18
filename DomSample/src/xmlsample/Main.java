package xmlsample;

public class Main {

  public static void main(String[] args) throws Exception {
    new DomTester("test.xml", "test.xsd").process();
  }
}
