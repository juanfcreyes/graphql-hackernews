package repository;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;

import pojos.User;

public class UserRepository {

	private final MongoCollection<Document> users;

	public UserRepository(MongoCollection<Document> users) {
		this.users = users;
	}

	public User findByEmail(String email) {
		Document doc = users.find(Filters.eq("email", email)).first();
		return user(doc);
	}

	public User findById(String id) {
		Document doc = users.find(Filters.eq("_id", new ObjectId(id))).first();
		return user(doc);
	}

	public List<User> getAllUsers() {
		List<User> allUsers = new ArrayList<>();
		for (Document doc : users.find()) {
			allUsers.add(user(doc));
		}
		return allUsers;
	}

	public User saveUser(User user) {
		Document doc = new Document();
		doc.append("name", user.getName());
		doc.append("email", user.getEmail());
		doc.append("password", user.getPassword());
		users.insertOne(doc);
		return user(doc);
	}

	private User user(Document doc) {
		if (doc == null) {
			return null;
		}
		return new User(doc.get("_id").toString(), doc.getString("name"), doc.getString("email"),
				doc.getString("password"));
	}
}
