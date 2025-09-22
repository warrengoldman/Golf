from fastapi import APIRouter, Depends, Request
from pydantic import BaseModel, Field
from starlette import status
from database import SessionLocal
from typing import Annotated, Optional
from sqlalchemy.orm import Session, InstrumentedAttribute
from models import Event, EventDate, Activity, Participant, EventConfig
from datetime import timezone, date
import datetime
from fastapi.templating import Jinja2Templates
router = APIRouter()

def get_db():
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()

db_dependency = Annotated[Session, Depends(get_db)]

templates = Jinja2Templates(directory="templates")

@router.get("/event/main-page")
async def main_page(request: Request):
    return templates.TemplateResponse("home.html", {"request": request})

@router.get("/{event_name}")
async def display_event(event_name: str, db: db_dependency, request: Request):
    """
    Handles GET requests to the /{event_name} path.
    Will retrieve data for event_name from the database
    """
    event = await get_event_json_sync(event_name, datetime.date(2025, 9, 22), db)
    return await get_event_display(event, request)

@router.get("/{event_name}/next")
async def display_event(event_name: str, db: db_dependency, request: Request):
    """
    Handles GET requests to the /{event_name} path.
    Will retrieve data for event_name from the database
    """

    event = await get_event_json_sync(event_name, db)
    return await get_event_display(event, request)

async def get_event_display(event: Event, request: Request):
    if type(event) is dict:
        return templates.TemplateResponse("home.html", {"request": request})

    event_view_only = False if event.event_config is None else event.event_config.event_view_only
    activity_view_only = False if event.event_config is None else event.event_config.activity_view_only
    participant_view_only = False if event.event_config is None else event.event_config.participant_view_only
    admin = request.query_params.get('admin')
    if admin:
        event_view_only = False
        activity_view_only = False
        participant_view_only = False
    else:
        event_param = request.query_params.get('event_view_only')
        if event_param is not None:
            event_view_only = event_param.lower() in ('true', '1', 'yes')
        activity_param = request.query_params.get('activity_view_only')
        if activity_param is not None:
            activity_view_only = activity_param.lower() in ('true', '1', 'yes')
        participant_param = request.query_params.get('participant_view_only')
        if participant_param is not None:
            participant_view_only = participant_param.lower() in ('true', '1', 'yes')

    return templates.TemplateResponse("event.html", {"request": request, "event": event, "event_date_view_only": event_view_only, "activity_view_only": activity_view_only, "participant_view_only": participant_view_only})

@router.get("/{event_name}/json")
async def get_event_json(event_name: str, db: db_dependency):
    event_json = await get_event_json_sync(event_name, None, db)
    return event_json

async def get_event_json_sync(event_name: str, event_date: date, db: db_dependency):
    """
    Handles GET requests to the /{event_name}/json path.
    Will retrieve data for event_name from the database and return it as JSON.
    If no data exists for event_name, it will return a 404 Not Found error.
    """
    event = db.query(Event).filter(Event.event_name == event_name).first()

    if not event:
        return {'error': f'Event not found for {event_name}. TBD Handle this', 'status': status.HTTP_404_NOT_FOUND}
    if event_date is not None:
        event_ret = Event()
        if event.event_dates:  # Access event_dates to ensure they are loaded
            for ed in event.event_dates:
                if ed.event_date > event_date:
                    event_ret.event_dates.append(ed)
                    activities = ed.activities  # Access activities to ensure they are loaded
                    if activities:
                        for act in activities:
                            _ = act.participants  # Access participants to ensure they are loaded
        if event.event_config:
            _ = event.event_config
        event_ret.event_config = event.event_config
        event_ret.event_name = event.event_name
        event_ret.description = event.description
        event_ret.id = event.id
        event_ret.create_date = event.create_date
        return event_ret
    else:
        if event.event_dates:  # Access event_dates to ensure they are loaded
            for ed in event.event_dates:
                activities = ed.activities  # Access activities to ensure they are loaded
                if activities:
                    for act in activities:
                        _ = act.participants  # Access participants to ensure they are loaded
        if event.event_config:
            _ = event.event_config
        return event

@router.get("/event/all")
async def get_events(db: db_dependency):
    events = db.query(Event).all()
    return events

