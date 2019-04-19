import logging
from flask import Flask
import backend.flaskr as route

app = Flask(__name__)
route.api.init_app(app)

@app.errorhandler(404)
def not_found(e):
    return '', 404

if __name__ != "__main__":
    gunicorn_logger = logging.getLogger("gunicorn.error")
    app.logger.handlers = gunicorn_logger.handlers
    app.logger.setLevel(gunicorn_logger.level)
app = Flask(__name__)
route.api.init_app(app)

if __name__ == "__main__":
    app.run(host="0.0.0.0", debug=True, port=5005)