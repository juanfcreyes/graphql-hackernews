package repository;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;

import pojos.Vote;

public class VoteRepository {

	private final MongoCollection<Document> votes;

	public VoteRepository(MongoCollection<Document> votes) {
		this.votes = votes;
	}

	public List<Vote> findByUserId(String userId) {
		List<Vote> list = new ArrayList<>();
		for (Document doc : votes.find(Filters.eq("userId", userId))) {
			list.add(vote(doc));
		}
		return list;
	}

	public List<Vote> findByLinkId(String linkId) {
		List<Vote> list = new ArrayList<>();
		for (Document doc : votes.find(Filters.eq("linkId", linkId))) {
			list.add(vote(doc));
		}
		return list;
	}

	public Vote saveVote(Vote vote) {
		Document doc = new Document();
		doc.append("userId", vote.getUserId());
		doc.append("linkId", vote.getLinkId());
		doc.append("createdAt", vote.getCreatedAt());
		votes.insertOne(doc);
		return vote(doc);
	}

	private Vote vote(Document doc) {
		return new Vote(doc.get("_id").toString(), doc.getString("createdAt"),
				doc.getString("userId"), doc.getString("linkId"));
	}
}