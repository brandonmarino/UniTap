package com.unitap.unitap.DataControl;

import com.unitap.unitap.Exceptions.InheritedExceptions.EncryptionException;

import org.simpleframework.xml.*;
import org.simpleframework.xml.core.Persister;

import java.io.StringReader;
import java.io.StringWriter;

/**
 * Created by Brandon Marino on 10/2/2015.
 */
public abstract class ExtensibleMarkupLanguage {

    /**
     * Collect an object's information and store it in an XML string
     * @param object some java object which has the @ <- natoations added
     * @param <T> The generic object which is being xml'd
     * @return the string of xml
     */
    public static <T> String marshal(T object) throws EncryptionException
    {
        Serializer serializer = new Persister();
        StringWriter sw = new StringWriter();
        try {
            serializer.write(object, sw);
            return sw.toString();
        }catch(Exception e){
            throw new EncryptionException("Could not marshal object");
        }
    }

    /**
     * This marshaller is a little bit volatile.
     * the typeParameter in the constructor is the main issue.
     * @param xml some string of xml
     * @param typeParameter This main root Class type which is getting Marshalled.  Can be found with object.getClass()
     * @return Some Generic type which has been generated from the XML string
     */
    public static <T> T unMarshal(String xml, Class<T> typeParameter) throws EncryptionException
    {
        Serializer serializer = new Persister();
        StringReader sr = new StringReader(xml);
        try {
            T output = typeParameter.newInstance();
            serializer.read(output, sr);
            return output;
        }catch(Exception e){
            throw new EncryptionException("Could not unmarshal file into an object");
        }
    }
}
