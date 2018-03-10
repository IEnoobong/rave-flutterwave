[![CircleCI](https://circleci.com/gh/IEnoobong/rave-flutterwave/tree/master.svg?style=svg)](https://circleci.com/gh/IEnoobong/rave-flutterwave/tree/master)

# Rave-Flutterwave-Kotlin

Kotlin (optimized for the JVM) library to consume Flutterwave's [Rave API](https://ravepay.co/api-documentation) **The easy way to 
collect payments from customers anywhere in the world**

## Usage

All calls happen asynchronously. Callbacks are executed on the background thread which performed the request.

### Maven
```
<dependency>
    <groupId>co.enoobong</groupId>
    <artifactId>Rave-Flutterwave</artifactId>
    <version>${rave.version}</version>
</dependency>
```

### Gradle
```markdown
implemetation "co.enoobong:rave-flutterwave:$rave_version"
```

### Kotlin
```
// Create a RavePay instance
val ravePay = RavePay.Builder()
            .setEnvironment(Environment.STAGING) //Defaults to STAGING
            .setSecretKey("FLWSECK-bb971402072265fb156e90a3578fe5e6-X")
            .setPublicKey("FLWPUBK-e634d14d9ded04eaf05d5b63a0a06d2f-X")
            .build()
            
// RavePay gives you access to the API's
            
val cardPayload = CardPayload(
                        "5438898014560229", "812", "08", "20", "NGN", "NG", 1000.0,
                        "eno@eno.com",
                        "07061234567", "Eno Wa", "Eno", "192.168.1.2", "TEST " + Date().toString(),
                        "http://test.test"
                    )
                    
                    cardPayload.suggestedAuth = "PIN"
                    cardPayload.pin = "3310"
            
ravePay.chargeCard(cardPayload, object : RaveCallback<ApiResponse<ChargeResponseData>>{
            override fun onSuccess(
                response: ApiResponse<ChargeResponseData>,
                responseAsJsonString: String
            ) {
                //Captures a successful request i.e 200...300 HTTP Codes
            }

            override fun onError(message: String?, responseAsJsonString: String?) {
                //Captures an unsuccessful request
            }

        })            

```

### Java

```
final RavePay ravePay = new RavePay.Builder()
                .setEnvironment(Environment.STAGING) //Defaults to STAGING
                .setSecretKey("FLWSECK-bb971402072265fb156e90a3578fe5e6-X")
                .setPublicKey("FLWPUBK-e634d14d9ded04eaf05d5b63a0a06d2f-X")
                .build();
                
final CardPayload cardPayload = new CardPayload(
                "5840406187553286", "116", "09", "18", "NGN", "NG", 1000,
                "ibangaenoobong@yahoo.com",
                "07061234567", "Eno Wa", "Eno", "192.168.1.2", "TEST " + new Date().toString(),
                "http://test.test");
                
        cardPayload.setSuggestedAuth("PIN");
        cardPayload.setPin("1111");                

build.preauthorizeCard(cardPayload, new RaveCallback<ApiResponse<PreauthorizeCardData>>() {
            @Override
            public void onSuccess(ApiResponse<PreauthorizeCardData> preauthorizeCardDataApiResponse, String s) {
                //Captures a successful request i.e 200...300 HTTP Codes
                // Do something with Response
            }

            @Override
            public void onError(String message, String response) {
                //Captures an unsuccessful request
                // Act accordingly 

            }
        });                
                
```

## Contributing


I'm more than welcome to contributions that improve the library and make it easier to use.
If you are willing to contribute to the project feel free to make a fork and submit a pull request. 
You can hit me up on [@IEnoobong](http://twitter.com/IEnoobong) on Twitter

## Donate

Want to buy me Jollof-Rice? Send here's my link https://payme.ng/ienoobong Thanks