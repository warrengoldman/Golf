from fastapi import FastAPI, Response
from fastapi.responses import HTMLResponse

app = FastAPI()

@app.get("/", response_class=HTMLResponse)
async def read_root():
    return "<h1>Hello, World!</h1>"
#from fastapi.staticfiles import StaticFiles
#app.mount("/static", StaticFiles(directory="static"), name="static")



# @app.get("/healthy")
# async def health_check():
#     """
#     Handles GET requests to the root path.
#     """
#     return {'status': 'Healthy'}
#
@app.head("/")
async def head_root():
    """
    Handles HEAD requests to the root path.
    Returns an empty response with a 200 OK status.
    """
    return Response(status_code=200)