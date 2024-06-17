package org.nterlearning.commerce.service;

import javax.ws.rs.*;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.nterlearning.xml.commerce.transaction_interface_0_1_0.*;
import org.nterlearning.xml.commerce.transaction_interface_0_1_0_wsdl.ValidationError;

import java.math.BigDecimal;


public interface TransactionRestInterface {

    @GET
    @Path("/{authorizedUser}/{transactionId}/")
    @Produces("application/xml")
    GetTransactionByIdResponse getTransactionById(
            @PathParam("authorizedUser") String authorizedUser,
            @PathParam("transactionId") String transactionId
    );

    @POST
	@Path("/notification")
    @Produces("application/x-www-form-urlencoded")
	@Consumes({"application/x-www-form-urlencoded"})
    Response notification(MultivaluedMap<String, String> map);

    @GET
    @Path("/paymentStatus/{institution}/{studentId}/{courseId}/{price}/")
    @Produces("application/xml")
    GetPaymentStatusResponse getPaymentStatus(
            @PathParam("institution") String institution,
            @PathParam("studentId") String studentId,
            @PathParam("courseId") String courseId,
            @PathParam("price") BigDecimal price
    ) throws ValidationError;


}
