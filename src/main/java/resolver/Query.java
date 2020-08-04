package resolver;

import java.util.List;

import com.coxautodev.graphql.tools.GraphQLRootResolver;

import pojos.Link;
import repository.LinkRepository;

public class Query implements GraphQLRootResolver {
    
    private final LinkRepository linkRepository;

    public Query(LinkRepository linkRepository) {
        this.linkRepository = linkRepository;
    }

    public List<Link> allLinks() {
        return linkRepository.getAllLinks();
    }
}