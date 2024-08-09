package br.com.infratec.util;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import lombok.SneakyThrows;

import javax.xml.namespace.QName;
import java.io.StringReader;
import java.io.StringWriter;

public class XmlUtils {

    private XmlUtils() {
    }

    @SneakyThrows
    public static String xmlToString(final Object source, final Class<?> type) {
        final var jaxbContext = JAXBContext.newInstance(type);
        final var marshaller = jaxbContext.createMarshaller();
        final var sw = new StringWriter();
        marshaller.marshal(source, sw);
        String xml = sw.toString();
        return replaces(xml);
    }

    @SneakyThrows
    public static <T> T stringToXml(final String source, final Class<T> type) {
        final var jaxbContext = JAXBContext.newInstance(type);
        final var unmarshaller = jaxbContext.createUnmarshaller();
        return (T) unmarshaller.unmarshal(new StringReader(source));
    }

    @SneakyThrows
    public static <T> String xmlToString(final T source, final Class<T> type, final String localPart) {
        final var jaxbContext = JAXBContext.newInstance(type);
        final var jaxbMarshaller = jaxbContext.createMarshaller();
        final var jaxbElement = new JAXBElement<>(new QName("", localPart), type, source);
        final var sw = new StringWriter();
        jaxbMarshaller.marshal(jaxbElement, sw);
        return sw.toString();
    }

    public static String replaces(String xml) {

        return xml.replace("<!\\[CDATA\\[<!\\[CDATA\\[", "<!\\[CDATA\\[")
                .replace("\\]\\]>\\]\\]>", "\\]\\]>")
                .replace("ns2:", "")
                .replace("ns3:", "")
                .replace("&lt;", "<")
                .replace("&gt;", ">")
                .replace("<Signature>", "<Signature xmlns=\"http://www.w3.org/2000/09/xmldsig#\">")
                .replace(" xmlns:ns2=\"http://www.w3.org/2000/09/xmldsig#\"", "")
                .replace(" xmlns=\"\" xmlns:ns3=\"http://www.portalfiscal.inf.br/nfe\"", "")
                .replace("<NFe>", "<NFe xmlns=\"http://www.portalfiscal.inf.br/nfe\">");

    }

}
