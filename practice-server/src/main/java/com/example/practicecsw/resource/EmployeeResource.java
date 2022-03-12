package com.example.practicecsw.resource;

import com.example.practicecsw.entity.Employee;
import com.example.practicecsw.model.EmployeeModel;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.SQLException;
import java.util.ArrayList;

@Path("/employees")
public class EmployeeResource {
    private EmployeeModel employeeModel;

    public EmployeeResource() {
        this.employeeModel = new EmployeeModel();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response findAll() {
        try {
            return Response.status(Response.Status.OK).entity(employeeModel.findAll()).build();
        } catch (SQLException e) {
            return Response.status(Response.Status.OK).entity(new ArrayList<>()).build();
        }
    }

    @GET()
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findById(@PathParam("id") int id) {
        try {
            return Response.status(Response.Status.OK).entity(employeeModel.findById(id)).build();
        } catch (SQLException e) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @POST()
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response save(Employee product) {
        try {
            Employee savedProduct = employeeModel.save(product);
            return Response.status(Response.Status.CREATED).entity(savedProduct).build();
        } catch (SQLException e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    @PUT()
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response update(@PathParam("id") int id, Employee product) {
        try {
            Employee foundProduct = employeeModel.findById(id);
            if (foundProduct == null) {
                return Response.status(Response.Status.BAD_REQUEST).build();
            }
            Employee updatedProduct = employeeModel.update(id, product);
            return Response.status(Response.Status.OK).entity(updatedProduct).build();
        } catch (SQLException e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    @DELETE()
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response delete(@PathParam("id") int id) {
        try {
            Employee foundProduct = employeeModel.findById(id);
            if (foundProduct == null) {
                return Response.status(Response.Status.BAD_REQUEST).build();
            }
            boolean isDeleted = employeeModel.delete(id);
            if (isDeleted) {
                return Response.status(Response.Status.OK).entity(true).build();
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return Response.status(Response.Status.BAD_REQUEST).build();
    }
}
