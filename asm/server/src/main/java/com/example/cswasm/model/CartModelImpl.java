package com.example.cswasm.model;

import com.example.cswasm.entity.Cart;
import com.example.cswasm.entity.CartItem;
import com.example.cswasm.utils.ConnectionHelper;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CartModelImpl implements CartModel {
    private Connection conn;

    private CartItemModel cartItemModel;

    public CartModelImpl() {
        conn = ConnectionHelper.getConnection();
        cartItemModel = new CartItemModel();
    }

    public Cart create(int userId) throws SQLException {
        PreparedStatement stmtCart = conn.prepareStatement("insert into shopping_carts (userId, shipName, shipAddress, shipPhone, totalPrice) values (?, '', '', '', 0)", Statement.RETURN_GENERATED_KEYS);
        stmtCart.setInt(1, userId);
        int affectedRows = stmtCart.executeUpdate();
        Cart Cart = new Cart();
        if (affectedRows > 0) {
            ResultSet resultSetGeneratedKeys = stmtCart.getGeneratedKeys();
            if (resultSetGeneratedKeys.next()) {
                int id = resultSetGeneratedKeys.getInt(1);
                Cart.setId(id);
                Cart.setUserId(userId);
            }
        }

        return Cart;
    }

    @Override
    public Cart get(int userId) throws SQLException {
        conn.setAutoCommit(false);
        try {
            PreparedStatement stmtCart = conn.prepareStatement("select * from shopping_carts where userId = ?", Statement.RETURN_GENERATED_KEYS);
            stmtCart.setInt(1, userId);
            ResultSet resultSet = stmtCart.executeQuery();
            if (resultSet.next()) {
                Cart Cart = new Cart();
                int CartId = resultSet.getInt("id");
                Cart.setId(CartId);
                Cart.setUserId(userId);
                Cart.setShipName(resultSet.getString("shipName"));
                Cart.setShipAddress(resultSet.getString("shipAddress"));
                Cart.setShipPhone(resultSet.getString("shipPhone"));
                Cart.setTotalPrice(resultSet.getDouble("totalPrice"));
                List<CartItem> cartItems = new ArrayList<>();
                try {
                    PreparedStatement stmtCartItems = conn.prepareStatement("select * from cart_items where CartId = ?", Statement.RETURN_GENERATED_KEYS);
                    stmtCartItems.setInt(1, CartId);
                    ResultSet resultSetCartItems = stmtCartItems.executeQuery();
                    while (resultSetCartItems.next()) {
                        CartItem cartItem = new CartItem();
                        cartItem.setShoppingCartId(CartId);
                        cartItem.setProductId(resultSetCartItems.getInt("productId"));
                        cartItem.setProductName(resultSetCartItems.getString("productName"));
                        cartItem.setUnitPrice(resultSetCartItems.getInt("unitPrice"));
                        cartItem.setQuantity(resultSetCartItems.getInt("quantity"));
                        cartItems.add(cartItem);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                Cart.setCartItems(cartItems);
                return Cart;
            }

            return null;
        } catch (Exception ex) {
            ex.printStackTrace();
            conn.rollback();
        } finally {
            conn.setAutoCommit(true);
        }
        return null;
    }

    public Cart save(Cart Cart) throws SQLException {
        conn.setAutoCommit(false);// begin transaction
        try {
            // trường hợp shopping cart null hoặc không có sản phẩm.
            if (Cart == null || Cart.getCartItems().size() == 0) {
                throw new Error("Shopping's null or empty.");
            }
            //Kiểm tra xem cart đã tồn tại hay chưa
            if (Cart.getId() == 0) {
                PreparedStatement stmtCart = conn.prepareStatement("insert into shopping_carts (userId, shipName, shipAddress, shipPhone, totalPrice) values (?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
                stmtCart.setInt(1, Cart.getUserId());
                stmtCart.setString(2, Cart.getShipName());
                stmtCart.setString(3, Cart.getShipAddress());
                stmtCart.setString(4, Cart.getShipPhone());
                stmtCart.setDouble(5, Cart.getTotalPrice());
                int affectedRows = stmtCart.executeUpdate();
                if (affectedRows > 0) {
                    ResultSet resultSetGeneratedKeys = stmtCart.getGeneratedKeys();
                    if (resultSetGeneratedKeys.next()) {
                        int id = resultSetGeneratedKeys.getInt(1);
                        Cart.setId(id);
                    }
                }
                if (Cart.getId() == 0) {
                    throw new Error("Can't insert shopping cart.");
                }
            }

            double totalPrice = 0;
            boolean isDeleted = false;
            List<CartItem> cartItemsAfterDelete = new ArrayList<>();
            CartItem itemToDelete = new CartItem();
            // update, insert and deduct cart items;
            for (CartItem item : Cart.getCartItems()) {
                item.setShoppingCartId(Cart.getId());
                CartItem cartItem = cartItemModel.findByProductIdAndCartId(item.getProductId(), item.getShoppingCartId());
                if (cartItem == null) {
                    PreparedStatement stmtCartItem = conn.prepareStatement("insert into cart_items (CartId, productId, productName, unitPrice, quantity) values (?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
                    stmtCartItem.setInt(1, item.getShoppingCartId());
                    stmtCartItem.setInt(2, item.getProductId());
                    stmtCartItem.setString(3, item.getProductName());
                    stmtCartItem.setDouble(4, item.getUnitPrice());
                    stmtCartItem.setInt(5, item.getQuantity());
                    int affectedCartItemRows = stmtCartItem.executeUpdate();
                    totalPrice += item.getQuantity() * item.getUnitPrice();
                    if (affectedCartItemRows == 0) { // lỗi
                        throw new Error("Insert cart item fails.");
                    }
                } else if (item.getQuantity() == -1) {
                    remove(item.getProductId());
                    cartItemsAfterDelete = Cart.getCartItems();
                    itemToDelete = item;
                    isDeleted = true;
                    conn.setAutoCommit(false);
                    break;
                } else {
                    PreparedStatement stmtCartItem = conn.prepareStatement("update cart_items set quantity = ? where productId =  ?", Statement.RETURN_GENERATED_KEYS);
                    stmtCartItem.setInt(1, item.getQuantity());
                    stmtCartItem.setInt(2, item.getProductId());
                    int affectedCartItemRows = stmtCartItem.executeUpdate();
                    if (affectedCartItemRows == 0) { // lỗi
                        throw new Error("Insert cart item fails.");
                    }
                    totalPrice += item.getQuantity() * item.getUnitPrice();
                }
            }

            if (isDeleted) {
                cartItemsAfterDelete.remove(itemToDelete);
                Cart.setCartItems(cartItemsAfterDelete);
            }
            PreparedStatement stmtUpdateTotalPrice = conn.prepareStatement("update shopping_carts set totalPrice = ? where id = ?", Statement.RETURN_GENERATED_KEYS);
            stmtUpdateTotalPrice.setDouble(1, totalPrice);
            stmtUpdateTotalPrice.setInt(2, Cart.getId());
            stmtUpdateTotalPrice.executeUpdate();
            Cart.setTotalPrice(totalPrice);
            conn.commit(); // lưu tất cả vào db.
        } catch (Exception ex) {
            ex.printStackTrace();
            Cart = null;
            conn.rollback();
        } finally {
            conn.setAutoCommit(true); // trả trạng thái auto commit default.
        }
        return Cart;
    }

    public boolean remove(int productId) throws SQLException {
        conn.setAutoCommit(false);// begin transaction
        try {
            PreparedStatement stmtDeleteCartItem = conn.prepareStatement("delete from cart_items where productId = ?");
            stmtDeleteCartItem.setInt(1, productId);
            int affectedCartItemRows = stmtDeleteCartItem.executeUpdate();
            if (affectedCartItemRows <= 0) {
                return false;
            }
            conn.commit();
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            conn.rollback();
        } finally {
            conn.setAutoCommit(true); // trả trạng thái auto commit default.
        }
        return false;
    }

    public boolean clear(int CartId) throws SQLException {
        conn.setAutoCommit(false);// begin transaction
        try {
            PreparedStatement stmtDeleteCartItem = conn.prepareStatement("delete from cart_items where CartId = ?");
            stmtDeleteCartItem.setInt(1, CartId);
            int affectedCartItemRows = stmtDeleteCartItem.executeUpdate();
            if (affectedCartItemRows <= 0) {
                return false;
            }
            PreparedStatement stmtDelete = conn.prepareStatement("delete from shopping_carts where id = ?");
            stmtDelete.setInt(1, CartId);
            int affectedRows = stmtDelete.executeUpdate();
            if (affectedRows <= 0) {
                return false;
            }
            conn.commit();
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            conn.rollback();
        } finally {
            conn.setAutoCommit(true); // trả trạng thái auto commit default.
        }
        return false;
    }

    @Override
    public boolean CheckCartExisting(Cart cart) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("select * from shopping_carts where id = ?", Statement.RETURN_GENERATED_KEYS);
        stmt.setInt(1, cart.getId());
        ResultSet resultSet = stmt.executeQuery();
        if (resultSet.next()) {
            return true;
        }
        return false;
    }
}