@router.get("/event/activity/{activity_id}/json")
async def get_activity_json(activity_id: int, db: db_dependency):
    activity = db.query(Activity).filter(Activity.id == activity_id).first()
    if not activity:
        return {'error': 'Activity not found.', 'status': status.HTTP_404_NOT_FOUND}
    _ = activity.participants  # Access participants to ensure they are loaded
    return activity

@router.get("/event/config/{event_id}")
async def get_event_config(event_id: int, db: db_dependency):
    event_config = db.query(EventConfig).filter(EventConfig.event_id == event_id).first()
    if not event_config:
        return {'error': f'EventConfig not found for {event_id}.', 'status': status.HTTP_404_NOT_FOUND}
    return event_config

@router.get("/event/config")
async def get_event_configs(db: db_dependency):
    event_configs = db.query(EventConfig).all()
    if not event_configs:
        return {'error': 'No event configs found.', 'status': status.HTTP_404_NOT_FOUND}
    return event_configs

class EventConfigRequest(BaseModel):
    event_view_only: Optional[bool] = False
    activity_view_only: Optional[bool] = False
    participant_view_only: Optional[bool] = False

@router.post("/event/{event_id}/config", status_code=status.HTTP_201_CREATED)
async def create_event_config(event_id: int, db: db_dependency, event_config_request: EventConfigRequest):
    event_view_only = 1 if event_config_request.event_view_only else 0
    activity_view_only = 1 if event_config_request.activity_view_only else 0
    participant_view_only = 1 if event_config_request.participant_view_only else 0
    new_event_config = EventConfig(event_id=event_id, event_view_only=event_view_only, activity_view_only=activity_view_only, participant_view_only=participant_view_only)
    db.add(new_event_config)
    db.commit()
    db.refresh(new_event_config)

@router.put("/event/config/{event_config_id}", status_code=status.HTTP_201_CREATED)
async def create_event_config(event_config_id: int, db: db_dependency, event_config_request: EventConfigRequest):
    event_config = db.query(EventConfig).filter(EventConfig.id == event_config_id).first()
    if event_config is None:
        return {'error': f'EventConfig not found for {event_config_id}.'}, status.HTTP_400_BAD_REQUEST
    event_config.event_view_only = 1 if event_config_request.event_view_only else 0
    event_config.activity_view_only = 1 if event_config_request.activity_view_only else 0
    event_config.participant_view_only = 1 if event_config_request.participant_view_only else 0
    db.commit()
    db.refresh(event_config)
    return event_config


class EventRequest(BaseModel):
    event_name: str = Field(min_length=3, max_length=15, pattern=r"[a-zA-Z0-9\-_/]+$")
    description: str = Field(default=None, max_length=100)
    event_date: Optional[date] = None
    event_view_only: Optional[bool] = False
    activity_view_only: Optional[bool] = False
    participant_view_only: Optional[bool] = False


class EventDateRequest(BaseModel):
    event_date: date = Field(default=None)

@router.post("/event", status_code=status.HTTP_201_CREATED)
async def create_event(db: db_dependency, event_request: EventRequest):
    """
    Handles POST requests to the /event path.
    Will create a new event in the database with the provided event_name and description.
    If an event with the same name already exists, it will return a 400 Bad Request error.
    """
    existing_event = db.query(Event).filter(Event.event_name == event_request.event_name).first()
    if existing_event:
        return {'error': 'Event with this name already exists.'}, status.HTTP_400_BAD_REQUEST

    new_event = Event(event_name=event_request.event_name, description=event_request.description, create_date=datetime.datetime.now(timezone.utc))
    db.add(new_event)
    db.commit()
    db.refresh(new_event)
    if event_request.event_date:
        event_date_request:EventDateRequest = EventDateRequest(event_date=event_request.event_date)
        await create_event_date(db, event_date_request, new_event.id)

    # Create EventConfig
    event_view_only = 1 if event_request.event_view_only else 0
    activity_view_only = 1 if event_request.activity_view_only else 0
    participant_view_only = 1 if event_request.participant_view_only else 0
    new_event_config = EventConfig(event_id=new_event.id, event_view_only=event_view_only, activity_view_only=activity_view_only, participant_view_only=participant_view_only)
    db.add(new_event_config)
    db.commit()
    db.refresh(new_event_config)

    return {'message': 'Event created successfully', 'event_id': new_event.id}

