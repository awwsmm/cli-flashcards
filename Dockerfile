FROM sbtscala/scala-sbt:eclipse-temurin-17.0.3_1.7.1_3.1.3 as build
WORKDIR /cli-flashcards/build
COPY src ./src
COPY build.sbt ./build.sbt
COPY project/build.properties ./project/build.properties
COPY project/plugins.sbt ./project/plugins.sbt
RUN sbt assembly

FROM arm64v8/eclipse-temurin:17.0.3_7-jre
COPY --from=build /cli-flashcards/build/target/scala-3.1.3/cli-flashcards-0.0.0.jar /cli-flashcards/run/cli-flashcards-0.0.0.jar
ENTRYPOINT ["java","-jar","./cli-flashcards/run/cli-flashcards-0.0.0.jar"]
CMD []