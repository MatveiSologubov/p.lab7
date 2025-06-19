package ru.itmo.client.commands;

import ru.itmo.client.network.UPDClient;
import ru.itmo.common.network.responses.ErrorResponse;
import ru.itmo.common.network.responses.Response;

/**
 * This is abstract class for creating commands that have interactions with server
 */
public abstract class NetworkCommand extends Command {
    protected final UPDClient client;

    public NetworkCommand(UPDClient client) {
        this.client = client;
    }

    /**
     * Handles unexpected and error responses
     *
     * @param response             Response to check
     * @param expectedResponseType Expected response type
     * @return Response with expected type if successful. Null otherwise
     */
    protected <T extends Response> T handleResponse(Response response, Class<T> expectedResponseType) {
        if (response instanceof ErrorResponse) {
            System.out.println("Error: " + ((ErrorResponse) response).message());
            return null;
        }

        if (expectedResponseType.isInstance(response)) {
            return expectedResponseType.cast(response);
        }

        System.out.println("Error: Received unexpected response type");
        return null;
    }
}
