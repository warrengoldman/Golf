def test_create_event_success(client):
    payload = {
        "event_name": "TestEvent",
        "description": "A test event",
        "event_date": "2024-07-01",
        "event_view_only": False,
        "activity_view_only": False,
        "participant_view_only": False
    }
    response = client.post("/event", json=payload)
    assert response.status_code == 201
    assert "event_id" in response.json()

def test_create_event_event_is_name(client):
    payload = {
        "event_name": "event",
        "description": "A test event",
        "event_date": "2024-07-01",
        "event_view_only": False,
        "activity_view_only": False,
        "participant_view_only": False
    }
    response = client.post("/event", json=payload)
    assert response.status_code == 400

def test_create_event_duplicate(client):
    event_name = "testeventdup"
    payload = {
        "event_name": event_name,
        "description": "Duplicate event"
    }
    # First creation should succeed
    response1 = client.post("/event", json=payload)
    assert response1.status_code == 201

    # Second creation should fail
    response2 = client.post("/event", json=payload)
    assert response2.status_code == 400
