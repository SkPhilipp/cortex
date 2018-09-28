package com.hileco.cortex.server;

import spark.Request;
import spark.Response;
import spark.Route;

public class TextRoute implements Route {

    private Route route;

    public TextRoute(Route route) {
        this.route = route;
    }

    @Override
    public Object handle(Request request, Response response) throws Exception {
        var result = route.handle(request, response);
        return "<pre>" + result + "</pre>";
    }
}
