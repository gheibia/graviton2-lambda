# graviton2-lambda

A simple Lambda function built with Java (Corretto 11) to be deployed twice: once using AWS's new Garviton CPU architecture and once, without.

Each deployed funtion is behind a REST API method allowing us to test the performance and cost of each deployment.

The entire stack is deployed using CloudFormation (see `template.yml`).

## Build and Deploy

```
$  ./gradlew -q clean packageLibs && mv build/distributions/graviton2.zip build/distributions/graviton2-lib.zip && ./gradlew -q build

$  aws cloudformation package --template-file template.yml --s3-bucket your-s3-bucket --output-template-file out.yml

$  aws cloudformation deploy --template-file out.yml --stack-name gaviton2 --capabilities CAPABILITY_NAMED_IAM --region us-east-1
```

These commands recpectively:
 - Build and package the application: function and its dependency JARs are packaged in 2 separate zip files
 - Prepare CloudFormation template: uplaod zip files to S3 and update the template with S3 URIs
 - Deploy the CloudFormation stack.


 ## Notes

 - **DO NOT** make your S3 bucket public. Instead configure your AWS CLI correctly to be able to upload sourze zip file to S3 (when running `aws cloudformation package`).
 - As it can be seen, `gradlew` is used for building the app. The wrapper script is in the code base and it will automatically pull `gradle` the first time it runs. thus, one doesn't need to install `gradle`.