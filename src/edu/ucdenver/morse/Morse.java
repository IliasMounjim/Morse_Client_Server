package edu.ucdenver.morse;
import java.util.HashMap;
import java.util.Scanner;

public class Morse {

    private final String alphabets = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
    private final String[] morseCodes = {".-", "-...", "-.-.", "-..", ".", "..-.", "--.", "....", "..", ".---", "-.-", ".-..", "--", "-.", "---",
            ".--.", "--.-", ".-.", "...", "-", "..-", "...-", ".--", "-..-", "-.--", "--..", ".----", "..---", "...--", "....-", ".....",
            "-....", "--...", "---..", "----.", "-----"};

    private HashMap<String, String> toText;
    private HashMap<String, String> toMorseCode;

    public Morse() {
        toText = new HashMap<>();
        toMorseCode = new HashMap<>();
        char[] alphaArray = alphabets.toCharArray();
        for(int i = 0; i < morseCodes.length; i++) {
            toText.put(morseCodes[i], String.valueOf(alphaArray[i]));
            toMorseCode.put(String.valueOf(alphaArray[i]), morseCodes[i]);
        }
    }

    public String encode(String s) {
        s = s.toUpperCase();
        String encoded = "";
        for(int i = 0; i < s.length(); i++) {
            String c = (String.valueOf(s.charAt(i)));
            if(s.charAt(i)==' '){
                encoded +=" ";
            }else{
                if (toMorseCode.containsKey(c)) {
                    encoded += toMorseCode.get(c) + "=";
                } else {
                    encoded += " ";
                }
            }
        }
        return encoded;
    }


    public String decode(String s) {
        String[] code = s.split("=");
        String decoded = "";
        for(int i = 0; i < code.length; i++) {
            if((code[i].toCharArray())[0]==' '){
                decoded += " ";
                code[i]=code[i].trim();
            }
            if (toText.containsKey(code[i])) {
                decoded += toText.get(code[i]);
            } else {
                decoded += " ";
            }
        }
        return decoded;
    }

    public static void main(String[] args) {
        Morse m = new Morse();
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter a sentence to encode");
        String s1 = sc.nextLine();
        String encoded = m.encode(s1);
        System.out.println(encoded);
        System.out.println("Enter a sentence to decode");
        String s2 = sc.nextLine();
        String decoded = m.decode(s2);
        System.out.println(decoded);
    }
}
