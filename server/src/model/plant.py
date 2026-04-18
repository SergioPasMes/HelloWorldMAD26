from enum import Enum
from typing import List, Optional


class PlantNecessity(Enum):
	SUN = "sun"
	WATER = "water"
	FRIENDS = "friends"
	OTHER = "other"


	@staticmethod
	def variants() -> List["PlantNecessity"]:
		return [
			PlantNecessity.SUN,
			PlantNecessity.WATER,
			PlantNecessity.FRIENDS,
			PlantNecessity.OTHER
		]

	@staticmethod
	def from_str(string: str) -> Optional["PlantNecessity"]:
		for variant in PlantNecessity.variants():
			if str(variant) == string:
				return variant

		return None

	def __str__(self):
		return self.value


class Plant:
	name: str
	picture: str
	main_necessity: PlantNecessity

	def __init__(self, name: str, picture: str, main_necessity: PlantNecessity) -> None:
		self.name = name
		self.picture = picture
		self.main_necessity = main_necessity


PLANTS = [
	Plant(
		"Cactus",
		"https://upload.wikimedia.org/wikipedia/commons/thumb/7/77/Parodia_penicillata.JPG/960px-Parodia_penicillata.JPG",
		PlantNecessity.SUN
	),
	Plant(
		"Lavender",
		"https://upload.wikimedia.org/wikipedia/commons/thumb/7/7e/Single_lavender_flower02.jpg/960px-Single_lavender_flower02.jpg",
		PlantNecessity.SUN
	),

	Plant(
		"Cattail",
		"https://upload.wikimedia.org/wikipedia/commons/thumb/3/3b/Ribnjak_Jegerseg_-_rogoz.jpg/960px-Ribnjak_Jegerseg_-_rogoz.jpg",
		PlantNecessity.WATER
	),
	Plant(
		"Fern",
		"https://upload.wikimedia.org/wikipedia/commons/thumb/8/82/Dipteris_conjugata_%E7%A0%B4%E5%82%98%E8%95%A8_001_%28%E5%A4%A9%E5%95%8F%29.jpg/960px-Dipteris_conjugata_%E7%A0%B4%E5%82%98%E8%95%A8_001_%28%E5%A4%A9%E5%95%8F%29.jpg",
		PlantNecessity.WATER
	),


	Plant(
		"Sunflower",
		"https://upload.wikimedia.org/wikipedia/commons/thumb/4/40/Sunflower_sky_backdrop.jpg/960px-Sunflower_sky_backdrop.jpg",
		PlantNecessity.FRIENDS
	),

	Plant(
		"Rose",
		"https://upload.wikimedia.org/wikipedia/commons/thumb/3/3a/Rosa_Precious_platinum.jpg/960px-Rosa_Precious_platinum.jpg",
		PlantNecessity.OTHER
	),
]
