package ru.itmo.server.commands;

import ru.itmo.common.models.User;
import ru.itmo.common.network.requests.RegisterRequest;
import ru.itmo.common.network.requests.Request;
import ru.itmo.common.network.responses.RegisterResponse;
import ru.itmo.common.network.responses.Response;
import ru.itmo.server.db.UserRepository;

import java.sql.SQLException;

public class Register extends Command {
    /**
     * execute command
     *
     * @param request request from client
     */
    @Override
    public Response execute(Request request) {
        RegisterRequest registerRequest = (RegisterRequest) request;
        User user = registerRequest.getUser();
        try {
            boolean success = UserRepository.register(user.getLogin(), user.getPassword());
            return new RegisterResponse(success);
        } catch (SQLException e) {
            return new RegisterResponse(false);
        }
    }
}
