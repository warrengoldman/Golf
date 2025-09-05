from fastapi import FastAPI, Response, Depends
from fastapi.responses import HTMLResponse
#from ..database import SessionLocal
#from typing import Annotated
#from sqlalchemy.orm import Session

app = FastAPI()


#
# def get_db():
#     db = SessionLocal()
#     try:
#         yield db
#     finally:
#         db.close()


#db_dependency = Annotated[Session, Depends(get_db)]

@app.get("/", response_class=HTMLResponse)
async def read_root():
    return "<h1>TODO Add page to create event. Also could list existing possible events which are clickable</h1>"
#from fastapi.staticfiles import StaticFiles
#app.mount("/static", StaticFiles(directory="static"), name="static")

@app.get("/{event_name}")
async def display_event(event_name: str):
    """
    Handles GET requests to the /{event_name} path.
    Will retrieve data for event_name from the database
    To be implemented in the future:
        If no data exists for event_name, redirect to root path with message set to "Event not found".
        If data exists, render a page with the event data.
    Currently, it just returns the event_name.
    """
    return {'event_name': event_name}


@app.get("/healthy")
async def health_check():
    """
    Handles GET requests to the root path.
    """
    return {'status': 'Healthy'}

@app.head("/")
async def head_root():
    """
    Handles HEAD requests to the root path.
    Returns an empty response with a 200 OK status.
    """
    return Response(status_code=200)