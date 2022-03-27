package com.example.cswasm.resource;

import javax.ws.rs.core.Response;

public interface CartResource {
    Response get(int userId);

    Response add(int userId, int productId, int quantity);

    Response update(int userId, int productId, int quantity);

    Response remove(int userId, int productId);

    Response clear(int userId);
}