@router.post("/event/{event_id}", status_code=status.HTTP_201_CREATED)
async def create_event_date(db: db_dependency, event_date_request: EventDateRequest, event_id: int):
    if event_date_request:
        event_date = event_date_request.event_date
        if event_date:
            new_event_date = EventDate(event_id=event_id, event_date=event_date, event_active=1, create_date=datetime.datetime.now(timezone.utc))
            db.add(new_event_date)
            db.commit()
            db.refresh(new_event_date)

class ActivityRequest(BaseModel):
    activity_name: str = Field(min_length=3, max_length=50)
    activity_time: str = Field(pattern=r"^(?:0[0-9]|1[0-9]|2[0-3]|[1-9]):[0-5]\d$")  # HH:MM format

@router.post("/eventdate/{event_date_id}", status_code=status.HTTP_201_CREATED)
async def create_event_date_activity(db: db_dependency, activity: ActivityRequest, event_date_id: int):
    event_date = db.query(EventDate).filter(EventDate.id == event_date_id).first()
    if not event_date:
        return {'error': 'Event date not found.'}, status.HTTP_404_NOT_FOUND

    activity_time_obj = datetime.datetime.strptime(activity.activity_time, "%H:%M").time()
    new_activity = Activity(event_date_id=event_date_id, activity_name=activity.activity_name, activity_time=activity_time_obj, create_date=datetime.datetime.now(timezone.utc))
    db.add(new_activity)
    db.commit()
    db.refresh(new_activity)

    return {'message': 'Activity created successfully', 'activity_id': new_activity.id}

class ParticipantRequest(BaseModel):
    participant_name: str = Field(min_length=1, max_length=100)
    contact_info: str = Field(default=None, max_length=100)

@router.post("/activity/{activity_id}")
async def add_participant_to_activity(db: db_dependency, activity_id: int, participant: ParticipantRequest):
    participant_name = participant.participant_name
    contact_info = participant.contact_info
    activity = db.query(Activity).filter(Activity.id == activity_id).first()
    if not activity:
        return {'error': 'Activity not found.'}, status.HTTP_404_NOT_FOUND

    new_participant = Participant(activity_id=activity_id, participant_name=participant_name, contact_info=contact_info, create_date=datetime.datetime.now(timezone.utc))
    db.add(new_participant)
    db.commit()
    db.refresh(new_participant)

    return {'message': 'Participant added successfully', 'participant_id': new_participant.id}

@router.delete("/event/{event_id}", status_code=status.HTTP_204_NO_CONTENT)
async def delete_event(db: db_dependency, event_id: int):
    event_date = db.query(Event).filter(Event.id == event_id).first()
    if not event_date:
        return {'error': 'Event not found.'}, status.HTTP_404_NOT_FOUND

    db.delete(event_date)
    db.commit()
    return {'message': 'Event deleted successfully'}

@router.delete("/eventactivity/{activity_id}", status_code=status.HTTP_204_NO_CONTENT)
async def delete_event_activity(db: db_dependency, activity_id: int):
    activity = db.query(Activity).filter(Activity.id == activity_id).first()
    if not activity:
        return {'error': 'Activity not found.'}, status.HTTP_404_NOT_FOUND

    db.delete(activity)
    db.commit()
    return {'message': 'Activity deleted successfully'}

@router.delete("/eventparticipant/{participant_id}", status_code=status.HTTP_204_NO_CONTENT)
async def delete_event_participant(db: db_dependency, participant_id: int):
    participant = db.query(Participant).filter(Participant.id == participant_id).first()
    if not participant:
        return {'error': 'Participant not found.'}, status.HTTP_404_NOT_FOUND

    db.delete(participant)
    db.commit()
    return {'message': 'Participant deleted successfully'}

@router.delete("/eventdate/{event_date_id}", status_code=status.HTTP_204_NO_CONTENT)
async def delete_event_participant(db: db_dependency, event_date_id: int):
    event_date = db.query(EventDate).filter(EventDate.id == event_date_id).first()
    if not event_date:
        return {'error': 'EventDate not found.'}, status.HTTP_404_NOT_FOUND

    db.delete(event_date)
    db.commit()
    return {'message': 'EventDate deleted successfully'}