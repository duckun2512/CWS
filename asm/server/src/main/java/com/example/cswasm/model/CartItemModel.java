package com.example.cswasm.model;

import com.example.cswasm.entity.CartItem;
import com.example.cswasm.utils.ConnectionHelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CartItemModel {
    private Connection conn;

    public CartItemModel() {
        conn = ConnectionHelper.getConnection();
    }

    public CartItem findByProductIdAndCartId(int productId, int shoppingCartId) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("select * from cart_items where productId = ? and shoppingCartId = ?");
        stmt.setInt(1, productId);
        stmt.setInt(2, shoppingCartId);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            String name = rs.getString("productName");
            double price = rs.getInt("unitPrice");
            int quantity = rs.getInt("quantity");
            return new CartItem(shoppingCartId, productId, name, price, quantity);
        }

        return null;
    }
}