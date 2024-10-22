package handler;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import service.ClearService;
import spark.Request;
import spark.Response;

public class ClearHandler {

    private final ClearService clearService;
    private final Gson gson = new Gson();

    public ClearHandler(ClearService clearService) {
        this.clearService = clearService;
    }

    public Object handle(Request req, Response res) {
        try {
            // clear the data
            clearService.clear();

            // Respond with a success message
            res.status(200); // Success
            return gson.toJson(new MessageResponse("Database cleared successfully."));

        } catch (DataAccessException e) {
            // Handle any unexpected exceptions
            res.status(500); // Internal Server Error
            return gson.toJson(new ErrorResponse("Error: Unable to clear the database. " + e.getMessage()));
        } catch (Exception e) {
            // Handle everything else
            res.status(500); // Internal Server Error
            return gson.toJson(new ErrorResponse("Error: Unable to clear the database. " + e.getMessage()));
        }

    }

    // Helper class for successful response
    record MessageResponse(String message) {
    }

    // Helper class for error response
    record ErrorResponse(String message) {
    }
}
