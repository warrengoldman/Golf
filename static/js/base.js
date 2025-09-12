const mainForm = document.getElementById('mainForm');
if (mainForm) {
    mainForm.addEventListener('submit', async function (event) {
        event.preventDefault();

        const form = event.target;
        const formData = new FormData(form);
        const data = Object.fromEntries(formData.entries());
        const eventName = data.event_name;
        const description = data.description;
        const payload = {
            event_name: eventName,
            description: description
        };
        if (data.event_date && data.event_date !== "") {
            payload["event_date"] = data.event_date;
        }
        if (data.event_view_only && data.event_view_only !== "") {
            payload["event_view_only"] = data.event_view_only;
        }

        try {
            const response = await fetch('/event', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(payload)
            });

            if (response.ok) {
                window.location = '/' + eventName;
            } else {
                // Handle error
                const errorData = await response.json();
                alert(`Error: ${errorData.detail}`);
            }
        } catch (error) {
            console.error('Error:', error);
            alert('An error occurred. Please try again.');
        }
    });
}

const addParticipantForms = document.getElementsByClassName('add-participant-form');
if (addParticipantForms) {
    Array.from(addParticipantForms).map(addParticipantForm => {
        addParticipantForm.addEventListener('submit', async function (event) {
            event.preventDefault();

            const form = event.target;
            const formData = new FormData(form);
            const data = Object.fromEntries(formData.entries());
            const participantName = data.participant_name;
            const contactInfo = data.contact_info;
            const url = '/activity/' + event.target.addParticipantBtn.dataset.activityId;
            const eventName = event.target.addParticipantBtn.dataset.eventName;
            const payload = {
                participant_name: participantName,
                contact_info: contactInfo,
            };

            try {
                const response = await fetch(url, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify(payload)
                });

                if (response.ok) {
                    window.location = '/' + eventName;
                } else {
                    // Handle error
                    const errorData = await response.json();
                    alert(`Error: ${errorData.detail}`);
                }
            } catch (error) {
                console.error('Error:', error);
                alert('An error occurred. Please try again.');
            }
        })
    });
}

const addActivityForms = document.getElementsByClassName('add-activity-form');
if (addActivityForms) {
    Array.from(addActivityForms).map(addActivityForm => {
        addActivityForm.addEventListener('submit', async function (event) {
            event.preventDefault();

            const form = event.target;
            const formData = new FormData(form);
            const data = Object.fromEntries(formData.entries());
            const activityName = data.activity_name;
            const activityTime = data.activity_time;
            const url = '/eventdate/' + event.target.addActivityBtn.dataset.eventDateId;
            const eventName = event.target.addActivityBtn.dataset.eventName;
            const payload = {
                activity_name: activityName,
                activity_time: activityTime,
            };

            try {
                const response = await fetch(url, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify(payload)
                });

                if (response.ok) {
                    window.location = '/' + eventName;
                } else {
                    // Handle error
                    const errorData = await response.json();
                    alert(`Error: ${errorData.detail}`);
                }
            } catch (error) {
                console.error('Error:', error);
                alert('An error occurred. Please try again.');
            }
        })
    });
}


const addEventForms = document.getElementsByClassName('add-event-form');
if (addEventForms) {
    Array.from(addEventForms).map(addEventForm => {
        addEventForm.addEventListener('submit', async function (event) {
            event.preventDefault();

            const form = event.target;
            const formData = new FormData(form);
            const data = Object.fromEntries(formData.entries());
            const eventDate = data.event_date;
            const url = '/event/' + event.target.addEventBtn.dataset.eventId;
            const eventName = event.target.addEventBtn.dataset.eventName;
            const payload = {
                event_date: eventDate,
            };

            try {
                const response = await fetch(url, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify(payload)
                });

                if (response.ok) {
                    window.location = '/' + eventName;
                } else {
                    // Handle error
                    const errorData = await response.json();
                    alert(`Error: ${errorData.detail}`);
                }
            } catch (error) {
                console.error('Error:', error);
                alert('An error occurred. Please try again.');
            }
        })
    });
}

async function removeParticipant(participantId, eventName) {
    const url = '/eventparticipant/' + participantId;
    try {
        const response = await fetch(url, {
            method: 'DELETE',
        });

        if (response.ok) {
            window.location = '/' + eventName;
        } else {
            // Handle error
            const errorData = await response.json();
            alert(`Error: ${errorData.detail}`);
        }
    } catch (error) {
        console.error('Error:', error);
        alert('An error occurred. Please try again.');
    }
}

async function removeActivity(activityId, eventName) {
    const url = '/eventactivity/' + activityId;
    try {
        const response = await fetch(url, {
            method: 'DELETE',
        });

        if (response.ok) {
            window.location = '/' + eventName;
        } else {
            // Handle error
            const errorData = await response.json();
            alert(`Error: ${errorData.detail}`);
        }
    } catch (error) {
        console.error('Error:', error);
        alert('An error occurred. Please try again.');
    }
}

async function removeEventDate(eventDateId, eventName) {
    const url = '/eventdate/' + eventDateId;
    try {
        const response = await fetch(url, {
            method: 'DELETE',
        });

        if (response.ok) {
            window.location = '/' + eventName;
        } else {
            // Handle error
            const errorData = await response.json();
            alert(`Error: ${errorData.detail}`);
        }
    } catch (error) {
        console.error('Error:', error);
        alert('An error occurred. Please try again.');
    }
}