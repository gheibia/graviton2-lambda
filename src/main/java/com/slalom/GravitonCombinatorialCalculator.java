package com.slalom;


import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class GravitonCombinatorialCalculator implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private static final String PATH_PARAM_N = "n";
    private static final String PATH_PARAM_K = "k";

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent event, Context context) {

        var setSize = event.getPathParameters().get(PATH_PARAM_N);
        var subsetSize = event.getPathParameters().get(PATH_PARAM_K);
        int n, k;
        try {
            n = Integer.parseInt(setSize);
            k = Integer.parseInt(subsetSize);
            validateInput(n, k);
        } catch (IllegalArgumentException ex) {
            return logErrorAndRespond(context, "valid input should look something like /d/d (set size - max 40, subset size)", ex);
        }

        var fact = calculateCombinations(n, k);
        return logAndRespond(context, fact);
    }

    private void validateInput(int n, int k) throws IllegalArgumentException {
        if (n < 1 || n > 40 || k > n)
            throw new IllegalArgumentException("invalid input");
    }

    /// Calculates n!/(k! * (n-k)!)
    private int calculateCombinations(int n, int k) {
        double kFactorial = 1;
        for (double i = 1; i <= k; i++)
            kFactorial *= i;

        double nMinuskFactorial = 1;
        for (double i = 1; i <= n - k; i++)
            nMinuskFactorial *= i;

        double nFactorial = nMinuskFactorial;
        for (double i = n - k + 1; i <= n; i++)
            nFactorial *= i;

        // the result is most definitely an integer
        return (int) (nFactorial / (kFactorial * nMinuskFactorial));
    }

    private APIGatewayProxyResponseEvent logAndRespond(Context context, int comb) {
        LambdaLogger logger = context.getLogger();
        logger.log(String.format("Calculated factorial: %d", comb));

        var response = new APIGatewayProxyResponseEvent();
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        response.setHeaders(headers);
        response.setStatusCode(200);
        response.setBody(GSON.toJson(new ValidResponse(comb)));
        return response;
    }

    private APIGatewayProxyResponseEvent logErrorAndRespond(Context context, String msg, Exception ex) {
        LambdaLogger logger = context.getLogger();
        logger.log(msg);
        if (ex != null)
            logger.log(String.format("Error: %s, %s", ex.getMessage(), Arrays.toString(ex.getStackTrace())));

        var response = new APIGatewayProxyResponseEvent();
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        response.setHeaders(headers);
        response.setStatusCode(400);
        response.setBody(GSON.toJson(new ErrorResponse(msg)));
        return response;
    }
}