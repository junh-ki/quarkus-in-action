# reservation-service

This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: <https://quarkus.io/>.

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:

```shell script
./mvnw quarkus:dev
```

> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at <http://localhost:8081/q/dev/>.

## Packaging and running the application

The application can be packaged using:

```shell script
./mvnw package
```

It produces the `quarkus-run.jar` file in the `target/quarkus-app/` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/quarkus-app/lib/` directory.

The application is now runnable using `java -jar target/quarkus-app/quarkus-run.jar`.

If you want to build an _über-jar_, execute the following command:

```shell script
./mvnw package -Dquarkus.package.jar.type=uber-jar
```

The application, packaged as an _über-jar_, is now runnable using `java -jar target/*-runner.jar`.

## Creating a native executable

You can create a native executable using:

```shell script
./mvnw package -Dnative
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using:

```shell script
./mvnw package -Dnative -Dquarkus.native.container-build=true
```

You can then execute your native executable with: `./target/reservation-service-1.0.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult <https://quarkus.io/guides/maven-tooling>.

## Related Guides

- REST ([guide](https://quarkus.io/guides/rest)): A Jakarta REST implementation utilizing build time processing and Vert.x. This extension is not compatible with the quarkus-resteasy extension, or any of the extensions that depend on it.
- ArC ([guide](https://quarkus.io/guides/cdi-reference)): Build time CDI dependency injection

## Integration Test (in native mode)
1. Switch to GraalVM
   ```cmd
   sdk use java 21.0.2-graalce
   ```
2. Go back to root and run the integration test in native mode with the module path
   - if it is for a single service repo
     ```cmd
     ./mvnw clean verify -Pnative
     ```
   - if it for a module in a mono-repo
     ```cmd
      cd ..
      ./mvnw -pl reservation-service clean verify -Pnative
     ```
3. Set it back to the normal Java SDK
   ```cmd
   sdk use java 21.0.3-zulu
   ```

## Kafka verification
```
brew install kafka
kafka-console-consumer --bootstrap-server localhost:44131 --topic invoices-adjust --from-beginning
```
-> The port number would be different each time so you should double-check it by running `docker ps`.

## Containerization
- To build and push an image (Docker by default) - only works if your local username aligns with your Docker Hub name and also you are logged in to Docker Hub.
   ```
   quarkus build \
   -Dquarkus.container-image.build=true \
   -Dquarkus.container-image.push=true \
   -Dquarkus.container-image.registry=docker.io \
   -Dquarkus.container-image.group=mrki102 \
   -Dquarkus.container-image.name=reservation-service \
   -Dquarkus.container-image.tag=1.0.0
   ```
   or
   ```
   ./mvnw clean package \
   -Dquarkus.container-image.build=true \
   -Dquarkus.container-image.push=true \
   -Dquarkus.container-image.registry=docker.io \
   -Dquarkus.container-image.group=mrki102 \
   -Dquarkus.container-image.name=reservation-service \
   -Dquarkus.container-image.tag=1.0.0
   ```
- To explicitly set the registry with the precedence over docker.io
   ```
   quarkus build \
   -Dquarkus.container-image.build=true \
   -Dquarkus.container-image.push=true \
   -Dquarkus.container-image.registry=docker.io \
   -Dquarkus.container-image.group=mrki102 \
   -Dquarkus.container-image.name=reservation-service \
   -Dquarkus.container-image.tag=1.0.0
   -Dquarkus.container-image.image=registry.io/username/reservation-service:1.0.0
   ```
  or
   ```
   ./mvnw clean package \
   -Dquarkus.container-image.build=true \
   -Dquarkus.container-image.push=true \
   -Dquarkus.container-image.registry=docker.io \
   -Dquarkus.container-image.group=mrki102 \
   -Dquarkus.container-image.name=reservation-service \
   -Dquarkus.container-image.tag=1.0.0
   -Dquarkus.container-image.image=registry.io/username/reservation-service:1.0.0
   ```
