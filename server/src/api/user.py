from flask import Blueprint, request, jsonify
import logging
import random
import string

from api.common import authenticate_user
from db import MadDB
from model.user import User

blueprint = Blueprint("user", __name__)
log = logging.getLogger("user")

USER_TOKEN_LENGTH = 64
USERNAME_MIN_LENGTH = 3
USERNAME_MAX_LENGTH = 16
USER_MIN_AGE = 16
USER_MAX_AGE = 100
USER_VALID_PROFILE_PICTURES = [f"pfp_{i}.png" for i in range(12)]


@blueprint.post("/api/v1/user")
def user_register():
	error_template = "Error creating user,"

	try:
		json = request.json
	except:
		log.error(f"{error_template} malformed request")
		return jsonify({"error": "Malformed request"}), 400

	required_fields = {"alias", "age", "profile_picture"}
	if required_fields != json.keys():
		missing_fields = required_fields - json.keys()
		log.error(f"{error_template} missing fields: {missing_fields}")
		return jsonify({"error": f"Missing fields: {missing_fields}"}), 400

	if len(json["alias"]) not in range(USERNAME_MIN_LENGTH, USERNAME_MAX_LENGTH + 1):
		log.error(f"{error_template} alias length of {len(json['alias'])} out of bounds [{USERNAME_MIN_LENGTH}, {USERNAME_MAX_LENGTH}]")
		return jsonify({"error": f"Alias length out of bounds [{USERNAME_MIN_LENGTH}, {USERNAME_MAX_LENGTH}]"}), 400

	if not str(json["age"]).isdigit():
		log.error(f"{error_template} age is not a number")
		return jsonify({"error": f"Age must be a number"}), 400

	if int(json["age"]) not in range(USER_MIN_AGE, USER_MAX_AGE + 1):
		log.error(f"{error_template} age {json['age']} out of bounds [{USER_MIN_AGE}, {USER_MAX_AGE}]")
		return jsonify({"error": f"Age out of bounds [{USER_MIN_AGE}, {USER_MAX_AGE}]"}), 400

	if json["profile_picture"] not in USER_VALID_PROFILE_PICTURES:
		log.error(f"{error_template} invalid profile picture: {json['profile_picture']}")
		return jsonify({"error": f"Invalid profile picture"}), 400


	token = "".join(
		random.choices(string.ascii_uppercase + string.ascii_lowercase + string.digits, k=USER_TOKEN_LENGTH)
	)
	user = User(
		user_token=token,
		alias=json["alias"],
		age=int(json["age"]),
		profile_picture=json["profile_picture"]
	)

	try:
		MadDB.get_instance().register_user(user)
	except Exception as e:
		log.error(f"{error_template} database error: {e}")
		return jsonify({"error": "Internal error"}), 500

	log.info(f"New user created successfully")
	log.info(f"	Alias:	{user.alias}")
	log.info(f"	Age:	{user.age}")
	log.info(f"	Pfp:	{user.profile_picture}")
	log.info(f"	Token:	{user.user_token}")
	return jsonify({"user_token": user.user_token}), 201



@blueprint.put("/api/v1/user")
def user_update():
	error_template = "Error updating user,"
	try:
		json = request.json
	except:
		log.error(f"{error_template} malformed request")
		return jsonify({"error": "Malformed request"}), 400

	user = authenticate_user(request)
	if user is None:
		log.error(f"{error_template} not authenticated")
		return jsonify({"error": "Not authenticated"}), 401

	new_user = user.clone()
	if "alias" in json:
		alias = json["alias"]
		if len(alias) not in range(USERNAME_MIN_LENGTH, USERNAME_MAX_LENGTH + 1):
			log.error(
				f"{error_template} new alias length of {len(alias)} out of bounds [{USERNAME_MIN_LENGTH}, {USERNAME_MAX_LENGTH}]")
			return jsonify({"error": f"Alias length out of bounds [{USERNAME_MIN_LENGTH}, {USERNAME_MAX_LENGTH}]"}), 400

		new_user.alias = alias

	if "age" in json:
		age = json["age"]
		if not str(age).isdigit():
			log.error(f"{error_template} new age is not a number")
			return jsonify({"error": "Age is not a number"}), 400

		if int(age) not in range(USER_MIN_AGE, USER_MAX_AGE + 1):
			log.error(
				f"{error_template} new age {age} out of bounds [{USER_MIN_AGE}, {USER_MAX_AGE}]")
			return jsonify({"error": f"Age out of bounds [{USER_MIN_AGE}, {USER_MAX_AGE}]"}), 400

		new_user.age = int(age)

	if "profile_picture" in json:
		profile_picture = json["profile_picture"]
		if profile_picture not in USER_VALID_PROFILE_PICTURES:
			log.error(f"{error_template} invalid new profile picture: {profile_picture}")
			return jsonify({"error": "Invalid profile picture"}), 400

		new_user.profile_picture = profile_picture

	if "score" in json:
		score = json["score"]
		if not isinstance(score, int):
			log.error(f"{error_template} score is not an integer")
			return jsonify({"error": "Score must be an integer"}), 400

		if score < 0:
			log.error(f"{error_template} score is negative")
			return jsonify({"error": "Score must be positive"}), 400

		new_user.score = score

	try:
		MadDB.get_instance().update_user(new_user)
	except Exception as e:
		log.error(f"{error_template} database error: {e}")
		return jsonify({"error": "Internal error"}), 500

	log.info("User profile updated successfully:")
	for field_name, old_value, new_value in [
		("Alias", user.alias, new_user.alias),
		("Age", user.age, new_user.age),
		("Pfp", user.profile_picture, new_user.profile_picture),
		("Score", user.score, new_user.score)
	]:
		if old_value != new_value:
			log.info(f"	{field_name}:	{old_value}	->	{new_value}")
		else:
			log.info(f"	{field_name}:	{old_value}")

	return "", 200


@blueprint.delete("/api/v1/user")
def user_delete():
	error_template = "Error deleting user,"
	user = authenticate_user(request)
	if user is None:
		log.error(f"{error_template} not authenticated")
		return jsonify({"error": "Not authenticated"}), 401

	MadDB.get_instance().delete_user(user)
	return "", 200
