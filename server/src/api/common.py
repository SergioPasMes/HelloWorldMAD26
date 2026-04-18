import logging
from typing import Dict

from db import MadDB
from model.user import User

log = logging.getLogger("common")


def authenticate_user(request) -> User | None:
	"""
	Uses the User-Token header to authenticate the current user.
	:param request: Flask request.
	:return: User value if found, `None` if not found.
	"""
	try:
		token = request.headers.get("User-Token")
		user = None

		if token is not None:
			user = MadDB.get_instance().find_user_by_token(token)

		if token is None:
			log.debug(f"No user token was provided")
		elif user is None:
			log.debug(f"User with token '{token}' not found")
		else:
			log.debug(f"Authenticated user {user.alias}")

		return user

	except Exception as e:
		log.error(f"Error authenticating user, database error: {e}")
		return None


def str_to_number(string: str) -> float | None:
	"""
	Tries to parse a floating point number from a string.
	"""
	try:
		return float(string)
	except:
		return None
