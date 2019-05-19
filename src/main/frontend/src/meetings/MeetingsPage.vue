<template>
  <div>
    <new-meeting-form @added="addNewMeeting($event)"></new-meeting-form>

    <span v-if="meetings.length === 0">
               Brak zaplanowanych spotkań.
           </span>
    <h3 v-else>
      Zaplanowane zajęcia ({{ meetings.length }})
    </h3>

    <meetings-list :meetings="meetings"
                   :username="username"
                   @delete="deleteMeeting($event)"
                   @attend="addParticipanttoMeeting($event)"
                   @unattend="removeParticipantFromMeeting($event)"></meetings-list>                   
  </div>
</template>

<script>
    import NewMeetingForm from "./NewMeetingForm";
    import MeetingsList from "./MeetingsList";

    export default {
        components: {NewMeetingForm, MeetingsList},
        props: ['username'],
        data() {
            return {
                meetings: []
            };
        },
        methods: {
            getMeetings() {
                this.$http.get('meetings')
                    .then(response => { 
                        this.meetings = response.body;
                    })
            },
            addNewMeeting(meeting) {
                this.$http.post('meetings', meeting)
                    .then(response => {
                        this.meetings.push(response.body);
                    });
                this.getMeetings();
            },
            deleteMeeting(meeting) {
                this.$http.delete('meetings/' + meeting.id.toString())
                    .then(response =>{
                        this.meetings.splice(this.meetings.indexOf(meeting), 1);
                    });
                this.meetings.splice(this.meetings.indexOf(meeting), 1);
                this.getMeetings();
            },
            addParticipanttoMeeting(meeting) {
               meeting.participants.push(this.username);
               this.$http.post('meetings/'+ meeting.id +'/participants', {login:this.username})
                     .then(response => {
                         console.log("enrolled");
                          this.getMeetings();
                     })
                     .catch(response => {
                          console.log("cannot enroll");
                     });
            },
            removeParticipantFromMeeting(meeting) {
                meeting.participants.splice(meeting.participants.indexOf(this.username), 1);                
                this.$http.delete('meetings/'+ meeting.id + '/participants/' + this.username)
                     .then(response => {
                         console.log("unenrolled");
                          this.getMeetings();
                     })
                     .catch(response => {
                          console.log("cannot unenroll");
                     });
            },            
        },
        mounted() {
            this.$nextTick(this.getMeetings());
        }
    }
</script>
