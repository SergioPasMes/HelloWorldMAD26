from typing import List

from flask import Blueprint, request, jsonify
import logging
import random


from api.common import authenticate_user, str_to_number
from model.location import Point, LOCATIONS, Location
from model.plant import PlantNecessity

MIN_DISTANCE_METERS = 200
MAX_DISTANCE_METERS = 10_000
NECESSITY_WEIGHT = 0.5
blueprint = Blueprint("mission", __name__)
log = logging.getLogger("mission")


def print_locations(user_location: Point, locations: List[Location]) -> None:
	for location in locations:
		distance = user_location.distance_meters_to(location.point)
		log.debug(f"	{location.name} ({distance / 1000:.3} km)")


@blueprint.get("/api/v1/mission")
def mission_get():
	error_template = "Error generating mission,"

	user = authenticate_user(request)
	if user is None:
		log.error(f"{error_template} not authenticated")
		return jsonify({"error": "Not authenticated"}), 401

	required_fields = {"necessity", "latitude", "longitude"}
	if required_fields != request.args.keys():
		missing_fields = required_fields - request.args.keys()
		log.error(f"{error_template} missing fields: {missing_fields}")
		return jsonify({"error": f"Missing fields: {missing_fields}"}), 400

	necessity = request.args.get("necessity")
	necessity = PlantNecessity.from_str(necessity)

	latitude = request.args.get("latitude")
	latitude = str_to_number(latitude)

	longitude = request.args.get("longitude")
	longitude = str_to_number(longitude)

	if necessity is None:
		log.error(f"{error_template} invalid necessity: {necessity}")
		return jsonify({"error": "Invalid necessity"}), 400

	if latitude is None or latitude > 90 or latitude < -90:
		log.error(f"{error_template} invalid latitude")
		return jsonify({"error": "Invalid latitude"}), 400

	if longitude is None or longitude > 180 or longitude < -180:
		log.error(f"{error_template} invalid longitude")
		return jsonify({"error": "Invalid longitude"}), 400


	# Filter locations by proximity
	user_location = Point(latitude, longitude)
	nearby_locations = list(filter(lambda l: MIN_DISTANCE_METERS <= user_location.distance_meters_to(l.point) <= MAX_DISTANCE_METERS, LOCATIONS))

	# If there are no nearby locations, choose the closest one.
	if not nearby_locations:
		sorted_locations = sorted(LOCATIONS, key=lambda l: user_location.distance_meters_to(l.point))
		nearby_locations = [sorted_locations[0]]
		log.debug(f"No nearby locations, defaulting to closest: {nearby_locations[0].name}")


	# Roll a dice, filter by necessity if needed.
	if random.random() <= NECESSITY_WEIGHT:
		log.debug("Filtering by necessity")
		candidate_locations = list(filter(lambda l: l.satisfies(necessity), nearby_locations))
		if len(candidate_locations) == 0:
			log.debug("No locations meeting necessities, undoing filter")
			candidate_locations = nearby_locations

	else:
		candidate_locations = nearby_locations

	log.debug("Candidate locations:")
	print_locations(user_location, candidate_locations)

	location = random.choice(candidate_locations)

	log.info(f"Generated mission:")
	log.info(f"	name:		{location.name}")
	log.info(f"	latitude:	{location.point.latitude}")
	log.info(f"	longitude:	{location.point.longitude}")
	log.info(f"	satisfies:	{[str(n) for n in location.satisfies_necessities]}")

	return jsonify({
		"name": location.name,
		"satisfies_necessities": [str(n) for n in location.satisfies_necessities],
		"latitude": location.point.latitude,
		"longitude": location.point.longitude
	}), 200
