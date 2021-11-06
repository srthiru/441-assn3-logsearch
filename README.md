# Logsearch application

### Objective
The aim is to create an application to search for logs in a given interval as provided by the user.

We implement two RESTful services using gRPC protocol and Finch-Finagle HTTP with the same functionality. We also create a lambda function that the services can leverage to perform the expensive task of searching for logs using the serverless architechture of lambda. This enables the services to process as many requests as possible while delegating the time consuming search task to the lambda function.

Task:

1. Take as input given interval (e.g: 2021-11-05 13:05:10) and a given delta (100 seconds)
2. Search for log messages within the given interval from the log files stored in AWS S3
3. Return a MD5-generated hashcode from the found log messages or return suitable a HTTP response to indicate if no logs were found

### Architecture


### Structure of the project

The task has been split into 4 sbt projects. The structure of the repo is as follows:
* The *logsearch-service* project consists of the two server implementations using gRPC and Finch
* The *logsearch-client-rest* project consists of the client implementation using Finagle HTTP to make an API call to the REST API implemented using Finch
* The *logsearch-client-grpc* project consists of the client implementation corresponding to the gRPC server
* The *logsearch-lambda* project consists of the implementation of the lambda function that was deployed
* Additionally, there is also a python script to update a hash lookup table that will be used by the lambda function

### Running the application
\
To run the application, first start the logsearch-service to start both the gRPC server and the Finch service. 

For all of the above, use the command `sbt-run` to run the respective projects.

### Implementation details and limitations
1. 
### Dependencies

### References

