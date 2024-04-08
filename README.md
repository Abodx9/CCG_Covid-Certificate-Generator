# CCG-Covid Certificate Generator

This project generates fake COVID QR certificates for testing purposes.
The certificates are generated using Java and can be customized to include various fields such as name, date of birth, vaccine type, etc. The certificates are not intended to be used for any illegal or unethical purposes and are only for testing purposes.


## Implementation

I have implemented many Libraries, they helped a lot while generating these Fake Certificates.
 
Some are:

```bash
  COSE
  com.google.zxing
  com.upokecenter.cbor
  nl.minvws.encoding.Base45

   ....etc
```
    
And to run this you need just to download the project and open it in any java editor.

I have not included any Libraries, so you should download them again, and import them to the project.


## Keys

- I found a valid public key

- I could not find the private key, so it may be server-sided.

Now after enforcing the check of the Private key, these Certs. are not working anymore.

They may still work in some countries that have not yet enforced the checking.

## Stages


- **Raw Data:** The certificate starts as raw data, which includes information such as the individual's name, date of birth, vaccination status, test results, and recovery status.

- **JSON Format:** The raw data is then converted to a structured format called JSON (JavaScript Object Notation), which is a lightweight data-interchange format that is easy for computers to read and write.

- **CBOR Object:** The JSON data is then encoded as a CBOR (Concise Binary Object Representation) object. CBOR is a binary data format that is more compact and efficient than JSON.

- **COSE Signing:** The CBOR object is then digitally signed using a secure cryptographic algorithm called COSE (CBOR Object Signing and Encryption). This process ensures that the data has not been tampered with or altered since it was created.

- **Zlib Compression:** The COSE signed data is then compressed using the Zlib compression algorithm.

- **Base45 Encoding:** The final step is to encode the compressed data using the Base45 encoding scheme. Base45 is a binary-to-text encoding scheme that uses a limited set of characters to represent binary data.

**A QR code is generated with the final Base45** 


## Stages (Pic): 

- This photo explain all stages.

![](https://github.com/Abodx9/CCG_Covid-Certificate-Generator/blob/main/How.png)


- The process can be reverse in order to decode the QR Code.


## Testing: 

Here is a simple Test that I made before enforce  the private key checking
![](https://github.com/Abodx9/CCG_Covid-Certificate-Generator/blob/main/Test.gif)