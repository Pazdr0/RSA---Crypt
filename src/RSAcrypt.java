import java.io.*;
import java.math.*;
import java.nio.file.*;


public class RSAcrypt {
	PublicKey client1 = new PublicKey();
	PrivateKey server1 = new PrivateKey();
	private final String textFile = "binText.bin";														// z tekstowego(Text.txt)/z binarnego(binText.bin)
	private final String cryptedTextFile = "Crypted.txt";
	private final int bitLength = 8;
	private String text;
	
	public class PublicKey {
		private int n;
		private int e;
	}
	public class PrivateKey {
		private int n;
		private int d;
	}

	public static void main(String[] args) {
		RSAcrypt rsa = new RSAcrypt();
		rsa.run();
	}

	public void run() {
		generateKeys();																				// Generowanie kluczy

		byte[] byteArray = readBytesToArray(textFile);												// Odczyt bajtów z pliku
		readTextFile();																				// Zczytywanie znaków z pliku
		
		BigInteger[] encrypted = encrypt(client1.n, client1.e, byteArray);							// Szyfrowanie
		decrypt(server1.n, server1.d, encrypted);
//		saveEncrypted(encrypted);
//		displayBytesFromFile(byteArray);
		displayBigArray(encrypted, "Zaszyfrowane");
//		displayBigArray(decrypt(server1.n, server1.d, encrypted), "Odszyfrowane");		
//		cryptedByteArray = readBytesToArray(cryptedTextFile);	// Deszyfrowanie
		System.out.println("\nKoniec");
	}
	
	public BigInteger[] encrypt(int modulus, int e, byte[] byteArray) {
		BigInteger[] cryptedArray = new BigInteger[byteArray.length];
		BigInteger bigMod = new BigInteger(Integer.toString(modulus));
		for (int i=0; i<byteArray.length; i++) {
			cryptedArray[i] = new BigInteger(Byte.toString(byteArray[i]));
		}
		
		for (int i=0; i<cryptedArray.length; i++) {
			cryptedArray[i] = cryptedArray[i].pow(e);
			cryptedArray[i] = cryptedArray[i].mod(bigMod);
		}
		return cryptedArray;
	}
	
	public BigInteger[] decrypt(int modulus, int d, BigInteger[] cryptedArray) {
		BigInteger bigMod = new BigInteger(Integer.toString(modulus));
		BigInteger[] decryptedArray = new BigInteger[cryptedArray.length];
		for (int i=0; i<cryptedArray.length; i++) {
			decryptedArray[i] = cryptedArray[i].pow(d);
			decryptedArray[i] = decryptedArray[i].mod(bigMod);
		}
		return decryptedArray;
	}
	
	public void saveEncrypted(BigInteger[] encrypted) {
		clear();
		for (int i=0; i<encrypted.length; i++) {
			saveToFile(encrypted[i].toString() + " ");
		}
	}
	
	public void displayBytesFromFile(byte[] arr) {
		System.out.println("\nTekst: \n" + text + "\n\nOdczytane bajty z pliku:");
		System.out.print(" | ");
		for (int i=0; i<arr.length; i++) {
			System.out.print(arr[i] + " | ");	
		}
	}
	public void displayBigArray(BigInteger[] arr, String name) {
		System.out.println("\n" + name + ": ");
		System.out.print(" | ");
		for (int i=0; i<arr.length; i++) {
			System.out.print(arr[i] + " | ");		
		}
	}
	
	public int getPublicKeyN() {
		return client1.n;
	}
	public int getPublicKeyE() {
		return client1.e;
	}
	public int getPrivateKeyN() {
		return server1.n;
	}
	public int getPrivateKeyD() {
		return server1.d;
	}
	
	public void generateKeys() {
		int p, q, n, phi, e, d;
		// wyznaczam klucz publiczny i prywatny
		p = primes();
		q = primes();
		n = p*q;
		phi = (p-1)*(q-1);
		e = findE(phi);
		d = findD(phi, e);
		//przypisuje im wartosci
		client1.n = n;
		client1.e = e;
		server1.n = n;
		server1.d = d;
		
		System.out.println("Wartości do obliczeń: \nLiczb pierwsza 1 = " + p + ", Liczb pierwsza 2 = " + q + ", Modulo = " + n + ", Fi = " + phi + ", Klucz publiczny = " + e + ", Klucz prywatny = " + d);
	}
	

	public int findD(int phi, int e) {
		BigInteger d, q, ksi;
		q = new BigInteger(Integer.toString(e));
		ksi = new BigInteger(Integer.toString(phi));
//		q = new BigInteger("7");
//		ksi = new BigInteger("120");
		d = q.modInverse(ksi);
//		System.out.println(d);
		
		return d.intValue();
	}
	
	public int findE(int phi) {									// losuje liczbe z zakresu od 1 do fi, jesli jest relatywnie pierwsza to ja przypisuje
		
		int E = (int)(Math.random()*(phi - 1) + 1);
		while (isRelativelyPrime(phi, E) != 1) {
			E = (int)(Math.random()*(phi - 1) + 1);
		}
		return E;
	}
	public int isRelativelyPrime(int a, int b) {					// sprawdza czy jest relatywnie pierwsza
		int c;
		while (b != 0) {
			c = a%b;
			a = b;
			b = c;
		}
		return a;		
	}
	
	public int primes() {								// wybiera liczby pierwsze
		int pr = (int)(Math.random()*(Math.pow(2,bitLength) - Math.pow(2,bitLength-1)) + Math.pow(2,bitLength-1));
		while(isPrime(pr) == false) {
			pr++;
		}
		return pr;
	}
	
	boolean isPrime(int N) {											// sprawdza czy liczba jest pierwsza
	    if (N%2==0) 
	    	return false;
	    for(int i=3; i<=N/2; i+=2) {
	        if (N%i == 0)
	            return false;
	    }
	    return true;
	}
	
	public void readTextFile() {										// wczytuje napis w postaci Stringa do zmeinnej
		try {
			BufferedReader reader = new BufferedReader(new FileReader(textFile));
			text = reader.readLine();
			reader.close();
		}
		catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	public byte[] readBytesToArray(String fileName) {									// wczytuje kolejne bajty z pliku do tablicy
		byte[] arr = {0};
		Path path = Paths.get(fileName);
		try {
			arr = Files.readAllBytes(path);
			return arr;
		}
		catch (IOException ex) {
			ex.printStackTrace();
		}
		return arr;
	}
	
	public void clear() {
		try {
			FileWriter writer = new FileWriter(cryptedTextFile);
			writer.write("");
			writer.close();
		} 
		catch(IOException ex) {
			ex.printStackTrace();
		}
	}
	public void saveToFile(String text) {
		try {
			FileWriter writer = new FileWriter(cryptedTextFile, true);
			writer.write(text);
			writer.close();
		} 
		catch(IOException ex) {
			ex.printStackTrace();
		}
	}
}
