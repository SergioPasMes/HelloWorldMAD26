import logging
from flask import Blueprint, request, jsonify

from api.common import authenticate_user
from db import MadDB

blueprint = Blueprint("ranking", __name__)
log = logging.getLogger("ranking")
RANKING_LIST_USER_COUNT = 50


@blueprint.get("/api/v1/ranking")
def ranking_list():
	error_template = "Error listing rankings,"

	user = authenticate_user(request)
	if user is None:
		log.error(f"{error_template} not authenticated")
		return jsonify({"error": "Not authenticated"}), 401

	try:
		users = MadDB.get_instance().get_best_ranked_users(RANKING_LIST_USER_COUNT)
	except Exception as e:
		log.error(f"{error_template} database error: {e}")
		return jsonify({"error": "Internal error"}), 500

	rankings = list(map(
		lambda u: {
			"alias": u.alias,
			"profile_picture": u.profile_picture,
			"score": u.score
		},
		users
	))

	log.info("Retrieved top users:")
	for user in rankings:
		log.info(f"	{user['alias']}:	{user['score']}")

	return jsonify(rankings), 200
