package graphql;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
import repository.VoteRepository;
import resolver.LinkResolver;
import resolver.Mutation;
import resolver.Query;
import resolver.SigninResolver;
import resolver.VoteResolver;

@SuppressWarnings("resource")
@WebServlet(urlPatterns = "/graphql")
public class GraphQLEndpoint extends SimpleGraphQLServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8415535769922827297L;

	private static final LinkRepository linkRepository;
	private static final UserRepository userRepository;
	private static final VoteRepository voteRepository;

	static {
		MongoDatabase mongo = new MongoClient().getDatabase("hackernews");
		linkRepository = new LinkRepository(mongo.getCollection("links"));
		userRepository = new UserRepository(mongo.getCollection("users"));
		voteRepository = new VoteRepository(mongo.getCollection("votes"));
	}

	public GraphQLEndpoint() {
		super(buildSchema());
	}

	private static GraphQLSchema buildSchema() {
		return SchemaParser.newParser().file("schema.graphqls")
				.resolvers(new Query(linkRepository, userRepository),
						new Mutation(linkRepository, userRepository, voteRepository), new SigninResolver(),
						new LinkResolver(userRepository), new VoteResolver(linkRepository, userRepository))
				.build().makeExecutableSchema();
	}

	@Override
	protected GraphQLContext createContext(Optional<HttpServletRequest> request,
			Optional<HttpServletResponse> response) {
		User user = request.map(req -> req.getHeader("Authorization")).filter(id -> !id.isEmpty())
				.map(id -> id.replace("Bearer ", "")).map(userRepository::findById).orElse(null);
		return new AuthContext(user, request, response);
	}

	@Override
	protected List<GraphQLError> filterGraphQLErrors(List<GraphQLError> errors) {
		return errors.stream().filter(e -> e instanceof ExceptionWhileDataFetching || super.isClientError(e)).map(
				e -> e instanceof ExceptionWhileDataFetching ? new SanitizedError((ExceptionWhileDataFetching) e) : e)
				.collect(Collectors.toList());
	}

}