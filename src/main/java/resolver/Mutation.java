package resolver;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.coxautodev.graphql.tools.GraphQLRootResolver;

import graphql.AuthContext;
import graphql.GraphQLException;
import graphql.schema.DataFetchingEnvironment;
import pojos.AuthData;
import pojos.Link;
import pojos.SigninPayload;
import pojos.User;
import pojos.Vote;
import repository.LinkRepository;
import repository.UserRepository;
import repository.VoteRepository;

public class Mutation implements GraphQLRootResolver {

	private final LinkRepository linkRepository;
	private final UserRepository userRepository;
	private final VoteRepository voteRepository;

	public Mutation(LinkRepository linkRepository, UserRepository userRepository, VoteRepository voteRepository) {
		this.linkRepository = linkRepository;
		this.userRepository = userRepository;
		this.voteRepository = voteRepository;
	}

	public Link createLink(String url, String description, DataFetchingEnvironment env) {
	     AuthContext context = env.getContext();
	     Link newLink = new Link(url, description, context.getUser().getId());
	     return linkRepository.saveLink(newLink);
	 }

	public User createUser(String name, AuthData auth) {
		User newUser = new User(name, auth.getEmail(), auth.getPassword());
		return userRepository.saveUser(newUser);
	}

	public SigninPayload signinUser(AuthData auth) throws IllegalAccessException {
		User user = userRepository.findByEmail(auth.getEmail());
		if (user.getPassword().equals(auth.getPassword())) {
			return new SigninPayload(user.getId(), user);
		}
		throw new GraphQLException("Invalid credentials");
	}
	
	public Vote createVote(String linkId, String userId) {
		String now = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
	    return voteRepository.saveVote(new Vote(now, userId, linkId));
	}
}