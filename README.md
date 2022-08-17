# cli-flashcards

A simple server which communicates via gRPC.

Note: in the examples below, the ports `9999` and `8888` can be replaced with any appropriate port, but the hostnames (`localhost` when running locally, `0.0.0.0` when running within a Docker container) should not be changed.

## built using

- Scala 3.1.3
- sbt 1.7.1
- Java 17.0.3-zulu

and also

- protobuf
- `akka-http`
- `json4s` and `upickle`

## compile, test, run, package

with

`sbt compile`

`sbt test`

`sbt run`

`sbt assembly`

### configuration

Note that `run` accepts host (`-h`/`--host`) and port (`-p`/`--port`) command-line arguments.

From the terminal

`$ sbt "run -p=9999 -h=127.0.0.1"`

From inside the `sbt` shell

`sbt> run -p=9999 -h=127.0.0.1`

This configuration can also be overridden by editing `application.conf` or by defining the `$CLIF_HOST` and `$CLIF_PORT` environment variables.

### example commands

1. make sure you're in the project root (`/cli-flashcards`) directory
2. make sure `$CLIF_HOST` and `$CLIF_PORT` are set appropriately
3. `sbt run -h=$CLIF_HOST -p=$CLIF_PORT`
4. then, in another terminal, to list all available categories of flashcards

`$ grpcurl -plaintext -import-path ./src/main/protobuf -proto flashcards.proto $CLIF_HOST:$CLIF_PORT cli_flashcards.FlashcardService/Categories`

5. and to list all flashcards in a category

`$ grpcurl -d '{"name": "example"}' -plaintext -import-path ./src/main/protobuf -proto flashcards.proto $CLIF_HOST:$CLIF_PORT cli_flashcards.FlashcardService/Flashcards`

## packaging

### as a jar

Publish a fat jar with `sbt assembly` and then run with Java 17+

`$ java -jar path/to/cli-flashcards-x.y.z.jar`

Usually, this is

`$ java -jar target/scala-3.1.3/cli-flashcards-x.y.z.jar`

add `host` and `port` flags after the jar name, like

`$ java -jar target/scala-3.1.3/cli-flashcards-x.y.z.jar -h=localhost -p=9999`

### as a Docker container

In the root directory (where the `Dockerfile` is) run

`$ docker build -t cli-flashcards:x.y.z .`

then, run with

`$ docker run -it -p $CLIF_PORT:8888 cli-flashcards:my-tag -h=0.0.0.0 -p=8888`

and interact with

`$ grpcurl -plaintext -import-path ./src/main/protobuf -proto flashcards.proto localhost:$CLIF_PORT cli_flashcards.FlashcardService/Categories`

or

`$ grpcurl -d '{"name": "example"}' -plaintext -import-path ./src/main/protobuf -proto flashcards.proto localhost:$CLIF_PORT cli_flashcards.FlashcardService/Flashcards`

Note that we `docker run` using `0.0.0.0`, but we `grpcurl` using `localhost`