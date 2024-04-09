import COSE.*;
import java.io.*;
import com.google.zxing.*;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.*;
import com.google.zxing.qrcode.QRCodeWriter;
import java.nio.file.Path;
import java.time.*;
import com.upokecenter.cbor.*;
import nl.minvws.encoding.Base45;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.*;


public class Main {

    public static void main(String[] args) throws CoseException, WriterException, DecoderException {

        // the health information are in json format you can modify them, to your own Data
        String json = "{\"v\":[{\"ci\":\"URN:UVCI:01:DE:187/37512422923\",\"co\":\"DE\",\"dn\":3,\"dt\":\"2021-11-24\",\"is\":\"Robert Koch-Institut\",\"ma\":\"ORG-100030215\",\"mp\":\"EU/1/20/1528\",\"sd\":3,\"tg\":\"840539006\",\"vp\":\"1119349007\"}],\"dob\":\"2000-05-12\",\"nam\":{\"fn\":\"Blabla\",\"gn\":\"Flafla\",\"fnt\":\"BLABLA\",\"gnt\":\"FLAFLA\"},\"ver\":\"1.0.0\"}";

        //using the system local time and convert them to Epoch , to work as issued and expiration infos.
        long issued = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() / 1000L;
        long expiration = LocalDateTime.now().plusYears(1).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() / 1000L;


        // Then we create a cbor object, that contains our health information as well as issued, expired time, issuer and version ...etc
        CBORObject map = CBORObject.NewMap();
        map.set(CBORObject.FromObject(1), CBORObject.FromObject("DE")); // for France is CNAM, if needed
        map.set(CBORObject.FromObject(6), CBORObject.FromObject(issued));
        map.set(CBORObject.FromObject(4), CBORObject.FromObject(expiration));
        CBORObject hcertVersion = CBORObject.NewMap();
        CBORObject hcert = CBORObject.FromJSONString(json);
        hcertVersion.set(CBORObject.FromObject(1), hcert);
        map.set(CBORObject.FromObject(-260), hcertVersion);

        // converting the whole data to encoded binary data .. cbor object -> Binary data
        byte[] cbor = map.EncodeToBytes();

        // Generating a random private key, that satisfy the needs. If you have the private key u can use it , but impossible lol
        // I have search a lot for the Pk but no use, so I think it is server sided.
        OneKey privateKey = OneKey.generateKey(AlgorithmID.ECDSA_256);

       // the key that I found while analysing the app and tracing their requests, it is trusted public key btw
        byte [] ki = Hex.decodeHex("E7714E8D7FF8689B");

        //You can use a random generated key ofc
       // byte[] kid = UUID.randomUUID().toString().getBytes(StandardCharsets.UTF_8);


        // Now signing the data with both keys, as we can see the most important is the Private key
        Sign1Message msg = new Sign1Message();
        msg.addAttribute(HeaderKeys.Algorithm, privateKey.get(KeyKeys.Algorithm), Attribute.PROTECTED);
        msg.addAttribute(HeaderKeys.KID, CBORObject.FromObject(ki), Attribute.PROTECTED);
        msg.SetContent(cbor);
        msg.sign(privateKey);


        // After singing up the data, we are converting them to byte array, so we can compress them
        byte[] cose = msg.EncodeToBytes();


        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        try (CompressorOutputStream deflateOut = new CompressorStreamFactory().createCompressorOutputStream(CompressorStreamFactory.DEFLATE, stream))
        {
            deflateOut.write(cose);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CompressorException e) {
            e.printStackTrace();
        }
        byte[] zip = stream.toByteArray();


        //After compressing the data, I noticed it was encoded in base45.
        //why base45 I do not know :)
        String vorlast = Base45.getEncoder().encodeToString(zip);


        //In this step we are adding HC1 to the base45 converted code from the previous stage
        // I think HC means Health Certificate  version 1. (: lol
        String last = "HC1:" + vorlast;


        //Lastly with the help of zxing library we are writing the final code to QR image, then export it.
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        int width = 300, height = 300;
        BitMatrix bitMatrix = null;

        try {

            bitMatrix = qrCodeWriter.encode(last, BarcodeFormat.QR_CODE, width, height);

            // You can change the path of the file
            File my = new File("C:\\Users\\abodx\\Projects\\Idea Projects\\QR_COVID\\fake.png");
            Path pth = my.toPath();


            MatrixToImageWriter.writeToPath(bitMatrix, "PNG", pth);

        } catch (WriterException e){
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        }

        System.out.println("\nDone \n\nmade by Abodx");

    }
}
