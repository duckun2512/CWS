package com.example.cswasm.resource;

import com.example.cswasm.entity.Cart;
import com.example.cswasm.entity.Product;
import com.example.cswasm.model.CartModel;
import com.example.cswasm.model.CartModelImpl;
import com.example.cswasm.model.ProductModel;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.SQLException;

@Path("/carts")
public class CartResourceImpl implements CartResource {
    private CartModel CartModel;
    private ProductModel productModel;

    public CartResourceImpl() {
        this.CartModel = new CartModelImpl();
        this.productModel = new ProductModel();
    }

    @GET
    @Path("/create")
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(@HeaderParam("Authorization") int userId) {
        try {
            Cart shoppingCart = this.CartModel.create(userId);
            return Response.status(Response.Status.OK).entity(shoppingCart).build();
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(new Cart()).build();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response get(@HeaderParam("Authorization") int userId) {
        try {
            Cart shoppingCart = this.CartModel.get(userId);
            return Response.status(Response.Status.OK).entity(shoppingCart).build();
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(new Cart()).build();
        }
    }

    @GET
    @Path("/add")
    @Produces(MediaType.APPLICATION_JSON)
    public Response add(@HeaderParam("Authorization") int userId, @QueryParam("productId") int productId, @QueryParam("quantity") int quantity) {
        // kiểm tra số lượng
        if (quantity <= 0) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        // kiểm tra sản phẩm.
        Product product = null;
        try {
            product = this.productModel.findById(productId);
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        if (product == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        Cart shoppingCart = null;
        try {
            // check shopping cart trong db theo id người dùng.
            shoppingCart = this.CartModel.get(userId);
        } catch (SQLException e) {
            e.printStackTrace();
            // trường hợp không có thì tạo mới.
        }
        if (shoppingCart == null) {
            shoppingCart = new Cart();
            shoppingCart.setUserId(userId);
        }
        // do something.
        shoppingCart.addition(product, quantity);
        try {
            shoppingCart = this.CartModel.save(shoppingCart);
        } catch (SQLException e) {
            e.printStackTrace();
            shoppingCart = null;
        }
        if (shoppingCart == null) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(new Cart()).build();
        }
        return Response.status(Response.Status.CREATED).entity(shoppingCart).build();
    }

    @GET
    @Path("/deduct")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deduct(@HeaderParam("Authorization") int userId, @QueryParam("productId") int productId, @QueryParam("quantity") int quantity) {
        // kiểm tra số lượng
        if (quantity <= 0) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        // kiểm tra sản phẩm.
        Product product = null;
        try {
            product = this.productModel.findById(productId);
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        if (product == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        Cart shoppingCart = null;
        try {
            // check shopping cart trong db theo id người dùng.
            shoppingCart = this.CartModel.get(userId);
        } catch (SQLException e) {
            e.printStackTrace();
            // trường hợp không có thì tạo mới.
        }
        if (shoppingCart == null) {
            shoppingCart = new Cart();
            shoppingCart.setUserId(userId);
        }
        // do something.
        shoppingCart.deduction(product, quantity);
        try {
            shoppingCart = this.CartModel.save(shoppingCart);
        } catch (SQLException e) {
            e.printStackTrace();
            shoppingCart = null;
        }
        if (shoppingCart == null) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(new Cart()).build();
        }
        return Response.status(Response.Status.CREATED).entity(shoppingCart).build();
    }


    @GET
    @Path("/update")
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public Response update(@HeaderParam("Authorization") int userId, @QueryParam("productId") int productId, @QueryParam("quantity") int quantity) {
        // kiểm tra số lượng
        if (quantity <= 0) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        // kiểm tra sản phẩm.
        Product product = null;
        try {
            product = this.productModel.findById(productId);
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        if (product == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        Cart shoppingCart = null;
        try {
            // check shopping cart trong db theo id người dùng.
            shoppingCart = this.CartModel.get(userId);
        } catch (SQLException e) {
            e.printStackTrace();
            // trường hợp không có thì tạo mới.
        }
        if (shoppingCart == null) {
            shoppingCart = new Cart();
            shoppingCart.setUserId(userId);
        }
        // do something.
        shoppingCart.update(product, quantity);
        try {
            shoppingCart = this.CartModel.save(shoppingCart);
        } catch (SQLException e) {
            e.printStackTrace();
            shoppingCart = null;
        }
        if (shoppingCart == null) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(new Cart()).build();
        }
        return Response.status(Response.Status.CREATED).entity(shoppingCart).build();
    }

    @GET
    @Path("/remove")
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public Response remove(@HeaderParam("Authorization") int userId, @QueryParam("productId") int productId) {
        // kiểm tra sản phẩm.
        Product product = null;
        try {
            product = this.productModel.findById(productId);
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        if (product == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        Cart shoppingCart = null;
        try {
            // check shopping cart trong db theo id người dùng.
            shoppingCart = this.CartModel.get(userId);
        } catch (SQLException e) {
            e.printStackTrace();
            // trường hợp không có thì tạo mới.
        }

        if (shoppingCart == null) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(new Cart()).build();
        }

        boolean isDeletedSuccess = false;

        try {
            isDeletedSuccess = this.CartModel.remove(productId);
        } catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(isDeletedSuccess).build();
        }

        return Response.status(Response.Status.CREATED).entity(isDeletedSuccess).build();
    }

    @GET
    @Path("/clear")
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public Response clear(@HeaderParam("Authorization") int userId) {
        Cart shoppingCart = null;
        try {
            // check shopping cart trong db theo id người dùng.
            shoppingCart = this.CartModel.get(userId);
        } catch (SQLException e) {
            e.printStackTrace();
            // trường hợp không có thì tạo mới.
        }

        if (shoppingCart == null) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(new Cart()).build();
        }

        boolean isDeletedSuccess = false;

        try {
            isDeletedSuccess = this.CartModel.clear(shoppingCart.getId());
        } catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(isDeletedSuccess).build();
        }

        return Response.status(Response.Status.CREATED).entity(isDeletedSuccess).build();
    }
}
