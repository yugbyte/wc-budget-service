package com.yb.wealth.care.budget.external.clients;

import com.yb.wealth.care.budget.external.clients.dto.ExpenseCategory;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.util.List;

@RegisterRestClient(configKey = "wc-ui-service")
@Path("/lookup/budgets")
public interface WcUiServiceClient {

   @GET
   @Path("/categories")
   @Produces(MediaType.APPLICATION_JSON)
   Uni<List<ExpenseCategory>> getExpenseCategories();

}
