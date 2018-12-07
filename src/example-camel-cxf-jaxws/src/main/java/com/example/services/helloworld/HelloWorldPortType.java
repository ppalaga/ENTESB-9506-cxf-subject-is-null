
package com.example.services.helloworld;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.annotation.XmlSeeAlso;
import com.example.types.helloworld.Greeting;
import com.example.types.helloworld.ObjectFactory;
import com.example.types.helloworld.Person;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.1.3-b02-
 * Generated source version: 2.1
 *
 */
@WebService(targetNamespace = "http://example.com/services/helloworld")
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
@XmlSeeAlso({
    ObjectFactory.class
})
public interface HelloWorldPortType {


    /**
     *
     * @param person
     * @return
     *     returns com.example.types.helloworld.Greeting
     * @throws FatalError
     */
    @WebMethod(action = "http://example.com/services/helloworld/sayHello")
    @WebResult(name = "greeting", targetNamespace = "http://example.com/types/helloworld", partName = "greeting")
    public Greeting sayHello(
        @WebParam(name = "person", targetNamespace = "http://example.com/types/helloworld", partName = "person")
        Person person)
        throws FatalError
    ;

}