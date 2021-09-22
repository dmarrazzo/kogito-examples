# Process Error Handling

## Description

A simple usage scenario of the Error Handling Strategy

The main process is 

![hello error](docs/images/hello-error-process.png)

Here the logic:

- `Custom Task` is a custom WorkItemHandler, the simple implementation is

  - Read the `Input` parameter
  - Return the `Result` string with the concatenation of `Hello ` and the value of the `Input`
  - Whether `Input` matches one of the following values `RETRY`, `COMPLETE`, `ABORT` or `RETHROW`; it throws a `ProcessWorkItemHandlerException` initialized with the corresponding strategy

- `Print Message` is script which print out the outcome of the previous Task (stored in `message` variable).

- Finally, an event sub-process is defined to catch all exceptions that reach the main process instance and to print out `Catch all` in the console.

In short, it is a sophisticated version of a **Hello World** process!

In order to probe the _Error Handling_ capabilities, you have to trigger the process with the **name** of the error handling strategy.
Regardless the strategy, the sub-process `error-handling` will be executed, then the main process execution is influenced by the strategy:

- `RETRY`: the `Custom Task` is executed again, the `Input` parameter of the task is refreshed using the outcome of the `error-handling` process.
- `COMPLETE`: the `Custom Task` is skipped, the `Result` parameter of the task is set with the corresponding outcome of the `error-handling` process.
- `ABORT`: the `Custom Task` is aborted and the containing process instance is terminated.
- `RETHROW`: the `Custom Task` is aborted and the exception is thrown back at containing process level.

The `error-handling` sub-process initiates a user task which goal is to repair the situation.

The process design leaves the user in full control:

- Analyze the error message to understand the root cause of the problem
- Override the default _error handling strategy_
- In case of `RETRY`, they can provide a different **input** parameter for the task
- In case of `COMPLETE`, they can set the output parameter **result** for the task, in other words, the user replaces the implementation of the task by simulating a response

Here the process model:

![error-handling-process](docs/images/error-handling-process.png)

The `Init` script calls the corresponding method of the class `ErrorHandlingScript` and there it performs the following:

- Logs the process variables
- Store the _error handling strategy_ in the `strategy` process variable 

The `Apply` script calls the corresponding method of the class `ErrorHandlingScript` and there it performs the following:

- Read the `strategy` variable
- Override the `Error` variable with a new `ProcessWorkItemHandlerException` initialized with the new strategy

## Build and run

### Prerequisites

You will need:
  - Java 11+ installed
  - Environment variable JAVA_HOME set accordingly
  - Maven 3.6.2+ installed

When using native image compilation, you will also need:
  - GraalVM 19.3+ installed
  - Environment variable GRAALVM_HOME set accordingly
  - GraalVM native image needs as well native-image extension: https://www.graalvm.org/reference-manual/native-image/
  - Note that GraalVM native image compilation typically requires other packages (glibc-devel, zlib-devel and gcc) to be installed too, please refer to GraalVM installation documentation for more details.

### Compile and Run in Local Dev Mode

```sh
mvn clean compile quarkus:dev
```

NOTE: With dev mode of Quarkus you can take advantage of hot reload for business assets like processes, rules, decision tables and java code. No need to redeploy or restart your running application.

### Package and Run in JVM mode

```sh
mvn clean package
java -jar target/quarkus-app/quarkus-run.jar
```

or on windows

```sh
mvn clean package
java -jar target\quarkus-app\quarkus-run.jar
```

### Package and Run using Local Native Image
Note that this requires GRAALVM_HOME to point to a valid GraalVM installation

```sh
mvn clean package -Pnative
```

To run the generated native executable, generated in `target/`, execute

```sh
./target/process-error-handling-runner
```
### OpenAPI (Swagger) documentation
[Specification at swagger.io](https://swagger.io/docs/specification/about/)

You can take a look at the [OpenAPI definition](http://localhost:8080/openapi?format=json) - automatically generated and included in this service - to determine all available operations exposed by this service. For easy readability you can visualize the OpenAPI definition file using a UI tool like for example available [Swagger UI](https://editor.swagger.io).

In addition, various clients to interact with this service can be easily generated using this OpenAPI definition.

When running in either Quarkus Development or Native mode, we also leverage the [Quarkus OpenAPI extension](https://quarkus.io/guides/openapi-swaggerui#use-swagger-ui-for-development) that exposes [Swagger UI](http://localhost:8080/swagger-ui/) that you can use to look at available REST endpoints and send test requests.

### Submit a request

To make use of this application it is as simple as putting a sending request to `http://localhost:8080/scripts`  with following content

```json
{
    "name" : "john"
}

```

Complete curl command can be found below:

```sh
curl -X POST -H 'Content-Type:application/json' -H 'Accept:application/json' -d '{"name" : "john"}' http://localhost:8080/scripts
```

Response should be similar to:

```json
{
    "id":"ab5239e2-f497-4684-b337-5a44440b38dd",
    "name":"john",
    "message":"Hello john"
}
```

And also in Quarkus log you should see a log entry:

```
Hello john"
```

## Deploying with Kogito Operator

In the [`operator`](operator) directory you'll find the custom resources needed to deploy this example on OpenShift with the [Kogito Operator](https://docs.jboss.org/kogito/release/latest/html_single/#chap_kogito-deploying-on-openshift).
