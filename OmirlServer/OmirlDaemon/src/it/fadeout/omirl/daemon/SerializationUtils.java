package it.fadeout.omirl.daemon;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;


public class SerializationUtils {
    /**
     * This method saves (serializes) any java bean object into xml file
     */
    public static void serializeObjectToXML(String xmlFileLocation, Object objectToSerialize,boolean isGZIP) throws Exception {
        FileOutputStream os = new FileOutputStream(xmlFileLocation);
        OutputStream inStream;
        inStream = (isGZIP) ?  new GZIPOutputStream(os) :  os;
        XMLEncoder encoder = new XMLEncoder(inStream);
        encoder.writeObject(objectToSerialize);
        encoder.close();
    }
 
    /**
     * Reads Java Bean Object From XML File
     */
    public static Object deserializeXMLToObject(String xmlFileLocation,boolean isGZIP) throws Exception {
        FileInputStream os = new FileInputStream(xmlFileLocation);
        InputStream outStream; 
        outStream = (isGZIP) ?  new GZIPInputStream(os) :  os;
        XMLDecoder decoder = new XMLDecoder(outStream);
        Object deSerializedObject = decoder.readObject();
        decoder.close();
 
        return deSerializedObject;
    }
}
