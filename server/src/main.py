#!/bin/env python3
import argparse
import coloredlogs
import sys

import api
import db

log = coloredlogs.logging.getLogger("main")
DEFAULT_LISTEN_PORT = 80
DEFAULT_LOG_LEVEL = "INFO"
LOG_LEVELS = [
	"CRITICAL", "ERROR", "WARNING", "INFO", "DEBUG"
]


def main_server():
	parser = argparse.ArgumentParser(
		prog="api_server",
		description="MAD API server",
		add_help=True,
	)
	parser.add_argument(
		"--db",
		metavar="PATH",
		help="SQLite database file path",
		required=True,
	)
	parser.add_argument(
		"-b", "--bind",
		metavar="ADDRESS",
		help="bind to this address (default: all interfaces)",
		default="0.0.0.0",
	)
	parser.add_argument(
		"-p", "--port",
		help=f"listen on this port (default: {DEFAULT_LISTEN_PORT})",
		default=DEFAULT_LISTEN_PORT,
	)
	parser.add_argument(
		"-l", "--log-level",
		metavar="LEVEL",
		help=f"logging level (default: {DEFAULT_LOG_LEVEL})",
		choices=LOG_LEVELS,
		default=DEFAULT_LOG_LEVEL,
	)
	args = parser.parse_args()

	coloredlogs.install(
		level=args.log_level,
		fmt='%(name)s %(levelname)s %(message)s'
	)

	log.info("Starting server")

	try:
		database = db.MadDB(db_path=args.db)
		db.MadDB.set_instance(database)
	except:
		sys.exit(1)

	api.start(
		bind_address=args.bind,
		port=args.port,
	)



if __name__ == '__main__':
	main_server()
