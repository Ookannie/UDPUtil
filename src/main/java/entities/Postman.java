/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entities;

import java.io.Serializable;
import java.util.HashMap;

/**
 *
 *
 */
public class Postman implements Serializable {

    private static final long serialVersionUID = 6942551579298299603L;
    HashMap<String, Object> paramsRequest;
    HashMap<String, Object> paramsResponse;

    public Postman() {
        paramsRequest = new HashMap<>();
        paramsResponse = new HashMap<>();
    }

    public HashMap<String, Object> getParams() {
        return paramsRequest;
    }

    public boolean addRequest(String key, Object obj) {
        if (paramsRequest != null) {
            paramsRequest.put(key, obj);
            return true;
        }
        return false;
    }

    public boolean addResponse(String key, Object obj) {
        if (paramsResponse != null) {
            paramsResponse.put(key, obj);
            return true;
        }
        return false;
    }

    public HashMap<String, Object> getResponse() {
        return paramsResponse;
    }

    public void setResponse(HashMap<String, Object> respuesta) {
        this.paramsResponse = respuesta;
    }

}
