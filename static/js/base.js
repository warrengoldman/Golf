const mainForm = document.getElementById('mainForm');
if (mainForm) {
    mainForm.addEventListener('submit', async function (event) {
        event.preventDefault();

        const form = event.target;
        const formData = new FormData(form);
        const data = Object.fromEntries(formData.entries());
        const eventName = data.event_name;
        const description = data.description;
        const eventDate = data.event_date;
        const payload = {
            event_name: eventName,
            description: description,
            event_date: eventDate,
        };

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

const addParticipantForm = document.getElementById('addParticipantForm');
if (addParticipantForm) {
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