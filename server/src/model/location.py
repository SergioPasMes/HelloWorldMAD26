from typing import List

import geopy.distance

from model.plant import PlantNecessity


class Point:
	latitude: float
	longitude: float

	def __init__(self, latitude: float, longitude: float) -> None:
		self.latitude = latitude
		self.longitude = longitude


	def distance_meters_to(self, other: "Point") -> float:
		"""
		Calculates the distance between two points in meters.
		"""
		return geopy.distance.distance((self.latitude, self.longitude), (other.latitude, other.longitude)).m


class Location:
	name: str
	satisfies_necessities: List[PlantNecessity]
	point: Point

	def __init__(self, name: str, necessities: List[PlantNecessity], point: Point) -> None:
		self.name = name
		self.satisfies_necessities = necessities
		self.point = point


	def satisfies(self, necessity: PlantNecessity) -> bool:
		"""
		Returns whether the location satisfies the given necessity.
		"""
		return necessity in self.satisfies_necessities


LOCATIONS = [
	Location(
		"Parque del Retiro",
		[PlantNecessity.WATER, PlantNecessity.FRIENDS],
		Point(40.415746718922755, -3.6829599468418217)
	),
	Location(
		"Real jardín botánico",
		[PlantNecessity.FRIENDS],
		Point(40.41253173771274, -3.6920239852097083)
	),
	Location(
		"Palacio Real de Madrid",
		[PlantNecessity.SUN],
		Point(40.4164894858149, -3.7145145763627436)
	),

	Location(
		"Mercado de San Fernando",
		[PlantNecessity.OTHER],
		Point(40.408070662291784, -3.704411525861016)
	),
	Location(
		"Centro comercial Parquesur",
		[PlantNecessity.SUN, PlantNecessity.OTHER],
		Point(40.340210622524786, -3.733018720009428)
	),
	Location(
		"Puerta del Sol",
		[PlantNecessity.FRIENDS, PlantNecessity.OTHER],
		Point(40.4169, -3.7035)
	),
	Location(
		"Plaza de Cibeles",
		[PlantNecessity.WATER, PlantNecessity.SUN],
		Point(40.4193, -3.6929)
	),
	Location(
		"Templo de Debod",
		[PlantNecessity.SUN, PlantNecessity.FRIENDS],
		Point(40.4240, -3.7177)
	),
	Location(
		"Museo del Prado",
		[PlantNecessity.OTHER, PlantNecessity.FRIENDS],
		Point(40.4137, -3.6921)
	),
	Location(
		"Plaza Mayor",
		[PlantNecessity.FRIENDS, PlantNecessity.OTHER],
		Point(40.4154, -3.7074)
	),
	Location(
		"Gran Vía",
		[PlantNecessity.OTHER, PlantNecessity.FRIENDS],
		Point(40.4200, -3.7050)
	),
	Location(
		"Puerta de Alcalá",
		[PlantNecessity.SUN],
		Point(40.41960877611426, -3.687812863576728)
	),
	Location(
		"Madrid Río",
		[PlantNecessity.WATER, PlantNecessity.FRIENDS],
		Point(40.39861606394164, -3.7153654616426857)
	),
]
