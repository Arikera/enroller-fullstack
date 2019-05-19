package com.company.enroller.controllers;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.company.enroller.model.Meeting;
import com.company.enroller.model.Participant;
import com.company.enroller.persistence.MeetingService;

@RestController
@RequestMapping("/api/meetings")
public class MeetingRestController {

	@Autowired
	MeetingService meetingService;

	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseEntity<?> getMeetings() {
        Collection<Meeting> meetings = meetingService.getAll();
        return new ResponseEntity<Collection<Meeting>>(meetings, HttpStatus.OK);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public ResponseEntity<?> getMeeting(@PathVariable("id") long id) {
		Meeting meeting = meetingService.findById(id);
		if (meeting == null) {
			return new ResponseEntity<>("Meeting does not exist.", HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<Meeting>(meeting, HttpStatus.OK);
	}

	@RequestMapping(value = "", method = RequestMethod.POST)
	public ResponseEntity<?> addMeeting(@RequestBody Meeting meeting) {
		 meetingService.addMeeting(meeting);
	     return new ResponseEntity<Meeting>(meeting, HttpStatus.CREATED);
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<?> delete(@PathVariable("id") long id) {
		Meeting meeting = meetingService.findById(id);
		if (meeting == null) {
			return new ResponseEntity<>("A meeting with id " + id + " does not exist", HttpStatus.NOT_FOUND);
		}
		meetingService.deleteMeeting(meeting);
		return new ResponseEntity<Meeting>(meeting, HttpStatus.NO_CONTENT);
	}

	@RequestMapping(value = "/{id}/participants", method = RequestMethod.GET)
	public ResponseEntity<?> getAllParticipants(@PathVariable("id") long id) {
		Meeting meeting = meetingService.findById(id);
		if (meeting == null) {
			return new ResponseEntity<>("A meeting with id " + id + " does not exist", HttpStatus.NOT_FOUND);
		}
		Collection<Participant> participants = meetingService.getAllParticipants(id);
		return new ResponseEntity<Collection<Participant>>(participants, HttpStatus.OK);
	}

	@RequestMapping(value = "/{id}/participants", method = RequestMethod.POST)
	public ResponseEntity<?> addParticipantToMeeting(@PathVariable("id") long id,
			@RequestBody Participant participant) {
		Meeting meeting = meetingService.findById(id);
		if (meeting == null) {
			return new ResponseEntity<>("A meeting with id " + id + " does not exist", HttpStatus.NOT_FOUND);
		}
		if (meetingService.ParticipantAlreadyExists(participant.getLogin())) {
			return new ResponseEntity<>("Participant with login " + participant.getLogin() + " does not exist.",
					HttpStatus.NOT_FOUND);
		}

		if (meetingService.findParticipantByLogin(id, participant.getLogin()) != null) {
			return new ResponseEntity("Unable to add. A participant with login " + participant.getLogin()
					+ " already added in to the Meeting.", HttpStatus.CONFLICT);
		}

		meeting.addParticipant(participant);
		meetingService.addParticipantToMeeting(meeting);

		return new ResponseEntity<Meeting>(meeting, HttpStatus.OK);
	}

	@RequestMapping(value = "/{id}/participants/{participantId}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteParticipantFromMeeting(@PathVariable("participantId") String login,
			@PathVariable("id") long id) {
		Meeting meeting = meetingService.findById(id);
		if (meeting == null) {
			return new ResponseEntity<>("A meeting with id " + id + " does not exist", HttpStatus.NOT_FOUND);
		}
		Participant participant = meetingService.findParticipantByLogin(id, login);
		if (participant == null) {
			return new ResponseEntity<>("Participant with login " + login + " was not enrolled to the meeting",
					HttpStatus.NOT_FOUND);
		}
		meeting.removeParticipant(participant);
		meetingService.deleteParticipantFromMeeting(meeting);
		return new ResponseEntity<Participant>(participant, HttpStatus.NO_CONTENT);
	}
}
