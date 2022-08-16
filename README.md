# cli-flashcards

A simple server which communicates via gRPC

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

`$ sbt "run -p=8080 -h=127.0.0.1"`

From inside the `sbt` shell

`sbt> run -p=8080 -h=127.0.0.1`

This configuration can also be overridden by editing `application.conf` or by defining the `$CLIF_HOST` and `$CLIF_PORT` environment variables.

### example commands

1. make sure you're in the project root (`/cli-flashcards`) directory
2. make sure `$CLIF_HOST` and `$CLIF_PORT` are set appropriately (`127.0.0.1` and `8080` are probably fine)
3. `sbt run`
4. then, to list all available categories of flashcards

`$ grpcurl -vv -plaintext -import-path ./src/main/protobuf -proto flashcards.proto $CLIF_HOST:$CLIF_PORT cli_flashcards.FlashcardService/Categories`

5. and to list all flashcards in a category

`$ grpcurl -vv -d '{"name": "example"}' -plaintext -import-path ./src/main/protobuf -proto flashcards.proto $CLIF_HOST:$CLIF_PORT cli_flashcards.FlashcardService/Flashcards`

## packaging

### as a jar

Publish a fat jar with `sbt assembly` and then run with Java 17+

`$ java -jar path/to/cli-flashcards-x.y.z.jar`

Usually, this is

`$ java -jar target/scala-3.1.3/cli-flashcards-x.y.z.jar`

add `host` and `port` flags after the jar name, like

`$ java -jar target/scala-3.1.3/cli-flashcards-x.y.z.jar -h=localhost -p=8081`

### as a Docker container

In the root directory (where the `Dockerfile` is) run

`$ docker build -t <username>/cli-flashcards:x.y.z .`