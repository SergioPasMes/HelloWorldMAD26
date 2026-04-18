from flask import Blueprint, request, jsonify
import logging
import numpy as np
import random


from api.common import authenticate_user
from model.plant import PLANTS

PLANT_CHOICES = 3
blueprint = Blueprint("plant", __name__)
log = logging.getLogger("plant")


@blueprint.get("/api/v1/plant")
def plant_get():
	error_template = "Error obtaining plants,"

	user = authenticate_user(request)
	if user is None:
		log.error(f"{error_template} not authenticated")
		return jsonify({"error": "Not authenticated"}), 401

	plants = np.random.choice(PLANTS, size=PLANT_CHOICES, replace=False)
	log.info("Obtained plants:")
	for plant in plants:
		log.info(f"	{plant.name}")

	response = [{"name": p.name, "picture": p.picture, "main_necessity": str(p.main_necessity)} for p in plants]
	return jsonify(response), 200
