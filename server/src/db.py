from typing import List

import coloredlogs
import os
import sqlite3

from model.user import User

log = coloredlogs.logging.getLogger("db")

DB_SINGLETON = None


class MadDB:

	def __init__(self, db_path: str) -> None:
		log.debug("Initializing SQLite database")
		if os.path.exists(db_path):
			log.debug("Found existing database")

		try:
			self._db = sqlite3.connect(db_path, check_same_thread=False)
			if not self._is_initialized():
				self._initialize_db()

		except Exception as e:
			log.error(f"Error opening SQLite database: {e}")
			raise e

		log.debug("Successfully opened SQLite database")

	@staticmethod
	def get_instance() -> "MadDB":
		return DB_SINGLETON

	@staticmethod
	def set_instance(instance: "MadDB") -> None:
		global DB_SINGLETON
		DB_SINGLETON = instance

	def _is_initialized(self) -> bool:
		"""
		Checks whether the database has been initialized.
		"""

		# Check whether the 'users' table exists.
		cur = self._db.execute("""
			SELECT name FROM sqlite_master WHERE type='table' AND name='users'
		""")
		result = cur.fetchone()

		return result is not None

	def _initialize_db(self) -> None:
		"""
		Creates all required tables.
		"""
		log.info("Initializing SQLite database...")

		try:
			cur = self._db.cursor()
			cur.execute("""
				CREATE TABLE users (
					user_id INTEGER PRIMARY KEY NOT NULL,
					user_token VARCHAR(64) UNIQUE NOT NULL,

					alias VARCHAR(16) NOT NULL,
					age INTEGER NOT NULL,
					profile_picture TEXT NOT NULL,
					score UNSIGNED INTEGER NOT NULL DEFAULT 0
				)
			""")
			cur.close()

		except Exception as e:
			log.error(f"Error initializing SQLite database: {e}")
			raise e

	def register_user(self, user: User) -> None:
		"""
		Adds a user to the database.
		"""
		cur = self._db.cursor()
		cur.execute("""
			INSERT INTO 
				users (user_token, alias, age, profile_picture)
				VALUES (?, ?, ?, ?)
		""", [user.user_token, user.alias, user.age, user.profile_picture])
		self._db.commit()

	def delete_user(self, user: User) -> None:
		"""
		Deletes a user account from the database.
		"""
		cur = self._db.cursor()
		cur.execute("""
			DELETE FROM users
			WHERE user_token = ?
		""", [user.user_token])
		self._db.commit()

	def find_user_by_token(self, token: str) -> User | None:
		"""
		Tries to find a user in the database with the given token.
		"""

		result = self._db.execute("""
			SELECT user_id, user_token, alias, age, profile_picture, score FROM users WHERE user_token = ?
		""", [token])
		user = result.fetchone()

		if user is None:
			return None

		return User(
			user_id=user[0],
			user_token=user[1],
			alias=user[2],
			age=user[3],
			profile_picture=user[4],
			score=user[5]
		)

	def get_best_ranked_users(self, user_limit: int) -> List[User]:
		"""
		Query the top users.

		:param: user_limit: Maximum number of users to return.
		"""
		result = self._db.execute("""
			SELECT user_id, user_token, alias, age, profile_picture, score FROM users ORDER BY score DESC LIMIT ?
		""", [user_limit])

		return [
			User(
				user_id=user_id,
				user_token=user_token,
				alias=alias,
				age=age,
				profile_picture=profile_picture,
				score=score
			)
			for (user_id, user_token, alias, age, profile_picture, score) in result.fetchall()
		]

	def update_user(self, user: User) -> None:
		"""
		Updates a user profile.
		"""
		cur = self._db.cursor()
		cur.execute("""
			UPDATE users
			SET	alias = ?, age = ?, profile_picture = ?, score = ?
			WHERE user_token = ?;
		""", [user.alias, user.age, user.profile_picture, user.score, user.user_token])
		self._db.commit()
