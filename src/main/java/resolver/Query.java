package resolver;

import java.util.List;

import com.coxautodev.graphql.tools.GraphQLRootResolver;

import pojos.Link;
import pojos.LinkFilter;
import pojos.User;
import repository.LinkRepository;
import repository.UserRepository;

public class Query implements GraphQLRootResolver {
    
    private final LinkRepository linkRepository;

    private final UserRepository userRepository;

    
    public Query(LinkRepository linkRepository, UserRepository userRepository) {
        this.linkRepository = linkRepository;
        this.userRepository = userRepository;
    }

    public List<Link> allLinks(LinkFilter filter) {
        return linkRepository.getAllLinks(filter);
    }
    
    public List<User> allUsers() {
        return userRepository.getAllUsers();
    }
}