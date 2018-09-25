package tmtrparser;

import javax.xml.stream.*;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;

public class StaxStreamProcessor implements AutoCloseable {
    private static final XMLInputFactory FACTORY = XMLInputFactory.newInstance();

    private final XMLStreamReader reader;

    public StaxStreamProcessor(InputStream is) throws XMLStreamException {
        reader = FACTORY.createXMLStreamReader(is);
    }
    public StaxStreamProcessor(String text) throws XMLStreamException {
        Reader strreader = new StringReader(text);
        //XMLInputFactory factory = XMLInputFactory.newInstance(); // Or newFactory()
        reader = FACTORY.createXMLStreamReader(strreader);
    }
    public XMLStreamReader getReader() {
        return reader;
    }


    @Override
    public void close() {
        if (reader != null) {
            try {
                reader.close();
            } catch (XMLStreamException e) { // empty
            }
        }
    }
    public boolean doUntil(int stopEvent, String value) throws XMLStreamException {
        while (reader.hasNext()) {
            int event = reader.next();

            if (event == stopEvent && value.equals(reader.getLocalName())) {
                 return true;
            }
        }
        return false;
    }
    public String getAttribute(String name) throws XMLStreamException {
        return reader.getAttributeValue(null, name);
    }
    public String getText() throws XMLStreamException {
        return reader.getElementText();

    }
    public String getAllText(){
        return reader.getText();
    }

}

