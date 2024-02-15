
# customs-service-status-frontend

This service provides a dashboard view of Service Availability.

## Running the service locally
`sbt run`

By default, the service runs on port 8993. Navigate to http://localhost:8993/customs-service-status/service-availability

#### Testing the service locally
`sbt test it:test`

#### Testing locally with test routes enabled
`sbt "run 8993 -Dplay.http.router=testOnlyDoNotUseInAppConf.Routes"`

### License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").