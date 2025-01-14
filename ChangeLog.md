# Changelog
All notable changes to this project will be documented in this file.
The format is based on [Keep a Changelog](http://keepachangelog.com/en/1.0.0/) and this project adheres
to [Semantic Versioning](http://semver.org/spec/v2.0.0.html).

## [Unreleased]

## [1.2.0] - 2022-03-18

- use `overwriteMode=ignore` if save mode is other than `Append` (#26)
- require non-nullable string fields `_from` and `_to` to write to edge collections (#25)
- configurable backoff retry delay for write requests (`retry.minDelay` and `retry.maxDelay`), disabled by default (#24)
- retry only if schema has non-nullable field `_key` (#23)
- retry on connection exceptions
- added `retry.maxAttempts` config param (#20)
- increased default timeout to 5 minutes
- reject writing decimal types with json content type (#18)
- report records causing write errors (#17)
- improved logging about connections and write tasks

## [1.1.1] - 2022-02-28

- retry timeout exception in truncate requests (#16) 
- fixed exception serialization bug (#15)

## [1.1.0] - 2022-02-23

- added driver timeout configuration option (#12)
- updated dependency `com.arangodb:arangodb-java-driver:6.16.1`

## [1.0.0] - 2021-12-11

- Initial Release
