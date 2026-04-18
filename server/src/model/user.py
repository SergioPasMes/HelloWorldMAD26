import copy


class User:
	user_id: int | None = None
	user_token: str
	alias: str
	age: int
	profile_picture: str
	score: int = 0

	def __init__(self, user_token: str, alias: str, age: int, profile_picture: str, user_id: int | None = None, score: int | None = None) -> None:
		self.user_token = user_token
		self.alias = alias
		self.age = age
		self.profile_picture = profile_picture
		self.user_id = user_id
		self.score = score


	def clone(self) -> "User":
		"""
		Creates a copy of this value.
		"""
		return copy.deepcopy(self)
