package com.company.enroller.persistence;

import java.util.Collection;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Transaction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.LogicalExpression;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Component;

import com.company.enroller.model.Meeting;
import com.company.enroller.model.Participant;

@Component("meetingService")
public class MeetingService {

	DatabaseConnector connector;

	public MeetingService() {
		connector = DatabaseConnector.getInstance();
	}

	public Collection<Meeting> getAll() {
		String hql = "FROM Meeting";
        Query query = connector.getSession().createQuery(hql);
        return query.list();
	}

	public Meeting findById(long id) {
		return (Meeting) connector.getSession().get(Meeting.class, id);
	}

	public Meeting addMeeting(Meeting meeting) {
		Transaction transaction = connector.getSession().beginTransaction();
		connector.getSession().save(meeting);
		transaction.commit();
		return meeting;
	}
	
	public void deleteMeeting(Meeting meeting) {
		Transaction transaction = connector.getSession().beginTransaction();
		connector.getSession().delete(meeting);
		transaction.commit();
	}
	
	public Collection<Participant> getAllParticipants(long id) {
		return ((Meeting) connector.getSession().get(Meeting.class, id)).getParticipants();
	}

	public boolean ParticipantAlreadyExists(String login) {
		return !(connector.getSession().get(Participant.class, login) != null);
	}

	public void addParticipantToMeeting(Meeting meeting) {
		Transaction transaction = connector.getSession().beginTransaction();
		connector.getSession().save(meeting);
		transaction.commit();
	}
	
	public void deleteParticipantFromMeeting(Meeting meeting) {
		Transaction transaction = connector.getSession().beginTransaction();
		connector.getSession().save(meeting);
		transaction.commit();
	}

	public Participant findParticipantByLogin(long id, String login) {
		Collection<Participant> participants = ((Meeting) connector.getSession().get(Meeting.class, id))
				.getParticipants();
		for (Participant participant : participants) {
			if (participant.getLogin().equals(login)) {
				return participant;
			}
		}
		connector.getSession().get(Meeting.class, id);
		return null;
	}
}
