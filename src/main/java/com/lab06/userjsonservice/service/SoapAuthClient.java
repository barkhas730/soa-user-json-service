package com.lab06.userjsonservice.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Service;
import org.springframework.ws.client.core.WebServiceTemplate;

import com.lab06.userjsonservice.soap.GetUserIdByTokenRequest;
import com.lab06.userjsonservice.soap.GetUserIdByTokenResponse;
import com.lab06.userjsonservice.soap.ValidateTokenRequest;
import com.lab06.userjsonservice.soap.ValidateTokenResponse;

@Service
public class SoapAuthClient {

    private final WebServiceTemplate webServiceTemplate;

    public SoapAuthClient(Jaxb2Marshaller marshaller,
                          @Value("${soap.service.url:http://localhost:8081/ws}") String soapServiceUrl) {
        this.webServiceTemplate = new WebServiceTemplate(marshaller);
        this.webServiceTemplate.setDefaultUri(soapServiceUrl);
    }

    public boolean validateToken(String token) {
        ValidateTokenRequest request = new ValidateTokenRequest();
        request.setToken(token);
        ValidateTokenResponse response = (ValidateTokenResponse) webServiceTemplate.marshalSendAndReceive(request);
        return response.isValid();
    }

    public Long getUserIdByToken(String token) {
        GetUserIdByTokenRequest request = new GetUserIdByTokenRequest();
        request.setToken(token);
        GetUserIdByTokenResponse response = (GetUserIdByTokenResponse) webServiceTemplate.marshalSendAndReceive(request);

        if (response.isFound()) {
            return response.getUserId();
        }

        return null;
    }
}
