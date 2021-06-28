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

    public List<Link> allLinks(LinkFilter filter, Number skip, Number first) {
        return linkRepository.getAllLinks(filter, skip.intValue(), first.intValue());
    }
    
    public Long numberOfLinks() {
    	return linkRepository.numberOfLinks();
    }
    
    public List<User> allUsers() {
        return userRepository.getAllUsers();
    }
}