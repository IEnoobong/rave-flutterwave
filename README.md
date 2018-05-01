[![CircleCI](https://circleci.com/gh/IEnoobong/rave-flutterwave/tree/master.svg?style=svg)](https://circleci.com/gh/IEnoobong/rave-flutterwave/tree/master)
[![codebeat badge](https://codebeat.co/badges/8aaf43cf-a48d-414b-8231-353aca8e5c37)](https://codebeat.co/projects/github-com-ienoobong-rave-flutterwave-master)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/99613c2e68544d3b8121389f42ad9ac1)](https://www.codacy.com/app/IEnoobong/rave-flutterwave?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=IEnoobong/rave-flutterwave&amp;utm_campaign=Badge_Grade)
[![Codacy Badge](https://api.codacy.com/project/badge/Coverage/99613c2e68544d3b8121389f42ad9ac1)](https://www.codacy.com/app/IEnoobong/rave-flutterwave?utm_source=github.com&utm_medium=referral&utm_content=IEnoobong/rave-flutterwave&utm_campaign=Badge_Coverage)

# Rave-Flutterwave-Kotlin

Kotlin (optimized for the JVM) library to consume Flutterwave's [Rave API](https://ravepay.co/api-documentation) **The easy way to 
collect payments from customers anywhere in the world**

It currently supports the following features:

* Account charge (NG Banks)

* Account charge (International for US and ZAR).

* Card Charge (Baked in support for 3DSecure/PIN).

* Encryption

* Transaction status check (Normal requery flow and xrequery).

* Retry transaction status check flow.

* Preauth -> Capture -> Refund/void flow.

* Support for USSD and Mcash (Alternative payment methods).

* List of banks for NG Account charge. (Get banks list).

* Get fees endpoint.

* Integrity Checksum (https://flutterwavedevelopers.readme.io/docs/checksum).

## Set Up

Go to [rave](http://ravepay.co/) and sign up.
This would provide you with a public and private authorization key which would be used throughout the library.


## Usage

All calls happen asynchronously.

### Maven

See https://bintray.com/ienoobong/Rave/rave-flutterwave-kotlin for detailed setup info
```
<dependency>
    <groupId>co.enoobong</groupId>
    <artifactId>rave-flutterwave-kotlin</artifactId>
    <version>${rave.version}</version>
</dependency>
```

### Gradle
```markdown
implementation "co.enoobong:rave-flutterwave-kotlin:$rave_version"
```

### Kotlin
```
// Create a RavePay instance
val ravePay = RavePay.Builder()
            .setEnvironment(Environment.STAGING) //Defaults to STAGING
            .setSecretKey("FLWSECK-bb971402072265fb156e90a3578fe5e6-X") //Secret Key gotten from dashboard in signup above
            .setPublicKey("FLWPUBK-e634d14d9ded04eaf05d5b63a0a06d2f-X") //Public Key gotten from dashboard in signup above
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
                "enoo@yahoo.com",
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
