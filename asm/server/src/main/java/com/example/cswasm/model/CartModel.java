package com.example.cswasm.model;

import com.example.cswasm.entity.Cart;

import java.sql.SQLException;

public interface CartModel {
    Cart get(int userId) throws SQLException;
    Cart create(int userId) throws SQLException;
    Cart save(Cart cart) throws SQLException;
    boolean remove(int id) throws SQLException;
    boolean clear(int id) throws SQLException;
    boolean CheckCartExisting(Cart cart) throws SQLException;
}
