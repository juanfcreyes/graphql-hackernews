package graphql;

import java.util.Optional;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.coxautodev.graphql.tools.SchemaParser;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

import graphql.schema.GraphQLSchema;
import graphql.servlet.GraphQLContext;
import graphql.servlet.SimpleGraphQLServlet;
import pojos.User;
import repository.LinkRepository;
import repository.UserRepository;
import resolver.LinkResolver;
import resolver.Mutation;
import resolver.Query;
import resolver.SigninResolver;

@SuppressWarnings("resource")
@WebServlet(urlPatterns = "/graphql")
public class GraphQLEndpoint extends SimpleGraphQLServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8415535769922827297L;

	private static final LinkRepository linkRepository;
	private static final UserRepository userRepository; // the new field

	static {
		MongoDatabase mongo = new MongoClient().getDatabase("hackernews");
		linkRepository = new LinkRepository(mongo.getCollection("links"));
		userRepository = new UserRepository(mongo.getCollection("users"));
	}

	public GraphQLEndpoint() {
		super(buildSchema());
	}

	private static GraphQLSchema buildSchema() {
        return SchemaParser.newParser()
                .file("schema.graphqls")
                .resolvers(
                        new Query(linkRepository, userRepository),
                        new Mutation(linkRepository, userRepository),
                        new SigninResolver(),
                        new LinkResolver(userRepository))
                .build()
                .makeExecutableSchema();
}
	
	@Override
	protected GraphQLContext createContext(Optional<HttpServletRequest> request, Optional<HttpServletResponse> response) {
	    User user = request
	        .map(req -> req.getHeader("Authorization"))
	        .filter(id -> !id.isEmpty())
	        .map(id -> id.replace("Bearer ", ""))
	        .map(userRepository::findById)
	        .orElse(null);
	    return new AuthContext(user, request, response);
	}
}