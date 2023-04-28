# Getting Started

### How to execute the project
After clone the project, open up the terminal, navigate to the folder where the project was cloned 
and then execute the command:

`./mvnw spring-boot:build-image`

After that we can see on the console one docker image called `docker.io/library/psm-io:0.0.1-SNAPSHOT`

Just execute the command below to run the application:

`docker run --rm -p 8080:8080 -d docker.io/library/psm-io:0.0.1-SNAPSHOT`

The application is up and running, on the root of the project have a postman collection under the folder called 
`collections`

The Documentation of the project is [here](http://localhost:8080/swagger-ui/index.html#/)

Also, the [H2 Console](http://localhost:8080/h2-console/)

The project was built using the follow stack-tech:
* Java 17
* spring-boot 3.0.6
* Lombok
* H2
