package br.com.infratec.util;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dsig.*;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.text.Normalizer;
import java.util.List;

public class XmlSignator {

    private static final String C14N_URL = "http://www.w3.org/TR/2001/REC-xml-c14n-20010315";

    private final XMLSignatureFactory xmlSignatureFactory;
    private final DigestMethod digestMethod;
    private final List<Transform> transforms;
    private final CanonicalizationMethod canonicalizationMethod;
    private final SignatureMethod signatureMethod;
    private final KeyInfo keyInfo;
    private final KeyStore.PrivateKeyEntry privateKeyEntry;

    private XmlSignator(final XMLSignatureFactory xmlSignatureFactory, final DigestMethod digestMethod, final List<Transform> transforms, final CanonicalizationMethod canonicalizationMethod, final SignatureMethod signatureMethod, final KeyInfo keyInfo, final KeyStore.PrivateKeyEntry privateKeyEntry) {
        this.xmlSignatureFactory = xmlSignatureFactory;
        this.digestMethod = digestMethod;
        this.transforms = transforms;
        this.canonicalizationMethod = canonicalizationMethod;
        this.signatureMethod = signatureMethod;
        this.keyInfo = keyInfo;
        this.privateKeyEntry = privateKeyEntry;
    }

    public static XmlSignator newInstance(final KeyStore.PrivateKeyEntry privateKeyEntry) throws InvalidAlgorithmParameterException, NoSuchAlgorithmException {
        final var xmlSignatureFactory = XMLSignatureFactory.getInstance();
        final var digestMethod = xmlSignatureFactory.newDigestMethod(DigestMethod.SHA1, null);
        final var transforms = createTransforms(xmlSignatureFactory);
        final var canonicalizationMethod = xmlSignatureFactory.newCanonicalizationMethod(CanonicalizationMethod.INCLUSIVE, (C14NMethodParameterSpec) null);
        final var signatureMethod = xmlSignatureFactory.newSignatureMethod(SignatureMethod.RSA_SHA1, null);
        final var keyInfo = createKeyInfo(xmlSignatureFactory, privateKeyEntry);

        return new XmlSignator(xmlSignatureFactory, digestMethod, transforms, canonicalizationMethod, signatureMethod, keyInfo, privateKeyEntry);
    }

    private static KeyInfo createKeyInfo(final XMLSignatureFactory xmlSignatureFactory, final KeyStore.PrivateKeyEntry privateKeyEntry) {
        final var x509Certificate = (X509Certificate) privateKeyEntry.getCertificate();
        final var keyInfoFactory = xmlSignatureFactory.getKeyInfoFactory();
        final var x509Data = keyInfoFactory.newX509Data(List.of(x509Certificate));
        return keyInfoFactory.newKeyInfo(List.of(x509Data));
    }

    private static List<Transform> createTransforms(final XMLSignatureFactory xmlSignatureFactory) throws InvalidAlgorithmParameterException, NoSuchAlgorithmException {
        final var envelopedTransform = xmlSignatureFactory.newTransform(Transform.ENVELOPED, (TransformParameterSpec) null);
        final var c14NTransform = xmlSignatureFactory.newTransform(C14N_URL, (TransformParameterSpec) null);

        return List.of(envelopedTransform, c14NTransform);
    }

    public String sign(final String xmlLiteral, final String... signableTags) throws IOException, SAXException, ParserConfigurationException, TransformerException, MarshalException, XMLSignatureException {

        final var xmlDocument = xmlLiteralAsDocument(normalizeXmlLiteral(xmlLiteral));

        for (final String signableTag : signableTags) {
            final var elements = xmlDocument.getElementsByTagName(signableTag);
            for (int i = 0; i < elements.getLength(); i++) {
                final var element = (Element) elements.item(i);
                element.setIdAttribute("Id", true);

                final var referenceUri = "#" + element.getAttribute("Id");

                final var reference = xmlSignatureFactory.newReference(referenceUri, digestMethod, transforms, null, null);
                final var signedInfo = xmlSignatureFactory.newSignedInfo(canonicalizationMethod, signatureMethod, List.of(reference));

                final var xmlSignature = xmlSignatureFactory.newXMLSignature(signedInfo, keyInfo);
                final var parentNode = element.getParentNode();
                if (parentNode.isEqualNode(xmlDocument.getDocumentElement())) {
                    xmlSignature.sign(new DOMSignContext(privateKeyEntry.getPrivateKey(), parentNode));
                } else {
                    xmlSignature.sign(new DOMSignContext(privateKeyEntry.getPrivateKey(), element));
                }
                normalizeSignature(xmlDocument);
            }
        }

        return documentAsXmlLiteral(xmlDocument);
    }

    private Document xmlLiteralAsDocument(final String xmlLiteral) throws ParserConfigurationException, IOException, SAXException {
        final var factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        return factory.newDocumentBuilder()
                .parse(new ByteArrayInputStream(xmlLiteral.getBytes()));
    }

    private String documentAsXmlLiteral(final Document xmlDocument) throws TransformerException {
        final var byteArrayOutputStream = new ByteArrayOutputStream();
        final var transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        transformer.transform(new DOMSource(xmlDocument), new StreamResult(byteArrayOutputStream));
        return byteArrayOutputStream.toString();
    }

    // TODO remove
    private void normalizeSignature(final Document xmlDocument) {
        final var signatureValues = xmlDocument.getElementsByTagName("SignatureValue");
        for (int i = 0; i < signatureValues.getLength(); i++) {
            final var element = (Element) signatureValues.item(i);
            element.setTextContent(element.getTextContent().replaceAll("\r\n", ""));
        }
        final var x509Certificates = xmlDocument.getElementsByTagName("X509Certificate");
        for (int i = 0; i < x509Certificates.getLength(); i++) {
            final var element = (Element) x509Certificates.item(i);
            element.setTextContent(element.getTextContent().replaceAll("\r\n", ""));
        }
    }

    public static String normalizeXmlLiteral(final String xmlLiteral) {
        final var charSequence = new StringBuilder(xmlLiteral == null ? "" : xmlLiteral);
        return Normalizer.normalize(charSequence, Normalizer.Form.NFKD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                .replaceAll("&amp;", "e");
    }
}
