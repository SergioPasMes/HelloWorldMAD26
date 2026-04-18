from flask import Flask
import waitress

import api.ranking as rank
import api.user as user
import api.plant as plant
import api.mission as location


def start(bind_address: str, port: int) -> None:
	"""
	Starts a Flask server.

	:param bind_address: Address to listen on. If `None`, listens on all interfaces.
	:param port: Port to listen on.
	"""

	app = Flask("mad")
	app.register_blueprint(rank.blueprint)
	app.register_blueprint(user.blueprint)
	app.register_blueprint(plant.blueprint)
	app.register_blueprint(location.blueprint)
	waitress.serve(app, host=bind_address, port=port, threads=1)
