package ru.itmo.server.commands;

import ru.itmo.common.models.User;
import ru.itmo.common.network.requests.AuthRequest;
import ru.itmo.common.network.requests.Request;
import ru.itmo.common.network.responses.AuthResponse;
import ru.itmo.common.network.responses.Response;
import ru.itmo.server.db.UserRepository;

import java.sql.SQLException;

public class Auth extends Command {
    /**
     * execute command
     *
     * @param request request from client
     */
    @Override
    public Response execute(Request request) {
        AuthRequest authRequest = (AuthRequest) request;
        User user = authRequest.getUser();
        try {
            boolean success = UserRepository.authenticate(user.getLogin(), user.getPassword());
            return new AuthResponse(success);
        } catch (SQLException e) {
            return new AuthResponse(false);
        }
    }
}
