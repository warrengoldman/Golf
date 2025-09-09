from fastapi import FastAPI, Response, status
from fastapi.responses import HTMLResponse
from fastapi.staticfiles import StaticFiles
from models import Base
from database import engine
from routers import event
from starlette.responses import RedirectResponse

app = FastAPI()

Base.metadata.create_all(bind=engine)

app.mount("/static", StaticFiles(directory="static"), name="static")

@app.get("/", response_class=HTMLResponse)
async def read_root():
    return RedirectResponse(url="/event/main-page", status_code=status.HTTP_302_FOUND)

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

app.include_router(event.router)