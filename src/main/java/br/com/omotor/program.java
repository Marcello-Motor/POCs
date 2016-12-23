package br.com.omotor;

import feign.*;
import javax.xml.namespace.QName;
import javax.xml.soap.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;

@Headers("Content-Type: application/xml")
interface BCClient {

    @RequestLine("POST /wssgs/services/FachadaWSSGS")
    @Headers("SOAPAction: 1")
    @Body("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\" ?>\n" +
            "  <SOAP-ENV:Envelope\n" +
            "   SOAP-ENV:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\"\n" +
            "   xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\"\n" +
            "   xmlns:SOAP-ENC=\"http://schemas.xmlsoap.org/soap/encoding/\"\n" +
            "   xmlns:xsi=\"http://www.w3.org/1999/XMLSchema-instance\"\n" +
            "   xmlns:xsd=\"http://www.w3.org/1999/XMLSchema\">\n" +
            "\t<SOAP-ENV:Body>\n" +
            "\t\t<ns1:getValor\n" +
            "\t\t xmlns:ns1=\"urn:MySoapServices\">\n" +
            "\t\t\t<in0 xsi:type=\"xsd:long\">{serieId}</in0>\n" +
            "\t\t\t<in1 xsi:type=\"xsd:string\">{date}</in1>\n" +
            "\t\t</ns1:getValor>\n" +
            "\t</SOAP-ENV:Body>\n" +
            "  </SOAP-ENV:Envelope>")
    String getSerieValue(@Param("serieId") int serieId, @Param("date") String date);

    @RequestLine("POST /wssgs/services/FachadaWSSGS")
    @Headers("SOAPAction: 1")
    String getSerieValue2(String content);
}

public class program {
    public static void main(String... args) {

        BCClient bcclient = Feign.builder().target(BCClient.class, "https://www3.bcb.gov.br");

        //String result = bcclient.getSerieValue(1, "20/12/2016");
        //System.out.println(result);

        String postRequest = createSOAPRequest("1", "20/12/2016");
        String postResponse = bcclient.getSerieValue2(postRequest);
        String valor = parseSOAPResponse(postResponse);
        System.out.println(valor);
    }

    private static String createSOAPRequest(String serieId, String date) {
        String postBody = "";
        try {
            MessageFactory messageFactory = MessageFactory.newInstance();
            SOAPFactory soapFactory = SOAPFactory.newInstance();

            SOAPMessage soapMessage = messageFactory.createMessage();

            SOAPEnvelope soapEnvelope = soapMessage.getSOAPPart().getEnvelope();

            SOAPBody soapBody = soapEnvelope.getBody();

            Name bodyName  = soapFactory.createName("getValor","ns1","urn:MySoapServices");
            SOAPBodyElement bodyElement = soapBody.addBodyElement(bodyName);

            Name childName = soapFactory.createName("in0");
            SOAPElement in0 = bodyElement.addChildElement(childName);
            in0.addTextNode(serieId);

            childName = soapFactory.createName("in1");
            SOAPElement in1 = bodyElement.addChildElement(childName);
            in1.addTextNode(date);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            soapMessage.writeTo(stream);

            postBody = new String(stream.toByteArray(), "utf-8");

        } catch (Exception e) {
            e.printStackTrace();
        }

        return postBody;
    }

    private static String parseSOAPResponse(String msg) {
        String valor = "";
        try {
            MessageFactory messageFactory = MessageFactory.newInstance();

            ByteArrayInputStream input = new ByteArrayInputStream(msg.getBytes());

            SOAPMessage soapMessage = messageFactory.createMessage(new MimeHeaders(), input);
            SOAPBody soapBody = soapMessage.getSOAPBody();
            java.util.Iterator iterator = soapBody.getChildElements(new QName("multiRef"));
            SOAPBodyElement bodyElement = (SOAPBodyElement)iterator.next();
            valor = bodyElement.getValue();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return valor;
    }
}
