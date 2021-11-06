# Logsearch application

### Objective
The aim is to create an application to search for logs in a given interval as provided by the user.

We implement two RESTful services using gRPC protocol and Finch-Finagle HTTP with the same functionality. We also create a lambda function that the services can leverage to perform the expensive task of searching for logs using the serverless architechture of lambda. This enables the services to process as many requests as possible while delegating the time consuming search task to the lambda function.

Task:

1. Take as input given interval (e.g: 2021-11-05 13:05:10) and a given delta (100 seconds)
2. Search for log messages within the given interval from the log files stored in AWS S3
3. Return a MD5-generated hashcode from the found log messages or return suitable a HTTP response to indicate if no logs were found

### Architecture

The architecture of the project is as below:

![image](https://user-images.githubusercontent.com/33444577/140598535-1e679802-8a8f-45d9-aaad-ab0add75c3e5.png)

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

#### Search

The log messages are found using binary search. By using the S3Object's `GetObjectRequest.withinRange` S3 API method, we are able to access a range of bytes from the file.

We can use the `getObjectMetadata.getContentLength` method to find the length of the file in bytes. With min=0 and max=lengthOfFile, we can perfomr a binary search as follows:

```
search - desired element

while(min<max){

  middle = (min+max)/2

  direction = middle.compareTo(search)

  if(direction == 0) found!
  if(direction > 0) min = middle
  if(direction < 0) max = middle
  
}
```

With an added tweak that, when reading the middle of a file using byte range and a length, the message could be starting from the midddle like this - "abcabc (interval): abcabc". So we iterate through this constant length to locate the interval within the partially read line and use it to get the comparison value for our binary search.

Since this is a constant factor, O(findLogs) = m.log(n) where m is the maximum length of the log message and n is the number of log messages across all files. And, because m << n, we can say that O(findLogs) ~= log(n).

Also, another detail is that we are using the hash lookup to locate the files we need to access for our search interval. The search files are aggregated here at hourly level. So if our interval + delta is as follows: "2021-11-05 22:59:50" delta=30, then the hourly files we have to lookup are - 2021-11-05 22:00 and 2021-11-05 23:00.

Here we can use the hash lookup to quickly determine if we have any log file in that hourly interval, and only if we have a file, we use binary search to check if there are any log messages present.

Also, when the search interval spans multiple files, there are three cases:

* For the first file, we read from some interval in the middle to the end of that file
* For all inbetween files, we read them completely as all the messages fall into the interval
* For the last file, we read from the beginning to the middle where the end interval occurs

This functionality for search interval across multiple files has been implemented, but was tested.

#### Log Generator

The log generator was deployed to EC2 and the log files generated were moved to S3 buckets. A lambda function that triggers when ".log" files get uploaded to S3 was used to update the Hash lookup. Currently, the log files were moved indirectly to the S3 buckets.

An implementation using logback framework was considered similar to the approach specified [here](https://github.com/mweagle/Logpig) by extending the existing `TimeBasedRollingWindowPolicy` class of the logback framework, but ultimately it was not implemented.

### Demo
Please find the youtube demo [here](https://www.youtube.com/watch?v=mnjlwxpp39I&feature=youtu.be)

### Deployment

The lambda function is deployed at this endpoint: `https://6hywkax1t9.execute-api.us-east-2.amazonaws.com/prod/`

Logsearch can be performed by making the following request - `https://6hywkax1t9.execute-api.us-east-2.amazonaws.com/prod/findlogs?interval="2021-10-19 23:37:14"&delta=10`

The current log files in S3 are from 2021-10-19 between 23:37:11 and 23:37:59; and 2021-11-05 between 01:28:57 and 01:33:23.

The output will be a HTTP status 200 along with the MD5 hashcode of retreived log messages and if no messages were present for the given interval, the output will be a HTTP 405 code.

### References

Some useful resources for the development:

* This stackoverflow [discussion](https://stackoverflow.com/questions/10010151/how-to-perform-a-binary-search-of-a-text-file) that gave the idea for the random search that was modifed for the purpose of S3. The solution that was used was actually the last response that was not the accepted answer.
* [Reference](https://docs.aws.amazon.com/code-samples/latest/catalog/java-s3-src-main-java-aws-example-s3-GetObject2.java.html) for S3 file operations
* ScalaPB Grpc client server [example](https://scalapb.github.io/docs/getting-started)
* Followed [this](https://www.baeldung.com/scala/finch-rest-apis) guide in creating the Finch server
