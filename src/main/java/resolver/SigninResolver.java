package resolver;

import com.coxautodev.graphql.tools.GraphQLResolver;

import pojos.SigninPayload;
import pojos.User;

public class SigninResolver implements GraphQLResolver<SigninPayload> {
    public User user(SigninPayload payload) {
        return payload.getUser();
    }
}